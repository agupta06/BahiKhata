package com.resolvebug.app.bahikhata;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private AdView adView;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    // Google Login
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient googleApiClient;

    // Email or Phone Login
    private ImageButton loginMethodImage;
    private TextView loginMethodText;

    // Facebook Login
    private CallbackManager mCallbackManager;
    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setAppName();
        setFirebaseAuthListener();
        setLoginMethods();
        setActionOnFacebookLogin();
        setAdsView();
        loginMethodImage = findViewById(R.id.loginMethodImage);
        loginMethodText = findViewById(R.id.loginMethodText);
        setUserSignInFragment();
    }

    private void setUserSignInFragment() {
        final GlobalOperations globalOperations = (GlobalOperations) getApplication();
        defaultLoginFragment(globalOperations);
        loginMethodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (globalOperations.isPhoneLogin()) {
                    mobileLoginFragment(globalOperations);
                } else {
                    emailLoginFragment(globalOperations);
                }
            }
        });
    }

    private void defaultLoginFragment(GlobalOperations globalOperations) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loginFrame, new UserSignInFragment());
        transaction.commit();
        globalOperations.setPhoneLogin(true);
        loginMethodText.setText(R.string.PHONE);
        loginMethodImage.setImageResource(R.drawable.ic_smartphone);
    }

    private void mobileLoginFragment(GlobalOperations globalOperations) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.replace(R.id.loginFrame, new UserMobileLoginFragment());
        transaction.commit();
        globalOperations.setPhoneLogin(false);
        loginMethodText.setText(R.string.EMAIL);
        loginMethodImage.setImageResource(R.drawable.ic_email);
    }

    private void emailLoginFragment(GlobalOperations globalOperations) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_left);
        transaction.replace(R.id.loginFrame, new UserSignInFragment());
        transaction.commit();
        globalOperations.setPhoneLogin(true);
        loginMethodText.setText(R.string.PHONE);
        loginMethodImage.setImageResource(R.drawable.ic_smartphone);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null)
                    firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, R.string.fail_google_signin, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void setAdsView() {
        adView = findViewById(R.id.loginAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int error) {
                adView.setVisibility(View.GONE);
            }
        });
    }

    private void setAppName() {
        TextView appName;
        appName = findViewById(R.id.main_toolbar_title);
        Typeface typeface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.font_app_name));
        appName.setTypeface(typeface);
    }

    private void setLoginMethods() {
        setGoogleLogin();
        setFacebookLogin();
    }

    // Google Login
    private void setGoogleLogin() {
        ImageButton googleSignInButton;
        googleSignInButton = findViewById(R.id.googleLogin);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(LoginActivity.this, R.string.ERROR, Toast.LENGTH_LONG).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.fail_authentication, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void setFirebaseAuthListener() {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {    // existing user
                    startMainActivity();
                }
            }
        };
    }

    private void startMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    // Facebook Login
    private void setFacebookLogin() {
        checkFacebookUserRegistrationStatus();
        setFacebookButtonOnClickListener();
    }

    private void checkFacebookUserRegistrationStatus() {
        FirebaseUser firebaseUser;
        firebaseUser = firebaseAuth.getCurrentUser();
        mCallbackManager = CallbackManager.Factory.create();
        if (firebaseUser != null) {     // existing user
            startMainActivity();
        }
    }

    private void setFacebookButtonOnClickListener() {
        ImageButton facebookLoginButton;
        facebookLoginButton = findViewById(R.id.facebookLoginButton);
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList(EMAIL, "public_profile", "user_friends"));
            }
        });
    }

    public void setActionOnFacebookLogin() {
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, R.string.CANCELLED, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, R.string.exception_facebook, Toast.LENGTH_LONG).show();

            }
        });
    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.fail_firebase_registration, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}