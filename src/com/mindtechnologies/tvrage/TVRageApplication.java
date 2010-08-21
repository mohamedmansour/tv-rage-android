package com.mindtechnologies.tvrage;

import com.mindtechnologies.tvrage.service.TVRageCachedServiceImpl;
import com.mindtechnologies.tvrage.service.TVRageService;

import android.app.Application;

public class TVRageApplication extends Application {
  public static final String SETTINGS = "MySettingsFile";
  public static final String PREF_COUNTRY = "country";
  
  private String[] items;
  private TVRageService service;
  private boolean initialized = false;
  private int view_index_day = 0;
  
  @Override
  public void onCreate() {
    // Instantiate a new REST service and array of countries for settings.
    service = new TVRageCachedServiceImpl(this);
    items = getResources().getStringArray(R.array.countries_array);
    super.onCreate();
  }
    
  public boolean isInitialized() {
    return initialized;
  }
  
  public TVRageService getService() {
    return service;
  }
  
  public String[] getItems() {
    return items;
  }

  public void setInitialized(boolean status) {
    initialized = status;
  }
  
  public int getViewIndexDay() {
    return view_index_day;
  }
  
  public void setViewIndexDay(int view_index_day) {
    this.view_index_day = view_index_day;
  }

  public String getItem(int position) {
    return items[position];
  }
}
