package com.resolvebug.app.bahikhata;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ExpenditureActivity extends AppCompatActivity {

    public AdView adView;
    private TextView pageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        initialize();
        setAdView();
        setTitleFont();
//        showIncomeTransactions();
    }

    private void initialize() {
        adView = findViewById(R.id.adView);
        pageTitle = findViewById(R.id.pageTitle);
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

//    private void showIncomeTransactions() {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.incomeTransactions, new NotesFragment());
//        transaction.commit();
//    }
}
