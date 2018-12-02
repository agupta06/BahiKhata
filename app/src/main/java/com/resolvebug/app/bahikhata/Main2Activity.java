package com.resolvebug.app.bahikhata;

import android.app.KeyguardManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;

public class Main2Activity extends AppCompatActivity {

    private CardView incomeCard;
    private CardView expenditureCard;
    private TextView pageTitle;
    private SQLiteDatabase mDatabase;
    public static final String DATABASE_NAME = "bahikhatadatabase";
    public AdView adView;
    public TextView totalExpenditureAmount;
    public TextView totalIncomeAmount;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        userAuthentication();
        initialize();
        setTitleFont();
        setAdView();
        setSupportActionBar(toolbar);
        setActionBar();
        navigationDrawerListener();
        performDBOperations();
        openIncomeActivity();
        openExpenditureActivity();
        setTotalIncomeAndExpenditure();
        setNavigationLayout();
        appLogout();
    }

    private void initialize() {
        incomeCard = findViewById(R.id.incomeCard);
        expenditureCard = findViewById(R.id.expenditureCard);
        pageTitle = findViewById(R.id.pageTitle);
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        adView = findViewById(R.id.adView);
        totalExpenditureAmount = findViewById(R.id.totalExpenditureAmount);
        totalIncomeAmount = findViewById(R.id.totalIncomeAmount);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
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

    private void openIncomeActivity() {
        incomeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new IncomeFragment());
            }
        });
    }

    private void openExpenditureActivity() {
        expenditureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new ExpenseFragment());
            }
        });
    }

    private void performDBOperations() {
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
                "    AMOUNT NUMERIC NOT NULL,\n" +
                "    MESSAGE VARCHAR(200) NOT NULL,\n" +
                "    IMPORTANT INTEGER NOT NULL DEFAULT 0);";

//      DROP A TABLE
//        String sql = "DROP TABLE IF EXISTS TRANSACTION_DETAILS";

//      ADD A NEW COLUMN
//        String sql = "ALTER TABLE TRANSACTION_DETAILS ADD COLUMN TRANSACTION_ID VARCHAR(20) DEFAULT 'GMT+05:30'";
//        String sql = "ALTER TABLE TRANSACTION_DETAILS ADD COLUMN TRANSACTION_ID VARCHAR(20)";

        mDatabase.execSQL(sql);
    }

    private void setTotalIncomeAndExpenditure() {
        Cursor totalDebitAmount = mDatabase.rawQuery("SELECT SUM(AMOUNT) FROM TRANSACTION_DETAILS WHERE TYPE='Debit'", null);
        if (totalDebitAmount.moveToFirst()) {
            double total = totalDebitAmount.getDouble(0);
            String amount = new DecimalFormat("##,##,##0.00").format(total);
            totalIncomeAmount.setText(amount);
        } else {
            Toast.makeText(this, "Some error occurred.", Toast.LENGTH_SHORT).show();
        }
        totalDebitAmount.close();

        Cursor totalCreditAmount = mDatabase.rawQuery("SELECT SUM(AMOUNT) FROM TRANSACTION_DETAILS WHERE TYPE='Credit'", null);
        if (totalCreditAmount.moveToFirst()) {
            double total = totalCreditAmount.getDouble(0);
            String amount = new DecimalFormat("##,##,##0.00").format(total);
            totalExpenditureAmount.setText(amount);
        } else {
            Toast.makeText(this, "Some error occurred.", Toast.LENGTH_SHORT).show();
        }
        totalCreditAmount.close();
    }

    private void setNavigationLayout() {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
//                        menuItem.setChecked(true);
                        displayView(menuItem.getTitle().toString());
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigationDrawerListener() {
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );
    }

    private void setActionBar() {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    public void displayView(String viewId) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (viewId) {
            case "Important":
                fragment = new EditTransactionFragment();
                title = "Events";
                break;
            case "Settings":
                Intent intent = new Intent(Main2Activity.this, SettingsActivity.class);
                startActivity(intent);
                title = "Settings";
                break;
            case "Rate Us":
                rateAppOnPlayStore();
                break;
            case "Share":
                shareAppWithFriends();
                break;
            case "About Us":
                fragment = new EditTransactionFragment();
                title = "Events";
                break;
            case "Logout":
                firebaseAuth.signOut();
                break;
            default:
                break;

        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.replaceMainActivityFrame, fragment);
            fragmentTransaction.commit();
        }
        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void appLogout() {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {    // user logged out
                    startActivity(new Intent(Main2Activity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    private void rateAppOnPlayStore() {
        try {   // open play store app
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException ex) {    // open play store in browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void shareAppWithFriends() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Bahi Khata - Money Tracking App");
        share.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=" + getPackageName());
        startActivity(Intent.createChooser(share, "Share Bahi Khata with friends"));
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        setTotalIncomeAndExpenditure();
        //Refresh your stuff here
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void userAuthentication() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent i = keyguardManager.createConfirmDeviceCredentialIntent(getResources().getString(R.string.unlock_app), getResources().getString(R.string.confirm_pattern_message));
            try {
                startActivityForResult(i, LOCK_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                try {
                    startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
                } catch (Exception ex) {
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

    private boolean isDeviceSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure();
    }
}
