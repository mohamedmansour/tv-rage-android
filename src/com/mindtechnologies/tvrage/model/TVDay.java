package com.mindtechnologies.tvrage.model;

import java.util.ArrayList;

/**
 * Represents a day full of shows.
 * 
 * @author Mohamed Mansour
 * @since 2010-04-30
 */
public class TVDay {
  private final String text;
  private final ArrayList<TVShow> shows = new ArrayList<TVShow>();
  
  /**
   * Constructor that represents a new day.
   * @param text
   */
  public TVDay(String text) {
    this.text = text;
  }
  
  /**
   * The text of the day.
   * @return textual description of the day.
   */
  public String getText() {
    return text;
  }
  
  /**
   * Add a show to the current day.
   * @param show The show to add.
   */
  public void addShow(TVShow show) {
    shows.add(show);
  }
  
  /**
   * The list of shows in the specified day.
   * @return all the aired shows during that day.
   */
  public ArrayList<TVShow> getShows() {
    return shows;
  }
  
  /**
   * Checks if the current day has any shows.
   * @return true if no shows present.
   */
  public boolean isEmpty() {
    return shows.size() == 0;
  }
}
