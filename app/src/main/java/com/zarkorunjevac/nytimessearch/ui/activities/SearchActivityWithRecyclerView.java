package com.zarkorunjevac.nytimessearch.ui.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zarkorunjevac.nytimessearch.R;
import com.zarkorunjevac.nytimessearch.databinding.ActivitySearchWithRecyclerViewBinding;
import com.zarkorunjevac.nytimessearch.models.Article;
import com.zarkorunjevac.nytimessearch.ui.adapters.ArticleAdapter;
import com.zarkorunjevac.nytimessearch.ui.callback.ArticleClickCallback;
import com.zarkorunjevac.nytimessearch.ui.fragments.SearchFiltersFragment;
import com.zarkorunjevac.nytimessearch.utils.Constants;
import com.zarkorunjevac.nytimessearch.utils.DateUtils;
import com.zarkorunjevac.nytimessearch.utils.EndlessRecyclerViewScrollListener;
import com.zarkorunjevac.nytimessearch.utils.NetworkUtils;
import com.zarkorunjevac.nytimessearch.utils.SpacesItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class SearchActivityWithRecyclerView extends AppCompatActivity {

    private static final String TAG = SearchActivityWithGridView.class.getCanonicalName();

    ArticleAdapter mArticleAdapter;

    ActivitySearchWithRecyclerViewBinding mBinding;

    ArrayList<Article> articles;

    String query;

    private Snackbar snackbar;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_with_recycler_view);

        articles = new ArrayList<>();
        query = "";

        setSupportActionBar(mBinding.toolbar);

        mBinding.toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.theme_text_dark));

        mArticleAdapter = new ArticleAdapter(this,mArticleClickCallback);
        StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        mBinding.rvArticles.addItemDecoration(decoration);

        mBinding.rvArticles.setLayoutManager(staggeredGridLayoutManager);

        mBinding.rvArticles.setAdapter(mArticleAdapter);

        scrollListener=new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };

        mBinding.rvArticles.addOnScrollListener(scrollListener);


    }


    private final ArticleClickCallback mArticleClickCallback = new ArticleClickCallback() {
        @Override
        public void onClick(Article article) {

            Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.ic_share);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, article.getWebUrl());

            int requestCode = 100;

            PendingIntent pendingIntent = PendingIntent.getActivity(SearchActivityWithRecyclerView.this,
                    requestCode,
                    shareIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            CustomTabsIntent.Builder builder=new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(SearchActivityWithRecyclerView.this, R.color.theme_red_primary));
            builder.setActionButton(bitmap, "Share Link", pendingIntent, true);
            CustomTabsIntent customTabsIntent=builder.build();

            customTabsIntent.launchUrl(SearchActivityWithRecyclerView.this, Uri.parse(article.getWebUrl()));
        }
        };


    private void loadNextDataFromApi(final int page) {

        if (NetworkUtils.isOnline(this, mBinding.rvArticles, snackbar)) {
            AsyncHttpClient client = new AsyncHttpClient();

            String url = Constants.SEARCH_URL;
            RequestParams params = makeParams(page);


            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "onSuccess: " + response.toString());
                    JSONArray articleJsonResults = null;

                    try {
                        articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                        ArrayList<Article> newArticles = Article.fromJSONARRay(articleJsonResults);
                        if (page == 0) {
                            mBinding.setIsLoading(false);
                        }
                        articles.addAll(newArticles);
                        mArticleAdapter.setArticleList(articles);
                        mArticleAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onSuccess: " + articles.toString());

                    } catch (JSONException e) {
                        Log.e(TAG, "onSuccess: ", e);
                    }

                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_search);
        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(ContextCompat.getColor(this,R.color.theme_text_dark));
        et.setHintTextColor(ContextCompat.getColor(this,R.color.theme_red_text));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    SearchActivityWithRecyclerView.this.query = query;
                    mBinding.setIsLoading(true);
                    articles.clear();
                    mArticleAdapter.notifyDataSetChanged();
                    scrollListener.resetState();
                    loadNextDataFromApi(0);
                    searchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchSettings:
                SearchFiltersFragment searchFiltersFragment = SearchFiltersFragment.newInstance();

                searchFiltersFragment.show(getFragmentManager(), "searchDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private RequestParams makeParams(int page) {

        RequestParams params = new RequestParams();
        params.add("api-key", "f8503cc0ea264dab82c1270bc88c96ce");
        params.put("page", page);
        params.put("q", query);

        //load settings

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String beginDate = pref.getString(Constants.BEGIN_DATE, "");
        if (!TextUtils.isEmpty(beginDate)) {
            Calendar calendar = DateUtils.dateFromString(beginDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            String queryDate = DateUtils.queryStringFromDate(calendar.getTime());
            params.put("begin_date", queryDate);
        }

        String sortOrder = "oldest";
        if (pref.getInt(Constants.SORT_ORDER, 0) != 0) {
            sortOrder = "newest";
        }
        params.put("sort", sortOrder);
        boolean arts = pref.getBoolean(Constants.ARTS, false);
        boolean sports = pref.getBoolean(Constants.SPORTS, false);
        boolean politics = pref.getBoolean(Constants.POLITICS, false);

        String fq = "";
        if (arts) fq += "Arts";
        if (sports) fq += "Sports";
        if (politics) fq += "Politics";

        if (!TextUtils.isEmpty(fq)) {
            String news_desk = String.format("news_desk:(%s)", fq);
            params.put("fq", news_desk);
        }
        Log.d(TAG, "makeParams: params=" + params.toString());
        return params;
    }
}
