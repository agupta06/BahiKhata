package com.resolvebug.app.bahikhata;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserMobileLoginFragment extends Fragment {

    private Button phoneLoginButton;
    private EditText signInMobile;
    private TextInputLayout otpInput;

    public UserMobileLoginFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_user_mobile_login, container, false);
        initializeComponents(view);
        setOnButtonClickListeners();
        return view;
    }

    private void initializeComponents(View view) {
        phoneLoginButton = view.findViewById(R.id.phoneLoginButton);
        signInMobile = view.findViewById(R.id.signInMobile);
        otpInput = view.findViewById(R.id.otpInput);
    }

    private void setOnButtonClickListeners() {
        phoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otpInput.setVisibility(view.VISIBLE);
                otpInput.requestFocus();
                phoneLoginButton.setText("Sign In");
            }
        });
    }
}
