package com.resolvebug.app.bahikhata;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddTransactionActivity extends AppCompatActivity {

    public AdView adView;
    private TextView pageTitle;
    private SQLiteDatabase mDatabase;
    public static final String DATABASE_NAME = "bahikhatadatabase";
    private TextInputEditText transactionAmount;
    private TextInputEditText transactionMessage;
    private Button saveTransactionButton;
    private ImageView backButton;
    private String txDate;
    private String txTime;
    private String txTimeZone;
    private String txId;
    private TextInputLayout inputTxMessage;
    private TextInputLayout inputTxAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        initialize();
        setAdView();
        setTitleFont();
        saveTransaction();
        pressBackButton();
    }

    private void initialize() {
        adView = findViewById(R.id.adView);
        pageTitle = findViewById(R.id.pageTitle);
        transactionAmount = findViewById(R.id.transactionAmount);
        transactionAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(10, 2)});
        transactionMessage = findViewById(R.id.transactionMessage);
        saveTransactionButton = findViewById(R.id.saveTransactionButton);
        backButton = findViewById(R.id.backButton);
        inputTxMessage = findViewById(R.id.inputTxMessage);
        inputTxAmount = findViewById(R.id.inputTxAmount);
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
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

    private void saveTransaction() {
        saveTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentDateTime();
                addTransaction();
            }
        });
    }

    private void setCurrentDateTime() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        txDate = new SimpleDateFormat("dd-MM-yyyy").format(timestamp);
        txTime = new SimpleDateFormat("HH:mm:ss.SSS").format(timestamp);
        TimeZone timeZone = TimeZone.getDefault();
        txTimeZone = timeZone.getDisplayName(false, TimeZone.SHORT);
        txId = getTransactionId();
    }

    private String getTransactionId() {
        FormatDateTime formatDateTime = new FormatDateTime();
        return formatDateTime.getTimeStamp();
    }

    private void addTransaction() {
        Intent intent = getIntent();
        String txType = intent.getStringExtra("addTransactionButton");
        if (transactionAmount.getText().toString().equals("") && transactionMessage.getText().toString().equals("")) {
            inputTxAmount.setError("Please enter some amount");
            inputTxMessage.setError("Please enter some message");
        } else if (transactionAmount.getText().toString().equals("")) {
            inputTxAmount.setError("Please enter some amount");
        } else if (transactionMessage.getText().toString().equals("")) {
            inputTxMessage.setError("Please enter some message");
        } else {
            String txAmount = transactionAmount.getText().toString().trim();
            String txMessage = transactionMessage.getText().toString().trim();
            if (inputsAreCorrect(Double.valueOf(txAmount), txMessage)) {
                String insertSQL = "INSERT INTO TRANSACTION_DETAILS \n" +
                        "(TRANSACTION_ID,DATE, TIME, TIME_ZONE, TYPE, AMOUNT, MESSAGE)\n" +
                        "VALUES \n" +
                        "(?, ?, ?, ?, ?, ?, ?);";
                mDatabase.execSQL(insertSQL, new String[]{txId, txDate, txTime, txTimeZone, txType, txAmount, txMessage});
                Toast.makeText(this, "Transaction Added Successfully", Toast.LENGTH_SHORT).show();
                resetInputs();
            }
        }
    }

    private void resetInputs() {
        transactionAmount.requestFocus();
        transactionAmount.setText(R.string.EMPTY_STRING);
        transactionMessage.setText(R.string.EMPTY_STRING);
    }

    private boolean inputsAreCorrect(Double txAmount, String txMessage) {
        if (txAmount <= 0) {
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

    private void pressBackButton() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }
}
