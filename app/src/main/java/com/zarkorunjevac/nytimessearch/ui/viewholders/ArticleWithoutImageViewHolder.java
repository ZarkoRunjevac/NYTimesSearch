package com.zarkorunjevac.nytimessearch.ui.viewholders;

import android.support.v7.widget.RecyclerView.ViewHolder;

import com.zarkorunjevac.nytimessearch.databinding.ItemArticleWithoutImageBinding;

/**
 * Created by zarkorunjevac on 23/09/17.
 */

public class ArticleWithoutImageViewHolder extends ViewHolder {

   public final ItemArticleWithoutImageBinding binding;

    public ArticleWithoutImageViewHolder(ItemArticleWithoutImageBinding binding){
        super(binding.getRoot());
        this.binding=binding;
    }
}
