package com.mindtechnologies.tvrage.model;

public enum TVLanguage {
  US("United States"),
  GB("Great Britain"),
  CA("Canada"),
  AU("Australia");
  
  String name;
  
  private TVLanguage(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
}
