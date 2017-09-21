package com.zarkorunjevac.nytimessearch.activities;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zarkorunjevac.nytimessearch.R;
import com.zarkorunjevac.nytimessearch.adapters.ArticleArrayAdapter;
import com.zarkorunjevac.nytimessearch.fragments.SearchFiltersFragment;
import com.zarkorunjevac.nytimessearch.models.Article;
import com.zarkorunjevac.nytimessearch.utils.Constants;
import com.zarkorunjevac.nytimessearch.utils.EndlessScrollListener;
import com.zarkorunjevac.nytimessearch.utils.DateUtils;

import com.zarkorunjevac.nytimessearch.utils.NetworkUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.gvResults)
    GridView gvResults;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Snackbar snackbar;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    private String query;

    public static final String TAG = SearchActivity.class.getCanonicalName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        articles = new ArrayList<>();
        query="";

        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);

                Article article = articles.get(i);

                intent.putExtra("article", article);

                startActivity(intent);

            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                Log.d(TAG, "onLoadMore: page="+page);
                loadNextDataFromApi(page);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

    }

    private void loadNextDataFromApi(int page){

        if(NetworkUtils.isOnline(this,gvResults,snackbar)){
            AsyncHttpClient client = new AsyncHttpClient();

            String url =Constants.SEARCH_URL;
            RequestParams params = makeParams(page);


            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "onSuccess: " + response.toString());
                    JSONArray articleJsonResults = null;

                    try {
                        articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                        ArrayList<Article> newArticles=Article.fromJSONARRay(articleJsonResults);
                        articles.addAll(newArticles);
                        adapter.notifyDataSetChanged();
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
        MenuItem searchItem=menu.findItem(R.id.action_search);

        final SearchView searchView=(SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query)){
                    SearchActivity.this.query=query;
                    adapter.clear();
                    articles.clear();
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
        if(!TextUtils.isEmpty(beginDate)){
            Calendar calendar = DateUtils.dateFromString(beginDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            String queryDate= DateUtils.queryStringFromDate(calendar.getTime());
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
