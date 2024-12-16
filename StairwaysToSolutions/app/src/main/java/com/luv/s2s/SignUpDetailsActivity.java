package com.luv.s2s;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpDetailsActivity extends AppCompatActivity {

    Button continues;
    String selectedSubject;
    EditText emailEditText, gradeEditText, schoolEditText;
    private DatabaseReference databaseReference;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_details);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        continues = findViewById(R.id.leaderboard);

        emailEditText = findViewById(R.id.emailEditText);
        gradeEditText = findViewById(R.id.gradeEditText);
        schoolEditText = findViewById(R.id.schoolEditText);

        final Spinner spinner = findViewById(R.id.planets_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.subjects, android.R.layout.simple_spinner_item);
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

        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedSubject.equals("Enter Favourite Subject")){
                    Toast.makeText(SignUpDetailsActivity.this, "Enter your favourite subject", Toast.LENGTH_SHORT).show();
                } else if (emailEditText.getText().toString().isEmpty()){
                    Toast.makeText(SignUpDetailsActivity.this, "Enter Your Email Id", Toast.LENGTH_SHORT).show();
                } else if (emailEditText.getText().toString().contains(".") || emailEditText.getText().toString().contains("#")
                        || emailEditText.getText().toString().contains("$") || emailEditText.getText().toString().contains("[") ||
                        emailEditText.getText().toString().contains("]")){
                    Toast.makeText(SignUpDetailsActivity.this, ". # $ [ ] symbols aren't allowed in username.", Toast.LENGTH_SHORT).show();
                }
                else if (gradeEditText.getText().toString().isEmpty()){
                    Toast.makeText(SignUpDetailsActivity.this, "Enter Your Grade", Toast.LENGTH_SHORT).show();
                } else if (schoolEditText.getText().toString().isEmpty()){
                    Toast.makeText(SignUpDetailsActivity.this, "Enter Your School Name", Toast.LENGTH_SHORT).show();
                } else {
                    saveUserData();
                }
            }
        });
    }

    private void saveUserData(){

        final String finalEmail = email;
        final String finalUsername = emailEditText.getText().toString();
        final String finalSubject = selectedSubject;
        final String finalGrade = gradeEditText.getText().toString();
        final String finalSchool = schoolEditText.getText().toString();
        final int finalPoints = 0;
        final int finalCoins = 0;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Account Settings");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        Map<String, Object> user = new HashMap<>();
        user.put("email", finalEmail);
        user.put("username", finalUsername);
        user.put("favoriteSubject", finalSubject);
        user.put("grade", finalGrade);
        user.put("school", finalSchool);
        user.put("points", finalPoints);
        user.put("coins", finalCoins);

        databaseReference.child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SignUpDetailsActivity.this, "Details saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpDetailsActivity.this, HomeActivity.class);
                startActivity(intent);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
