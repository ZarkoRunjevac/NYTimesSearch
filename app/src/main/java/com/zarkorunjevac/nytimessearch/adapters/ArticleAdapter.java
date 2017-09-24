package com.zarkorunjevac.nytimessearch.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.zarkorunjevac.nytimessearch.R;
import com.zarkorunjevac.nytimessearch.databinding.ItemArticleWithImageBinding;
import com.zarkorunjevac.nytimessearch.databinding.ItemArticleWithoutImageBinding;
import com.zarkorunjevac.nytimessearch.models.Article;
import com.zarkorunjevac.nytimessearch.ui.callback.ArticleClickCallback;
import com.zarkorunjevac.nytimessearch.ui.viewholders.ArticleWithImageViewHolder;
import com.zarkorunjevac.nytimessearch.ui.viewholders.ArticleWithoutImageViewHolder;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by zarkorunjevac on 23/09/17.
 */

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<? extends Article> mArticleList;

    @Nullable
    private final ArticleClickCallback mArticleClickCallback;

    private final int ARTICLE_WITH_IMAGE=0, ARTICLE_WITHOUT_IMAGE=1;

    final WeakReference<Context> mContext;

    public ArticleAdapter(Context context,@Nullable ArticleClickCallback articleClickCallback){
        mArticleClickCallback=articleClickCallback;
        mContext=new WeakReference<Context>(context);
    }

    public void setArticleList(final List<? extends Article> articleList){
        if(mArticleList==null){
            mArticleList=articleList;
            notifyItemRangeChanged(0,articleList.size());
        }else{
            DiffUtil.DiffResult result=DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mArticleList.size();
                }

                @Override
                public int getNewListSize() {
                    return articleList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mArticleList.get(oldItemPosition).getId().equals(articleList.get(newItemPosition).getId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Article article=articleList.get(newItemPosition);
                    Article old=mArticleList.get(oldItemPosition);

                    return article.getId().equals(old.getId())
                            && article.getHeadline()==old.getHeadline()
                            && article.getWebUrl()==old.getWebUrl()
                            && article.getThumbnail()==old.getThumbnail()
                            && article.getSnippet()==old.getSnippet();
                }
            });
            mArticleList=articleList;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Article article=mArticleList.get(position);
        if(article.hasImage()){
            return ARTICLE_WITH_IMAGE;
        }else{
            return ARTICLE_WITHOUT_IMAGE;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==ARTICLE_WITH_IMAGE){
            ItemArticleWithImageBinding binding1= DataBindingUtil
                    .inflate(LayoutInflater.from(parent.getContext()), R.layout.item_article_with_image,
                            parent,false);
            binding1.setCallback(mArticleClickCallback);

            return new ArticleWithImageViewHolder(binding1);
        }else{
            ItemArticleWithoutImageBinding binding2=DataBindingUtil
                    .inflate(LayoutInflater.from(parent.getContext()),R.layout.item_article_without_image,
                            parent,false);
            binding2.setCallback(mArticleClickCallback);
            return new ArticleWithoutImageViewHolder(binding2);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType()==ARTICLE_WITH_IMAGE){
            ArticleWithImageViewHolder viewHolder1=(ArticleWithImageViewHolder)holder;
            Article article=mArticleList.get(position);
            viewHolder1.binding.setArticle(article);
            String thumbnail=article.getThumbnail();
            Picasso.with(mContext.get())
                    .load(thumbnail)
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder1.binding.ivImage);
            viewHolder1.binding.executePendingBindings();

        }else{
            ArticleWithoutImageViewHolder viewHolder2=(ArticleWithoutImageViewHolder)holder;
            viewHolder2.binding.setArticle(mArticleList.get(position));
            viewHolder2.binding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return mArticleList==null ? 0 : mArticleList.size();
    }
}
