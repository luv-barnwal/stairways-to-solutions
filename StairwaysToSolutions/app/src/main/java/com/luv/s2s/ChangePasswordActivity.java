package com.luv.s2s;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText newPassword, passwordChange;
    CheckBox robotCheck;
    Button yay;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();
        newPassword = findViewById(R.id.changePasswordEditText);
        passwordChange = findViewById(R.id.confirmPasswordEditText);
        robotCheck = findViewById(R.id.robotCheck);
        yay = findViewById(R.id.store);

        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");

        yay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPassword.getText().toString().isEmpty()){
                    Toast.makeText(ChangePasswordActivity.this, "Enter a new Password", Toast.LENGTH_SHORT).show();
                } else if (passwordChange.getText().toString().isEmpty()){
                    Toast.makeText(ChangePasswordActivity.this, "Confirm your new password", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.getText().toString().equals(passwordChange.getText().toString())){
                    Toast.makeText(ChangePasswordActivity.this, "Make sure your confirmed password is same as the new one", Toast.LENGTH_SHORT).show();
                } else if (!robotCheck.isChecked()){
                    Toast.makeText(ChangePasswordActivity.this, "Confirm you are not a robot.", Toast.LENGTH_SHORT).show();
                } else {
//                    mAuth.updatePassword(newPassword.getText().toString());
                }
            }
        });
    }
}