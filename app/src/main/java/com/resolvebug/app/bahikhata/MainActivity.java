package com.resolvebug.app.bahikhata;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);

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

    private void setFirebaseAuthAndListener() {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {    // user logged out
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        };
    }

    private void googleLogout() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
            }
        });
    }
}
