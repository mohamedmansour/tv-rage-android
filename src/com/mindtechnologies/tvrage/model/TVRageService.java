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
  private final boolean showAllWeek;
  
  private List<TVShow> shows = new ArrayList<TVShow>();
  
  public TVRageService(String url, String lang, boolean showAllWeek) {
    this.url = url + lang;
    Log.d(TAG, url);
    this.showAllWeek = showAllWeek;
  }

  public void fetchSchedule() {
    // In case running it again.
    shows.clear();
    try {
      URL url = new URL(this.url);
      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
      String str = null;
      boolean dayFound = false;
      while ((str = in.readLine()) != null) {
        Pattern p = Pattern.compile("\\[(DAY|TIME|SHOW)\\](.*?)\\[/\\1]");
        Matcher m = p.matcher(str);
        if (m.matches()) {
          TVRow type = TVRow.valueOf(m.group(1));
          String content = m.group(2);
          if (type == TVRow.SHOW) {
            String[] showArr = content.split("\\^");
            shows.add(new TVShow(type, showArr[0], showArr[1], showArr[2], showArr[3]));
          } else if (type == TVRow.DAY) {
            if (dayFound && !showAllWeek) {
              break;
            }
            if (!dayFound) {
              dayFound = true;
            }
            shows.add(new TVShow(type, content));
          } else if (type == TVRow.TIME) {
            shows.add(new TVShow(type, content));
          }
        }
      }
      in.close();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Log.d(TAG, "Processed " + shows.size() + " shows!");
  }
  
  public List<TVShow> getShows() {
    return shows;
  }
}
