package com.resolvebug.app.bahikhata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class TrialMainActivity extends AppCompatActivity {

    // Literals
    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;

    // TextView
    private TextView pageTitle;

    // Button
    private Button addTransactionButton;

    // ViewPager
    private ViewPager viewPager;

    // AdView
    private AdView adView;

    // TabLayout
    private TabLayout tabLayout;

    // Database
    private SQLiteDatabase mDatabase;
    public static final String DATABASE_NAME = "bahikhatadatabase";

    // Network Connection
    private BroadcastReceiver mNetworkReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // main
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_trial);

        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();

        initializeId();
        setTitleFont();
        setAdView();
        setupViewPager(viewPager);
        openCreateTransactionFragment();
        performDBOperations();

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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.replace(R.id.transactionFrames, new CreateTransactionFragment());
        transaction.commit();
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new NotesFragment(), "NOTES");
        adapter.addFragment(new CreditsFragment(), "CREDITS");
        adapter.addFragment(new DebitsFragment(), "DEBITS");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void performDBOperations() {
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS TRANSACTION_DETAILS (\n" +
                "    ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    TRANSACTION_ID LONG NOT NULL,\n" +
                "    DATE VARCHAR(10) NOT NULL,\n" +
                "    TIME VARCHAR(10) NOT NULL,\n" +
                "    TIME_ZONE VARCHAR(10) NOT NULL,\n" +
                "    TYPE VARCHAR(200) NOT NULL, \n" +
                "    AMOUNT VARCHAR(30) NOT NULL,\n" +
                "    MESSAGE VARCHAR(200) NOT NULL,\n" +
                "    IMPORTANT VARCHAR(2) NOT NULL DEFAULT '0');";

//      DROP A TABLE
//        String sql = "DROP TABLE IF EXISTS TRANSACTION_DETAILS";

//      ADD A NEW COLUMN
//        String sql = "ALTER TABLE TRANSACTION_DETAILS ADD COLUMN TRANSACTION_ID VARCHAR(20) DEFAULT 'GMT+05:30'";
//        String sql = "ALTER TABLE TRANSACTION_DETAILS ADD COLUMN TRANSACTION_ID VARCHAR(20)";

        mDatabase.execSQL(sql);
    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void dialog(boolean value, Context context) {
        if (value) {
            Toast.makeText(context, "Internet Connection successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Internet Connection failed", Toast.LENGTH_SHORT).show();
        }
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



