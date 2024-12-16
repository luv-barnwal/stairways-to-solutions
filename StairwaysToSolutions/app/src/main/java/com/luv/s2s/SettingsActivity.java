package com.luv.s2s;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    BottomNavigationView navView;
    EditText emailChange;
    Spinner subjectsSpinner;
    Button go;
    String selectedItem;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    ArrayAdapter<CharSequence> adapter;

    public static boolean isValidEmail(CharSequence target){
        if (target == null){
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        emailChange = findViewById(R.id.emailChange);
        subjectsSpinner = findViewById(R.id.subjects_spinner);
        go = findViewById(R.id.rewards);

        getUserInfo();

        adapter = ArrayAdapter.createFromResource(this, R.array.subjects, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectsSpinner.setAdapter(adapter);

        subjectsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = subjectsSpinner.getItemAtPosition(subjectsSpinner.getSelectedItemPosition()).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailChange.getText().toString().isEmpty()) {
                    Toast.makeText(SettingsActivity.this, "Enter a new Email", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(emailChange.getText().toString())){
                    emailChange.setHint("Use a proper email id");
                } else if (selectedItem.equals("Enter Favourite Subject")){
                    Toast.makeText(SettingsActivity.this, "Select Your Favorite Subject", Toast.LENGTH_SHORT).show();
                } else {
                    saveUserInfo();
                }
            }
        });
    }

    private void saveUserInfo(){

        final String finalEmail = emailChange.getText().toString();
        final String finalSubject = selectedItem;

        databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int finalPoints = Integer.parseInt(String.valueOf(snapshot.child("points").getValue()));
                    int finalCoins = Integer.parseInt(String.valueOf(snapshot.child("coins").getValue()));
                    String finalGrade = snapshot.child("grade").getValue().toString();
                    String finalSchool = snapshot.child("school").getValue().toString();
                    String finalUsername = snapshot.child("username").getValue().toString();
                    Map<String, Object> users = new HashMap<>();
                    users.put("email", finalEmail);
                    users.put("username", finalUsername);
                    users.put("favoriteSubject", finalSubject);
                    users.put("grade", finalGrade);
                    users.put("school", finalSchool);
                    users.put("points", finalPoints);
                    users.put("coins", finalCoins);

                    final EditText password = new EditText(SettingsActivity.this);
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("Enter your password to change details")
                            .setView(password)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    ProgressDialog progressDialog = new ProgressDialog(SettingsActivity.this);
                                    progressDialog.setTitle("Account Settings");
                                    progressDialog.setMessage("Please Wait...");
                                    progressDialog.show();

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password.getText().toString());
                                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                                user1.updateEmail(finalEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            databaseReference.child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                                                    .setValue(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(SettingsActivity.this, "Details saved", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                                                                    startActivity(intent);
                                                                    progressDialog.cancel();
                                                                }

                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });

    }

    private void getUserInfo(){

        databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    emailChange.setText(snapshot.child("email").getValue().toString());
                    selectedItem = snapshot.child("favoriteSubject").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()){

                        case R.id.navigation_home :
                            Intent mainIntent = new Intent(SettingsActivity.this, HomeActivity.class);
                            startActivity(mainIntent);
                            break;

                        case R.id.navigation_profile :
//                            Intent settingsIntent = new Intent(SettingsActivity.this, ProfileActivity.class);
//                            startActivity(settingsIntent);
                            break;

                        case R.id.navigation_logout :

                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle("Are you sure you want to log out?")
                                    .setMessage("This will take you to the home screen")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            FirebaseAuth.getInstance().signOut();

                                            Intent logOutIntent = new Intent(SettingsActivity.this, DashBoardActivity.class);
                                            startActivity(logOutIntent);
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                            break;
                    }
                    return true;
                }
            };
}