package com.mindtechnologies.tvrage.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;


public class TVRageService {
  private static final String TAG = "TVRageService";

  private final String url;
  
  private ArrayList<ArrayList<TVShow>> shows = new ArrayList<ArrayList<TVShow>>();
  
  public TVRageService(String url, String lang, boolean showAllWeek) {
    this.url = url + lang;
    Log.d(TAG, this.url);
  }

  public void fetchSchedule() {
    // In case running it again.
    shows.clear();
    ArrayList<TVShow> dayShows = null;
    try {
      URL url = new URL(this.url);
      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
      String str = null;
      while ((str = in.readLine()) != null) {
        Pattern p = Pattern.compile("\\[(DAY|TIME|SHOW)\\](.*?)\\[/\\1]");
        Matcher m = p.matcher(str);
        if (m.matches()) {
          TVRow type = TVRow.valueOf(m.group(1));
          String content = m.group(2);
          if (type == TVRow.SHOW) {
            String[] showArr = content.split("\\^");
            dayShows.add(new TVShow(type, showArr[0], showArr[1], showArr[2], showArr[3]));
          } else if (type == TVRow.DAY) {
            if (dayShows != null) {
              shows.add(dayShows);
            }
            dayShows = new ArrayList<TVShow>();
            dayShows.add(new TVShow(type, content));
          } else if (type == TVRow.TIME) {
            dayShows.add(new TVShow(type, content));
          }
        }
      }
      in.close();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    if (dayShows != null) {
      if (dayShows.size() == 0) {
        dayShows.add(new TVShow(TVRow.SHOW, "None"));
      }
      shows.add(dayShows);
    }
    
    Log.d(TAG, "Processed " + shows.size() + " shows!");
  }
  
  public List<TVShow> getDayShows() {
    if (shows.size() == 0)
      return new ArrayList<TVShow>();
    return shows.get(0);
  }
  
  public List<TVShow> getWeekShows() {
    ArrayList<TVShow> allShows = new ArrayList<TVShow>();
    for (ArrayList<TVShow> dayShow : shows) {
      allShows.addAll(dayShow);
    }
    return allShows;
  }
}
