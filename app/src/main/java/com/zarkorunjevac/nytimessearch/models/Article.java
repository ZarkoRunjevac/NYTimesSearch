package com.zarkorunjevac.nytimessearch.models;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Article implements Serializable {
  private final static String TAG=Article.class.getCanonicalName();
  private final String BASE_URL="http://www.nytimes.com/";

  private String webUrl;
  private String snippet;

  private String source;
  private List<Object> multimedia = null;
  private String headline;
  private List<Object> keywords = null;
  private String pubDate;
  private String documentType;
  private String newDesk;
  private String thumbnail;
  private String typeOfMaterial;
  private String id;
  private Integer wordCount;
  private Integer score;
  private String uri;
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  public Article(JSONObject jsonObject){
    try {
      this.webUrl=jsonObject.getString("web_url");
      this.headline=jsonObject.getJSONObject("headline").getString("main");

      JSONArray multimedia=jsonObject.getJSONArray("multimedia");

      if(multimedia.length()>0){
        JSONObject multimediaJSON=multimedia.getJSONObject(0);
        this.thumbnail=BASE_URL+multimediaJSON.getString("url");
      }else{
        this.thumbnail="";
      }

    }catch (JSONException e){

    }
  }
  public static ArrayList<Article> fromJSONARRay(JSONArray array){
    ArrayList<Article> articles=new ArrayList<>();
    for(int x=0;x<array.length();x++){
      try {
        articles.add(new Article(array.getJSONObject(x)));
      }catch (JSONException e){
        Log.e(TAG, "fromJSONARRay: ", e);
      }
    }
    return articles;
  }


  public String getWebUrl() {
    return webUrl;
  }

  public void setWebUrl(String webUrl) {
    this.webUrl = webUrl;
  }

  public String getSnippet() {
    return snippet;
  }

  public void setSnippet(String snippet) {
    this.snippet = snippet;
  }


  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public List<Object> getMultimedia() {
    return multimedia;
  }

  public void setMultimedia(List<Object> multimedia) {
    this.multimedia = multimedia;
  }

  public String getHeadline() {
    return headline;
  }

  public void setHeadline(String headline) {
    this.headline = headline;
  }

  public List<Object> getKeywords() {
    return keywords;
  }

  public void setKeywords(List<Object> keywords) {
    this.keywords = keywords;
  }

  public String getPubDate() {
    return pubDate;
  }

  public void setPubDate(String pubDate) {
    this.pubDate = pubDate;
  }

  public String getDocumentType() {
    return documentType;
  }

  public void setDocumentType(String documentType) {
    this.documentType = documentType;
  }

  public String getNewDesk() {
    return newDesk;
  }

  public void setNewDesk(String newDesk) {
    this.newDesk = newDesk;
  }



  public String getTypeOfMaterial() {
    return typeOfMaterial;
  }

  public void setTypeOfMaterial(String typeOfMaterial) {
    this.typeOfMaterial = typeOfMaterial;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer getWordCount() {
    return wordCount;
  }

  public void setWordCount(Integer wordCount) {
    this.wordCount = wordCount;
  }

  public Integer getScore() {
    return score;
  }

  public void setScore(Integer score) {
    this.score = score;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }


  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }
}
