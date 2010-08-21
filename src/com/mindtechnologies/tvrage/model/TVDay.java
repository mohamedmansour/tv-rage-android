package com.mindtechnologies.tvrage.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a day full of shows.
 * 
 * @author Mohamed Mansour
 * @since 2010-04-30
 */
public class TVDay {
  private String date;
  private List<TVShow> shows = new ArrayList<TVShow>();
  
  /**
   * Constructor that represents a new day.
   * @param text
   */
  public TVDay(String date) {
    this.date = date;
  }
  
  /**
   * The text of the day.
   * @return textual description of the day.
   */
  public String getDate() {
    return date;
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
  public List<TVShow> getShows() {
    return shows;
  }
  
  /**
   * Checks if the current day has any shows.
   * @return true if no shows present.
   */
  public boolean isEmpty() {
    return shows.size() == 0;
  }
  
  @Override
  public String toString() {
    return date + " [" + shows.size() + " shows]";
  }
}
