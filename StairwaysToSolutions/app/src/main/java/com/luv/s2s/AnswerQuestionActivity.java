package com.luv.s2s;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnswerQuestionActivity extends AppCompatActivity {

    Uri filePath;
    private static int RESULT_LOAD_IMAGE = 1;
    EditText totalAnswer;
    Button continues, quit;
    Spinner attachments;
    ArrayList<String> allAttachments = new ArrayList<>();
    TextView theAttachments;
    ImageView image1, image2, image3, image4, image5;
    int numberOfImages = 0;
    String askedBy, question, likes;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);

        Intent intent = getIntent();
        askedBy = intent.getStringExtra("askedBy");
        question = intent.getStringExtra("question");
        likes = intent.getStringExtra("likes");

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        theAttachments = findViewById(R.id.attachments);
        totalAnswer = findViewById(R.id.totalquestion);
        attachments = findViewById(R.id.spinner1);
        continues = findViewById(R.id.continues);
        quit = findViewById(R.id.quit);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);

        String[] items = new String[]{"Add Attachments", "Link", "Image", "Code", "PDF"};
//        "Video",
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        attachments.setAdapter(arrayAdapter);

        attachments.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (attachments.getSelectedItem().equals("Link")){

                    AlertDialog.Builder builder = new AlertDialog.Builder(AnswerQuestionActivity.this);
                    builder.setTitle("Paste The Link You Want to Add");
// | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    final EditText input = new EditText(AnswerQuestionActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String attachments_link = input.getText().toString();
                            allAttachments.add(attachments_link);
                            theAttachments.append("Link: " + attachments_link + "\n\n");
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                } else if (attachments.getSelectedItem().equals("Image")){

                    if (image2.getVisibility() == View.GONE) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    } else {
                        Toast.makeText(AnswerQuestionActivity.this, "Sorry:( You can't add any more images.", Toast.LENGTH_SHORT).show();
                    }
//                } else if (attachments.getSelectedItem().equals("Video")){
//
//                    if (video3.getVisibility() == View.GONE) {
//
//                        Intent intent = new Intent();
//                        intent.setType("video/*");
//                        intent.setAction(Intent.ACTION_GET_CONTENT);
//                        startActivityForResult(Intent.createChooser(intent, "Complete action using"), LOAD_VIDEO);
//
//                    } else {
//                        Toast.makeText(AnswerQuestionActivity.this, "Sorry:( You can't add any more videos.", Toast.LENGTH_SHORT).show();
//                    }
                } else if (attachments.getSelectedItem().equals("Code")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AnswerQuestionActivity.this);
                    builder.setTitle("Paste The Code You Want to Add");
// | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    final EditText input = new EditText(AnswerQuestionActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String attachments_link = input.getText().toString();
                            allAttachments.add(attachments_link);
                            theAttachments.append("Code: " + attachments_link + "\n\n");
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                } else if (attachments.getSelectedItem().equals("PDF")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AnswerQuestionActivity.this);
                    builder.setTitle("Paste The PDF's Link from Google Drive You Want to Add");
// | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    final EditText input = new EditText(AnswerQuestionActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String attachments_link = input.getText().toString();
                            allAttachments.add(attachments_link);
                            theAttachments.append("PDF: " + attachments_link + "\n\n");
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            }

//                theAttachments.setText((CharSequence) allAttachments);

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final String[] currentUser = new String[1];

        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    currentUser[0] = snapshot.child("username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalAnswer.getText().toString().isEmpty()){
                    Toast.makeText(AnswerQuestionActivity.this, "Please enter an answer.", Toast.LENGTH_SHORT).show();
                } else {
                    ProgressDialog progressDialog = new ProgressDialog(AnswerQuestionActivity.this);
                    progressDialog.setTitle("Saving Answer");
                    progressDialog.setMessage("Please Wait...");
                    progressDialog.show();
                    databaseReference.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                    if (dataSnapshot.child("question").getValue().toString().equals(question) && dataSnapshot.child("askedBy").getValue().toString().equals(askedBy)
                                            && dataSnapshot.child("likes").getValue().toString().equals(likes)) {
                                        Map<String, Object> answerData = new HashMap<>();
                                        answerData.put("answeredBy", currentUser[0]);
                                        answerData.put("answer", totalAnswer.getText().toString());
                                        answerData.put("likes", 0);
                                        String random = UUID.randomUUID().toString();
                                        if (theAttachments.getText().toString().isEmpty()){
                                            answerData.put("attachments", "");
                                        } else {
                                            answerData.put("attachments", theAttachments.getText().toString());
                                        }
                                        answerData.put("correctAnswer", false);
                                        if (numberOfImages == 0){
                                            answerData.put("images", 0);
                                        } else if (numberOfImages == 1){
                                            answerData.put("images", 1);
                                            answerData.put("imageUUID", random);
                                        } else if (numberOfImages == 2){
                                            answerData.put("images", 2);
                                            answerData.put("imageUUID", random);
                                        }
                                        if (numberOfImages >= 1) {
                                            String questio;
                                            if (question.contains("/")){
                                                questio = question.replaceAll("/", "by");
                                            } else {
                                                questio = question;
                                            }
                                            StorageReference reference = storageReference.child("images/").child("questions/").child(questio + "/").child(random + "/" + "image1");
                                            reference.putBytes(getByteArray(image1)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    if (numberOfImages == 2) {
                                                        StorageReference reference2 = storageReference.child("images/").child("questions/").child(questio + "/").child(random + "/" + "image2");
                                                        reference2.putBytes(getByteArray(image2)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(AnswerQuestionActivity.this, "Your Image failed to upload. E: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(AnswerQuestionActivity.this, "Your Image failed to upload. E: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        dataSnapshot.getRef().child("answers").push().setValue(answerData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(AnswerQuestionActivity.this, "Answer Saved!", Toast.LENGTH_SHORT).show();
                                                    progressDialog.cancel();
                                                    Intent intent = new Intent(AnswerQuestionActivity.this, QuestionsActivity.class);
                                                    startActivity(intent);
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
        });
    }

    public byte[] getByteArray(ImageView imageView){
        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                if (image1.getVisibility() == View.GONE){
                    image1.setImageBitmap(bitmap);
                    image1.setVisibility(View.VISIBLE);
                    numberOfImages = 1;
                } else if (image2.getVisibility() == View.GONE){
                    image2.setImageBitmap(bitmap);
                    image2.setVisibility(View.VISIBLE);
                    numberOfImages = 2;
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}