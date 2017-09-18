package com.zarkorunjevac.nytimessearch.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.zarkorunjevac.nytimessearch.R;
import com.zarkorunjevac.nytimessearch.utils.Constants;
import com.zarkorunjevac.nytimessearch.utils.Utils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zarkorunjevac on 17/09/17.
 */

public class SearchFiltersFragment extends DialogFragment {

    private static final int REQUEST_DATE=0;
    @BindView(R.id.etDate)EditText tvBeginDate;
    @BindView(R.id.spSortOrder)Spinner spSortOrder;
    @BindView(R.id.cbArts)    CheckBox cbArts;
    @BindView(R.id.cbSports) CheckBox cbSports;
    @BindView(R.id.cbPolitics) CheckBox cbPolitics;
    @BindView(R.id.btnSave)    Button btnSave;
    @BindView(R.id.btnCancel) Button btnCancel;
    private Unbinder unbinder;

    public static SearchFiltersFragment newInstance(){
        return new SearchFiltersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.SearchFilterDialog);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_filter,container,false);
        unbinder= ButterKnife.bind(this,view);
        tvBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date=null;
                String dateString=tvBeginDate.getText().toString();
                if( dateString!=null && !TextUtils.isEmpty(dateString)){
                    date= Utils.dateFromString(dateString).getTime();
                }
                DatePickerFragment dialog=DatePickerFragment.newInstance(date);
                dialog.setTargetFragment(SearchFiltersFragment.this,REQUEST_DATE);
                dialog.show(getFragmentManager(),"dialogDate");
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
                dismiss();
            }
        });

        loadSettings();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=Activity.RESULT_OK){
            return;
        }

        if(requestCode==REQUEST_DATE){
            Date date=(Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            String dateString=Utils.stringFromDate(date);
            tvBeginDate.setText(dateString);
        }
    }
    private void saveSettings(){
        SharedPreferences pref= PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor edit=pref.edit();
        edit.putString(Constants.BEGIN_DATE,tvBeginDate.getText().toString());
        edit.putInt(Constants.SORT_ORDER,spSortOrder.getSelectedItemPosition());
        edit.putBoolean(Constants.ARTS,cbArts.isChecked());
        edit.putBoolean(Constants.SPORTS,cbSports.isChecked());
        edit.putBoolean(Constants.POLITICS,cbPolitics.isChecked());
        edit.commit();
    }

    private void loadSettings(){
        SharedPreferences pref= PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        tvBeginDate.setText(pref.getString(Constants.BEGIN_DATE,""));
        spSortOrder.setSelection(pref.getInt(Constants.SORT_ORDER,0));
        cbArts.setChecked(pref.getBoolean(Constants.ARTS,false));
        cbSports.setChecked(pref.getBoolean(Constants.SPORTS,false));
        cbPolitics.setChecked(pref.getBoolean(Constants.POLITICS,false));

    }

}
