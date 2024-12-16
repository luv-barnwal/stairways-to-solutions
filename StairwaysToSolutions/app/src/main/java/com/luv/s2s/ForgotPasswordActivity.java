package com.luv.s2s;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText getNumberEditText;
//    Button check;

//    int generatedNumber;
    String email = "";
    private static final String TAG = "Password Reset";


//    protected void sendEmail() {
//
//        Random random = new Random();
//        generatedNumber = random.nextInt(10000 - 1001) + 1001;
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    GMailSender sender = new GMailSender("stairwaystosolutions@gmail.com",
//                            "s2s@1234");
//                    sender.sendMail("Code for Changing Password in Stairways to Solutions", "Your Code is " + generatedNumber +
//                                    "\nThis is only one-time use.",
//                            "stairwaystosolutions@gmail.com", email);
//                } catch (Exception e) {
//                    Log.e("SendMail", e.getMessage(), e);
//                }
//            }
//        }).start();
//    }
//
//
//    public void send(View view){
//
//        sendEmail();
//        check.setVisibility(View.VISIBLE);
//
//    }
//
//    public void check(View view){
//
//        if (getNumberEditText.getText().toString().equals(Integer.toString(generatedNumber))){
//
//            Toast.makeText(this, "Correct Number given!", Toast.LENGTH_SHORT).show();
//
//            Intent intent = new Intent(this, ChangePasswordActivity.class);
//            intent.putExtra("email", email);
//            startActivity(intent);
//
//        } else {
//
//            Toast.makeText(this, "Check the Number", Toast.LENGTH_SHORT).show();
//
//        }
//
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        getNumberEditText = findViewById(R.id.getNumberEditText);
        FirebaseAuth auth = FirebaseAuth.getInstance();
//        check = findViewById(R.id.questions);

        Intent intent = getIntent();

        email = intent.getStringExtra("email");

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });

    }
}