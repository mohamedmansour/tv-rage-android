package com.mindtechnologies.tvrage.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

/**
 * TVRage Service Client. That accesses the public TVRage XML feed.
 * 
 * @author Mohamed Mansour
 * @since 2010-04-28
 */
public class TVRageService {
  private static final String TAG = "TVRageService";
  private final String url;
  private TVLanguage lang;
  private ArrayList<TVDay> shows;

  /**
   * Constructor that takes the URL and saved default user language.
   * @param url
   * @param lang
   */
  public TVRageService(String url) {
    this.url = url;
    this.lang = TVLanguage.US;
    this.shows = new ArrayList<TVDay>();
  }

  /**
   * Sets the local language for the service.
   * @param lang
   */
  public void setLanguage(TVLanguage lang) {
    this.lang = lang;
  }
  
  /**
   * Fetch a brand new schedule from TVRage public XML feed.
   */
  public void fetchSchedule() {
    // In case running it again.
    shows.clear();
    TVDay tvDay = null;
    
    try {
      // Prepare the localized URL.
      URL url = new URL(this.url + this.lang);
      
      // Visit the URL and scrape the TV schedule.
      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
      String str = null;
      while ((str = in.readLine()) != null) {
        // Checks for tags that start with DAY, TIME, and SHOW that have the
        // following format "[DAY] ... [/DAY]"
        Pattern p = Pattern.compile("\\[(DAY|TIME|SHOW)\\](.*?)\\[/\\1]");
        Matcher m = p.matcher(str);
        if (m.matches()) {
          // Parse out the groupings for what we matched.
          TVRow type = TVRow.valueOf(m.group(1));
          String content = m.group(2);
          
          // Handle every line, the DAY row should always begin first, otherwise
          // the algorithm will fail since it needs to create DAY buckets, where
          // each bucket consists of TIMES and SHOWS sequentially.
          switch (type) {
          case DAY:
            if (tvDay != null) {
              shows.add(tvDay);
            }
            tvDay = new TVDay(content);
            break;
          case TIME:
            tvDay.addShow(new TVShow(type, content));
            break;
          case SHOW:
            String[] showArr = content.split("\\^");
            tvDay.addShow(new TVShow(type, showArr[0], showArr[1], showArr[2], showArr[3]));
            break;
          }
        }
      }
      in.close();
    } catch (MalformedURLException e) {
      Log.e(TAG, e.getMessage());
    } catch (IOException e) {
      Log.e(TAG, e.getMessage());
    }
    
    // Handle the case where overflows occur and add them back to the newest
    // bucket. This case should always happen at the end of any TIME groupings.
    if (tvDay != null) {
      if (tvDay.isEmpty()) {
        tvDay.addShow(new TVShow(TVRow.SHOW, "None"));
      }
      shows.add(tvDay);
    }
    
    Log.d(TAG, "Processed " + shows.size() + " shows for " + tvDay.getText());
  }
  
  /**
   * Retrieve the requested shows for the day at the specified index.
   * @param index The index of the week, where 0 is todays date.
   * @return the TV shows for the returned day.
   */
  public TVDay getDayShows(int index) {
    if (shows.size() == 0) {
      TVDay errorDay = new TVDay("Service Error");
      errorDay.addShow(new TVShow(TVRow.SHOW, "Cannot fetch tv service"));
      return errorDay;
    }
    return shows.get(index);
  }
}
