package com.zarkorunjevac.nytimessearch.utils;

import android.databinding.BindingAdapter;
import android.view.View;

/**
 * Created by zarkorunjevac on 23/09/17.
 */

public class BindingAdapters {
    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
