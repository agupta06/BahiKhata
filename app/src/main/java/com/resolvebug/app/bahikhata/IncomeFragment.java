package com.resolvebug.app.bahikhata;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class IncomeFragment extends Fragment implements DebitsRecyclerViewAdapter.OnItemClickListener {

    public AdView adView;
    private TextView pageTitle;
    private List<CardItems> cardItemsList;
    private SQLiteDatabase mDatabase;
    public static final String DATABASE_NAME = "bahikhatadatabase";
    private RecyclerView recyclerView;
    private Button addIncomeButton;
    private TextView totalIncomeAmount;
    private ImageView backButton;

    public IncomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_income, container, false);
        initialize(view);
        setAdView();
        setTitleFont();
        showIncomeTransactions();
        addNewTransaction();
        setTotalIncomeAndExpenditure();
        pressBackButton();
        return view;
    }

    private void initialize(View view) {
        adView = view.findViewById(R.id.adView);
        pageTitle = view.findViewById(R.id.pageTitle);
        recyclerView = view.findViewById(R.id.recyclerView);
        addIncomeButton = view.findViewById(R.id.addIncomeButton);
        totalIncomeAmount = view.findViewById(R.id.totalIncomeAmount);
        backButton = view.findViewById(R.id.backButton);
    }

    private void setTitleFont() {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cookie-Regular.ttf");
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        debitsRecyclerViewAdapter = new DebitsRecyclerViewAdapter(getContext(), cardItemsList, mDatabase, this);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(debitsRecyclerViewAdapter);
        mDatabase = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
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
        addIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddTransactionFragment();
            }
        });
    }

    private void openAddTransactionFragment() {
        Fragment fragment = new AddTransactionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("txType", "Debit");
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setTotalIncomeAndExpenditure() {
        Cursor totalDebitAmount = mDatabase.rawQuery("SELECT SUM(AMOUNT) FROM TRANSACTION_DETAILS WHERE TYPE='Debit'", null);
        if (totalDebitAmount.moveToFirst()) {
            double total = totalDebitAmount.getDouble(0);
            String amount = new DecimalFormat("##,##,##0.00").format(total);
            totalIncomeAmount.setText(amount);
        } else {
            Toast.makeText(getActivity(), "Some error occurred.", Toast.LENGTH_SHORT).show();
        }
        totalDebitAmount.close();
    }

    private void pressBackButton() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
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
