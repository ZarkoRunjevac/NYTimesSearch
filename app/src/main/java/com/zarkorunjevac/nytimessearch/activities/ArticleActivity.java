package com.zarkorunjevac.nytimessearch.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.zarkorunjevac.nytimessearch.R;
import com.zarkorunjevac.nytimessearch.models.Article;

public class ArticleActivity extends AppCompatActivity {

  @BindView(R.id.wvArticle)WebView wvArticle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_article);
    ButterKnife.bind(this);

    final Article article=(Article)getIntent().getSerializableExtra("article");
    wvArticle.setWebViewClient(new WebViewClient(){
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
      }
    });
    wvArticle.loadUrl(article.getWebUrl());

  }
}
