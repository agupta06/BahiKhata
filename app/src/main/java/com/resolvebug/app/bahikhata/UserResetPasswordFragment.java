package com.resolvebug.app.bahikhata;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserResetPasswordFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private TextView signIn;
    private Button resetPasswordButton;
    private EditText resetPasswordEmail;

    public UserResetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        container.removeAllViews();
        View view = inflater.inflate(R.layout.fragment_user_reset_password, container, false);
        setFirebaseAuthListener();
        initializeComponents(view);
        setOnButtonClickListeners();
        return view;
    }

    private void setOnButtonClickListeners() {
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUserAccount();
            }
        });
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetUserPassword();
            }
        });
    }

    private void initializeComponents(View view) {
        signIn = view.findViewById(R.id.signinButton);
        resetPasswordButton = view.findViewById(R.id.resetPasswordButton);
        resetPasswordEmail = view.findViewById(R.id.resetPasswordEmail);
    }

    private void setFirebaseAuthListener() {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {    // existing user
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                }
            }
        };
    }

    private void signInUserAccount() {
        UserSignInFragment createAccountFragment = new UserSignInFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.add(R.id.loginFrame, createAccountFragment, "SIGNIN_USER_ACCOUNT").commit();
    }

    private void resetUserPassword() {
        String userEmailId = resetPasswordEmail.getText().toString();

        if (TextUtils.isEmpty(userEmailId)) {
            Toast.makeText(getActivity(), "Enter your email!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.sendPasswordResetEmail(userEmailId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Fail to send reset password email!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
