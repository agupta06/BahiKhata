package com.resolvebug.app.bahikhata;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class IncomeActivity extends AppCompatActivity implements DebitsRecyclerViewAdapter.OnItemClickListener {

    public AdView adView;
    private TextView pageTitle;
    private List<CardItems> cardItemsList;
    private SQLiteDatabase mDatabase;
    public static final String DATABASE_NAME = "bahikhatadatabase";
    private RecyclerView recyclerView;
    private Button addCreditsButton;
    private TextView totalIncomeAmount;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        initialize();
        setAdView();
        setTitleFont();
        showIncomeTransactions();
        addNewTransaction();
        setTotalIncomeAndExpenditure();
        pressBackButton();
    }

    private void initialize() {
        adView = findViewById(R.id.adView);
        pageTitle = findViewById(R.id.pageTitle);
        recyclerView = findViewById(R.id.recyclerView);
        addCreditsButton = findViewById(R.id.addCreditsButton);
        totalIncomeAmount = findViewById(R.id.totalIncomeAmount);
        backButton = findViewById(R.id.backButton);
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

    private void showIncomeTransactions() {
        DebitsRecyclerViewAdapter debitsRecyclerViewAdapter;
        cardItemsList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        debitsRecyclerViewAdapter = new DebitsRecyclerViewAdapter(this, cardItemsList, mDatabase, this);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(debitsRecyclerViewAdapter);
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        getAllDataFromDB();
    }

    private void getAllDataFromDB() {
        Cursor allData = mDatabase.rawQuery("SELECT * FROM TRANSACTION_DETAILS WHERE TYPE='Debit' ORDER BY TRANSACTION_ID DESC", null);
        if (allData.moveToFirst()) {
            do {
                cardItemsList.add(new CardItems(
                        allData.getString(1),
                        allData.getString(2),
                        allData.getString(3),
                        allData.getString(4),
                        allData.getString(5),
                        allData.getDouble(6),
                        allData.getString(7),
                        allData.getString(8)
                ));
            } while (allData.moveToNext());
        }
        allData.close();
    }

    private void addNewTransaction() {
        addCreditsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IncomeActivity.this, AddTransactionActivity.class);
                intent.putExtra("addTransactionButton", "Debit");
                startActivity(intent);
            }
        });
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
    }

    private void pressBackButton() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        showIncomeTransactions();
        setTotalIncomeAndExpenditure();
        //Refresh your stuff here
    }

    @Override
    public void onItemClick(int position) {

    }
}
