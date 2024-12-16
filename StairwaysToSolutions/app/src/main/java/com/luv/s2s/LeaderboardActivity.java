package com.luv.s2s;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Objects;

public class LeaderboardActivity extends AppCompatActivity {

    Button daily, weekly, yearly, allTime;
    String selectedButton = "3";//0=daily, 1=weekly, 2=yearly, 3=allTime
    TextView title;
    ListView listView;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    private void findAllTimeLeaderboard(){

        final ArrayList<String> userData = new ArrayList<>();
        final int[] points = new int[1];
        databaseReference.child("users").orderByChild("points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String winner = "";
                    int i = (int) snapshot.getChildrenCount();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (i == 1){
                            winner = dataSnapshot.child("username").getValue().toString();
                        }
                        userData.add(0, (i) + ") Username: " + Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString() +
                                "\nPoints: " + Objects.requireNonNull(dataSnapshot.child("points").getValue()).toString());
                        points[0] = Integer.parseInt(Integer.toString(Integer.parseInt(dataSnapshot.child("points").getValue().toString())));
                        i -= 1;
                    }
                    int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    if (currentHour == 12){
                        String finalWinner = winner;
                        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        if (dataSnapshot.child("username").getValue().toString().equals(finalWinner)){
                                            int currentPoints = Integer.parseInt(String.valueOf(dataSnapshot.child("points").getValue()));
                                            int newPoints = currentPoints + 1000;
                                            String coins = dataSnapshot.child("coins").getValue().toString();
                                            int finalCoins = Integer.parseInt(coins);
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
                                            int currentDate = Calendar.getInstance().get(Calendar.DATE);
                                            int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
                                            if (currentMonth == 13){
                                                currentMonth = 1;
                                            }
                                            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                                            int finalCurrentMonth = currentMonth;
                                            databaseReference.child("winners").child("allTimeLeaderboard").child(Integer.toString(currentYear))
                                                    .child(Integer.toString(finalCurrentMonth)).child(Integer.toString(currentDate))
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (!snapshot.exists()){
                                                                dataSnapshot.getRef().setValue(users);
                                                                Map<String, Object> winnerData = new HashMap<>();
                                                                winnerData.put("winner", finalWinner);
                                                                winnerData.put("points", points[0]);
                                                                databaseReference.child("winners").child("allTimeLeaderboard").child(Integer.toString(currentYear))
                                                                        .child(Integer.toString(finalCurrentMonth)).child(Integer.toString(currentDate)).setValue(winnerData)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
                                                                                    Map<String, Object> notification = new HashMap<>();
                                                                                    notification.put("notification", "Congratulations you won the all time leaderboard on " +
                                                                                            currentDate + "-" + finalCurrentMonth + "-" + currentYear);
                                                                                    int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
                                                                                    notification.put("createdAt", currentHour + ":" + currentMinute + "  " +
                                                                                            currentDate + "-" + finalCurrentMonth + "-" + currentYear);
                                                                                    databaseReference.child("notifications").child(finalUsername).addValueEventListener(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                            int number;
                                                                                            if (snapshot.exists()){
                                                                                                    number = (int) snapshot.getChildrenCount() + 2;
                                                                                            } else {
                                                                                                number = 1;
                                                                                            }
                                                                                            Query query = databaseReference.child("notifications").child(finalUsername).orderByChild("notification")
                                                                                                    .equalTo("Congratulations you won the all time leaderboard on " +
                                                                                                            currentDate + "-" + finalCurrentMonth + "-" + currentYear);
                                                                                            query.addValueEventListener(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                    if (!snapshot.exists()){
                                                                                                        databaseReference.child("notifications").child(finalUsername)
                                                                                                                .child(String.valueOf(number)).setValue(notification);
//                                                                                                        databaseReference.child("winners").child("allTimeLeaderboard").child("winnersList").addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                                                            @Override
//                                                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                                                                if (snapshot.exists()){
//                                                                                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                                                                                                                        if (dataSnapshot.child("winner").getValue().toString().equals(finalUsername)){
//                                                                                                                            int wonTimes = Integer.parseInt(dataSnapshot.child("numberOfTimes").getValue().toString()) + 1;
//                                                                                                                            dataSnapshot.getRef().child("numberOfTimes").setValue(wonTimes);
//                                                                                                                        } else {
//                                                                                                                            int count = (int) (dataSnapshot.getChildrenCount() + 1);
//                                                                                                                            Map<String, Object> winnerDatas = new HashMap<>();
//                                                                                                                            winnerDatas.put("winner", finalUsername);
//                                                                                                                            winnerDatas.put("numberOfTimes", 1);
//                                                                                                                            snapshot.getRef().child(String.valueOf(count)).setValue(winnerDatas).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                                                @Override
//                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    Toast.makeText(LeaderboardActivity.this,
                                                                                                                                            "The winner of All Time Leaderboard is " + finalWinner, Toast.LENGTH_SHORT).show();
//                                                                                                                                }
//                                                                                                                            });
//                                                                                                                        }
//                                                                                                                    }
//                                                                                                                }
//                                                                                                            }
//
//                                                                                                            @Override
//                                                                                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                                                            }
//                                                                                                        });
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                                }
                                                                                            });
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                        }
                                                                                    });
                                                                                   }
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
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                CustomListAdapter customListAdapter = new CustomListAdapter(LeaderboardActivity.this, R.layout.custom_list, userData);
                listView.setAdapter(customListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void findYearlyLeaderboard(){
        final ArrayList<String> userData = new ArrayList<>();
        final int[] points = new int[1];
        databaseReference.child("yearlyLeaderboard").orderByChild("points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String winner = "";
                    int i = (int) snapshot.getChildrenCount();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (i == 1){
                            winner = dataSnapshot.child("username").getValue().toString();
                        }
                        userData.add(0, (i) + ") Username: " + Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString() +
                                "\nPoints: " + Objects.requireNonNull(dataSnapshot.child("points").getValue()).toString());
                        points[0] = Integer.parseInt(Integer.toString(Integer.parseInt(dataSnapshot.child("points").getValue().toString())));
                        i -= 1;
                    }
                    int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                    if (currentDay == 365){
                        String finalWinner = winner;
                        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        if (dataSnapshot.child("username").getValue().toString().equals(finalWinner)){
                                            int currentPoints = Integer.parseInt(String.valueOf(dataSnapshot.child("points").getValue()));
                                            int newPoints = currentPoints + 100000;
                                            String coins = dataSnapshot.child("coins").getValue().toString();
                                            int finalCoins = Integer.parseInt(coins);
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
                                            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                                            databaseReference.child("winners").child("yearlyLeaderboard").child(Integer.toString(currentYear))
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (!snapshot.exists()){
                                                                dataSnapshot.getRef().setValue(users);
                                                                Map<String, Object> winnerData = new HashMap<>();
                                                                winnerData.put("winner", finalWinner);
                                                                winnerData.put("points", points[0]);
                                                                databaseReference.child("winners").child("yearlyLeaderboard").child(Integer.toString(currentYear))
                                                                        .setValue(winnerData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
                                                                                    Map<String, Object> notification = new HashMap<>();
                                                                                    int currentDate = Calendar.getInstance().get(Calendar.DATE);
                                                                                    int finalCurrentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
                                                                                    notification.put("notification", "Congratulations you won the yearly leaderboard on " +
                                                                                            currentDate + "-" + finalCurrentMonth + "-" + currentYear);
                                                                                    int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
                                                                                    int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                                                                                    notification.put("createdAt", currentHour + ":" + currentMinute + "  " +
                                                                                            currentDate + "-" + finalCurrentMonth + "-" + currentYear);
                                                                                    databaseReference.child("notifications").child(finalUsername).addValueEventListener(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                            int number;
                                                                                            if (snapshot.exists()){
                                                                                                number = (int) snapshot.getChildrenCount() + 2;
                                                                                            } else {
                                                                                                number = 1;
                                                                                            }
                                                                                            Query query = databaseReference.child("notifications").child(finalUsername).orderByChild("notification")
                                                                                                    .equalTo("Congratulations you won the yearly leaderboard on " +
                                                                                                            currentDate + "-" + finalCurrentMonth + "-" + currentYear);
                                                                                            query.addValueEventListener(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                    if (!snapshot.exists()){
                                                                                                        databaseReference.child("notifications").child(finalUsername)
                                                                                                                .child(String.valueOf(number)).setValue(notification);
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                                }
                                                                                            });
//                                                                                            databaseReference.child("winners").child("yearlyTimeLeaderboard").child("winnersList").addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                                                @Override
//                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                                                    if (snapshot.exists()){
//                                                                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                                                                                                            if (dataSnapshot.child("winner").getValue().toString().equals(finalUsername)){
//                                                                                                                int wonTimes = Integer.parseInt(dataSnapshot.child("numberOfTimes").getValue().toString()) + 1;
//                                                                                                                dataSnapshot.getRef().child("numberOfTimes").setValue(wonTimes);
//                                                                                                            } else {
//                                                                                                                int count = (int) (dataSnapshot.getChildrenCount() + 1);
//                                                                                                                Map<String, Object> winnerDatas = new HashMap<>();
//                                                                                                                winnerDatas.put("winner", finalUsername);
//                                                                                                                winnerDatas.put("numberOfTimes", 1);
//                                                                                                                snapshot.getRef().child(String.valueOf(count)).setValue(winnerDatas).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                                    @Override
//                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        Toast.makeText(LeaderboardActivity.this,
                                                                                                                                "The winner of Yearly Leaderboard is " + finalWinner, Toast.LENGTH_SHORT).show();
                                                                                                                        databaseReference.child("yearlyLeaderboard").removeValue();
//                                                                                                                    }
//
//                                                                                                                });
//                                                                                                            }
//                                                                                                        }
//                                                                                                    }
//                                                                                                }
//
//                                                                                                @Override
//                                                                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                                                }
//                                                                                            });
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                        }
                                                                                    });
                                                                                }
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
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                CustomListAdapter customListAdapter = new CustomListAdapter(LeaderboardActivity.this, R.layout.custom_list, userData);
                listView.setAdapter(customListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void findWeeklyLeaderboard(){
        final ArrayList<String> userData = new ArrayList<>();
        final int[] points = new int[1];
        databaseReference.child("weeklyLeaderboard").orderByChild("points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String winner = "";
                    int i = (int) snapshot.getChildrenCount();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (i == 1){
                            winner = dataSnapshot.child("username").getValue().toString();
                        }
                        userData.add(0, (i) + ") Username: " + Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString() +
                                "\nPoints: " + Objects.requireNonNull(dataSnapshot.child("points").getValue()).toString());
                        points[0] = Integer.parseInt(Integer.toString(Integer.parseInt(dataSnapshot.child("points").getValue().toString())));
                        i -= 1;
                    }
                    int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    if (currentDay == Calendar.SUNDAY){
                        String finalWinner = winner;
                        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        if (dataSnapshot.child("username").getValue().toString().equals(finalWinner)){
                                            int currentPoints = Integer.parseInt(String.valueOf(dataSnapshot.child("points").getValue()));
                                            int newPoints = currentPoints + 700;
                                            String coins = dataSnapshot.child("coins").getValue().toString();
                                            int finalCoins = Integer.parseInt(coins);
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
                                            int currentDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                                            int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
                                            if (currentMonth == 13){
                                                currentMonth = 1;
                                            }
                                            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                                            int finalCurrentMonth = currentMonth;
                                            databaseReference.child("winners").child("weeklyLeaderboard").child(Integer.toString(currentYear))
                                                    .child(Integer.toString(finalCurrentMonth)).child(Integer.toString(currentDate))
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (!snapshot.exists()){
                                                                dataSnapshot.getRef().setValue(users);
                                                                Map<String, Object> winnerData = new HashMap<>();
                                                                winnerData.put("winner", finalWinner);
                                                                winnerData.put("points", points[0]);
                                                                databaseReference.child("winners").child("weeklyLeaderboard").child(Integer.toString(currentYear))
                                                                        .child(Integer.toString(finalCurrentMonth)).child(Integer.toString(currentDate)).setValue(winnerData)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
                                                                                    Map<String, Object> notification = new HashMap<>();
                                                                                    notification.put("notification", "Congratulations you won the weekly leaderboard on " +
                                                                                            currentDate + "-" + finalCurrentMonth + "-" + currentYear);
                                                                                    int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
                                                                                    int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                                                                                    notification.put("createdAt", currentHour + ":" + currentMinute + "  " +
                                                                                            currentDate + "-" + finalCurrentMonth + "-" + currentYear);
                                                                                    databaseReference.child("notifications").child(finalUsername).addValueEventListener(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                            int number;
                                                                                            if (snapshot.exists()){
                                                                                                number = (int) snapshot.getChildrenCount() + 2;
                                                                                            } else {
                                                                                                number = 1;
                                                                                            }
                                                                                            Query query = databaseReference.child("notifications").child(finalUsername).orderByChild("notification")
                                                                                                    .equalTo("Congratulations you won the weekly leaderboard on " +
                                                                                                            currentDate + "-" + finalCurrentMonth + "-" + currentYear);
                                                                                            query.addValueEventListener(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                    if (!snapshot.exists()){
                                                                                                        databaseReference.child("notifications").child(finalUsername)
                                                                                                                .child(String.valueOf(number)).setValue(notification);
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                                }
                                                                                            });
//                                                                                            databaseReference.child("winners").child("weeklyLeaderboard").child("winnersList").addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                                                @Override
//                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                                                    if (snapshot.exists()){
//                                                                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                                                                                                            if (dataSnapshot.child("winner").getValue().toString().equals(finalUsername)){
//                                                                                                                int wonTimes = Integer.parseInt(dataSnapshot.child("numberOfTimes").getValue().toString()) + 1;
//                                                                                                                dataSnapshot.getRef().child("numberOfTimes").setValue(wonTimes);
//                                                                                                            } else {
//                                                                                                                int count = (int) (dataSnapshot.getChildrenCount() + 1);
//                                                                                                                Map<String, Object> winnerDatas = new HashMap<>();
//                                                                                                                winnerDatas.put("winner", finalUsername);
//                                                                                                                winnerDatas.put("numberOfTimes", 1);
//                                                                                                                snapshot.getRef().child(String.valueOf(count)).setValue(winnerDatas).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                                    @Override
//                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        Toast.makeText(LeaderboardActivity.this,
                                                                                                                                "The winner of Weekly Leaderboard is " + finalWinner, Toast.LENGTH_SHORT).show();
                                                                                                                        databaseReference.child("weeklyLeaderboard").removeValue();
//                                                                                                                    }
//                                                                                                                });
//                                                                                                            }
//                                                                                                        }
//                                                                                                    }
//                                                                                                }
//
//                                                                                                @Override
//                                                                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                                                }
//                                                                                            });

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                        }
                                                                                    });
                                                                                }
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
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                CustomListAdapter customListAdapter = new CustomListAdapter(LeaderboardActivity.this, R.layout.custom_list, userData);
                listView.setAdapter(customListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void findDailyLeaderboard() {
        final ArrayList<String> userData = new ArrayList<>();
        final int[] points = new int[1];
        databaseReference.child("dailyLeaderboard").orderByChild("points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String winner = "";
                    int i = (int) snapshot.getChildrenCount();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (i == 1){
                            winner = dataSnapshot.child("username").getValue().toString();
                        }
                        userData.add(0, (i) + ") Username: " + Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString() +
                                "\nPoints: " + Objects.requireNonNull(dataSnapshot.child("points").getValue()).toString());
                        points[0] = Integer.parseInt(Integer.toString(Integer.parseInt(dataSnapshot.child("points").getValue().toString())));
                        i -= 1;
                    }
                    int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    if (currentHour == 12){
                        String finalWinner = winner;
                        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        if (dataSnapshot.child("username").getValue().toString().equals(finalWinner)){
                                            int currentPoints = Integer.parseInt(String.valueOf(dataSnapshot.child("points").getValue()));
                                            int newPoints = currentPoints + 100;
                                            String coins = dataSnapshot.child("coins").getValue().toString();
                                            int finalCoins = Integer.parseInt(coins);
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
                                            int currentDate = Calendar.getInstance().get(Calendar.DATE);
                                            int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
                                            if (currentMonth == 13){
                                                currentMonth = 1;
                                            }
                                            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                                            int finalCurrentMonth = currentMonth;
                                            databaseReference.child("winners").child("dailyLeaderboard").child(Integer.toString(currentYear))
                                                    .child(Integer.toString(finalCurrentMonth)).child(Integer.toString(currentDate))
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (!snapshot.exists()){
                                                                dataSnapshot.getRef().setValue(users);
                                                                Map<String, Object> winnerData = new HashMap<>();
                                                                winnerData.put("winner", finalWinner);
                                                                winnerData.put("points", points[0]);
                                                                databaseReference.child("winners").child("dailyLeaderboard").child(Integer.toString(currentYear))
                                                                        .child(Integer.toString(finalCurrentMonth)).child(Integer.toString(currentDate)).setValue(winnerData)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
                                                                                    Map<String, Object> notification = new HashMap<>();
                                                                                    notification.put("notification", "Congratulations you won the daily leaderboard on " +
                                                                                            currentDate + "-" + finalCurrentMonth + "-" + currentYear);
                                                                                    int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
                                                                                    notification.put("createdAt", currentHour + ":" + currentMinute + "  " +
                                                                                            currentDate + "-" + finalCurrentMonth + "-" + currentYear);
                                                                                    databaseReference.child("notifications").child(finalUsername).addValueEventListener(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                            int number;
                                                                                            if (snapshot.exists()){
                                                                                                number = (int) snapshot.getChildrenCount() + 2;
                                                                                            } else {
                                                                                                number = 1;
                                                                                            }
                                                                                            Query query = databaseReference.child("notifications").child(finalUsername).orderByChild("notification")
                                                                                                    .equalTo("Congratulations you won the daily leaderboard on " +
                                                                                                            currentDate + "-" + finalCurrentMonth + "-" + currentYear);
                                                                                            query.addValueEventListener(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                    if (!snapshot.exists()){
                                                                                                        databaseReference.child("notifications").child(finalUsername)
                                                                                                                .child(String.valueOf(number)).setValue(notification);
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                                }
                                                                                            });
//                                                                                            databaseReference.child("winners").child("dailyLeaderboard").child("winnersList").addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                                                @Override
//                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                                                    if (snapshot.exists()){
//                                                                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                                                                                                            if (dataSnapshot.child("winner").getValue().toString().equals(finalUsername)){
//                                                                                                                int wonTimes = Integer.parseInt(dataSnapshot.child("numberOfTimes").getValue().toString()) + 1;
//                                                                                                                dataSnapshot.getRef().child("numberOfTimes").setValue(wonTimes);
//                                                                                                            } else {
//                                                                                                                int count = (int) (dataSnapshot.getChildrenCount() + 1);
//                                                                                                                Map<String, Object> winnerDatas = new HashMap<>();
//                                                                                                                winnerDatas.put("winner", finalUsername);
//                                                                                                                winnerDatas.put("numberOfTimes", 1);
//                                                                                                                snapshot.getRef().child(String.valueOf(count)).setValue(winnerDatas).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                                    @Override
//                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        Toast.makeText(LeaderboardActivity.this,
                                                                                                                                "The winner of Daily Leaderboard is " + finalWinner, Toast.LENGTH_SHORT).show();
                                                                                                                        databaseReference.child("dailyLeaderboard").removeValue();
//                                                                                                                    }
//                                                                                                                });
//                                                                                                            }
//                                                                                                        }
//                                                                                                    }
//                                                                                                }
//
//                                                                                                @Override
//                                                                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                                                }
//                                                                                            });
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                        }
                                                                                    });
                                                                                }
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
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                CustomListAdapter customListAdapter = new CustomListAdapter(LeaderboardActivity.this, R.layout.custom_list, userData);
                listView.setAdapter(customListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        findAllTimeLeaderboard();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        daily = findViewById(R.id.button);
        weekly = findViewById(R.id.button2);
        yearly = findViewById(R.id.button3);
        allTime = findViewById(R.id.button4);
        title = findViewById(R.id.textView13);
        listView = findViewById(R.id.leaderboard);

        allTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (selectedButton) {
                    case "0":
                        daily.setVisibility(View.VISIBLE);
                        allTime.setVisibility(View.INVISIBLE);
                        title.setText("All Time");
                        selectedButton = "3";
                        findAllTimeLeaderboard();
                        break;
                    case "1":
                        weekly.setVisibility(View.VISIBLE);
                        allTime.setVisibility(View.INVISIBLE);
                        title.setText("All Time");
                        selectedButton = "3";
                        findAllTimeLeaderboard();
                        break;
                    case "2":
                        yearly.setVisibility(View.VISIBLE);
                        allTime.setVisibility(View.INVISIBLE);
                        title.setText("All Time");
                        selectedButton = "3";
                        findAllTimeLeaderboard();
                        break;
                    case "3":
                        allTime.setVisibility(View.INVISIBLE);
                        title.setText("All Time");
                        selectedButton = "3";
                        findAllTimeLeaderboard();
                        break;
                }
            }
        });

        yearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (selectedButton) {
                    case "0":
                        daily.setVisibility(View.VISIBLE);
                        yearly.setVisibility(View.INVISIBLE);
                        title.setText("Yearly");
                        selectedButton = "2";
                        findYearlyLeaderboard();
                        break;
                    case "1":
                        weekly.setVisibility(View.VISIBLE);
                        yearly.setVisibility(View.INVISIBLE);
                        title.setText("Yearly");
                        selectedButton = "2";
                        findYearlyLeaderboard();
                        break;
                    case "2":
                        yearly.setVisibility(View.VISIBLE);
                        title.setText("Yearly");
                        selectedButton = "2";
                        findYearlyLeaderboard();
                        break;
                    case "3":
                        allTime.setVisibility(View.VISIBLE);
                        yearly.setVisibility(View.INVISIBLE);
                        title.setText("Yearly");
                        selectedButton = "2";
                        findYearlyLeaderboard();
                        break;
                }
            }
        });

        weekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (selectedButton) {
                    case "0":
                        daily.setVisibility(View.VISIBLE);
                        weekly.setVisibility(View.INVISIBLE);
                        title.setText("Weekly");
                        selectedButton = "1";
                        findWeeklyLeaderboard();
                        break;
                    case "1":
                        weekly.setVisibility(View.INVISIBLE);
                        title.setText("Weekly");
                        selectedButton = "1";
                        findWeeklyLeaderboard();
                        break;
                    case "2":
                        yearly.setVisibility(View.VISIBLE);
                        weekly.setVisibility(View.INVISIBLE);
                        title.setText("Weekly");
                        selectedButton = "1";
                        findWeeklyLeaderboard();
                        break;
                    case "3":
                        allTime.setVisibility(View.VISIBLE);
                        weekly.setVisibility(View.INVISIBLE);
                        title.setText("Weekly");
                        selectedButton = "1";
                        findWeeklyLeaderboard();
                        break;
                }
            }
        });

        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (selectedButton) {
                    case "0":
                        daily.setVisibility(View.INVISIBLE);
                        title.setText("Daily");
                        selectedButton = "0";
                        findDailyLeaderboard();
                        break;
                    case "1":
                        weekly.setVisibility(View.VISIBLE);
                        daily.setVisibility(View.INVISIBLE);
                        title.setText("Daily");
                        selectedButton = "0";
                        findDailyLeaderboard();
                        break;
                    case "2":
                        yearly.setVisibility(View.VISIBLE);
                        daily.setVisibility(View.INVISIBLE);
                        title.setText("Daily");
                        selectedButton = "0";
                        findDailyLeaderboard();
                        break;
                    case "3":
                        allTime.setVisibility(View.VISIBLE);
                        daily.setVisibility(View.INVISIBLE);
                        title.setText("Daily");
                        selectedButton = "0";
                        findDailyLeaderboard();
                        break;
                }
            }
        });
    }
}