package com.zarkorunjevac.nytimessearch.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zarkorunjevac.nytimessearch.R;
import com.zarkorunjevac.nytimessearch.models.Article;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zarko.runjevac on 9/15/2017.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    private final WeakReference<Context> mContext;
    private final static String TAG = ArticleArrayAdapter.class.getSimpleName();

    static class ViewHolder {

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.ivImage)
        ImageView ivImage;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public ArticleArrayAdapter(Context context, ArrayList<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
        mContext = new WeakReference<Context>(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Article article = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {

            convertView = LayoutInflater.from(mContext.get()).inflate(R.layout.item_article_result, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTitle.setText(article.getHeadline());

        viewHolder.ivImage.setImageResource(0);

        String thumbnail=article.getThumbnail();

        if (!TextUtils.isEmpty(thumbnail)) {
            Log.d(TAG, "getView: thumbnail=" + thumbnail);
            Picasso.with(getContext())
                    .load(thumbnail)
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.ivImage);
        }

        return convertView;
    }
}
