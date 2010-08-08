package com.mindtechnologies.tvrage.model;

/**
 * Representation of a TV Show.
 * 
 * @author Mohamed Mansour
 * @since 2010-04-30
 */
public class TVShow {
  private final String time;
  private final String name;
  private final int sid;
  private final String network;
  private final String title;
  private final String episode;
  private final String link;
  private final TVRow type;
  
  // Builder Pattern.
  public static class Builder {
    // Required parameters.
    private final String time;
    private final String name;
    private final TVRow type;
    
    // Optional parameters.
    private String network;
    private String title;
    private String episode;
    private String link;
    private int sid;
    
    public Builder(String time) {
      this.time = time;
      this.name = time;
      this.type = TVRow.TIME;
    }
    
    public Builder(String time, String name) {
      this.time = time;
      this.name = name;
      this.type = TVRow.SHOW;
    }

    public Builder sid(int val) { sid = val; return this; }
    public Builder network(String val) { network = val; return this; }
    public Builder title(String val) { title = val; return this; }
    public Builder episode(String val) { episode = val; return this; }
    public Builder link(String val) { link = val; return this; }
    
    public TVShow build() {
      return new TVShow(this);
    }
  }
  
  private TVShow(Builder builder) {
    time = builder.time;
    name = builder.name;
    sid = builder.sid;
    network = builder.network;
    title = builder.title;
    episode = builder.episode;
    link = builder.link;
    type = builder.type;
  }
  
  public String getTime() { return time; }
  public String getName() { return name;}
  public int getSid() { return sid; }
  public String getNetwork() { return network; }
  public String getTitle() { return title; }
  public String getEpisode() { return episode; }
  public String getLink() { return link; }
  public TVRow getType() { return type; }
  
  @Override
  public String toString() {
    return network + " - " + title + " "  + episode;
  }
}
