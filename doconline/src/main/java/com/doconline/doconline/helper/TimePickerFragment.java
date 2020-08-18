package com.doconline.doconline.helper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.fragment.app.DialogFragment;

/**
 * Created by chiranjit on 23/06/17.
 */

public class TimePickerFragment extends DialogFragment
{
    TimePickerDialog.OnTimeSetListener onTimeSet;
    private int hour, minute;

    public TimePickerFragment() {}


    public void setCallBack(TimePickerDialog.OnTimeSetListener onTime)
    {
        onTimeSet = onTime;
    }


    @SuppressLint("NewApi")
    @Override
    public void setArguments(Bundle args)
    {
        super.setArguments(args);

        hour = args.getInt("hour");
        minute = args.getInt("minute");
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        return new TimePickerDialog(getActivity(), onTimeSet, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }
}