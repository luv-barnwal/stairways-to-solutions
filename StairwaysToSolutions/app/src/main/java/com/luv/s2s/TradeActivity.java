package com.luv.s2s;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeActivity extends AppCompatActivity {

    String otherUser, currentUser;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    Button trade;
    ImageView plus, minus;
    TextView endAmount, numberOfPoints, tradeWith, received;
    RadioButton coinsTrade, pointsTrade;
    RadioGroup group;
    int selectedOption = 0; // 0 = Points, 1 = Coins
    int numberPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        trade = findViewById(R.id.trade);
        plus = findViewById(R.id.plus);
        minus = findViewById(R.id.minus);
        endAmount = findViewById(R.id.endamount);
        numberOfPoints = findViewById(R.id.numberofPoints);
        tradeWith = findViewById(R.id.tradeWith);
        received = findViewById(R.id.received);
        coinsTrade = findViewById(R.id.coinsTrade);
        pointsTrade = findViewById(R.id.pointsTrade);
        group = findViewById(R.id.group);
        numberPoints = Integer.parseInt(numberOfPoints.getText().toString());

        Intent intent = getIntent();
        currentUser = intent.getStringExtra("current");
        otherUser = intent.getStringExtra("username");

        tradeWith.setText("Trade with " + otherUser);

        int compares = currentUser.compareTo(otherUser);
        String titleQ = "";
        if (compares < 0){
            titleQ = currentUser + " + " + otherUser;
        } else if (compares > 0){
            titleQ = otherUser + " + " + currentUser;
        }

        String finalTitleQ = titleQ;
        databaseReference.child("trades").child(titleQ).child(otherUser).child(Integer.toString(0)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    group.setVisibility(View.GONE);
                    minus.setVisibility(View.GONE);
                    plus.setVisibility(View.GONE);
                    if (snapshot.child(Integer.toString(0)).child("receiver").getValue().toString().equals(currentUser)){
                        endAmount.setText(snapshot.child(Integer.toString(0)).child("coins").getValue().toString());
                        numberOfPoints.setText(snapshot.child(Integer.toString(1)).child("points").getValue().toString());
                        trade.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            int newCoins = Integer.parseInt(snapshot.child("coins").getValue().toString()) + Integer.parseInt(endAmount.getText().toString());
                                            int finalPoints = Integer.parseInt(snapshot.child("points").getValue().toString()) - Integer.parseInt(numberOfPoints.getText().toString());
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
                                            users.put("points", finalPoints);
                                            users.put("coins", newCoins);
                                            snapshot.getRef().setValue(users);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                if (dataSnapshot.child("username").getValue().toString().equals(otherUser)){
                                                    int finalCoins = Integer.parseInt(dataSnapshot.child("coins").getValue().toString()) - Integer.parseInt(endAmount.getText().toString());
                                                    int newPoints = Integer.parseInt(dataSnapshot.child("points").getValue().toString()) + Integer.parseInt(numberOfPoints.getText().toString());
                                                    String finalGrade = dataSnapshot.child("grade").getValue().toString();
                                                    String finalSchool = dataSnapshot.child("school").getValue().toString();
                                                    String finalEmail = dataSnapshot.child("email").getValue().toString();
                                                    String finalUsername = dataSnapshot.child("username").getValue().toString();
                                                    String finalSubject = dataSnapshot.child("favoriteSubject").getValue().toString();
                                                    Map<String, Object> users = new HashMap<>();
                                                    users.put("email", finalEmail);
                                                    users.put("username", finalUsername);
                                                    users.put("favoriteSubject", finalSubject);
                                                    users.put("grade", finalGrade);
                                                    users.put("school", finalSchool);
                                                    users.put("points", newPoints);
                                                    users.put("coins", finalCoins);
                                                    dataSnapshot.getRef().setValue(users);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                databaseReference.child("trades").child(finalTitleQ).removeValue();
                                Toast.makeText(TradeActivity.this, "Trade Successful!", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(TradeActivity.this, ChatListActivity.class);
                                startActivity(intent1);
                            }
                        });
                    } else if (snapshot.child(Integer.toString(1)).child("receiver").getValue().toString().equals(currentUser)){
                        trade.setText("Exchange Coins To Points");
                        received.setText("Points Received");
                        endAmount.setText(snapshot.child(Integer.toString(1)).child("points").getValue().toString());
                        numberOfPoints.setText(snapshot.child(Integer.toString(0)).child("coins").getValue().toString());
                        trade.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            int newCoins = Integer.parseInt(snapshot.child("coins").getValue().toString()) - Integer.parseInt(numberOfPoints.getText().toString());
                                            int finalPoints = Integer.parseInt(snapshot.child("points").getValue().toString()) + Integer.parseInt(endAmount.getText().toString());
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
                                            users.put("points", finalPoints);
                                            users.put("coins", newCoins);
                                            snapshot.getRef().setValue(users);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                if (dataSnapshot.child("username").getValue().toString().equals(otherUser)){
                                                    int finalCoins = Integer.parseInt(dataSnapshot.child("coins").getValue().toString()) + Integer.parseInt(numberOfPoints.getText().toString());
                                                    int newPoints = Integer.parseInt(dataSnapshot.child("points").getValue().toString()) - Integer.parseInt(endAmount.getText().toString());
                                                    String finalGrade = dataSnapshot.child("grade").getValue().toString();
                                                    String finalSchool = dataSnapshot.child("school").getValue().toString();
                                                    String finalEmail = dataSnapshot.child("email").getValue().toString();
                                                    String finalUsername = dataSnapshot.child("username").getValue().toString();
                                                    String finalSubject = dataSnapshot.child("favoriteSubject").getValue().toString();
                                                    Map<String, Object> users = new HashMap<>();
                                                    users.put("email", finalEmail);
                                                    users.put("username", finalUsername);
                                                    users.put("favoriteSubject", finalSubject);
                                                    users.put("grade", finalGrade);
                                                    users.put("school", finalSchool);
                                                    users.put("points", newPoints);
                                                    users.put("coins", finalCoins);
                                                    dataSnapshot.getRef().setValue(users);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                databaseReference.child("trades").child(finalTitleQ).removeValue();
                                Toast.makeText(TradeActivity.this, "Trade Successful!", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(TradeActivity.this, ChatListActivity.class);
                                startActivity(intent1);
                            }
                        });
                    }
                } else {
                    coinsTrade.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked){
                                String points = numberOfPoints.getText().toString();
                                numberOfPoints.setText(endAmount.getText().toString());
                                numberPoints = Integer.parseInt(numberOfPoints.getText().toString());
                                endAmount.setText(points);
                                trade.setText("Exchange Coins To Points");
                                received.setText("Points Received");
                                selectedOption = 1;
                            }
                        }
                    });

                    plus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (selectedOption == 0) {
                                if (numberPoints < 1000) {

                                    plus.setEnabled(true);

                                    numberPoints += 50;

                                    numberOfPoints.setText(Integer.toString(numberPoints));

                                    endAmount.setText(Integer.toString(numberPoints / 50));

                                } else {

                                    plus.setEnabled(false);
                                }
                            } else if (selectedOption == 1){
                                if (numberPoints < 20){

                                    plus.setEnabled(true);

                                    numberPoints ++;

                                    numberOfPoints.setText(Integer.toString(numberPoints));

                                    endAmount.setText(Integer.toString(numberPoints * 50));
                                }
                            }
                        }
                    });

                    minus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (selectedOption == 0) {

                                if (numberPoints > 0) {

                                    minus.setEnabled(true);

                                    numberPoints -= 50;

                                    numberOfPoints.setText(Integer.toString(numberPoints));

                                    endAmount.setText(Integer.toString(numberPoints / 50));
                                } else {

                                    minus.setEnabled(false);
                                }
                            } else if (selectedOption == 1){
                                if (numberPoints > 0){

                                    minus.setEnabled(true);

                                    numberPoints --;

                                    numberOfPoints.setText(Integer.toString(numberPoints));

                                    endAmount.setText(Integer.toString(numberPoints * 50));
                                }
                            }
                        }
                    });

                    pointsTrade.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked){
                                String points = numberOfPoints.getText().toString();
                                numberOfPoints.setText(endAmount.getText().toString());
                                numberPoints = Integer.parseInt(numberOfPoints.getText().toString());
                                endAmount.setText(points);
                                trade.setText("Exchange Points To Coins");
                                received.setText("Coins Received");
                                selectedOption = 0;
                            }
                        }
                    });

                    trade.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int compare = currentUser.compareTo(otherUser);
                            String title = "";
                            if (compare < 0){
                                title = currentUser + " + " + otherUser;
                            } else if (compare > 0){
                                title = otherUser + " + " + currentUser;
                            }
                            List<List<Map<String, Object>>> tradeData = new ArrayList<>();
                            if (selectedOption == 0) {
                                List<Map<String, Object>> trades = new ArrayList<>();
                                Map<String, Object> coinsInfo = new HashMap<>();
                                Map<String, Object> pointsInfo = new HashMap<>();
                                coinsInfo.put("coins", Integer.parseInt(endAmount.getText().toString()));
                                coinsInfo.put("receiver", otherUser);
                                pointsInfo.put("points", Integer.parseInt(numberOfPoints.getText().toString()));
                                pointsInfo.put("receiver", currentUser);
                                trades.add(coinsInfo);
                                trades.add(pointsInfo);
                                tradeData.add(trades);
                            } else if (selectedOption == 1){
                                List<Map<String, Object>> trades = new ArrayList<>();
                                Map<String, Object> coinsInfo = new HashMap<>();
                                Map<String, Object> pointsInfo = new HashMap<>();
                                pointsInfo.put("points", Integer.parseInt(endAmount.getText().toString()));
                                pointsInfo.put("receiver", otherUser);
                                coinsInfo.put("coins", Integer.parseInt(numberOfPoints.getText().toString()));
                                coinsInfo.put("receiver", currentUser);
                                trades.add(coinsInfo);
                                trades.add(pointsInfo);
                                tradeData.add(trades);
                            }
                            databaseReference.child("trades").child(title).child(currentUser).setValue(tradeData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(TradeActivity.this, "Trade request sent!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}