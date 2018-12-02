package com.resolvebug.app.bahikhata;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private CardView incomeCard;
    private CardView expenditureCard;
    private SQLiteDatabase mDatabase;
    public static final String DATABASE_NAME = "bahikhatadatabase";
    public TextView totalExpenditureAmount;
    public TextView totalIncomeAmount;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initialize(view);
        performDBOperations();
        openIncomeFragment();
        openExpenditureFragment();
        setTotalIncomeAndExpenditure();

        return view;
    }

    private void initialize(View view) {
        incomeCard = view.findViewById(R.id.incomeCard);
        expenditureCard = view.findViewById(R.id.expenditureCard);
        mDatabase = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        totalExpenditureAmount = view.findViewById(R.id.totalExpenditureAmount);
        totalIncomeAmount = view.findViewById(R.id.totalIncomeAmount);
    }

    private void openIncomeFragment() {
        incomeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new IncomeFragment());
            }
        });
    }

    private void openExpenditureFragment() {
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
            Toast.makeText(getContext(), "Some error occurred.", Toast.LENGTH_SHORT).show();
        }
        totalDebitAmount.close();

        Cursor totalCreditAmount = mDatabase.rawQuery("SELECT SUM(AMOUNT) FROM TRANSACTION_DETAILS WHERE TYPE='Credit'", null);
        if (totalCreditAmount.moveToFirst()) {
            double total = totalCreditAmount.getDouble(0);
            String amount = new DecimalFormat("##,##,##0.00").format(total);
            totalExpenditureAmount.setText(amount);
        } else {
            Toast.makeText(getContext(), "Some error occurred.", Toast.LENGTH_SHORT).show();
        }
        totalCreditAmount.close();
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        setTotalIncomeAndExpenditure();
        //Refresh your stuff here
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
