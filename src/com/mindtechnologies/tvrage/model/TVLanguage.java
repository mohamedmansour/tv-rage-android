package com.mindtechnologies.tvrage.model;

/**
 * Language selection for shows.
 * 
 * @author Mohamed Mansour
 * @since 2010-05-03
 */
public enum TVLanguage {
  US("United States"),
  GB("Great Britain"),
  CA("Canada"),
  AU("Australia");
  
  String name;
  
  /**
   * Constructor that takes the name of the abbreviation.
   * @param name
   */
  private TVLanguage(String name) {
    this.name = name;
  }
  
  /**
   * The full name of language.
   * @return
   */
  public String getName() {
    return name;
  }
}
