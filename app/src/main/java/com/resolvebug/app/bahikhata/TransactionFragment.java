package com.resolvebug.app.bahikhata;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionFragment extends Fragment {

    private RecyclerView recyclerView;
    private TransactionsRecyclerViewAdapter transactionsRecyclerViewAdapter;
    List<CardItems> cardItemsList;
    private SQLiteDatabase mDatabase;

    public TransactionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cardItemsList = new ArrayList<>();
        recyclerView = getView().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getView().getContext()));

        mDatabase = getActivity().openOrCreateDatabase(MainActivity.DATABASE_NAME, android.content.Context.MODE_PRIVATE, null);
        getAllDataFromDB();

        transactionsRecyclerViewAdapter = new TransactionsRecyclerViewAdapter(getView().getContext(), cardItemsList);
        recyclerView.setAdapter(transactionsRecyclerViewAdapter);
    }

    private void getAllDataFromDB() {
        //we used rawQuery(sql, selectionargs) for fetching all the employees
        Cursor allData = mDatabase.rawQuery("SELECT * FROM TRANSACTION_DETAILS", null);

        //if the cursor has some data
        if (allData.moveToFirst()) {
            //looping through all the records
            do {
                //pushing each record in the employee list
                cardItemsList.add(new CardItems(
                        allData.getString(4),
                        allData.getString(5)
                ));
            } while (allData.moveToNext());
        }
        //closing the cursor
        allData.close();
    }

}
