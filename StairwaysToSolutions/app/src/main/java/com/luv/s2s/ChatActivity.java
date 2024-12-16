package com.luv.s2s;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    String activeUser;
    ArrayList<String> messages = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    TextView name;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String currentUser;
    int counter;
    Button trade;

    public void sendTrade(View view){
        Intent intent = new Intent(ChatActivity.this, TradeActivity.class);
        intent.putExtra("current", currentUser);
        intent.putExtra("username", activeUser);
        startActivity(intent);
    }

    public void sendChat(View view){

        final EditText chatEditText = findViewById(R.id.chatEditText);

        int compare = currentUser.compareTo(activeUser);
        String title = "";
        if (compare < 0){
            title = currentUser + " + " + activeUser;
        } else if (compare > 0){
            title = activeUser + " + " + currentUser;
        }

        final String messageContent = chatEditText.getText().toString();

        Map<String, Object> message = new HashMap<>();
        message.put("sender", currentUser);
        message.put("message", messageContent);

        int value = counter + 1;

        databaseReference.child("chats").child(title).child(value + "").updateChildren(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    chatEditText.setText("");
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        trade = findViewById(R.id.tradeConnections);

        Intent intent = getIntent();

        activeUser = intent.getStringExtra("username");
        currentUser = intent.getStringExtra("current");

        name = findViewById(R.id.name);

        name.setText("Chat with " + activeUser);

        ListView chatListView = findViewById(R.id.chatLstView);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);

        chatListView.setAdapter(arrayAdapter);

        databaseReference.child("extras").child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("coinsToTrades").getValue().equals(true)){
                        databaseReference.child("extras").child(activeUser).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    if (snapshot.child("coinsToTrades").getValue().equals(true)){
                                        trade.setVisibility(View.VISIBLE);
                                    } else {
                                        trade.setVisibility(View.GONE);
                                        Toast.makeText(ChatActivity.this, "The person whom you are chatting with hasn't bought trades yet. Message them to buy it, if you want to trade with them.", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    trade.setVisibility(View.GONE);
                                    Toast.makeText(ChatActivity.this, "The person whom you are chatting with hasn't bought trades yet. Message them to buy it, if you want to trade with them.", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        trade.setVisibility(View.GONE);
                        Toast.makeText(ChatActivity.this, "Sorry! You haven't bought trades yet. Go To Rewards to be able to trade with other users.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    trade.setVisibility(View.GONE);
                    Toast.makeText(ChatActivity.this, "Sorry! You haven't bought trades yet. Go To Rewards to be able to trade with other users.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        int compare = currentUser.compareTo(activeUser);
        String title = "";
        if (compare < 0){
            title = currentUser + " + " + activeUser;
        } else if (compare > 0){
            title = activeUser + " + " + currentUser;
        }

        databaseReference.child("chats").child(title).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    messages.clear();

                    counter = (int) snapshot.getChildrenCount();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                        String messageContent = dataSnapshot.child("message").getValue().toString();
                        String sender = dataSnapshot.child("sender").getValue().toString();

                        if (!sender.equals(currentUser)){
                            messageContent = "> " + messageContent;
                        }

                        messages.add(messageContent);
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}