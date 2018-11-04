package com.resolvebug.app.bahikhata;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserMobileLoginFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private Button otpRequestButton;
    private EditText userPhoneNumber;
    private EditText signInOtp;
    private TextInputLayout oneTimePassword;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    public UserMobileLoginFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_mobile_login, container, false);
        initializeComponents(view);
        setOnOtpButtonClickListeners();
        return view;
    }

    private void initializeComponents(View view) {
        otpRequestButton = view.findViewById(R.id.otpRequestButton);
        signInOtp = view.findViewById(R.id.signInOtp);
        userPhoneNumber = view.findViewById(R.id.userPhoneNumber);
        userPhoneNumber.requestFocus();
        oneTimePassword = view.findViewById(R.id.oneTimePassword);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void setOnOtpButtonClickListeners() {
        otpRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobileNumber = userPhoneNumber.getText().toString().trim();
                String otp = signInOtp.getText().toString().trim();
                if (!mobileNumber.equals("")) {
                    oneTimePassword.setVisibility(view.VISIBLE);
                    oneTimePassword.requestFocus();
                    otpRequestButton.setText("Sign In");
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            mobileNumber,
                            60,
                            TimeUnit.SECONDS,
                            getActivity(),
                            callBacks
                    );
                }
                if(!otp.equals("")){
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        callBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

}
