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
import java.util.Objects;
import java.util.UUID;

public class ViewQuestionActivity extends AppCompatActivity {

    String question, username, likes;
    TextView totalQuestion, questionTitle, attachments, numberOfLikes;
    Button returnBack, continues, answers;
    ImageView like, dislike, report;
    Boolean liked = false, disliked = false, member = false;
    int numberOfPointsGained = 0, images = 0;
    ImageView image1, image2;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_question);

        report = findViewById(R.id.report);
        totalQuestion = findViewById(R.id.totalquestion);
        questionTitle = findViewById(R.id.question);
        attachments = findViewById(R.id.attachments);
        returnBack = findViewById(R.id.returnBack);
        continues = findViewById(R.id.continues);
        answers = findViewById(R.id.answers);
        report = findViewById(R.id.report);
        numberOfLikes = findViewById(R.id.likes);
        like = findViewById(R.id.like);
        dislike = findViewById(R.id.dislike);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        databaseReference.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                && dataSnapshot.child("likes").getValue().toString().equals(likes)) {
                            images = Integer.parseInt(dataSnapshot.child("images").getValue().toString());
                            if (images > 0) {
                                String questio;
                                if (question.contains("/")) {
                                    questio = question.replaceAll("/", "by");
                                } else {
                                    questio = question;
                                }
                                storageReference.child("images/").child("questions/").child(questio).child("image1").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        image1.setVisibility(View.VISIBLE);
                                        Picasso.get().load(uri).into(image1);
                                        if (images > 1) {
                                            storageReference.child("images/").child("questions/").child(questio).child("image2").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    image2.setVisibility(View.VISIBLE);
                                                    Picasso.get().load(uri).into(image2);
                                                }
                                            });
                                        }
                                    }
                                });
                                Toast.makeText(ViewQuestionActivity.this, "Question Loaded", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewQuestionActivity.this, QuestionsActivity.class);
                startActivity(intent);
            }
        });

        answers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("questions").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                        && dataSnapshot.child("likes").getValue().toString().equals(likes)) {
                                    dataSnapshot.getRef().child("answers").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                Intent intent = new Intent(ViewQuestionActivity.this, ViewAnswersActivity.class);
                                                intent.putExtra("username", username);
                                                intent.putExtra("question", question);
                                                intent.putExtra("likes", likes);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(ViewQuestionActivity.this, "There are no answers to this question. Be the first to answer!", Toast.LENGTH_SHORT).show();
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

        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewQuestionActivity.this, AnswerQuestionActivity.class);
                intent.putExtra("askedBy", username);
                intent.putExtra("question", question);
                intent.putExtra("likes", likes);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        question = intent.getStringExtra("question");
        String[] arr = Objects.requireNonNull(intent.getStringExtra("usernameAndLikes")).split("\n");
        username = arr[0];
        likes = arr[1];

        numberOfLikes.setText(likes);

        databaseReference.child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                && dataSnapshot.child("likes").getValue().toString().equals(likes)) {
                            questionTitle.setText(question);
                            totalQuestion.setText(dataSnapshot.child("detailedQuestion").getValue().toString());
                            attachments.setText(dataSnapshot.child("attachments").getValue().toString());
                            images = Integer.parseInt(dataSnapshot.child("images").getValue().toString());
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
                if (snapshot.exists()) {
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

        databaseReference.child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                && dataSnapshot.child("likes").getValue().toString().equals(likes)) {
//                            if (dataSnapshot.child("likedBy").hasChild(currentUser[0])) {
//                                liked = true;
//                                like.setImageResource(R.drawable.like_selected);
//                            } else {
//                                liked = false;
//                                like.setImageResource(R.drawable.like_not_selected);
//                            }
                            dataSnapshot.child("likedBy").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshots) {
                                    if(snapshots.exists()){
                                        for (DataSnapshot dataSnapshots : snapshots.getChildren()){
                                            if (dataSnapshots.getValue().toString().equals(currentUser[0])){
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
                                            && dataSnapshot.child("likes").getValue().toString().equals(likes)) {
                                        dataSnapshot.getRef().child("likedBy").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshots) {
                                                if (snapshots.exists()){
                                                    for (DataSnapshot dataSnapshots : snapshots.getChildren()){
                                                        if (dataSnapshots.getValue().toString().equals(currentUser[0])){
                                                            dataSnapshots.getRef().removeValue();
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        int liken = Integer.parseInt(likes) - 1;
                                        dataSnapshot.child("likes").getRef().setValue(liken).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                } else {
                    databaseReference.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                            && dataSnapshot.child("likes").getValue().toString().equals(likes)) {
                                        int numberOfUsers = (int) dataSnapshot.child("likedBy").getChildrenCount() + 1;
                                        dataSnapshot.child("likedBy").getRef().push().setValue(currentUser[0]);
                                        int liken = Integer.parseInt(likes) + 1;
                                        dataSnapshot.child("likes").getRef().setValue(liken);
                                        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                                        if (dataSnapshot1.child("username").getValue().toString().equals(username)) {
                                                            int points = Integer.parseInt(dataSnapshot1.child("points").getValue().toString()) + 5;
                                                            if (member){
                                                                numberOfPointsGained += 10;
                                                                points += 5;
                                                            } else {
                                                                numberOfPointsGained += 5;
                                                            }
                                                            dataSnapshot1.child("points").getRef().setValue(points).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        likes = Integer.toString(liken);
                                                                        numberOfLikes.setText(likes);
                                                                        like.setImageResource(R.drawable.like_selected);
                                                                        liked = true;
                                                                        leaderBoard();
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
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                && dataSnapshot.child("likes").getValue().toString().equals(likes)) {
//                            if (dataSnapshot.child("likedBy").hasChild(currentUser[0])) {
//                                liked = true;
//                                like.setImageResource(R.drawable.like_selected);
//                            } else {
//                                liked = false;
//                                like.setImageResource(R.drawable.like_not_selected);
//                            }
                            dataSnapshot.child("dislikedBy").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshots) {
                                    if(snapshots.exists()){
                                        for (DataSnapshot dataSnapshots : snapshots.getChildren()){
                                            if (dataSnapshots.getValue().toString().equals(currentUser[0])){
                                                disliked = true;
                                                dislike.setImageResource(R.drawable.like_selected);
                                            } else {
                                                disliked = false;
                                                dislike.setImageResource(R.drawable.like_not_selected);
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
                                            && dataSnapshot.child("likes").getValue().toString().equals(likes)) {
                                        dataSnapshot.getRef().child("dislikedBy").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshots) {
                                                if (snapshots.exists()){
                                                    for (DataSnapshot dataSnapshots : snapshots.getChildren()){
                                                        if (dataSnapshots.getValue().toString().equals(currentUser[0])){
                                                            dataSnapshots.getRef().removeValue();
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        int liken = Integer.parseInt(likes) + 1;
                                        dataSnapshot.child("likes").getRef().setValue(liken).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                } else {
                    databaseReference.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(username)
                                            && dataSnapshot.child("likes").getValue().toString().equals(likes)) {
                                        int numberOfUsers = (int) dataSnapshot.child("likedBy").getChildrenCount() + 1;
                                        dataSnapshot.child("dislikedBy").getRef().push().setValue(currentUser[0]);
                                        int liken = Integer.parseInt(likes) - 1;
                                        dataSnapshot.child("likes").getRef().setValue(liken);
                                        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                                        if (dataSnapshot1.child("username").getValue().toString().equals(username)) {
                                                            int points = Integer.parseInt(dataSnapshot1.child("points").getValue().toString()) - 5;
                                                            if (member){
                                                                numberOfPointsGained -= 2;
                                                                points += 3;
                                                            } else {
                                                                numberOfPointsGained -= 5;
                                                            }
                                                            dataSnapshot1.child("points").getRef().setValue(points).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        likes = Integer.toString(liken);
                                                                        numberOfLikes.setText(likes);
                                                                        dislike.setImageResource(R.drawable.dislike_selected);
                                                                        disliked = true;
                                                                        leaderBoard();
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

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText password = new EditText(ViewQuestionActivity.this);
                password.setInputType(InputType.TYPE_CLASS_TEXT);
                new AlertDialog.Builder(ViewQuestionActivity.this)
                        .setTitle("Report " + question)
                        .setMessage("Enter your complaint")
                        .setView(password)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String, Object> complaint = new HashMap<>();
                                complaint.put("reportedBy", currentUser[0]);
                                complaint.put("offender", username);
                                complaint.put("question", question);
                                complaint.put("problem", password.getText().toString());
                                String uuid = UUID.randomUUID().toString();
                                databaseReference.child("complaints").child(uuid).setValue(complaint).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(ViewQuestionActivity.this, "Complaint Sent!", Toast.LENGTH_SHORT).show();
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