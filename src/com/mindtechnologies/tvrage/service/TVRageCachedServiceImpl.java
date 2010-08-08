package com.mindtechnologies.tvrage.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.content.Context;
import android.util.Log;

import com.mindtechnologies.tvrage.R;
import com.mindtechnologies.tvrage.model.TVDay;
import com.mindtechnologies.tvrage.model.TVLanguage;

/**
 * TVRage service implementation that supports caching. The initial request
 * caches the contents to disk as an XML format. Everytime the applications
 * runs, it will read the cached item, validate its okay, then use it.
 * 
 * @author Mohamed Mansour
 * @since 2010-08-08
 */
public class TVRageCachedServiceImpl implements TVRageService {
  private final static String TAG = "TVRageCachedServiceImpl";
  private final static String CACHE_FILE = "tvrage_cache";
  private final String scheduleUrl;
  private final String showUrl;
  private final Context context;
  
  private TVLanguage lang;
  private Map<String, TVDay> shows;
  
  public TVRageCachedServiceImpl(Context context) {
    this.scheduleUrl = context.getResources().getString(R.string.tvrage_url);
    this.showUrl = context.getResources().getString(R.string.tvrage_show_url);
    this.context = context;
    this.shows = new HashMap<String, TVDay>();
    this.lang = TVLanguage.US;
  }
  
  @Override
  public void setLanguage(TVLanguage lang) {
    this.lang = lang;
  }

  @Override
  public TVDay getDayShows(int day_view_index) {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, day_view_index);
    String key = (new SimpleDateFormat("yyyy-M-d")).format(cal.getTime());
    return this.shows.get(key);
  }

  @Override
  public List<TVDay> getShows() {
    return new ArrayList<TVDay>();
  }

  @Override
  public void fetchSchedule() {
    // Check if the cache is valid. If any case of invalid cache, try to fetch
    // a new one from online. If that fails, delete the cache.
    if (!isCacheValid()) {
      if (!persistCache()) {
        invalidateCache();
        throw new RuntimeException("Service is unavailable. Please try again " +
                                   "later.");
      }
    }
    readCache();
  }

  /**
   * Checks if the TVRage cache file is valid in the internal storage system.
   * @return true if the cache exists other wise can't find it in the system.
   */
  private boolean isCacheValid() {
    File cachedFile = context.getFileStreamPath(CACHE_FILE);
    return cachedFile.exists();
  }
  
  /**
   * Invalidate the cache since something went wrong along the way. Such as, 
   * failed to fetch the service, writing corruption, etc.
   */
  private void invalidateCache() {
    File cachedFile = context.getFileStreamPath(CACHE_FILE);
    if (cachedFile.exists()) {
      cachedFile.delete();
    }
  }
  
  /**
   * Persist cache coming from the TVRage Feed to disk.
   * @return true if persisting was successful.
   */
  private boolean persistCache() {
    FileOutputStream fos = null;
    InputStream is = null;
    try {
      // Open file.
      fos = context.openFileOutput(CACHE_FILE, Context.MODE_PRIVATE);
      
      // Fetch Service from TV Rage.
      URL sheduleLocaleURL = null;
      try {
        sheduleLocaleURL = new URL(scheduleUrl + this.lang);
      } catch (MalformedURLException e) {
        Log.e(TAG, "Invalid Service URL: " + scheduleUrl + this.lang, e);
        return false;
      }
      
      // Persist it.
      is = getInputStream(sheduleLocaleURL);
      int c;
      try {
        while ((c = is.read()) != -1) {
          fos.write(c);
        }
      } catch (IOException e) {
        Log.e(TAG, "Cannot write input stream into outputstream", e);
        return false;
      }
    } catch (Exception e) {
      Log.e(TAG, e.getMessage(), e);
      return false;
    } finally {
      if (is != null) try { is.close(); } catch (IOException e) {}
      if (fos != null) try { fos.close(); } catch (IOException e) {}
    }
    return true;
  }
  
  /**
   * Read the cache and store the content in the collection.
   */
  private void readCache() {
    FileInputStream is = null;
    SAXParserFactory factory = SAXParserFactory.newInstance();
    try {
        is = context.openFileInput(CACHE_FILE);
        SAXParser parser = factory.newSAXParser();
        FullScheduleHandler handler = new FullScheduleHandler();
        parser.parse(is, handler);
        shows = handler.getShows();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (is != null) try { is.close(); } catch (IOException e) {}
    }
  }
  
  
  /**
   * Safely get the input stream.
   * @param url The URL to convert to an input stream.
   * @return a safe input stream.
   */
  private InputStream getInputStream(URL url) {
    try {
      return url.openConnection().getInputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
