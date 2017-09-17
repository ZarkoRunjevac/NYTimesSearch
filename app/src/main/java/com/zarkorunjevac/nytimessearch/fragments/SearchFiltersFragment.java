package com.zarkorunjevac.nytimessearch.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zarkorunjevac.nytimessearch.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zarkorunjevac on 17/09/17.
 */

public class SearchFiltersFragment extends DialogFragment {

    @BindView(R.id.etDate)EditText tvBeginDate;
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
                //DatePickerFragment dialog=DatePickerFragment.newInstance();
                //dialog.show(getFragmentManager(),"dialogDate");
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
