package com.resolvebug.app.bahikhata;

import android.app.KeyguardManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class TrialMainActivity extends AppCompatActivity {

    // Literals
    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;
    private FrameLayout transactionFrames;

    // TextView
    private TextView pageTitle;

    // Floating Action Button
    private FloatingActionButton addTransactionButton;

    // ViewPager
    private ViewPager viewPager;

    // AdView
    private AdView adView;

    // TabLayout
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // main
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_trial);
        initializeId();
        setTitleFont();
        setAdView();
        setupViewPager(viewPager);
        openCreateTransactionFragment();

//        authenticateApp();
    }

    private void initializeId() {
        pageTitle = findViewById(R.id.pageTitle);
        addTransactionButton = findViewById(R.id.addTransactionButton);
        viewPager = findViewById(R.id.viewPager);
        adView = findViewById(R.id.adView);
        tabLayout = findViewById(R.id.tabLayout);
    }

    private void setTitleFont() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Cookie-Regular.ttf");
        pageTitle.setTypeface(typeface);
    }

    private void setAdView() {
        final AdView adView = findViewById(R.id.adView);
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

    private void openCreateTransactionFragment() {
        addTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTransaction();
            }
        });
    }

    private void createTransaction() {
        FragmentManager fm = getSupportFragmentManager();
        CreateTransactionFragment fragment = new CreateTransactionFragment();
        fm.beginTransaction().add(R.id.transactionFrames, fragment).commit();
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new NotesFragment(), "NOTES");
        adapter.addFragment(new CreditsFragment(), "CREDITS");
        adapter.addFragment(new DebitsFragment(), "DEBITS");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    //    method to authenticate app
//    private void authenticateApp() {
//        //Get the instance of KeyGuardManager
//        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//
//        //Check if the device version is greater than or equal to Lollipop(21)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            //Create an intent to open device screen lock screen to authenticate
//            //Pass the Screen Lock screen Title and Description
//            Intent i = keyguardManager.createConfirmDeviceCredentialIntent(getResources().getString(R.string.app_name), getResources().getString(R.string.confirm_pattern_message));
//            try {
//                //Start activity for result
//                startActivityForResult(i, LOCK_REQUEST_CODE);
//            } catch (Exception e) {
//
//                //If some exception occurs means Screen lock is not set up please set screen lock
//                //Open Security screen directly to enable patter lock
//                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
//                try {
//
//                    //Start activity for result
//                    startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
//                } catch (Exception ex) {
//
//                    //If app is unable to find any Security settings then user has to set screen lock manually
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case LOCK_REQUEST_CODE:
//                if (resultCode == RESULT_OK) {
//                    //If screen lock authentication is success update text
//                } else {
//                    //If screen lock authentication is failed update text
//                }
//                break;
//            case SECURITY_SETTING_REQUEST_CODE:
//                //When user is enabled Security settings then we don't get any kind of RESULT_OK
//                //So we need to check whether device has enabled screen lock or not
//                if (isDeviceSecure()) {
//                    //If screen lock enabled show toast and start intent to authenticate user
//                    authenticateApp();
//                } else {
//                    //If screen lock is not enabled just update text
//                }
//
//                break;
//        }
//    }
//
//    /**
//     * method to return whether device has screen lock enabled or not
//     **/
//    private boolean isDeviceSecure() {
//        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//
//        //this method only work whose api level is greater than or equal to Jelly_Bean (16)
//        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure();
//
//        //You can also use keyguardManager.isDeviceSecure(); but it requires API Level 23
//
//    }
}



