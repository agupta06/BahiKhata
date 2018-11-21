package com.resolvebug.app.bahikhata;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.util.Calendar;

public class RecordDateSelectorDialog extends AppCompatDialogFragment {

    private TextInputEditText toDateSelected;
    private TextInputEditText fromDateSelected;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.record_date_selector_dialog, null);
        initialize(view);
        initializeDatePickerFragment();
        return createDatePickerDialog(view);
    }

    private void initialize(View view) {
        toDateSelected = view.findViewById(R.id.toDateSelected);
        fromDateSelected = view.findViewById(R.id.fromDateSelected);
    }

    private Dialog createDatePickerDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle("Select Dates")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    private void initializeDatePickerFragment() {
        toDateSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String currentDate = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
                                toDateSelected.setText(currentDate);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
                dpd.show();
            }
        });

        fromDateSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String currentDate = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
                                fromDateSelected.setText(currentDate);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
                dpd.show();
            }
        });
    }

}
