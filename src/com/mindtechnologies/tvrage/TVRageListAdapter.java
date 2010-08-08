package com.mindtechnologies.tvrage;

import java.util.List;

import com.mindtechnologies.tvrage.model.TVShow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * A custom list adapter that allows different row renders for the list view.
 * Whenever a time is present, it will make the row darker, and all other entries
 * lighter.
 * 
 * @author Mohamed Mansour
 * @since 2010-04-30
 */
public class TVRageListAdapter extends ArrayAdapter<TVShow> {
  private LayoutInflater inflater;
  
  /**
   * Constructor that creates an adapter for the ListView.
   * @param context the application context.
   * @param viewId the view id of the list view to apply the adapter to. 
   */
  public TVRageListAdapter(Context context, int viewId) {
    super(context, viewId);
    this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }
  
  /**
   * Inform the adapter that a new list of shows is present.
   * @param shows
   */
  public void setShows(List<TVShow> shows) {
    clear();
    for (TVShow show : shows) {
      add(show);
    }
  }
  
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    TVShow showItem = getItem(position);
    TextView view = null;
    switch (showItem.getType()) {
    case TIME:
      view = (TextView) inflater.inflate(R.layout.list_separator, parent, false);
      view.setText(showItem.getTime());
      break;
    default:
      view = (TextView) inflater.inflate(R.layout.list_item, parent, false);
      view.setText(showItem.toString());
      break;
    }
    return view;
  }
}
