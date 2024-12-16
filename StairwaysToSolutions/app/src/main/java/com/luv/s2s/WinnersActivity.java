package com.luv.s2s;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class WinnersActivity extends AppCompatActivity {

    Button daily, weekly, yearly, allTime;
    String selectedButton = "3";//0=daily, 1=weekly, 2=yearly, 3=allTime
    TextView title;
    ListView listView;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    private void findAllTimeLeaderboard(){
        ArrayList<String> winners = new ArrayList<>();
        databaseReference.child("winners").child("allTimeLeaderboard").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                dataSnapshot1.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
//                                            for (String winner : winners){
//                                                if (winner.contains(snapshot1.child("winner").getValue().toString())){
//                                                   String[] strings = winner.split("\n");
//                                                   int wonTimes = Integer.parseInt(strings[1]) + 1;
//                                                   winners.remove(winner);
//                                                   winners.add(snapshot1.child("winner").getValue().toString() + "\n" + wonTimes);
//                                                } else {
                                                winners.add(snapshot1.child("winner").getValue().toString());
//                                                }
//                                                Toast.makeText(WinnersActivity.this, Arrays.toString(new ArrayList[]{winners}), Toast.LENGTH_SHORT).show();
                                        }
//                                        }
                                        ArrayList<String> winnersList = new ArrayList<>();
                                        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        int occurences = Collections.frequency(winners, dataSnapshot.child("username").getValue().toString());
                                                        if (occurences != 0){
                                                            winnersList.add(dataSnapshot.child("username").getValue().toString() + "\n" + occurences);
                                                        }
                                                    }
//                                                    ArrayList<Integer> ints = new ArrayList<>();
//                                                    ArrayList<String> allWinners = (ArrayList<String>) winnersList.clone();
//                                                    for (String winner: winnersList){
//                                                        String[] strings = winner.split("\n");
//                                                        int number = Integer.parseInt(strings[1]);
//                                                        winnersList.clear();
//                                                        ints.add(number);
//                                                    }
//                                                    Collections.sort(ints);
//                                                    for (int i = 0; i <= ints.size(); i++){
//                                                        for (String winner: allWinners){
//
//                                                        }
//                                                    }

                                                    Winner[] winner = new Winner[winnersList.size()];
                                                    ArrayList<Integer> ints = new ArrayList<>();
                                                    ArrayList<String> strings = new ArrayList<>();
                                                    for (String winnerz: winnersList){
                                                        String[] win = winnerz.split("\n");
                                                        strings.add(win[0]);
                                                        ints.add(Integer.valueOf(win[1]));
                                                    }

                                                    for (int i = 0; i<winnersList.size(); i++){
                                                        winner[i] = new Winner(strings.get(i), ints.get(i));
                                                    }

                                                    Arrays.sort(winner, new WinnerComparator());

                                                    winnersList.clear();

                                                    for (Winner value : winner) {
                                                        winnersList.add(value.toString());
                                                    }

                                                    CustomListAdapter customListAdapter = new CustomListAdapter(WinnersActivity.this, R.layout.custom_list, winnersList);
                                                    listView.setAdapter(customListAdapter);
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

    private void findYearlyLeaderboard(){
        ArrayList<String> winners = new ArrayList<>();
        databaseReference.child("winners").child("yearlyLeaderboard").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    dataSnapshot.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
//                                dataSnapshot1.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
//                                            for (String winner : winners){
//                                                if (winner.contains(snapshot1.child("winner").getValue().toString())){
//                                                   String[] strings = winner.split("\n");
//                                                   int wonTimes = Integer.parseInt(strings[1]) + 1;
//                                                   winners.remove(winner);
//                                                   winners.add(snapshot1.child("winner").getValue().toString() + "\n" + wonTimes);
//                                                } else {
                                            winners.add(dataSnapshot.child("winner").getValue().toString());
//                                                }
//                                                Toast.makeText(WinnersActivity.this, Arrays.toString(new ArrayList[]{winners}), Toast.LENGTH_SHORT).show();
                                        }
//                                        }
                                        ArrayList<String> winnersList = new ArrayList<>();
                                        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        int occurences = Collections.frequency(winners, dataSnapshot.child("username").getValue().toString());
                                                        if (occurences != 0){
                                                            winnersList.add(dataSnapshot.child("username").getValue().toString() + "\n" + occurences);
                                                        }
                                                    }
//                                                    ArrayList<Integer> ints = new ArrayList<>();
//                                                    ArrayList<String> allWinners = (ArrayList<String>) winnersList.clone();
//                                                    for (String winner: winnersList){
//                                                        String[] strings = winner.split("\n");
//                                                        int number = Integer.parseInt(strings[1]);
//                                                        winnersList.clear();
//                                                        ints.add(number);
//                                                    }
//                                                    Collections.sort(ints);
//                                                    for (int i = 0; i <= ints.size(); i++){
//                                                        for (String winner: allWinners){
//
//                                                        }
//                                                    }

                                                    Winner[] winner = new Winner[winnersList.size()];
                                                    ArrayList<Integer> ints = new ArrayList<>();
                                                    ArrayList<String> strings = new ArrayList<>();
                                                    for (String winnerz: winnersList){
                                                        String[] win = winnerz.split("\n");
                                                        strings.add(win[0]);
                                                        ints.add(Integer.valueOf(win[1]));
                                                    }

                                                    for (int i = 0; i<winnersList.size(); i++){
                                                        winner[i] = new Winner(strings.get(i), ints.get(i));
                                                    }

                                                    Arrays.sort(winner, new WinnerComparator());

                                                    winnersList.clear();

                                                    for (Winner value : winner) {
                                                        winnersList.add(value.toString());
                                                    }

                                                    CustomListAdapter customListAdapter = new CustomListAdapter(WinnersActivity.this, R.layout.custom_list, winnersList);
                                                    listView.setAdapter(customListAdapter);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void findWeeklyLeaderboard(){
        ArrayList<String> winners = new ArrayList<>();
        databaseReference.child("winners").child("weeklyLeaderboard").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                dataSnapshot1.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
//                                            for (String winner : winners){
//                                                if (winner.contains(snapshot1.child("winner").getValue().toString())){
//                                                   String[] strings = winner.split("\n");
//                                                   int wonTimes = Integer.parseInt(strings[1]) + 1;
//                                                   winners.remove(winner);
//                                                   winners.add(snapshot1.child("winner").getValue().toString() + "\n" + wonTimes);
//                                                } else {
                                            winners.add(snapshot1.child("winner").getValue().toString());
//                                                }
//                                                Toast.makeText(WinnersActivity.this, Arrays.toString(new ArrayList[]{winners}), Toast.LENGTH_SHORT).show();
                                        }
//                                        }
                                        ArrayList<String> winnersList = new ArrayList<>();
                                        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        int occurences = Collections.frequency(winners, dataSnapshot.child("username").getValue().toString());
                                                        if (occurences != 0){
                                                            winnersList.add(dataSnapshot.child("username").getValue().toString() + "\n" + occurences);
                                                        }
                                                    }
//                                                    ArrayList<Integer> ints = new ArrayList<>();
//                                                    ArrayList<String> allWinners = (ArrayList<String>) winnersList.clone();
//                                                    for (String winner: winnersList){
//                                                        String[] strings = winner.split("\n");
//                                                        int number = Integer.parseInt(strings[1]);
//                                                        winnersList.clear();
//                                                        ints.add(number);
//                                                    }
//                                                    Collections.sort(ints);
//                                                    for (int i = 0; i <= ints.size(); i++){
//                                                        for (String winner: allWinners){
//
//                                                        }
//                                                    }

                                                    Winner[] winner = new Winner[winnersList.size()];
                                                    ArrayList<Integer> ints = new ArrayList<>();
                                                    ArrayList<String> strings = new ArrayList<>();
                                                    for (String winnerz: winnersList){
                                                        String[] win = winnerz.split("\n");
                                                        strings.add(win[0]);
                                                        ints.add(Integer.valueOf(win[1]));
                                                    }

                                                    for (int i = 0; i<winnersList.size(); i++){
                                                        winner[i] = new Winner(strings.get(i), ints.get(i));
                                                    }

                                                    Arrays.sort(winner, new WinnerComparator());

                                                    winnersList.clear();

                                                    for (Winner value : winner) {
                                                        winnersList.add(value.toString());
                                                    }

                                                    CustomListAdapter customListAdapter = new CustomListAdapter(WinnersActivity.this, R.layout.custom_list, winnersList);
                                                    listView.setAdapter(customListAdapter);
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

    private void findDailyLeaderboard(){
        ArrayList<String> winners = new ArrayList<>();
        databaseReference.child("winners").child("dailyLeaderboard").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                dataSnapshot1.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
//                                            for (String winner : winners){
//                                                if (winner.contains(snapshot1.child("winner").getValue().toString())){
//                                                   String[] strings = winner.split("\n");
//                                                   int wonTimes = Integer.parseInt(strings[1]) + 1;
//                                                   winners.remove(winner);
//                                                   winners.add(snapshot1.child("winner").getValue().toString() + "\n" + wonTimes);
//                                                } else {
                                            winners.add(snapshot1.child("winner").getValue().toString());
//                                                }
//                                                Toast.makeText(WinnersActivity.this, Arrays.toString(new ArrayList[]{winners}), Toast.LENGTH_SHORT).show();
                                        }
//                                        }
                                        ArrayList<String> winnersList = new ArrayList<>();
                                        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        int occurences = Collections.frequency(winners, dataSnapshot.child("username").getValue().toString());
                                                        if (occurences != 0){
                                                            winnersList.add(dataSnapshot.child("username").getValue().toString() + "\n" + occurences);
                                                        }
                                                    }
//                                                    ArrayList<Integer> ints = new ArrayList<>();
//                                                    ArrayList<String> allWinners = (ArrayList<String>) winnersList.clone();
//                                                    for (String winner: winnersList){
//                                                        String[] strings = winner.split("\n");
//                                                        int number = Integer.parseInt(strings[1]);
//                                                        winnersList.clear();
//                                                        ints.add(number);
//                                                    }
//                                                    Collections.sort(ints);
//                                                    for (int i = 0; i <= ints.size(); i++){
//                                                        for (String winner: allWinners){
//
//                                                        }
//                                                    }

                                                    Winner[] winner = new Winner[winnersList.size()];
                                                    ArrayList<Integer> ints = new ArrayList<>();
                                                    ArrayList<String> strings = new ArrayList<>();
                                                    for (String winnerz: winnersList){
                                                        String[] win = winnerz.split("\n");
                                                        strings.add(win[0]);
                                                        ints.add(Integer.valueOf(win[1]));
                                                    }

                                                    for (int i = 0; i<winnersList.size(); i++){
                                                        winner[i] = new Winner(strings.get(i), ints.get(i));
                                                    }

                                                    Arrays.sort(winner, new WinnerComparator());

                                                    winnersList.clear();

                                                    for (Winner value : winner) {
                                                        winnersList.add(value.toString());
                                                    }

                                                    CustomListAdapter customListAdapter = new CustomListAdapter(WinnersActivity.this, R.layout.custom_list, winnersList);
                                                    listView.setAdapter(customListAdapter);
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

    @Override
    protected void onStart() {
        super.onStart();
        findAllTimeLeaderboard();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winners);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        daily = findViewById(R.id.button);
        weekly = findViewById(R.id.button2);
        yearly = findViewById(R.id.button3);
        allTime = findViewById(R.id.button4);
        title = findViewById(R.id.textView13);
        listView = findViewById(R.id.winners);

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