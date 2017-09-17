package com.zarkorunjevac.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
  @BindView(R.id.etQuery) EditText etQuery;
  @BindView(R.id.gvResults) GridView gvResults;
  @BindView(R.id.btnSearch) Button btnSearch;
  @BindView(R.id.toolbar)
  Toolbar toolbar;

  ArrayList<Article> articles;
  ArticleArrayAdapter adpter;

  public static final String TAG=SearchActivity.class.getCanonicalName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    ButterKnife.bind(this);

    setSupportActionBar(toolbar);

    articles=new ArrayList<>();

    adpter=new ArticleArrayAdapter(this, articles);
    gvResults.setAdapter(adpter);

    gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener(){
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent=new Intent(getApplicationContext(),ArticleActivity.class);

        Article article=articles.get(i);

        intent.putExtra("article",article);

        startActivity(intent);

      }
    });

  }

  public void onArticleSearch(View view) {
    String query=etQuery.getText().toString();

    AsyncHttpClient client=new AsyncHttpClient();

    String url="https://api.nytimes.com/svc/search/v2/articlesearch.json";
    RequestParams params=new RequestParams();
    params.add("api-key","f8503cc0ea264dab82c1270bc88c96ce");
    params.put("page",0);
    params.put("q",query);

    client.get(url,params,new JsonHttpResponseHandler(){
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d(TAG, "onSuccess: "+response.toString());
        JSONArray articleJsonResults=null;

        try {
          articleJsonResults=response.getJSONObject("response").getJSONArray("docs");
          articles.clear();
          articles.addAll(Article.fromJSONARRay(articleJsonResults));
          adpter.addAll(articles);
          Log.d(TAG, "onSuccess: "+articles.toString());
        }catch (JSONException e){
          Log.e(TAG, "onSuccess: ",e );
        }

      }
    });
  }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.searchSettings:
                SearchFiltersFragment searchFiltersFragment=SearchFiltersFragment.newInstance();

                searchFiltersFragment.show(getFragmentManager(),"searchDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
