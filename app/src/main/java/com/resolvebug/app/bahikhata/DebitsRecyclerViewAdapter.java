package com.resolvebug.app.bahikhata;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DebitsRecyclerViewAdapter extends RecyclerView.Adapter<DebitsRecyclerViewAdapter.RecyclerViewHolder> {

    private Context context;
    private List<CardItems> cardItemsList;
    private LinearLayout transactionCardlayout;
    private DebitsRecyclerViewAdapter.OnItemClickListener mListener;
    private ArrayList<Integer> selectedItems = new ArrayList<>();
    private boolean multiSelect = false;
    private ActionMode mMode;
    private SQLiteDatabase mDatabase;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(DebitsRecyclerViewAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public DebitsRecyclerViewAdapter(Context context, List<CardItems> cardItemsList, SQLiteDatabase mDatabase, OnItemClickListener mListener) {
        this.context = context;
        this.cardItemsList = cardItemsList;
        this.mDatabase = mDatabase;
        this.mListener = mListener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.transaction_card_layout, null);
        return new RecyclerViewHolder(view, mListener);
    }

    // "final" is added just because of image view. in case it creates any problem in future, remove image view and remove "final"
    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        final CardItems cardItems = cardItemsList.get(position);
        double amount = cardItems.getAmount();
        String totalAmount = new DecimalFormat("##,##,##0.00").format(amount);
        holder.itemAmount.setText(totalAmount);
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
        holder.update(position);
    }

    private void setDefaultImportantTransaction(DebitsRecyclerViewAdapter.RecyclerViewHolder holder, CardItems cardItems) {
        if (cardItems.getImportant().equals("0")) {
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
        reloadDebitsTransactions();
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
        OnItemClickListener listener;
        LinearLayout transactionCardlayout;

        public RecyclerViewHolder(View itemView, final DebitsRecyclerViewAdapter.OnItemClickListener listener) {
            super(itemView);
            itemAmount = itemView.findViewById(R.id.item_amount);
            itemMessage = itemView.findViewById(R.id.item_message);
            important = itemView.findViewById(R.id.important);
            item_date = itemView.findViewById(R.id.item_date);
            transactionCardlayout = itemView.findViewById(R.id.transaction_linear_layout);
            transactionCardlayout.setOnClickListener(new View.OnClickListener() {
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
            this.listener = listener;
        }

        void update(final Integer value) {
            if (selectedItems.contains(value)) {
                transactionCardlayout.setBackgroundColor(Color.LTGRAY);
            } else {
                transactionCardlayout.setBackgroundColor(Color.WHITE);
            }
            transactionCardlayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem(value);
                    return true;
                }
            });
            transactionCardlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!multiSelect) {
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(position);
                            }
                        }
                    }
                    selectItem(value);
                }
            });
        }

        void selectItem(Integer item) {
            if (multiSelect) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item);
                    transactionCardlayout.setBackgroundColor(Color.WHITE);
                } else {
                    selectedItems.add(item);
                    transactionCardlayout.setBackgroundColor(Color.LTGRAY);
                }
            }
            if (selectedItems.size() == 0) {
                if (mMode != null) {
                    mMode.finish();
                }
            }
            if (selectedItems != null && selectedItems.size() > 0) {
                mMode.setTitle(selectedItems.size() + " items selected");
            }
        }
    }

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mMode = mode;
            mode.getMenuInflater().inflate(R.menu.contextual_action_bar_menu, menu);
            multiSelect = true;
//            menu.add("Delete");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.deleteItems:
                    for (Integer intItem : selectedItems) {
                        String sql = "DELETE FROM TRANSACTION_DETAILS WHERE TYPE='Debit' AND TRANSACTION_ID = ?";
                        mDatabase.execSQL(sql, new String[]{cardItemsList.get(intItem).getTransactionId()});
                    }
                    reloadDebitsTransactions();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };

    private void reloadDebitsTransactions() {
        Cursor allData = mDatabase.rawQuery("SELECT * FROM TRANSACTION_DETAILS WHERE TYPE='Debit' ORDER BY TRANSACTION_ID DESC", null);
        cardItemsList.clear();
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
        notifyDataSetChanged();
    }
}
