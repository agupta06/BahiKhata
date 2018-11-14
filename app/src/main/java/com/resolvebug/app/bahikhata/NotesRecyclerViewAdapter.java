package com.resolvebug.app.bahikhata;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.RecyclerViewHolder> {

    private Context context;
    private List<CardItems> cardItemsList;
    private OnItemClickListener mListener;
    private LinearLayout notesCardLayout;

    // Database
    SQLiteDatabase mDatabase;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public NotesRecyclerViewAdapter(Context context, List<CardItems> cardItemsList, SQLiteDatabase mDatabase) {
        this.context = context;
        this.cardItemsList = cardItemsList;
        this.mDatabase = mDatabase;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notes_cardview_layout, null);
        return new RecyclerViewHolder(view, mListener);
    }

    // "final" is added just because of image view. in case it creates any problem in future, remove image view and remove "final"
    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        final CardItems cardItems = cardItemsList.get(position);
        holder.itemAmount.setText(String.valueOf(cardItems.getAmount()));
        holder.itemMessage.setText(cardItems.getMessage());
        holder.item_date.setText(cardItems.getDate());
        setDefaultImportantTransaction(holder, cardItems);
        holder.important.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String importantTx = cardItems.getImportant();
                String importantTxId = cardItems.getTransactionId();
                if (importantTx.equals("0")) {
                    holder.important.setImageResource(R.drawable.ic_baseline_favorite_24px);
                    updateImportantTransaction("1", importantTxId);
                } else {
                    holder.important.setImageResource(R.drawable.ic_baseline_favorite_border_24px);
                    updateImportantTransaction("0", importantTxId);
                }
            }
        });
    }

    private void setDefaultImportantTransaction(RecyclerViewHolder holder, CardItems cardItems) {
        String importantTx = cardItems.getImportant();
        if (importantTx.equals("0")) {
            holder.important.setImageResource(R.drawable.ic_baseline_favorite_border_24px);
        } else {
            holder.important.setImageResource(R.drawable.ic_baseline_favorite_24px);
        }
    }

    private void updateImportantTransaction(String importantTransaction, String importantTxId) {
        String sql = "UPDATE TRANSACTION_DETAILS \n" +
                "SET IMPORTANT = ? \n" +
                "WHERE TRANSACTION_ID = ?;\n";
        mDatabase.execSQL(sql, new String[]{importantTransaction, importantTxId});
        reloadNotesTransactions();
    }

    private void reloadNotesTransactions() {
        Cursor allData = mDatabase.rawQuery("SELECT * FROM TRANSACTION_DETAILS WHERE TYPE='Notes'", null);
        if (allData.moveToFirst()) {
            cardItemsList.clear();
            do {
                cardItemsList.add(new CardItems(
                        allData.getString(1),
                        allData.getString(2),
                        allData.getString(3),
                        allData.getString(4),
                        allData.getString(5),
                        allData.getString(6),
                        allData.getString(7),
                        allData.getString(8)
                ));
            } while (allData.moveToNext());
        }
        allData.close();
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return cardItemsList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView itemAmount;
        TextView itemMessage;
        TextView item_date;
        ImageView important;

        public RecyclerViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemAmount = itemView.findViewById(R.id.item_amount);
            itemMessage = itemView.findViewById(R.id.item_message);
            item_date = itemView.findViewById(R.id.item_date);
            important = itemView.findViewById(R.id.important);
            notesCardLayout = itemView.findViewById(R.id.notes_linear_layout);
            notesCardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}
