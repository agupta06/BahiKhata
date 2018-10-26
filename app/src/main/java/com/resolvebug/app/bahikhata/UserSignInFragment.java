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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserSignInFragment extends Fragment {

    private TextView register, forgotPassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Button signInButton;
    private EditText signInEmail, signInPassword;

    public UserSignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        container.removeAllViews();
        View view = inflater.inflate(R.layout.fragment_user_sign_in, container, false);
        setFirebaseAuthListener();
        initializeComponents(view);
        setOnButtonClickListeners();
        return view;
    }

    private void setOnButtonClickListeners() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationFragment();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserResetPasswordFragment();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void initializeComponents(View view) {
        register = view.findViewById(R.id.register);
        signInEmail = view.findViewById(R.id.signInEmail);
        signInPassword = view.findViewById(R.id.signInPassword);
        forgotPassword = view.findViewById(R.id.forgotPassword);
        signInButton = view.findViewById(R.id.signInButton);
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

    public void loginUser() {
        String userEmailId = signInEmail.getText().toString();
        String userPassword = signInPassword.getText().toString();

        if (TextUtils.isEmpty(userEmailId) || TextUtils.isEmpty(userPassword)) {
            Toast.makeText(getActivity(), R.string.error_empty_field, Toast.LENGTH_LONG).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(userEmailId, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        Toast.makeText(getActivity(), R.string.error_signin, Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

    private void openUserResetPasswordFragment() {
        UserResetPasswordFragment createAccountFragment = new UserResetPasswordFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.add(R.id.loginFrame, createAccountFragment, "RESET_USER_PASSWORD").commit();
    }

    private void openRegistrationFragment() {
        UserRegistrationFragment createAccountFragment = new UserRegistrationFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.add(R.id.loginFrame, createAccountFragment, "CREATE_NEW_ACCOUNT").commit();
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
