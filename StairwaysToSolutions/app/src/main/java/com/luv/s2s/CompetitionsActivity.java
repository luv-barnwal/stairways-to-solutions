package com.luv.s2s;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CompetitionsActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> questionDetails = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    int rewardAmount;
    Button claim;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitions);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        claim = findViewById(R.id.claim);
        listView = findViewById(R.id.competitionsListView);

        databaseReference.child("competitions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    questionDetails.add(snapshot.child("competition").getValue().toString());
                    questionDetails.add(snapshot.child("rules").getValue().toString());
                    questionDetails.add(snapshot.child("rewards").getValue().toString());

                    rewardAmount = Integer.parseInt(snapshot.child("rewards").getValue().toString());

                    arrayAdapter = new ArrayAdapter<>(CompetitionsActivity.this, android.R.layout.simple_list_item_1, questionDetails);
                    listView.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(CompetitionsActivity.this, "Sorry no competitions available right now. Please check back on Saturday 12:00:00 AM IST.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String username = snapshot.child("username").getValue().toString();
                    int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

                    Query query = databaseReference.child("notifications").child(username).orderByChild("notification").equalTo("You have completed " + currentMonth + "'s competition");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()){
                                databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            int coins = Integer.parseInt(snapshot.child("coins").getValue().toString());
                                            if (coins > 200){
                                                claim.setVisibility(View.VISIBLE);
                                                claim.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        int newCoins = coins + rewardAmount;
                                                        String finalGrade = snapshot.child("grade").getValue().toString();
                                                        String finalSchool = snapshot.child("school").getValue().toString();
                                                        String finalEmail = snapshot.child("email").getValue().toString();
                                                        String finalUsername = snapshot.child("username").getValue().toString();
                                                        String finalSubject = snapshot.child("favoriteSubject").getValue().toString();
                                                        int finalPoints = Integer.parseInt(snapshot.child("points").getValue().toString());
                                                        Map<String, Object> users = new HashMap<>();
                                                        users.put("email", finalEmail);
                                                        users.put("username", finalUsername);
                                                        users.put("favoriteSubject", finalSubject);
                                                        users.put("grade", finalGrade);
                                                        users.put("school", finalSchool);
                                                        users.put("points", finalPoints);
                                                        users.put("coins", newCoins);
                                                        snapshot.getRef().setValue(users);
                                                        Map<String, Object> notification = new HashMap<>();
                                                        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                                                        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
                                                        int currentDate = Calendar.getInstance().get(Calendar.DATE);
                                                        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                                                        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
                                                        notification.put("notification", "You have completed " + currentMonth + "'s competition");
                                                        notification.put("createdAt", currentHour + ":" + currentMinute + "  " + currentDate + "-" + currentMonth + "-" + currentYear);
                                                        final int[] number = new int[]{0};
                                                        databaseReference.child("notifications").child(finalUsername).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists()){
                                                                    number[0] = (int) snapshot.getChildrenCount() + 2;
                                                                } else {
                                                                    number[0] = 1;
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                        Query query = databaseReference.child("notifications").child(finalUsername).orderByChild("notification")
                                                                .equalTo("You have completed " + currentMonth + "'s competition");
                                                        query.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (!snapshot.exists()){
                                                                    databaseReference.child("notifications").child(finalUsername)
                                                                            .child(String.valueOf(number[0])).setValue(notification);
                                                                    Toast.makeText(CompetitionsActivity.this, "Congratulations for winning this month's competition", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                Toast.makeText(CompetitionsActivity.this, "You have already claimed this month's reward.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

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