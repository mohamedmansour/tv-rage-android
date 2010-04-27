package com.mindtechnologies.tvrage.model;

public class TVShow {
  private final String network;
  private final String name;
  private final String episode;
  private final String url;
  private final TVRow type;
  
  public TVShow(TVRow type, String name) {
    this(type, null, name, null, null);
  }
  
  public TVShow(TVRow type, String network, String name, String episode, String url) {
    this.type = type;
    this.network = network;
    this.name = name;
    this.episode = episode;
    this.url = url;
  }

  public TVRow getType() {
    return type;
  }
  
  public String getNetwork() {
    return network;
  }

  public String getName() {
    return name;
  }

  public String getEpisode() {
    return episode;
  }

  public String getUrl() {
    return url;
  }
  
  @Override
  public String toString() {
    return network + " - " + name + " "  + episode;
  }
}
