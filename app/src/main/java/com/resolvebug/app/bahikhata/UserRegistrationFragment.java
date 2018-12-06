package com.resolvebug.app.bahikhata;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserRegistrationFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private TextView signIn;
    private Button registerButton;
    private TextInputEditText registerEmail, registerPassword;


    public UserRegistrationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        container.removeAllViews();
        View view = inflater.inflate(R.layout.fragment_user_registration, container, false);
        setFirebaseAuthListener();
        initializeComponents(view);
        setOnButtonClickListeners();
        return view;
    }

    private void setFirebaseAuthListener() {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {    // existing user
                    startActivity(new Intent(getActivity(), MainActivityOld.class));
                    getActivity().finish();
                }
            }
        };
    }

    private void initializeComponents(View view) {
        registerEmail = view.findViewById(R.id.registerEmail);
        registerEmail.requestFocus();
        registerPassword = view.findViewById(R.id.registerPassword);
        signIn = view.findViewById(R.id.signinButton);
        registerButton = view.findViewById(R.id.registerButton);
    }

    private void setOnButtonClickListeners() {
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUserAccount();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }

    private void signInUserAccount() {
        UserSignInFragment createAccountFragment = new UserSignInFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_left);
        transaction.add(R.id.loginFrame, createAccountFragment, "SIGNIN_USER_ACCOUNT").commit();
    }

    private void registerNewUser() {
        String userEmailId = registerEmail.getText().toString();
        String userPassword = registerPassword.getText().toString();

        if (TextUtils.isEmpty(userEmailId) || TextUtils.isEmpty(userPassword)) {
            Toast.makeText(getActivity(), R.string.error_empty_field, Toast.LENGTH_LONG).show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(userEmailId, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), R.string.success_registration, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), R.string.fail_registration, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
