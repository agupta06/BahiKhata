package com.resolvebug.app.bahikhata;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Main2Activity extends AppCompatActivity {

    private CardView incomeCard;
    private CardView expenditureCard;
    private TextView pageTitle;
    private SQLiteDatabase mDatabase;
    public static final String DATABASE_NAME = "bahikhatadatabase";
    public AdView adView;
    public TextView totalExpenditureAmount;
    public TextView totalIncomeAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initialize();
        setTitleFont();
        setAdView();
        performDBOperations();
        openIncomeActivity();
        openExpenditureActivity();
        setTotalIncomeAndExpenditure();
    }

    private void initialize() {
        incomeCard = findViewById(R.id.incomeCard);
        expenditureCard = findViewById(R.id.expenditureCard);
        pageTitle = findViewById(R.id.pageTitle);
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        adView = findViewById(R.id.adView);
        totalExpenditureAmount = findViewById(R.id.totalExpenditureAmount);
        totalIncomeAmount = findViewById(R.id.totalIncomeAmount);
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
                Intent intent = new Intent(Main2Activity.this, IncomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void openExpenditureActivity() {
        expenditureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, ExpenditureActivity.class);
                startActivity(intent);
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
            totalIncomeAmount.setText(Long.toString(totalDebitAmount.getLong(0)));
        } else {
            Toast.makeText(this, "Some error occurred.", Toast.LENGTH_SHORT).show();
        }
        totalDebitAmount.close();

        Cursor totalCreditAmount = mDatabase.rawQuery("SELECT SUM(AMOUNT) FROM TRANSACTION_DETAILS WHERE TYPE='Credit'", null);
        if (totalCreditAmount.moveToFirst()) {
            totalExpenditureAmount.setText(Long.toString(totalCreditAmount.getLong(0)));
        } else {
            Toast.makeText(this, "Some error occurred.", Toast.LENGTH_SHORT).show();
        }
        totalCreditAmount.close();
    }


}
