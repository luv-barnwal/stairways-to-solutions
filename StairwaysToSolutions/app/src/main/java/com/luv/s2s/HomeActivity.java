package com.luv.s2s;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView navView;
//    Button sub1, sub2, sub3, sub4, sub5, sub6, sub7, sub8, sub9, sub10, sub11, sub12;
//    List<String> subjects;

    ImageView connections, store, questions, rewards, competitions, knowUs, leaderboard, winners;
    TextView points;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private AdView mAdView;
    String username;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        connections = findViewById(R.id.connections);
        store = findViewById(R.id.store);
        questions = findViewById(R.id.questions);
        leaderboard = findViewById(R.id.leaderboard);
        rewards = findViewById(R.id.rewards);
        competitions = findViewById(R.id.competitions);
        knowUs = findViewById(R.id.knowUs);
        winners = findViewById(R.id.winners);
        points = findViewById(R.id.points);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent intent = getIntent();
        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    points.setText(snapshot.child("points").getValue().toString());
                    username = snapshot.child("username").getValue().toString();
                    Bundle extras = intent.getExtras();

                    if (extras != null) {
                        if (intent.getStringExtra("login").equals("first")) {
                            databaseReference.child("extras").child(username).child("membership").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        if (snapshot.getValue().toString().equals("Basic")) {
                                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("coins").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    int coin = Integer.parseInt(snapshot.getValue().toString()) + 5;
                                                    snapshot.getRef().setValue(coin);
                                                    Toast.makeText(HomeActivity.this, "Congratulations! You earnt 5 coins for logging in!", Toast.LENGTH_SHORT).show();
                                                    mAdView.setVisibility(View.INVISIBLE);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        } else if (snapshot.getValue().toString().equals("Basic")) {
                                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("coins").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    int coin = Integer.parseInt(snapshot.getValue().toString()) + 10;
                                                    snapshot.getRef().setValue(coin);
                                                    Toast.makeText(HomeActivity.this, "Congratulations! You earnt 10 coins for logging in!", Toast.LENGTH_SHORT).show();
                                                    mAdView.setVisibility(View.INVISIBLE);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                    Intent intent1 = new Intent(HomeActivity.this, NewsActivity.class);
                                    startActivity(intent1);
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
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        questions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, QuestionsActivity.class);
                startActivity(intent);
            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, StoreActivity.class);
                startActivity(intent);
            }
        });

        winners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, WinnersActivity.class);
                startActivity(intent);
            }
        });

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });

        connections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ChatListActivity.class);
                startActivity(intent);
            }
        });

        knowUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, KnowUsActivity.class);
                startActivity(intent);
            }
        });

        competitions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CompetitionsActivity.class);
                startActivity(intent);
            }
        });

        rewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, RewardsActivity.class);
                startActivity(intent);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()){

                        case R.id.navigation_home :
                            Intent mainIntent = new Intent(HomeActivity.this, HomeActivity.class);
                            startActivity(mainIntent);
                            break;

                        case R.id.navigation_settings :
                            Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                            startActivity(settingsIntent);
                            Log.i("Settings", "Success!");
                            break;

                        case R.id.navigation_notifications :
                            Intent notificationsIntent = new Intent(HomeActivity.this, NotificationsActivity.class);
                            startActivity(notificationsIntent);
                            Log.i("Notifications", "Success");
                            break;

                        case R.id.navigation_logout :

                            new AlertDialog.Builder(HomeActivity.this)
                                    .setTitle("Are you sure you want to log out?")
                                    .setMessage("This will take you to the home screen")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            for(UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()){

                                                if(user.getProviderId().equals("google.com")){
                                                    FirebaseAuth.getInstance().signOut();
                                                    mGoogleSignInClient.signOut().addOnCompleteListener(HomeActivity.this,
                                                            new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Intent logOutIntent = new Intent(HomeActivity.this, DashBoardActivity.class);
                                                                    startActivity(logOutIntent);
                                                                    finish();
                                                                }
                                                            });
                                                } else {
                                                    FirebaseAuth.getInstance().signOut();
                                                    Intent logOutIntent = new Intent(HomeActivity.this, DashBoardActivity.class);
                                                    startActivity(logOutIntent);
                                                    finish();
                                                }

                                            }


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
