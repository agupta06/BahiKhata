package com.resolvebug.app.bahikhata;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class CreateTransactionFragment extends Fragment {

    // TextView
    private TextView pageTitle;

    // ImageView
    private ImageView closeFragment;

    // AdView
    private AdView adView;

    // EditText
    private TextInputEditText transactionAmount;
    private TextInputEditText transactionMessage;

    // Spinners
    private Spinner transactionTypes;

    // Button
    private Button saveButton;

    // Database
    private SQLiteDatabase mDatabase;
    public static final String DATABASE_NAME = "bahikhatadatabase";

    // Variables
    private String txDate;
    private String txTime;
    private String txTimeZone;
    private String txId;

    public CreateTransactionFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_transaction, container, false);

        initializeId(view);
        setTitleFont();
        setAdView(view);
        closeCreateTransactionFragment();
        saveTransaction();

        return view;
    }

    private void initializeId(View view) {
        pageTitle = view.findViewById(R.id.pageTitle);
        closeFragment = view.findViewById(R.id.closeFragment);
        adView = view.findViewById(R.id.adView);
        transactionAmount = view.findViewById(R.id.transactionAmount);
        transactionMessage = view.findViewById(R.id.transactionMessage);
        transactionTypes = view.findViewById(R.id.transactionType);
        saveButton = view.findViewById(R.id.saveButton);
        if (getActivity() != null) {
            mDatabase = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        }
    }

    private void setTitleFont() {
        if (getActivity() != null) {
            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cookie-Regular.ttf");
            pageTitle.setTypeface(typeface);
        }
    }

    private void setAdView(View view) {
        adView = view.findViewById(R.id.adView);
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

    private void closeCreateTransactionFragment() {
        closeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAllTransactionFragment();
            }
        });
    }

    private void openAllTransactionFragment() {
        Intent intent = new Intent(getActivity(), TrialMainActivity.class);
        if (getActivity() != null) {
            getActivity().startActivity(intent);
        }
    }

    private void saveTransaction() {
        saveButton.setOnClickListener(new View.OnClickListener() {
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

    private void addTransaction() {
        if (transactionAmount.getText() != null && transactionMessage.getText() != null) {
            String txAmount = transactionAmount.getText().toString().trim();
            String txMessage = transactionMessage.getText().toString().trim();
            String txType = transactionTypes.getSelectedItem().toString();
            if (inputsAreCorrect(Double.valueOf(txAmount), txMessage)) {
                String insertSQL = "INSERT INTO TRANSACTION_DETAILS \n" +
                        "(TRANSACTION_ID,DATE, TIME, TIME_ZONE, TYPE, AMOUNT, MESSAGE)\n" +
                        "VALUES \n" +
                        "(?, ?, ?, ?, ?, ?, ?);";
                mDatabase.execSQL(insertSQL, new String[]{txId, txDate, txTime, txTimeZone, txType, txAmount, txMessage});
                Toast.makeText(getActivity(), "Transaction Added Successfully", Toast.LENGTH_SHORT).show();
                resetInputs();
            }
        } else {
            Toast.makeText(getActivity(), "Field(s) are empty", Toast.LENGTH_SHORT).show();

        }
    }

    private String getTransactionId() {
        FormatDateTime formatDateTime = new FormatDateTime();
        return formatDateTime.getTimeStamp();
    }

    private void resetInputs() {
        transactionAmount.requestFocus();
        transactionAmount.setText(R.string.EMPTY_STRING);
        transactionMessage.setText(R.string.EMPTY_STRING);
    }

    private boolean inputsAreCorrect(Double txAmount, String txMessage) {
        if (txAmount <= 0) {
            transactionAmount.requestFocus();
            Toast.makeText(getActivity(), "Fields are empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (txMessage.isEmpty()) {
            transactionMessage.requestFocus();
            Toast.makeText(getActivity(), "Fields are empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
