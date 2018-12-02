package com.resolvebug.app.bahikhata;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class DebitsFragment extends Fragment implements DebitsRecyclerViewAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private DebitsRecyclerViewAdapter debitsRecyclerViewAdapter;
    List<CardItems> cardItemsList;
    private SQLiteDatabase mDatabase;

    public DebitsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_debits, container, false);
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
        debitsRecyclerViewAdapter = new DebitsRecyclerViewAdapter(getView().getContext(), cardItemsList, mDatabase, this);
        debitsRecyclerViewAdapter.setOnItemClickListener(new DebitsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openEditTransactionFragment(position, cardItemsList);
            }
        });
        recyclerView.setAdapter(debitsRecyclerViewAdapter);
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

    private void openEditTransactionFragment(int position, List<CardItems> cardItemsList) {
        EditTransactionFragment editTransactionFragment = new EditTransactionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("debitsTransactionId", cardItemsList.get(position).getTransactionId());
        editTransactionFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.transactionFrames, editTransactionFragment, "EditTransactionFragment").commit();
    }

    @Override
    public void onItemClick(int position) {
        openEditTransactionFragment(position, cardItemsList);
    }
}
