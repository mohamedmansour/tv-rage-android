package com.mindtechnologies.tvrage.service;

import java.util.Map;

import com.mindtechnologies.tvrage.model.TVDay;
import com.mindtechnologies.tvrage.model.TVLanguage;


public interface TVRageService {

  /**
   * Sets the local language for the service. Invalidate cache to fetch a new
   * document.
   * @param lang
   */
  public void setLanguage(TVLanguage valueOf);
  
  /**
   * Retrieve the requested shows for the day at the specified index.
   * @param index The index of the week, where 0 is todays date.
   * @return the TV shows for the returned day.
   */
  public TVDay getDayShows(int day_view_index);
  
  /**
   * All the shows of the week.
   * @return an array list of shows for every day.
   */
  public Map<String, TVDay> getShows();
  
  /**
   * Fetch a brand new schedule from TVRage public XML feed.
   */
  public void fetchSchedule();

}
