package com.luv.s2s;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RewardsActivity extends AppCompatActivity {

    TextView coins;
    Button coinsToBounty, coinsToTrades, coinsToPoints, watchAdd;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        coins = findViewById(R.id.coins);
        coinsToBounty = findViewById(R.id.coinsToBounty);
        coinsToTrades = findViewById(R.id.coinsToTrades);
        coinsToPoints = findViewById(R.id.coinsToPoints);
        watchAdd = findViewById(R.id.watchAdd);

        watchAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RewardsActivity.this, WatchAdActivity.class);
                startActivity(intent);
            }
        });

        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    coins.setText(snapshot.child("coins").getValue().toString());
                    username = snapshot.child("username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        coinsToTrades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            if (Integer.parseInt(snapshot.child("coins").getValue().toString()) >= 300){
                                int newCoins = Integer.parseInt(snapshot.child("coins").getValue().toString()) - 300;
                                int newPoints = Integer.parseInt(snapshot.child("points").getValue().toString());
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
                                databaseReference.child("extras").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshots) {
                                        if (snapshots.exists()){
                                            if (snapshots.child("coinsToTrades").exists()){
                                                Toast.makeText(RewardsActivity.this, "You have already bought coins to trades", Toast.LENGTH_SHORT).show();
                                            } else {
                                                snapshots.getRef().child("coinsToTrades").setValue(true);
                                                snapshot.getRef().setValue(users);
                                                Toast.makeText(RewardsActivity.this, "Trade Successful!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            databaseReference.child("extras").child(username).child("coinsToTrades").setValue(true);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                Toast.makeText(RewardsActivity.this, "Sorry! You don't have enough coins.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        coinsToBounty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            if (Integer.parseInt(snapshot.child("coins").getValue().toString()) >= 100){
                                int newCoins = Integer.parseInt(snapshot.child("coins").getValue().toString()) - 100;
                                int newPoints = Integer.parseInt(snapshot.child("points").getValue().toString());
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
                                databaseReference.child("extras").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshots) {
                                        if (snapshots.exists()){
                                            if (snapshots.child("coinsToBounty").exists()){
                                                Toast.makeText(RewardsActivity.this, "You have already bought coins to bounty", Toast.LENGTH_SHORT).show();
                                            } else {
                                                snapshots.getRef().child("coinsToBounty").setValue(true);
                                                snapshot.getRef().setValue(users);
                                                Toast.makeText(RewardsActivity.this, "Trade Successful!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            databaseReference.child("extras").child(username).child("coinsToBounty").setValue(true);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                Toast.makeText(RewardsActivity.this, "Sorry! You don't have enough coins.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        coinsToPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            if (Integer.parseInt(snapshot.child("coins").getValue().toString()) >= 5){
                                int newCoins = Integer.parseInt(snapshot.child("coins").getValue().toString()) - 5;
                                int newPoints = Integer.parseInt(snapshot.child("points").getValue().toString()) + 200;
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
                                Toast.makeText(RewardsActivity.this, "Trade Successful!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RewardsActivity.this, "Sorry! You don't have enough coins.", Toast.LENGTH_SHORT).show();
                            }
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