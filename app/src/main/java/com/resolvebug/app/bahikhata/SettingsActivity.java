package com.resolvebug.app.bahikhata;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SettingsActivity extends AppCompatActivity {

    public AdView adView;
    private TextView pageTitle;
    private Switch darkThemeSwitch;
    private ImageView backButton;
    private CardView languageChangeCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initialize();
        setAdView();
        setTitleFont();
        pressBackButton();
        setTheme();
        openLanguageSupportFragment();
    }

    private void initialize() {
        adView = findViewById(R.id.adView);
        pageTitle = findViewById(R.id.pageTitle);
        darkThemeSwitch = findViewById(R.id.darkThemeSwitch);
        backButton = findViewById(R.id.backButton);
        languageChangeCard = findViewById(R.id.languageChangeCard);
        initializeSharedPreferences();
        initializeDarkThemeSwitch();
    }

    private void initializeSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String darkTheme = sharedPreferences.getString("darkTheme", "");
        if (darkTheme != null && darkTheme.equals("")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("darkTheme", "");
            editor.apply();
        }
    }

    private void setTitleFont() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Cookie-Regular.ttf");
        pageTitle.setTypeface(typeface);
    }

    private void setAdView() {
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

    private void pressBackButton() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initializeDarkThemeSwitch() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String darkTheme = sharedPreferences.getString("darkTheme", "");
        if (darkTheme != null && darkTheme.equals("true")) {
            darkThemeSwitch.setChecked(true);
        } else {
            darkThemeSwitch.setChecked(false);
        }
    }

    private void setTheme() {
        darkThemeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (darkThemeSwitch.isChecked()) {
                    editor.putString("darkTheme", "true");
                } else {
                    editor.putString("darkTheme", "false");
                }
                editor.apply();
            }
        });
    }

    private void openLanguageSupportFragment() {
        languageChangeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new LanguageSupportFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.settings_frame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

}
