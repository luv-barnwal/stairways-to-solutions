package com.luv.s2s;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity {

    ListView userListView;
    ArrayList<String> users = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String currentUser;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        userListView = findViewById(R.id.userListView);
        searchView = findViewById(R.id.searchView);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);

                intent.putExtra("username", users.get(i));
                intent.putExtra("current", currentUser);
                startActivity(intent);

            }
        });

        users.clear();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);

        userListView.setAdapter(arrayAdapter);

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String username = dataSnapshot.child("username").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();
                        if (!email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                            users.add(username);
                        } else {
                            currentUser = username;
                        }
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Filter.FilterListener listener = new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        if (!(count > 0)){
                            Toast.makeText(ChatListActivity.this, "There is no user with this username, please search again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                arrayAdapter.getFilter().filter(query, listener);
//                if (users.contains(query)){
//                } else {
//                    Toast.makeText(ChatListActivity.this, "There is no user with this username, please search again.", Toast.LENGTH_SHORT).show();
//                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Filter.FilterListener listener = new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        if (!(count > 0)){
                            Toast.makeText(ChatListActivity.this, "There is no user with this username, please search again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                arrayAdapter.getFilter().filter(newText, listener);
//                if (users.contains(newText)){
//                    arrayAdapter.getFilter().filter(newText);
//                } else {
//                    Toast.makeText(ChatListActivity.this, "There is no user with this username, please search again.", Toast.LENGTH_SHORT).show();
//                }
                return false;
            }
        });
    }
}