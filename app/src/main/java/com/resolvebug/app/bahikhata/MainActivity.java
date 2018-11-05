package com.resolvebug.app.bahikhata;

import android.app.KeyguardManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab, fab1, fab2, fab3, fab4;
    private Animation fabOpen, fabClose, mainFabOpen;
    private boolean isOpen = false;

    private AdView adView;

    private TextView appName, fabSelectedText;

    private ImageButton imageButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Button logoutButton;

    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);
        userAuthentication();

        fab = findViewById(R.id.main_fab);
        fab1 = findViewById(R.id.main_fab1);
        fab2 = findViewById(R.id.main_fab2);
        fab3 = findViewById(R.id.main_fab3);
        fab4 = findViewById(R.id.main_fab4);
        logoutButton = findViewById(R.id.logoutButton);

        mainFabOpen = AnimationUtils.loadAnimation(this, R.anim.main_fab_open);
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        fab.startAnimation(mainFabOpen);

        fabSelectedText = findViewById(R.id.fab_selected_text);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab1();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab2();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab3();
            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        adView = findViewById(R.id.main_adView);
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

        appName = findViewById(R.id.main_toolbar_title);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Cookie-Regular.ttf");
        appName.setTypeface(typeface);

        imageButton = findViewById(R.id.home_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {    // user logged out
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
            }
        });
    }

    private void userAuthentication() {
        //Get the instance of KeyGuardManager
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        //Check if the device version is greater than or equal to Lollipop(21)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Create an intent to open device screen lock screen to authenticate
            //Pass the Screen Lock screen Title and Description
            Intent i = keyguardManager.createConfirmDeviceCredentialIntent(getResources().getString(R.string.unlock_app), getResources().getString(R.string.confirm_pattern_message));
            try {
                //Start activity for result
                startActivityForResult(i, LOCK_REQUEST_CODE);
            } catch (Exception e) {

                //If some exception occurs means Screen lock is not set up please set screen lock
                //Open Security screen directly to enable patter lock
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                try {

                    //Start activity for result
                    startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
                } catch (Exception ex) {

                    //If app is unable to find any Security settings then user has to set screen lock manually
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOCK_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    //If screen lock authentication is success update text
                } else {
                    //If screen lock authentication is failed update text
                }
                break;
            case SECURITY_SETTING_REQUEST_CODE:
                //When user is enabled Security settings then we don't get any kind of RESULT_OK
                //So we need to check whether device has enabled screen lock or not
                if (isDeviceSecure()) {
                    //If screen lock enabled show toast and start intent to authenticate user
                    //authenticateApp();
                } else {
                    //If screen lock is not enabled just update text
                }

                break;
        }
    }

    /**
     * method to return whether device has screen lock enabled or not
     **/
    private boolean isDeviceSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        //this method only work whose api level is greater than or equal to Jelly_Bean (16)
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure();

        //You can also use keyguardManager.isDeviceSecure(); but it requires API Level 23

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void animateFab() {
        if (isOpen) {
            closeFabAnimation();
            fab.setImageResource(R.drawable.ic_touch_app_black_24dp);
            fabSelectedText.setText(getResources().getString(R.string.TAP_TO_SELECT));
        } else {
            startFabAnimation();
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            fab.setImageResource(R.drawable.ic_clear_black_24dp);
            fabSelectedText.setText(getResources().getString(R.string.EMPTY_STRING));
        }
    }

    private void animateFab1() {
        if (isOpen) {
            closeFabAnimation();
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed600)));
            fab.setImageResource(R.drawable.ic_remove_white_24dp);
            fabSelectedText.setText(getResources().getString(R.string.DEBIT_AMOUNT));
        }
    }

    private void animateFab2() {
        if (isOpen) {
            closeFabAnimation();
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen600)));
            fab.setImageResource(R.drawable.ic_add_white_24dp);
            fabSelectedText.setText(getResources().getString(R.string.CREDIT_AMOUNT));
        }
    }

    private void animateFab3() {
        if (isOpen) {
            closeFabAnimation();
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBlue600)));
            fab.setImageResource(R.drawable.ic_insert_comment_white_24dp);
            fabSelectedText.setText(getResources().getString(R.string.MAKE_A_NOTE));
        }
    }

    private void startFabAnimation() {
        fab1.startAnimation(fabOpen);
        fab2.startAnimation(fabOpen);
        fab3.startAnimation(fabOpen);
        fab1.setClickable(true);
        fab2.setClickable(true);
        fab3.setClickable(true);
        isOpen = true;
    }

    private void closeFabAnimation() {
        fab1.startAnimation(fabClose);
        fab2.startAnimation(fabClose);
        fab3.startAnimation(fabClose);
        fab1.setClickable(false);
        fab2.setClickable(false);
        fab3.setClickable(false);
        isOpen = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.this.finish();
    }
}
