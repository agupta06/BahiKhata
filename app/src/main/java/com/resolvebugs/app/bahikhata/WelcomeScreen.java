package com.resolvebugs.app.bahikhata;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeScreen extends AppCompatActivity {

    private LinearLayout upToDown, downToUp;
    private Animation animUpToDown, animDownToUp;
    private TextView appName;
    private static int SPLASH_TIME_OUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        setAnimation();
        setAppName();
        boolean isNetworkAvailable = isNetworkAvailable();
        if (isNetworkAvailable) {
            setSplashScreen();
        } else {
            Toast.makeText(getApplicationContext(), "No Connection", Toast.LENGTH_LONG).show();
        }
    }

    private void setAnimation() {
        upToDown = findViewById(R.id.upToDown);
        downToUp = findViewById(R.id.downToUp);
        animUpToDown = AnimationUtils.loadAnimation(this, R.anim.welcome_screen_uptodown);
        animDownToUp = AnimationUtils.loadAnimation(this, R.anim.welcome_screen_downtoup);
        upToDown.setAnimation(animUpToDown);
        downToUp.setAnimation(animDownToUp);
    }

    private void setSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeScreen.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void setAppName() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.font_app_name));
        appName = findViewById(R.id.app_name);
        appName.setTypeface(typeface);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
