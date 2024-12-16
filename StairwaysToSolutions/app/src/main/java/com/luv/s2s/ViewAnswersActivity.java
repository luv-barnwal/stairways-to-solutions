package com.luv.s2s;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAnswersActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> answerers = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    String askedBy, question, likes;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_answers);

        listView = findViewById(R.id.answersListView);
        Intent intent = getIntent();
        askedBy = intent.getStringExtra("username");
        question = intent.getStringExtra("question");
        likes = intent.getStringExtra("likes");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(askedBy)
                                && dataSnapshot.child("likes").getValue().toString().equals(likes)) {
                                dataSnapshot.child("answers").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshots) {
                                        if (snapshots.exists()){
                                            for (DataSnapshot dataSnapshots : snapshots.getChildren()){
//                                                String id = dataSnapshot.getRef().push().getKey();
                                                answerers.add(dataSnapshots.child("answeredBy").getValue().toString());
                                            }
                                            arrayAdapter = new ArrayAdapter<>(ViewAnswersActivity.this, android.R.layout.simple_list_item_1, answerers);
                                            listView.setAdapter(arrayAdapter);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent1 = new Intent(ViewAnswersActivity.this, AnswerActivity.class);
                intent1.putExtra("askedBy", askedBy);
                intent1.putExtra("answeredBy", answerers.get(i));
                intent1.putExtra("question", question);
                intent1.putExtra("numberOfLikes", likes);
                startActivity(intent1);
            }
        });
    }
}