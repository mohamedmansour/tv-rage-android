package com.mindtechnologies.tvrage.model;

import java.util.ArrayList;

public class TVDay {
  private final String text;
  private final ArrayList<TVShow> shows = new ArrayList<TVShow>();
  
  public TVDay(String text) {
    this.text = text;
  }
  
  public String getText() {
    return text;
  }
  
  public void addShow(TVShow show) {
    shows.add(show);
  }
  
  public ArrayList<TVShow> getShows() {
    return shows;
  }
  
  public boolean isEmpty() {
    return shows.size() == 0;
  }
}
