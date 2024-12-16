package com.luv.s2s;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnswerActivity extends AppCompatActivity {

    String question, username, answerer, likes, questionLikes;
    TextView totalAnswer, attachments, numberOfLikes;
    Button returnBack, continues;
    ImageView like, dislike, correctAnswer, removeAnswer, report;
    Boolean liked = false, disliked = false, correct = false, member = false;
    int numberOfPointsGained = 0, images = 0;
    ImageView image1, image2;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Intent intent = getIntent();
        question = intent.getStringExtra("question");
        username = intent.getStringExtra("askedBy");
        answerer = intent.getStringExtra("answeredBy");
        questionLikes = intent.getStringExtra("numberOfLikes");
        correctAnswer = findViewById(R.id.correctAnswer);
        removeAnswer = findViewById(R.id.removeAnswer);
        totalAnswer = findViewById(R.id.totalquestion);
        attachments = findViewById(R.id.attachments);
        returnBack = findViewById(R.id.returnBack);
        continues = findViewById(R.id.continues);
        report = findViewById(R.id.report);
        numberOfLikes = findViewById(R.id.likes);
        like = findViewById(R.id.like);
        dislike = findViewById(R.id.dislike);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);

        databaseReference.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                && dataSnapshot.child("likes").getValue().toString().equals(questionLikes)) {
                            dataSnapshot.child("answers").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshots) {
                                    if (snapshots.exists()){
                                        for (DataSnapshot dataSnapshots : snapshots.getChildren()){
                                            if (dataSnapshots.child("answeredBy").getValue().toString().equals(answerer)){
                                                likes = dataSnapshots.child("likes").getValue().toString();
                                                numberOfLikes.setText(likes);
                                                totalAnswer.setText(dataSnapshots.child("answer").getValue().toString());
                                                attachments.setText(dataSnapshot.child("attachments").getValue().toString());
                                                if (dataSnapshots.child("correctAnswer").getValue().toString().equals("true")){
                                                    correct = true;
                                                    correctAnswer.setImageResource(R.drawable.correct_answer_selected);
                                                } else {
                                                    correct = false;
                                                    correctAnswer.setImageResource(R.drawable.correct_answer_not_selected);
                                                }
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final String[] currentUser = new String[1];

        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    currentUser[0] = snapshot.child("username").getValue().toString();
                    databaseReference.child("extras").child(currentUser[0]).child("membership").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                member = true;
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

        databaseReference.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                && dataSnapshot.child("likes").getValue().toString().equals(questionLikes)) {
                            dataSnapshot.getRef().child("answers").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshots) {
                                    if (snapshots.exists()){
                                        for (DataSnapshot dataSnapshots : snapshots.getChildren()){
                                            if (dataSnapshots.child("answeredBy").getValue().toString().equals(answerer) && dataSnapshots.child("likes").getValue().toString().equals(likes)){
                                                images = Integer.parseInt(dataSnapshots.child("images").getValue().toString());
                                                if (images > 0) {
                                                    String random = dataSnapshots.child("imageUUID").getValue().toString();
                                                    String questio;
                                                    if (question.contains("/")) {
                                                        questio = question.replaceAll("/", "by");
                                                    } else {
                                                        questio = question;
                                                    }
                                                    storageReference.child("images/").child("questions/").child(questio).child(random).child("image1").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            image1.setVisibility(View.VISIBLE);
                                                            Picasso.get().load(uri).into(image1);
                                                            if (images > 1) {
                                                                storageReference.child("images/").child("questions/").child(questio).child(random).child("image2").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri uri) {
                                                                        image2.setVisibility(View.VISIBLE);
                                                                        Picasso.get().load(uri).into(image2);
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                    Toast.makeText(AnswerActivity.this, "Question Loaded", Toast.LENGTH_SHORT).show();
                                                }
                                                if (currentUser[0].equals(username)){
                                                    correctAnswer.setClickable(true);
                                                    removeAnswer.setVisibility(View.VISIBLE);
                                                    correctAnswer.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            databaseReference.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()){
                                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                                            if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                                                                    && dataSnapshot.child("likes").getValue().toString().equals(questionLikes)) {
                                                                                dataSnapshot.child("answers").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshots) {
                                                                                        if(snapshots.exists()){
                                                                                            for (DataSnapshot dataSnapshots : snapshots.getChildren()){
                                                                                                if (!correct) {
                                                                                                    dataSnapshots.child("correctAnswer").getRef().setValue(true);
                                                                                                    correctAnswer.setImageResource(R.drawable.correct_answer_selected);
                                                                                                    correct = true;
                                                                                                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                            if (snapshot.exists()){
                                                                                                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                                                                                                    if (member){
                                                                                                                        numberOfPointsGained += 60;
                                                                                                                        int points = Integer.parseInt(dataSnapshot1.child("points").getValue().toString()) + 60;
                                                                                                                        dataSnapshot1.child("points").getRef().setValue(points);
                                                                                                                    } else {
                                                                                                                        numberOfPointsGained += 30;
                                                                                                                        int points = Integer.parseInt(dataSnapshot1.child("points").getValue().toString()) + 30;
                                                                                                                        dataSnapshot1.child("points").getRef().setValue(points);
                                                                                                                    }
                                                                                                                    leaderBoard();
                                                                                                                }
                                                                                                            }
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                                        }
                                                                                                    });
                                                                                                } else {
                                                                                                    dataSnapshots.child("correctAnswer").getRef().setValue(false);
                                                                                                    correctAnswer.setImageResource(R.drawable.correct_answer_not_selected);
                                                                                                    correct = false;
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
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                        }
                                                    });

                                                    removeAnswer.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            new AlertDialog.Builder(AnswerActivity.this)
                                                                    .setTitle("Are you sure you want to delete this answer")
                                                                    .setMessage("This is irreversible")
                                                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            databaseReference.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    if (snapshot.exists()){
                                                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                                                            if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                                                                                    && dataSnapshot.child("likes").getValue().toString().equals(questionLikes)) {
                                                                                                dataSnapshot.child("answers").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(@NonNull DataSnapshot snapshots) {
                                                                                                        if (snapshots.exists()){
                                                                                                            for (DataSnapshot dataSnapshots : snapshots.getChildren()){
                                                                                                                if (dataSnapshots.child("answeredBy").getValue().toString().equals(answerer) && dataSnapshots.child("likes").getValue().toString().equals(likes)){
                                                                                                                    dataSnapshots.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                                            Toast.makeText(AnswerActivity.this, "Answer Deleted!", Toast.LENGTH_SHORT).show();
                                                                                                                            Intent intent1 = new Intent(AnswerActivity.this, ViewAnswersActivity.class);
                                                                                                                            intent1.putExtra("username", username);
                                                                                                                            intent1.putExtra("question", question);
                                                                                                                            intent1.putExtra("likes", likes);
                                                                                                                            startActivity(intent1);
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
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });
                                                                        }
                                                                    })
                                                                    .setNegativeButton("Cancel", null)
                                                                    .show();
                                                        }
                                                    });
                                                }
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                && dataSnapshot.child("likes").getValue().toString().equals(questionLikes)) {
                            dataSnapshot.child("answers").getRef().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshots) {
                                    if (snapshots.exists()){
                                        for (DataSnapshot dataSnapshots : snapshots.getChildren()){
                                            if (dataSnapshots.child("answeredBy").getValue().toString().equals(answerer) && dataSnapshots.child("likes").getValue().toString().equals(likes)){
                                                dataSnapshots.child("likedBy").getRef().addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                        if (snapshot1.exists()){
                                                            for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()){
                                                                if (dataSnapshot1.getValue().equals(currentUser[0])){
                                                                    liked = true;
                                                                    like.setImageResource(R.drawable.like_selected);
                                                                } else {
                                                                    liked = false;
                                                                    like.setImageResource(R.drawable.like_not_selected);
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

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liked) {
                    databaseReference.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                            && dataSnapshot.child("likes").getValue().toString().equals(questionLikes)) {
                                        dataSnapshot.child("answers").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshots) {
                                                if (snapshots.exists()) {
                                                    for (DataSnapshot dataSnapshots : snapshots.getChildren()) {
                                                        if (dataSnapshots.child("answeredBy").getValue().toString().equals(answerer) && dataSnapshots.child("likes").getValue().toString().equals(likes)) {
                                                            dataSnapshots.child("likedBy").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                                    if (snapshot1.exists()) {
                                                                        for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {
                                                                            if (dataSnapshot1.getValue().toString().equals(currentUser[0])) {
                                                                                dataSnapshot1.getRef().removeValue();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                            int liken = Integer.parseInt(likes) - 1;
                                                            dataSnapshots.child("likes").getRef().setValue(liken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        likes = Integer.toString(liken);
                                                                        numberOfLikes.setText(likes);
                                                                        like.setImageResource(R.drawable.like_not_selected);
                                                                        liked = false;
                                                                    }
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
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    databaseReference.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                            && dataSnapshot.child("likes").getValue().toString().equals(questionLikes)) {
                                        dataSnapshot.child("answers").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshots) {
                                                if (snapshots.exists()){
                                                    for (DataSnapshot dataSnapshots : snapshots.getChildren()){
                                                        if (dataSnapshots.child("answeredBy").getValue().toString().equals(answerer) && dataSnapshots.child("likes").getValue().toString().equals(likes)){
                                                            dataSnapshots.child("likedBy").getRef().push().setValue(currentUser[0]);
                                                            int liken = Integer.parseInt(likes) + 1;
                                                            dataSnapshots.child("likes").getRef().setValue(liken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if (snapshot.exists()){
                                                                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                                                                        if (dataSnapshot1.child("username").getValue().toString().equals(answerer)){
                                                                                            likes = Integer.toString(liken);
                                                                                            numberOfLikes.setText(likes);
                                                                                            like.setImageResource(R.drawable.like_not_selected);
                                                                                            liked = false;
                                                                                            if (member){
                                                                                                numberOfPointsGained += 10;
                                                                                                int points = Integer.parseInt(dataSnapshot1.child("points").getValue().toString()) + 10;
                                                                                                dataSnapshot1.child("points").getRef().setValue(points);
                                                                                            } else {
                                                                                                int points = Integer.parseInt(dataSnapshot1.child("points").getValue().toString()) + 5;
                                                                                                dataSnapshot1.child("points").getRef().setValue(points);
                                                                                                numberOfPointsGained += 5;
                                                                                            }
                                                                                            leaderBoard();
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
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        databaseReference.child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                && dataSnapshot.child("likes").getValue().toString().equals(questionLikes)) {
                            dataSnapshot.child("answers").getRef().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshots) {
                                    if (snapshots.exists()){
                                        for (DataSnapshot dataSnapshots : snapshots.getChildren()){
                                            if (dataSnapshots.child("answeredBy").getValue().toString().equals(answerer) && dataSnapshots.child("likes").getValue().toString().equals(likes)){
                                                dataSnapshots.child("dislikedBy").getRef().addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                        if (snapshot1.exists()){
                                                            for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()){
                                                                if (dataSnapshot1.getValue().equals(currentUser[0])){
                                                                    disliked = true;
                                                                    dislike.setImageResource(R.drawable.dislike_selected);
                                                                } else {
                                                                    disliked = false;
                                                                    dislike.setImageResource(R.drawable.dislike_not_selected);
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

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (disliked) {
                    databaseReference.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                            && dataSnapshot.child("likes").getValue().toString().equals(questionLikes)) {
                                        dataSnapshot.child("answers").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshots) {
                                                if (snapshots.exists()) {
                                                    for (DataSnapshot dataSnapshots : snapshots.getChildren()) {
                                                        if (dataSnapshots.child("answeredBy").getValue().toString().equals(answerer) && dataSnapshots.child("likes").getValue().toString().equals(likes)) {
                                                            dataSnapshots.child("dislikedBy").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                                    if (snapshot1.exists()) {
                                                                        for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {
                                                                            if (dataSnapshot1.getValue().toString().equals(currentUser[0])) {
                                                                                dataSnapshot1.getRef().removeValue();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                            int liken = Integer.parseInt(likes) + 1;
                                                            dataSnapshots.child("likes").getRef().setValue(liken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        likes = Integer.toString(liken);
                                                                        numberOfLikes.setText(likes);
                                                                        dislike.setImageResource(R.drawable.dislike_not_selected);
                                                                        disliked = false;
                                                                    }
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
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    databaseReference.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                            && dataSnapshot.child("likes").getValue().toString().equals(questionLikes)) {
                                        dataSnapshot.child("answers").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshots) {
                                                if (snapshots.exists()){
                                                    for (DataSnapshot dataSnapshots : snapshots.getChildren()){
                                                        if (dataSnapshots.child("answeredBy").getValue().toString().equals(answerer) && dataSnapshots.child("likes").getValue().toString().equals(likes)){
                                                            dataSnapshots.child("dislikedBy").getRef().push().setValue(currentUser[0]);
                                                            int liken = Integer.parseInt(likes) - 1;
                                                            dataSnapshots.child("likes").getRef().setValue(liken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if (snapshot.exists()){
                                                                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                                                                        if (dataSnapshot1.child("username").getValue().toString().equals(answerer)) {
                                                                                            likes = Integer.toString(liken);
                                                                                            numberOfLikes.setText(likes);
                                                                                            dislike.setImageResource(R.drawable.dislike_not_selected);
                                                                                            disliked = false;
                                                                                            if (member){
                                                                                                numberOfPointsGained -= 2;
                                                                                                int liker = Integer.parseInt(dataSnapshot1.child("points").getValue().toString()) - 2;
                                                                                                dataSnapshot1.getRef().child("points").setValue(liker);
                                                                                            } else {
                                                                                                numberOfPointsGained -= 5;
                                                                                                int liker = Integer.parseInt(dataSnapshot1.child("points").getValue().toString()) - 5;
                                                                                                dataSnapshot1.getRef().child("points").setValue(liker);
                                                                                            }
                                                                                            leaderBoard();
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
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(AnswerActivity.this, ViewAnswersActivity.class);
                intent1.putExtra("username", username);
                intent1.putExtra("likes", questionLikes);
                intent1.putExtra("questions", question);
                startActivity(intent1);
            }
        });

        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(AnswerActivity.this, ViewQuestionActivity.class);
                intent1.putExtra("question", question);
                intent1.putExtra("usernameAndLikes", username + "\n" + questionLikes);
                startActivity(intent1);
            }
        });


        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText password = new EditText(AnswerActivity.this);
                password.setInputType(InputType.TYPE_CLASS_TEXT);
                new AlertDialog.Builder(AnswerActivity.this)
                        .setTitle("Report " + question)
                        .setMessage("Enter your complaint")
                        .setView(password)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String, Object> complaint = new HashMap<>();
                                complaint.put("reportedBy", currentUser[0]);
                                complaint.put("offender", answerer);
                                complaint.put("question", question);
                                complaint.put("answer", totalAnswer);
                                complaint.put("problem", password.getText().toString());
                                String uuid = UUID.randomUUID().toString();
                                databaseReference.child("complaints").child(uuid).setValue(complaint).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(AnswerActivity.this, "Complaint Sent!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    public void leaderBoard() {
        databaseReference.child("dailyLeaderboard").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child(answerer).exists()) {
                            HashMap<String, Object> leaderboard = new HashMap<>();
                            leaderboard.put("username", answerer);
                            int points = Integer.parseInt(dataSnapshot.child("points").getValue().toString()) + numberOfPointsGained;
                            leaderboard.put("points", points);
                            dataSnapshot.getRef().setValue(leaderboard);
                        } else {
                            int position = (int) (snapshot.getChildrenCount() + 2);
                            HashMap<String, Object> leaderboard = new HashMap<>();
                            leaderboard.put("username", answerer);
                            leaderboard.put("points", numberOfPointsGained);
                            snapshot.getRef().child(Integer.toString(position)).setValue(leaderboard);
                        }
                    }
                } else {
                    HashMap<String, Object> leaderboard = new HashMap<>();
                    leaderboard.put("username", answerer);
                    leaderboard.put("points", numberOfPointsGained);
                    databaseReference.child("dailyLeaderboard").child(Integer.toString(1)).setValue(leaderboard);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("weeklyLeaderboard").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child(answerer).exists()) {
                            HashMap<String, Object> leaderboard = new HashMap<>();
                            leaderboard.put("username", answerer);
                            int points = Integer.parseInt(dataSnapshot.child("points").getValue().toString()) + numberOfPointsGained;
                            leaderboard.put("points", points);
                            dataSnapshot.getRef().setValue(leaderboard);
                        } else {
                            int position = (int) (snapshot.getChildrenCount() + 2);
                            HashMap<String, Object> leaderboard = new HashMap<>();
                            leaderboard.put("username", answerer);
                            leaderboard.put("points", numberOfPointsGained);
                            snapshot.getRef().child(Integer.toString(position)).setValue(leaderboard);
                        }
                    }
                } else {
                    HashMap<String, Object> leaderboard = new HashMap<>();
                    leaderboard.put("username", answerer);
                    leaderboard.put("points", numberOfPointsGained);
                    databaseReference.child("weeklyLeaderboard").child(Integer.toString(1)).setValue(leaderboard);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("yearlyLeaderboard").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child(username).exists()) {
                            HashMap<String, Object> leaderboard = new HashMap<>();
                            leaderboard.put("username", username);
                            int points = Integer.parseInt(dataSnapshot.child("points").getValue().toString()) + numberOfPointsGained;
                            leaderboard.put("points", points);
                            dataSnapshot.getRef().setValue(leaderboard);
                        } else {
                            int position = (int) (snapshot.getChildrenCount() + 2);
                            HashMap<String, Object> leaderboard = new HashMap<>();
                            leaderboard.put("username", username);
                            leaderboard.put("points", numberOfPointsGained);
                            snapshot.getRef().child(Integer.toString(position)).setValue(leaderboard);
                        }
                    }
                } else {
                    HashMap<String, Object> leaderboard = new HashMap<>();
                    leaderboard.put("username", username);
                    leaderboard.put("points", numberOfPointsGained);
                    databaseReference.child("yearlyLeaderboard").child(Integer.toString(1)).setValue(leaderboard);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}