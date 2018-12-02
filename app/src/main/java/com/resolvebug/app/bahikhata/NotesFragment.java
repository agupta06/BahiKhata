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

public class NotesFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotesRecyclerViewAdapter notesRecyclerViewAdapter;
    private List<CardItems> cardItemsList;
    private SQLiteDatabase mDatabase;

    public NotesFragment() {

    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cardItemsList = new ArrayList<>();
        recyclerView = getView().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getView().getContext()));

        mDatabase = getActivity().openOrCreateDatabase(MainActivityOld.DATABASE_NAME, android.content.Context.MODE_PRIVATE, null);
        getAllDataFromDB();

        notesRecyclerViewAdapter = new NotesRecyclerViewAdapter(getView().getContext(), cardItemsList, mDatabase);
        notesRecyclerViewAdapter.setOnItemClickListener(new NotesRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //openEditTransactionFragment(position,cardItemsList);
            }
        });
        recyclerView.setAdapter(notesRecyclerViewAdapter);

    }

    private void getAllDataFromDB() {
        //we used rawQuery(sql, selectionargs) for fetching all the employees
        Cursor allData = mDatabase.rawQuery("SELECT * FROM TRANSACTION_DETAILS WHERE TYPE='Notes' ORDER BY TRANSACTION_ID DESC", null);

        //if the cursor has some data
        if (allData.moveToFirst()) {
            //looping through all the records
            do {
                //pushing each record in the employee list
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
        //closing the cursor
        allData.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    private void openEditTransactionFragment(int position, List<CardItems> cardItemsList) {
        EditTransactionFragment editTransactionFragment = new EditTransactionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("notesTransactionId", cardItemsList.get(position).getTransactionId());
        editTransactionFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.transactionFrames, editTransactionFragment, "EditTransactionFragment").commit();
    }

}
