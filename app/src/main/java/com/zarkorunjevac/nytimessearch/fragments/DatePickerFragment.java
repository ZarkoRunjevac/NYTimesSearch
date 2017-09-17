package com.zarkorunjevac.nytimessearch.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.zarkorunjevac.nytimessearch.R;

import java.util.Date;

/**
 * Created by zarkorunjevac on 17/09/17.
 */

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE="date";
    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date){
        Bundle args=new Bundle();
        args.putSerializable(ARG_DATE,date);

        DatePickerFragment fragment=new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date,null);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(android.R.string.ok,null)
                .create();
    }
}
