package com.mindtechnologies.tvrage.service;


import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.mindtechnologies.tvrage.model.TVDay;
import com.mindtechnologies.tvrage.model.TVShow;
import com.mindtechnologies.tvrage.model.TVShow.Builder;



public class FullScheduleHandler extends DefaultHandler {
  private final static String TAG = "FullScheduleHandler";
  
  private final static String DAY_TAG = "DAY";
  private final static String ATTR_TAG = "attr";
  private final static String NAME_TAG = "name";
  private final static String TIME_TAG = "time";
  private final static String SHOW_TAG = "show";
  private final static String SID_TAG = "sid";
  private final static String NETWORK_TAG = "network";
  private final static String TITLE_TAG = "title";
  private final static String EPISODE_TAG = "ep";
  private final static String LINK_TAG = "link";
  
  private String tempVal;
  private Map<String, TVDay> shows;
  private TVDay day;
  private Builder show;
  private String time;
  private long sTime = 0;
  
  public Map<String, TVDay> getShows() {
    return shows;
  }
  @Override
  public void fatalError(SAXParseException e) throws SAXException {
    // TODO Auto-generated method stub
    super.fatalError(e);
  }
  @Override
  public void startDocument() throws SAXException {
    shows = new HashMap<String, TVDay>();
    day = null;
    sTime = System.nanoTime();
  }

  @Override
  public void endDocument() throws SAXException {
    Log.i(TAG, "Parsing completed in: " +
          ((System.nanoTime() - sTime) / 1000000000.0) +
          " has " + shows.size() + " days archived!" );
  }
  
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    tempVal = new String(ch,start,length);
  }

  @Override
  public void startElement(String uri, String localName, String name,
                           Attributes attributes) throws SAXException {
    if (localName.equals(DAY_TAG)) {
      day = new TVDay(attributes.getValue(ATTR_TAG));
    } else if (localName.equals(TIME_TAG)) {
      time = attributes.getValue(ATTR_TAG);
      day.addShow(new TVShow.Builder(time).build());
    } else if (localName.equals(SHOW_TAG)) {
      show = new TVShow.Builder(time, attributes.getValue(NAME_TAG));
    }
  }

  @Override
  public void endElement(String uri, String localName, String name)
      throws SAXException  {
    if (localName.equals(DAY_TAG)) {
      shows.put(day.getDate(), day);
    } else if (localName.equals(SHOW_TAG)) {
      day.addShow(show.build());
    } else if (localName.equals(SID_TAG)) {
      show.sid(Integer.parseInt(tempVal));
    } else if (localName.equals(NETWORK_TAG)) {
      show.network(tempVal);
    } else if (localName.equals(TITLE_TAG)) {
      show.title(tempVal);
    } else if (localName.equals(EPISODE_TAG)) {
      show.episode(tempVal);
    } else if (localName.equals(LINK_TAG)) {
      show.link(tempVal);
    }
    tempVal = "";
  }
}
