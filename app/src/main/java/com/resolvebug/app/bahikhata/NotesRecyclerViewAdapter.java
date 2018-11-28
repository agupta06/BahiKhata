package com.resolvebug.app.bahikhata;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.RecyclerViewHolder> {

    private Context context;
    private List<CardItems> cardItemsList;
    private OnItemClickListener mListener;
    private LinearLayout notesCardLayout;
    private SQLiteDatabase mDatabase;
    private ArrayList<Integer> selectedItems = new ArrayList<>();
    private boolean multiSelect = false;
    private ActionMode mMode;

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
        int layout = R.layout.notes_cardview_layout_without_date;
        View view = inflater.inflate(layout, null);
        return new RecyclerViewHolder(view, mListener);
    }

    // "final" is added just because of image view. in case it creates any problem in future, remove image view and remove "final"
    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        final CardItems cardItems = cardItemsList.get(position);
        double amount =  cardItems.getAmount();
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
        holder.cardMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, holder.cardMenu);
                popupMenu.inflate(R.menu.options_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.deleteItem:

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Are you sure?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String sql = "DELETE FROM TRANSACTION_DETAILS WHERE TYPE='Debit' AND TRANSACTION_ID = ?";
                                        mDatabase.execSQL(sql, new String[]{cardItemsList.get(position).getTransactionId()});
                                        reloadNotesTransactions();
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();

                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        holder.update(position);
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


    @Override
    public int getItemCount() {
        return cardItemsList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView itemAmount;
        TextView itemMessage;
        TextView item_date;
        ImageView important;
        TextView cardMenu;

        public RecyclerViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemAmount = itemView.findViewById(R.id.item_amount);
            itemMessage = itemView.findViewById(R.id.item_message);
            item_date = itemView.findViewById(R.id.item_date);
            important = itemView.findViewById(R.id.important);
            cardMenu = itemView.findViewById(R.id.cardMenu);
            notesCardLayout = itemView.findViewById(R.id.notes_linear_layout);
        }

        void update(final Integer value) {
            if (selectedItems.contains(value)) {
                notesCardLayout.setBackgroundColor(Color.LTGRAY);
            } else {
                notesCardLayout.setBackgroundColor(Color.WHITE);
            }
            notesCardLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem(value);
                    return true;
                }
            });
            notesCardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectItem(value);
                }
            });
        }

        void selectItem(Integer item) {
            if (multiSelect) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item);
                    notesCardLayout.setBackgroundColor(Color.WHITE);
                } else {
                    selectedItems.add(item);
                    notesCardLayout.setBackgroundColor(Color.LTGRAY);
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
                    reloadNotesTransactions();
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

    public void openEditTransactionFragment(int position) {
        AppCompatActivity activity = (AppCompatActivity) context;
        EditTransactionFragment editTransactionFragment = new EditTransactionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("notesTxId", cardItemsList.get(position).getTransactionId());
        bundle.putDouble("notesTxAmount", cardItemsList.get(position).getAmount());
        bundle.putString("notesTxMessage", cardItemsList.get(position).getMessage());
        bundle.putString("notesTxType", cardItemsList.get(position).getType());
        bundle.putString("notesTxImportant", cardItemsList.get(position).getImportant());
        bundle.putString("notesTxDate", cardItemsList.get(position).getDate());
        bundle.putString("notesTxTime", cardItemsList.get(position).getTime());
        editTransactionFragment.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.transactionFrames, editTransactionFragment).addToBackStack(null).commit();
    }

}
