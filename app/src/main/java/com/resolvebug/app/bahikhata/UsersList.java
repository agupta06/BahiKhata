package com.resolvebug.app.bahikhata;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class UsersList extends ArrayAdapter<User> {

    private Activity context;
    private List<User> users;
    private DatabaseReference databaseReference;
    private EditText editText;

    public UsersList(@NonNull Activity context, List<User> users, DatabaseReference databaseReference, EditText editText) {
        super(context, R.layout.user_list_layout, users);
        this.context = context;
        this.users = users;
        this.databaseReference = databaseReference;
        this.editText = editText;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View listItemView = layoutInflater.inflate(R.layout.user_list_layout, null, true);

        TextView textView = listItemView.findViewById(R.id.textName);
        Button buttonUpdate = listItemView.findViewById(R.id.buttonUpdate);
        Button buttonDelete = listItemView.findViewById(R.id.buttonDelete);

        final User user = users.get(position);
        textView.setText(user.getName());

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(user.getId()).removeValue();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(user.getName());
                FirebaseCrud.userId = user.getId();
            }
        });

        return listItemView;
    }
}
