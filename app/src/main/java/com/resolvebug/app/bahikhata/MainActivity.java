package com.resolvebug.app.bahikhata;

import android.app.DatePickerDialog;
import android.app.KeyguardManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    // TextView
    private TextView appName;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    // Button
    private Button homeButton;
    private Button saveButton;
    private Button logoutButton;

    // EditText
    private EditText transactionAmount;
    private EditText transactionMessage;

    // Literals
    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;
    public static final String DATABASE_NAME = "bahikhatadatabase";

    // Spinners
    private Spinner transactionTypes;

    // Database
    SQLiteDatabase mDatabase;

    // Others
    //private FloatingActionButton fab, fab1, fab2, fab3, fab4;
    //private Animation fabOpen, fabClose, mainFabOpen;
    //private boolean isOpen = false;
    //private TextView fabSelectedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);
        userAuthentication();
        initialize();
        setTitleFont();
        setAdView();

        // create a new table or update an existing database
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        createTable();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTransaction();
            }
        });


        initializeDatePickerFragment();
        setHomeButton();
        setUserLogoutButton();


//        fab = findViewById(R.id.main_fab);
//        fab1 = findViewById(R.id.main_fab1);
//        fab2 = findViewById(R.id.main_fab2);
//        fab3 = findViewById(R.id.main_fab3);
//        fab4 = findViewById(R.id.main_fab4);
//        mainFabOpen = AnimationUtils.loadAnimation(this, R.anim.main_fab_open);
//        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
//        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
//
//        fab.startAnimation(mainFabOpen);

//        fabSelectedText = findViewById(R.id.fab_selected_text);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                animateFab();
//            }
//        });
//
//        fab1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                animateFab1();
//            }
//        });
//
//        fab2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                animateFab2();
//            }
//        });
//
//        fab3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                animateFab3();
//            }
//        });
//
//        fab4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private void initialize() {
        appName = findViewById(R.id.main_toolbar_title);
        homeButton = findViewById(R.id.homeButton);
        logoutButton = findViewById(R.id.logoutButton);
        transactionAmount = findViewById(R.id.transactionAmount);
        transactionMessage = findViewById(R.id.transactionMessage);
        saveButton = findViewById(R.id.saveButton);
        firebaseAuth = FirebaseAuth.getInstance();
        transactionTypes = findViewById(R.id.transactionType);
    }

    private void setHomeButton() {
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setTitleFont() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Cookie-Regular.ttf");
        appName.setTypeface(typeface);
    }

    private void setUserLogoutButton() {
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

    private void setAdView() {
        final AdView adView = findViewById(R.id.main_adView);
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

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.this.finish();
    }


    private void initializeDatePickerFragment() {
        ImageView calendarImage = findViewById(R.id.calendarImage);
        calendarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        TextView calendarDatePicker = findViewById(R.id.calendarDatePicker);
        calendarDatePicker.setText(currentDate);
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        String currentTime = DateFormat.getTimeInstance(DateFormat.FULL).format(calendar.getTime());
        TextView calendarTimePicker = findViewById(R.id.calendarTimePicker);
        calendarTimePicker.setText(currentTime);
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS TransactionDetails (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    txAmount DOUBLE NOT NULL,\n" +
                "    txMessage VARCHAR(200) NOT NULL,\n" +
                "    txDate DATETIME NOT NULL,\n" +
                "    txType VARCHAR(200) NOT NULL );";

//        String sql = "DROP TABLE IF EXISTS TransactionDetails";

        mDatabase.execSQL(sql);
    }

    private void addTransaction() {
        String txAmount = transactionAmount.getText().toString().trim();
        String txMessage = transactionMessage.getText().toString().trim();
        String txType = transactionTypes.getSelectedItem().toString();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String txDate = sdf.format(cal.getTime());

        if (inputsAreCorrect(txAmount, txMessage)) {

            String insertSQL = "INSERT INTO TransactionDetails \n" +
                    "(txAmount, txMessage, txDate, txType)\n" +
                    "VALUES \n" +
                    "(?, ?, ?, ?);";
            mDatabase.execSQL(insertSQL, new String[]{txAmount, txMessage, txDate, txType});
            Toast.makeText(this, "Transaction Added Successfully", Toast.LENGTH_SHORT).show();
            resetInputs();
        }
    }

    private void resetInputs() {
        transactionAmount.requestFocus();
        transactionAmount.setText(R.string.EMPTY_STRING);
        transactionMessage.setText(R.string.EMPTY_STRING);
    }

    private boolean inputsAreCorrect(String txAmount, String txMessage) {
        if (txAmount.isEmpty() || Integer.parseInt(txAmount) <= 0) {
            transactionAmount.requestFocus();
            Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (txMessage.isEmpty()) {
            transactionMessage.requestFocus();
            Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

//        private void animateFab() {
//        if (isOpen) {
//            closeFabAnimation();
//            fab.setImageResource(R.drawable.ic_touch_app_black_24dp);
//            fabSelectedText.setText(getResources().getString(R.string.TAP_TO_SELECT));
//        } else {
//            startFabAnimation();
//            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
//            fab.setImageResource(R.drawable.ic_clear_black_24dp);
//            fabSelectedText.setText(getResources().getString(R.string.EMPTY_STRING));
//        }
//    }
//
//    private void animateFab1() {
//        if (isOpen) {
//            closeFabAnimation();
//            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed600)));
//            fab.setImageResource(R.drawable.ic_remove_white_24dp);
//            fabSelectedText.setText(getResources().getString(R.string.DEBIT_AMOUNT));
//        }
//    }
//
//    private void animateFab2() {
//        if (isOpen) {
//            closeFabAnimation();
//            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen600)));
//            fab.setImageResource(R.drawable.ic_add_white_24dp);
//            fabSelectedText.setText(getResources().getString(R.string.CREDIT_AMOUNT));
//        }
//    }
//
//    private void animateFab3() {
//        if (isOpen) {
//            closeFabAnimation();
//            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBlue600)));
//            fab.setImageResource(R.drawable.ic_insert_comment_white_24dp);
//            fabSelectedText.setText(getResources().getString(R.string.MAKE_A_NOTE));
//        }
//    }
//
//    private void startFabAnimation() {
//        fab1.startAnimation(fabOpen);
//        fab2.startAnimation(fabOpen);
//        fab3.startAnimation(fabOpen);
//        fab1.setClickable(true);
//        fab2.setClickable(true);
//        fab3.setClickable(true);
//        isOpen = true;
//    }
//
//    private void closeFabAnimation() {
//        fab1.startAnimation(fabClose);
//        fab2.startAnimation(fabClose);
//        fab3.startAnimation(fabClose);
//        fab1.setClickable(false);
//        fab2.setClickable(false);
//        fab3.setClickable(false);
//        isOpen = false;
//    }
}
