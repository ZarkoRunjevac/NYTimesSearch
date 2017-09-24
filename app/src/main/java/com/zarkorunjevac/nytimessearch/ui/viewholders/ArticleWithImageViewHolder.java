package com.zarkorunjevac.nytimessearch.ui.viewholders;

import android.support.v7.widget.RecyclerView.ViewHolder;
import com.zarkorunjevac.nytimessearch.databinding.ItemArticleWithImageBinding;

/**
 * Created by zarkorunjevac on 23/09/17.
 */

public class ArticleWithImageViewHolder extends ViewHolder {


   public final ItemArticleWithImageBinding binding;

    public ArticleWithImageViewHolder(ItemArticleWithImageBinding binding){
        super(binding.getRoot());
        this.binding=binding;
    }

}
