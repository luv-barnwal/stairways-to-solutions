package com.luv.s2s;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class QuestionsActivity extends AppCompatActivity {

    BottomNavigationView navView;
    String selectedSubject = "All";
    SearchView searchView;
    ListView listView;
    List<Map<String, String>> questionData = new ArrayList<>();
    ArrayList<String> questions = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    String selectedQuestion, selectedUsername;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        searchView = findViewById(R.id.search);
        listView = findViewById(R.id.listViewPopularQuestions);

        databaseReference.child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String question = dataSnapshot.child("question").getValue().toString();
                        String username = dataSnapshot.child("askedBy").getValue().toString();
                        int likes = Integer.parseInt(dataSnapshot.child("likes").getValue().toString());
                        Map<String, String> questionInfo = new HashMap<>();
                        questionInfo.put("question", question);
                        questionInfo.put("usernameAndLikes", username + "\n" + likes);
                        questionData.add(questionInfo);
                        questions.add(question);
                    }
                    arrayAdapter = new ArrayAdapter(QuestionsActivity.this, android.R.layout.simple_list_item_1, questions);
                    listView.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final Spinner spinner = findViewById(R.id.subjects);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.all_and_subjects, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectedSubject = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(!selectedSubject.equals("All")){
            databaseReference.child("questions").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        questionData.clear();
                        questions.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if (dataSnapshot.child("subject").getValue().toString().equals(selectedQuestion)){
                                String question = dataSnapshot.child("question").getValue().toString();
                                String username = dataSnapshot.child("askedBy").getValue().toString();
                                int likes = Integer.parseInt(dataSnapshot.child("likes").getValue().toString());
                                Map<String, String> questionInfo = new HashMap<>();
                                questionInfo.put("question", question);
                                questionInfo.put("usernameAndLikes", username + "\n" + likes);
                                questionData.add(questionInfo);
                                questions.add(question);
                            }
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Filter.FilterListener listener = new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        if (!(count > 0)){
                            Toast.makeText(QuestionsActivity.this, "There is no question available, please search again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                arrayAdapter.getFilter().filter(query, listener);
//                if (users.contains(query)){
//                } else {
//                    Toast.makeText(ChatListActivity.this, "There is no user with this username, please search again.", Toast.LENGTH_SHORT).show();
//                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Filter.FilterListener listener = new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        if (!(count > 0)){
                            Toast.makeText(QuestionsActivity.this, "There is no question available, please search again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                arrayAdapter.getFilter().filter(newText, listener);
//                if (users.contains(newText)){
//                    arrayAdapter.getFilter().filter(newText);
//                } else {
//                    Toast.makeText(ChatListActivity.this, "There is no user with this username, please search again.", Toast.LENGTH_SHORT).show();
//                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(QuestionsActivity.this, ViewQuestionActivity.class);
                Map<String, String> selectedQuestionDetails = questionData.get(i);
                selectedQuestion = selectedQuestionDetails.get("question");
                selectedUsername = selectedQuestionDetails.get("usernameAndLikes");
                intent.putExtra("question", selectedQuestion);
                intent.putExtra("usernameAndLikes", selectedUsername);
                startActivity(intent);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()){

                        case R.id.ask_navigation :
                            Intent mainIntent = new Intent(QuestionsActivity.this, AskQuestionActivity.class);
                            startActivity(mainIntent);
                            break;

                        case R.id.navigation_quit :

                            Intent logOutIntent = new Intent(QuestionsActivity.this, HomeActivity.class);
                            startActivity(logOutIntent);
                            finish();
                            break;
                    }
                    return true;
                }
            };
}