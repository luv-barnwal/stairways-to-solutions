package com.luv.s2s;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class AskQuestionActivity extends AppCompatActivity {

    String selectedSubject;
    EditText question, totalquestion;
    Button continues, quit;
    Spinner attachments;
    ArrayList<String> allAttachments = new ArrayList<>();
    TextView theAttachments;
    ImageView image1, image2;
    int numberOfImages = 0;
    int RESULT_LOAD_IMAGE = 1;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri filePath;
    String currentUser;
    int numberOfQuestions = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);

        theAttachments = findViewById(R.id.attachments);
        question = findViewById(R.id.question);
        totalquestion = findViewById(R.id.totalquestion);
        attachments = findViewById(R.id.spinner1);
        continues = findViewById(R.id.continues);
        quit = findViewById(R.id.quit);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        final Spinner spinner = findViewById(R.id.subjects2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.subjects_exclusive, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    currentUser = snapshot.child("username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AskQuestionActivity.this, QuestionsActivity.class);
                startActivity(intent);
            }
        });

        String[] items = new String[]{"Add Attachments", "Link", "Image", "Code", "PDF"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        attachments.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectedSubject = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        attachments.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (attachments.getSelectedItem().equals("Link")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(AskQuestionActivity.this);
                    builder.setTitle("Paste The Link You Want to Add");
// | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    final EditText input = new EditText(AskQuestionActivity.this);
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
                } else if (attachments.getSelectedItem().equals("Image")) {

                    if (image2.getVisibility() == View.GONE) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, RESULT_LOAD_IMAGE);
//                        uploadImage();
                    } else {
                        Toast.makeText(AskQuestionActivity.this, "Sorry:( You can't add any more images.", Toast.LENGTH_SHORT).show();
                    }
                } else if (attachments.getSelectedItem().equals("Code")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AskQuestionActivity.this);
                    builder.setTitle("Paste The Code You Want to Add");
// | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    final EditText input = new EditText(AskQuestionActivity.this);
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
                } else if (attachments.getSelectedItem().equals("PDF")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AskQuestionActivity.this);
                    builder.setTitle("Paste The PDF's Link from Google Drive You Want to Add");
// | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    final EditText input = new EditText(AskQuestionActivity.this);
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

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final boolean[] questionAvailable = {true};

        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (question.getText().toString().isEmpty()) {
                    Toast.makeText(AskQuestionActivity.this, "Please Enter The Question", Toast.LENGTH_SHORT).show();
                } else if (totalquestion.getText().toString().isEmpty()) {
                    Toast.makeText(AskQuestionActivity.this, "Please Enter The Question's Details", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                numberOfQuestions = (int) snapshot.getChildrenCount() + 1;
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    if (dataSnapshot.child("question").getValue().toString().equals(question.getText().toString())) {
                                        questionAvailable[0] = false;
                                    }
                                    if (questionAvailable[0]) {
                                        ProgressDialog progressDialog = new ProgressDialog(AskQuestionActivity.this);
                                        progressDialog.setTitle("Saving Question");
                                        progressDialog.setMessage("Please Wait...");
                                        progressDialog.show();
                                        Map<String, Object> questionData = new HashMap<>();
                                        questionData.put("question", question.getText().toString());
                                        questionData.put("detailedQuestion", totalquestion.getText().toString());
                                        questionData.put("askedBy", currentUser);
                                        if (theAttachments.getText().toString().isEmpty()){
                                            questionData.put("attachments", "");
                                        } else {
                                            questionData.put("attachments", theAttachments);
                                        }
                                        questionData.put("subject", selectedSubject);
                                        questionData.put("likes", 0);
                                        if (numberOfImages == 0){
                                            questionData.put("images", 0);
                                        } else if (numberOfImages == 1){
                                            questionData.put("images", 1);
                                        } else if (numberOfImages == 2){
                                            questionData.put("images", 2);
                                        }
                                        if (numberOfImages >= 1) {
                                            String questio;
                                            if (question.getText().toString().contains("/")){
                                                questio = question.getText().toString().replaceAll("/", "by");
                                            } else {
                                                questio = question.getText().toString();
                                            }
                                            StorageReference reference = storageReference.child("images/").child("questions/").child(questio + "/" + "image1");
                                            reference.putBytes(getByteArray(image1)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    if (numberOfImages == 2) {
                                                        StorageReference reference2 = storageReference.child("images/").child("questions/").child(questio + "/" + "image2");
                                                        reference2.putBytes(getByteArray(image2)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(AskQuestionActivity.this, "Your Image failed to upload. E: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(AskQuestionActivity.this, "Your Image failed to upload. E: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        databaseReference.child("questions").child(String.valueOf(numberOfQuestions)).setValue(questionData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AskQuestionActivity.this, "Question Saved!", Toast.LENGTH_SHORT).show();
                                                    progressDialog.cancel();
                                                    Intent intent = new Intent(AskQuestionActivity.this, QuestionsActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AskQuestionActivity.this, "Error in saving question. E: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(AskQuestionActivity.this, "This question has already been taken.", Toast.LENGTH_SHORT).show();
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

//    private void uploadImage(){
//        if (filePath != null){
//            ProgressDialog progressDialog = new ProgressDialog(AskQuestionActivity.this);
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
//            String images;
//            if (numberOfImages == 1){
//                images = "image1";
//            } else {
//                images = "image2";
//            }
//            StorageReference reference = storageReference.child("images/").child("questions/").child(question.getText().toString() + "/" + images);
//            reference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    progressDialog.dismiss();
//                    Toast.makeText(AskQuestionActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    progressDialog.dismiss();
//                    Toast.makeText(AskQuestionActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
//                }
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                    double progress = (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
//                    progressDialog.setMessage("Uploaded: " + (int) progress + "%");
//                }
//            });
//        }
//    }
}