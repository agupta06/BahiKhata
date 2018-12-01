package com.resolvebug.app.bahikhata;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.CompoundButton;
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
    private TextView currentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setDefaultAppTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initialize();
        setAdView();
        setTitleFont();
        pressBackButton();
        setTheme();
        openLanguageSupportFragment();
        setNightMode();
    }

    private void initialize() {
        adView = findViewById(R.id.adView);
        pageTitle = findViewById(R.id.pageTitle);
        darkThemeSwitch = findViewById(R.id.darkThemeSwitch);
        backButton = findViewById(R.id.backButton);
        languageChangeCard = findViewById(R.id.languageChangeCard);
        currentLanguage = findViewById(R.id.current_language);
        initializeSharedPreferences();
//        initializeDarkThemeSwitch();
        setCurrentLanguage();
    }

    private void setCurrentLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = sharedPreferences.getString("App_Language", "");
        switch (language) {
            case "en":
                currentLanguage.setText(getString(R.string.ENGLISH));
                break;
            case "hi":
                currentLanguage.setText(getString(R.string.HINDI));
                break;
            case "mr":
                currentLanguage.setText(getString(R.string.MARATHI));
                break;
            default:
                currentLanguage.setText(getString(R.string.ENGLISH));
                break;

        }
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

    private void setDefaultAppTheme() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkAppTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
    }

    private void setNightMode() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            darkThemeSwitch.setChecked(true);
        }
        darkThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    restartApp();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    restartApp();
                }
            }
        });
    }

    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
        finish();
    }

}
