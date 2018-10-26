package com.resolvebug.app.bahikhata;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseCrud extends AppCompatActivity {

    private Button buttonSave;
    private EditText editText;
    private DatabaseReference databaseReference;
    private ListView listViewUsers;
    private List<User> users;
    public static String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_crud);

        users = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        buttonSave = findViewById(R.id.buttonSave);
        editText = findViewById(R.id.editText);
        listViewUsers = findViewById(R.id.listView);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                if (TextUtils.isEmpty(userId)) { // save
                    String id = databaseReference.push().getKey();
                    User user = new User(id, name);
                    databaseReference.child(id).setValue(user);
                    Toast.makeText(getApplicationContext(), "user created successfully", Toast.LENGTH_LONG).show();
                } else { // update
                    databaseReference.child(userId).child("name").setValue(name);
                    Toast.makeText(getApplicationContext(), "user updated successfully", Toast.LENGTH_LONG).show();
                    userId = "";
                }
                editText.setText(null);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    users.add(user);
                }

                UsersList userAdapter = new UsersList(FirebaseCrud.this, users, databaseReference, editText);
                listViewUsers.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
