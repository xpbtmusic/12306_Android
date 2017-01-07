package com.akari.tickets.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import com.akari.tickets.R;
import com.akari.tickets.utils.DateUtil;



public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private TextView chooseDate;
    private TextView chooseDate2;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        chooseDate = (TextView) getActivity().findViewById(R.id.choose_date);
        chooseDate2 = (TextView) getActivity().findViewById(R.id.choose_date2);

        String date = chooseDate.getText().toString();
        int year = Integer.parseInt(date.split("-")[0]);
        int month = Integer.parseInt(date.split("-")[1]);
        int day = Integer.parseInt(date.split("-")[2]);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), this, year, month - 1, day);
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (long) 29 * 24 * 60 * 60 * 1000);

        return dialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String dateStr = DateUtil.getDateStr(year, month, day);
        chooseDate.setText(dateStr);
        chooseDate2.setText("");
    }

}
