package com.mindtechnologies.tvrage;

import java.util.List;

import com.mindtechnologies.tvrage.model.TVShow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TVRageListAdapter extends ArrayAdapter<TVShow> {
  private LayoutInflater inflater;
  
  public TVRageListAdapter(Context context, int viewId, List<TVShow> objects) {
    super(context, viewId, objects);
    this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }
  
  public void addList(List<TVShow> objects) {
    for (TVShow show : objects) {
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
      view.setText(showItem.getName());
      break;
    default:
      view = (TextView) inflater.inflate(R.layout.list_item, parent, false);
      view.setText(showItem.toString());
      break;
    }
    return view;
  }
}
