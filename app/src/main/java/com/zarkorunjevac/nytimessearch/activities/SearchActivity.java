package com.zarkorunjevac.nytimessearch.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
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
import com.zarkorunjevac.nytimessearch.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    @BindView(R.id.etQuery)
    EditText etQuery;
    @BindView(R.id.gvResults)
    GridView gvResults;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ArrayList<Article> articles;
    ArticleArrayAdapter adpter;

    public static final String TAG = SearchActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        articles = new ArrayList<>();

        adpter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adpter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);

                Article article = articles.get(i);

                intent.putExtra("article", article);

                startActivity(intent);

            }
        });

    }

    public void onArticleSearch(View view) {


        AsyncHttpClient client = new AsyncHttpClient();

        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = makeParams();


        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess: " + response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.clear();
                    articles.addAll(Article.fromJSONARRay(articleJsonResults));
                    adpter.addAll(articles);
                    Log.d(TAG, "onSuccess: " + articles.toString());
                } catch (JSONException e) {
                    Log.e(TAG, "onSuccess: ", e);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    private RequestParams makeParams() {
        String query = etQuery.getText().toString();
        RequestParams params = new RequestParams();
        params.add("api-key", "f8503cc0ea264dab82c1270bc88c96ce");
        params.put("page", 0);
        params.put("q", query);

        //load settings

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String beginDate = pref.getString(Constants.BEGIN_DATE, "");
        Calendar calendar = Utils.dateFromString(beginDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        String queryDate=Utils.queryStringFromDate(calendar.getTime());
        params.put("begin_date", queryDate);
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
