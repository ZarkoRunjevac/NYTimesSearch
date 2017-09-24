package com.zarkorunjevac.nytimessearch.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.zarkorunjevac.nytimessearch.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zarkorunjevac on 17/09/17.
 */

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE="date";
    public static final String EXTRA_DATE="com.zarkorunjevac.nytimessearch.fragments.DATE";


    @BindView(R.id.dpDate)  DatePicker dpDate;
    private Unbinder unbinder;

    public static DatePickerFragment newInstance(Date date){
        Bundle args=new Bundle();
        args.putSerializable(ARG_DATE,date);

        DatePickerFragment fragment=new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {



        Date date=(Date)getArguments().getSerializable(ARG_DATE);

        Calendar calendar=Calendar.getInstance();

        if(date!=null){
            calendar.setTime(date);
        }
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date,null);
        unbinder= ButterKnife.bind(this,view);

        dpDate.init(year, month,day,null);

        return new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int year=dpDate.getYear();
                        int month=dpDate.getMonth();
                        int day=dpDate.getDayOfMonth();
                        Date date=new GregorianCalendar(year,month,day).getTime();
                        sendResult(Activity.RESULT_OK,date);
                    }
                })
                .create();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void sendResult(int resultCode, Date date){
        if(getTargetFragment()==null){
            return;
        }
        Intent intent=new Intent();

        intent.putExtra(EXTRA_DATE,date);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
