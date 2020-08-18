package com.doconline.doconline.helper;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
{

    DatePickerDialog.OnDateSetListener ondateSet;
    private int year, min_year, max_year, month, day;

    public DatePickerFragment() {}


    public void setCallBack(DatePickerDialog.OnDateSetListener ondate)
    {
        ondateSet = ondate;
    }


    @SuppressLint("NewApi")
    @Override
    public void setArguments(Bundle args)
    {
        super.setArguments(args);

        year = args.getInt("year");
        max_year = args.getInt("max_year");
        min_year = args.getInt("min_year");
        month = args.getInt("month");
        day = args.getInt("day");
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), ondateSet, year, month, day);

        //int mYear    = calendar.get(Calendar.YEAR) - 18;
        //int mMonth   = calendar.get(Calendar.MONTH);
        //int mDay     = calendar.get(Calendar.DAY_OF_MONTH);

        if(max_year != 0)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.set(max_year, month, day);
            dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        }

        if(min_year != 0)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.set(min_year, month, day);
            dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        }

        return dialog;
    }
}