package com.zarkorunjevac.nytimessearch.ui.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.zarkorunjevac.nytimessearch.R;
import com.zarkorunjevac.nytimessearch.models.Article;
import com.zarkorunjevac.nytimessearch.utils.NetworkUtils;

public class ArticleActivity extends AppCompatActivity {

  @BindView(R.id.wvArticle)WebView wvArticle;
  @BindView(R.id.toolbar) Toolbar toolbar;
  Snackbar snackbar;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_article);
    ButterKnife.bind(this);

    setSupportActionBar(toolbar);

    final Article article=(Article)getIntent().getSerializableExtra("article");
    if(NetworkUtils.isOnline(this,wvArticle,snackbar)) {
      wvArticle.setWebViewClient(new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
          view.loadUrl(url);
          return true;
        }
      });
      wvArticle.loadUrl(article.getWebUrl());
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    getMenuInflater().inflate(R.menu.menu_article, menu);

    MenuItem item = menu.findItem(R.id.menu_item_share);
    ShareActionProvider miShare = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");

    shareIntent.putExtra(Intent.EXTRA_TEXT, wvArticle.getUrl());

    miShare.setShareIntent(shareIntent);


    return super.onCreateOptionsMenu(menu);
  }
}
