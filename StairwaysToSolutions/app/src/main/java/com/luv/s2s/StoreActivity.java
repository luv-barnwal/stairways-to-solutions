package com.luv.s2s;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.billingclient.api.BillingClient.SkuType.SUBS;

public class StoreActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    Button trade;
    ImageView add, minus;
    TextView endAmount, numberOfPoints;
    int numberPoints;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    private static final String PREF_FILE = "My_Pref";
    private static ArrayList<String> subcribeItemIDs = new ArrayList<String>(){
        {
            add("basic_membership");
            add("premium_membership");
        }
    };
    private BillingClient billingClient;
    private static ArrayList<String> subscribeItemDisplay = new ArrayList<String>(){
        {
            add("Basic Membership $2.49");
            add("Premium Membership $4.49");
        }
    };
    ArrayAdapter<String> arrayAdapter;
    ListView listView;

    private boolean verifyValidSignature(String signedData, String signature) {
        try {
// To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
            String base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoVbqCy4D1hzUg/R2RnofP5dxm03Oz8ddhUq8BP5cqE/7mK0C2PCtBDiVX+DfdLu13q2srslZRiHQ2evHG9dxnOV2P15PIXeQ4U//ksvpSaWZmk4vrZcgVSsnXfF4ipmxBQxjDd1WZuGEIQBDlI4sRjYTaSURKr6dtCy45Vcd4WgWuJH2y8TJE72JJeB55UUw7u34/wDRcoyao+bPqH6mcRIQF8KQVVskXPyExVh6RyGHH3xW2Nl5QyNaI7TWhm8PEprJFj910VWB+XmKIDG6Hf/Wbqkz0tVPmyjQHbYGDXjs/IwsvCCHAw/T7OXZe3y9HnaiNjrFkBQ510YHyP4hBQIDAQAB";
            return Security.verifyPurchase(base64Key, signedData, signature);
        } catch (IOException e) {
            return false;
        }
    }

    void handlePurchases(List<Purchase> purchases) {
        for(Purchase purchase:purchases) {
            final int index=subcribeItemIDs.indexOf(purchase.getSkus().get(0));
//purchase found
            if(index>-1) {
//if item is purchased
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
                {
                    if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
// Invalid purchase
// show error to user
                        Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
                        continue;//skip current iteration only because other items in purchase list must be checked if present
                    }
// else purchase is valid
//if item is purchased/subscribed and not Acknowledged
                    if (!purchase.isAcknowledged()) {
                        AcknowledgePurchaseParams acknowledgePurchaseParams =
                                AcknowledgePurchaseParams.newBuilder()
                                        .setPurchaseToken(purchase.getPurchaseToken())
                                        .build();
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams,
                                new AcknowledgePurchaseResponseListener() {
                                    @Override
                                    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                                        if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
//if purchase is acknowledged
//then saved value in preference
                                            saveSubscribeItemValueToPref(subcribeItemIDs.get(index),true);
                                            Toast.makeText(getApplicationContext(), subcribeItemIDs.get(index)+" Item Subscribed", Toast.LENGTH_SHORT).show();
                                            notifyList();
                                        }
                                    }
                                });
                    }
//else item is purchased and also acknowledged
                    else {
// Grant entitlement to the user on item purchase
                        if(!getSubscribeItemValueFromPref(subcribeItemIDs.get(index))){
                            saveSubscribeItemValueToPref(subcribeItemIDs.get(index),true);
                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String username = snapshot.child("username").getValue().toString();
                                    String membership = "";
                                    if (index == 0){
                                        membership = "Basic";
                                    } else if (index == 1){
                                        membership = "Premium";
                                    }
                                    databaseReference.child("extras").child(username).child("membership").setValue(membership).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), subcribeItemIDs.get(index)+" Item Subscribed.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            notifyList();
                        }
                    }
                }
//if purchase is pending
                else if(  purchase.getPurchaseState() == Purchase.PurchaseState.PENDING)
                {
                    Toast.makeText(getApplicationContext(),
                            subcribeItemIDs.get(index)+" Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
                }
//if purchase is refunded or unknown
                else if( purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE)
                {
//mark purchase false in case of UNSPECIFIED_STATE
                    saveSubscribeItemValueToPref(subcribeItemIDs.get(index),false);
                    Toast.makeText(getApplicationContext(), subcribeItemIDs.get(index)+" Purchase Status Unknown", Toast.LENGTH_SHORT).show();
                    notifyList();
                }
            }
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
//if item newly purchased
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases);
        }
//if item already purchased then check and reflect changes
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(SUBS);
            List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            if(alreadyPurchases!=null){
                handlePurchases(alreadyPurchases);
            }
        }
//if purchase cancelled
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(getApplicationContext(),"Purchase Canceled",Toast.LENGTH_SHORT).show();
        }
// Handle any other error msgs
        else {
            Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void initiatePurchase(final String PRODUCT_ID) {
        List<String> skuList = new ArrayList<>();
        skuList.add(PRODUCT_ID);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(SUBS);
        BillingResult billingResult = billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);
        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            billingClient.querySkuDetailsAsync(params.build(),
                    new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(@NonNull BillingResult billingResult,
                                                         List<SkuDetails> skuDetailsList) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                if (skuDetailsList != null && skuDetailsList.size() > 0) {
                                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetailsList.get(0))
                                            .build();
                                    billingClient.launchBillingFlow(StoreActivity.this, flowParams);
                                } else {
//try to add item/product id "s1" "s2" "s3" inside subscription in google play console
                                    Toast.makeText(getApplicationContext(), "Subscribe Item " + PRODUCT_ID + " not Found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        " Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Sorry Subscription not Supported. Please Update Play Store", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases().setListener(this).build();

        trade = findViewById(R.id.trade);
        add = findViewById(R.id.plus);
        minus = findViewById(R.id.minus);
        numberOfPoints = findViewById(R.id.numberofPoints);
        endAmount = findViewById(R.id.endamount);
        numberPoints = 50;
        listView = findViewById(R.id.subscriptionsListView);

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(SUBS);
                    List<Purchase> queryPurchases = queryPurchase.getPurchasesList();
                    if(queryPurchases!=null && queryPurchases.size()>0){
                        handlePurchases(queryPurchases);
                    }
                    ArrayList<Integer> purchaseFound =new ArrayList<Integer> ();
                    if(queryPurchases!=null && queryPurchases.size()>0){
                        for(Purchase p:queryPurchases){
                            int index=subcribeItemIDs.indexOf(p.getSkus().get(0));
                            if(index>-1)
                            {
                                purchaseFound.add(index);
                                if(p.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
                                {
                                    saveSubscribeItemValueToPref(subcribeItemIDs.get(index),true);
                                }
                                else{
                                    saveSubscribeItemValueToPref(subcribeItemIDs.get(index),false);
                                }
                            }
                        }
//items that are not found in purchase list mark false
//indexOf returns -1 when item is not in foundlist
                        for(int i=0;i < subcribeItemIDs.size(); i++){
                            if(purchaseFound.indexOf(i)==-1){
                                saveSubscribeItemValueToPref(subcribeItemIDs.get(i),false);
                            }
                        }
                    }
//if purchase list is empty that means no item is not purchased/Subscribed
//Or purchase is refunded or canceled
//so mark them all false
                    else{
                        for( String purchaseItem: subcribeItemIDs ){
                            saveSubscribeItemValueToPref(purchaseItem,false);
                        }
                    }
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
            }
        });

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subscribeItemDisplay);
        listView.setAdapter(arrayAdapter);
        notifyList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if(getSubscribeItemValueFromPref(subcribeItemIDs.get(position))){
                    Toast.makeText(getApplicationContext(),subcribeItemIDs.get(position)+" is Already Subscribed",Toast.LENGTH_SHORT).show();
                    //selected item is already purchased/subscribed
                    return;
                }
                //initiate purchase on selected product/subscribe item click
                //check if service is already connected
                if (billingClient.isReady()) {
                    initiatePurchase(subcribeItemIDs.get(position));
                }
                //else reconnect service
                else{
                    billingClient = BillingClient.newBuilder(StoreActivity.this).enablePendingPurchases().setListener(StoreActivity.this).build();
                    billingClient.startConnection(new BillingClientStateListener() {
                        @Override
                        public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                initiatePurchase(subcribeItemIDs.get(position));
                            } else {
                                Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
                                Toast.makeText(StoreActivity.this, "Your phone does not support billing version 3.0", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onBillingServiceDisconnected() {
                        }
                    });
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (numberPoints < 1000) {

                    add.setEnabled(true);

                    numberPoints += 50;

                    numberOfPoints.setText(Integer.toString(numberPoints));

                    endAmount.setText(Integer.toString(numberPoints / 50));

                } else {

                    add.setEnabled(false);
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (numberPoints > 0) {

                    minus.setEnabled(true);

                    numberPoints -= 50;

                    numberOfPoints.setText(Integer.toString(numberPoints));

                    endAmount.setText(Integer.toString(numberPoints / 50));
                } else {

                    minus.setEnabled(false);
                }
            }
        });

        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            int newPoints = Integer.parseInt(snapshot.child("points").getValue().toString()) - numberPoints;
                            int newCoins = Integer.parseInt(snapshot.child("coins").getValue().toString()) + Integer.parseInt(endAmount.getText().toString());
                            String finalGrade = snapshot.child("grade").getValue().toString();
                            String finalSchool = snapshot.child("school").getValue().toString();
                            String finalEmail = snapshot.child("email").getValue().toString();
                            String finalUsername = snapshot.child("username").getValue().toString();
                            String finalSubject = snapshot.child("favoriteSubject").getValue().toString();
                            Map<String, Object> users = new HashMap<>();
                            users.put("email", finalEmail);
                            users.put("username", finalUsername);
                            users.put("favoriteSubject", finalSubject);
                            users.put("grade", finalGrade);
                            users.put("school", finalSchool);
                            users.put("points", newPoints);
                            users.put("coins", newCoins);
                            snapshot.getRef().setValue(users);
                        }
                        Toast.makeText(StoreActivity.this, "Trade Successful!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void notifyList(){
        subscribeItemDisplay.clear();
        for(String p:subcribeItemIDs){
            subscribeItemDisplay.add("Subscribe Status of "+p+" = "+getSubscribeItemValueFromPref(p));
        }
        arrayAdapter.notifyDataSetChanged();
    }

    private SharedPreferences getPreferenceObject() {
        return getApplicationContext().getSharedPreferences(PREF_FILE, 0);
    }
    private SharedPreferences.Editor getPreferenceEditObject() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_FILE, 0);
        return pref.edit();
    }
    private boolean getSubscribeItemValueFromPref(String PURCHASE_KEY){
        return getPreferenceObject().getBoolean(PURCHASE_KEY,false);
    }
    private void saveSubscribeItemValueToPref(String PURCHASE_KEY,boolean value){
        getPreferenceEditObject().putBoolean(PURCHASE_KEY,value).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(billingClient!=null){
            billingClient.endConnection();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}