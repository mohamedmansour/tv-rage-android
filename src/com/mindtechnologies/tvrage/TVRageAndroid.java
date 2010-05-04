package com.mindtechnologies.tvrage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.mindtechnologies.tvrage.model.TVDay;
import com.mindtechnologies.tvrage.model.TVLanguage;
import com.mindtechnologies.tvrage.model.TVRageService;

/**
 * Main TV Rage Guide activity. This Android application only shows the new TV
 * episodes airing through the whole week navigable per day.
 * 
 * @author Mohamed Mansour
 * @since 2010-04-30
 */
public class TVRageAndroid extends Activity implements OnItemClickListener, OnClickListener {
  private static final String TAG = "TVRageService";
  private static final String SETTINGS = "MySettingsFile";
  private static final String PREF_COUNTRY = "country";
  private String[] items;
  private TVRageService service;
  private int day_view_index = 0;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    // Instantiate a new REST service and array of countries for settings.
    service = new TVRageService(getResources().getString(R.string.tvrage_url));
    items = getResources().getStringArray(R.array.countries_array);

    // Setup the ListView of shows.
    ListView lv = getListView();
    lv.setAdapter(new TVRageListAdapter(this, lv.getId()));
    lv.setTextFilterEnabled(true);
    lv.setOnItemClickListener(this);
    
    // Fetch a brand new schedule from the TV Rage REST service.
    doRefresh(getFreshSchedule());
    
    // Add click listeners for the next/previous buttons. 
    getPreviousDayButton().setOnClickListener(this);
    getNextDayButton().setOnClickListener(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.options_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
    case R.id.refresh:
      doRefresh(getFreshSchedule());
      return true;
    case R.id.settings:
      displaySettings();
      return true;
    case R.id.about:
      displayAbout();
      return true;
    }
    return false;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Toast.makeText(getApplicationContext(),
                   ((TextView) view).getText(),
                   Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.next_day:
      onNextDay();
      break;
    case R.id.previous_day:
      onPreviousDay();
      break;
    }
  }

  /**
   * Refresh the list view with the previous day of shows if exists.
   */
  private void onPreviousDay() {
    // We can't go back anymore.
    if (day_view_index == 0) {
      return;
    }
    
    // If we can't go back one more time, then disable the previous button. 
    if (day_view_index == 1) {
      getPreviousDayButton().setEnabled(false);
    }
    
    // Once we reached the end of the list, we can re-enable the next button. 
    if (day_view_index == 6) {
      getNextDayButton().setEnabled(true);
    }
    
    // Render the new day items to the list view.
    doRefresh(service.getDayShows(--day_view_index));
  }

  /**
   * Refresh the list view with the next day of shows if exists.
   */
  private void onNextDay() {
    // We can't go forwards anymore.
    if (day_view_index == 6) {
      return;
    }
    
    // If we can't go forward one more time, then disable the next button. 
    if (day_view_index == 5) {
      getNextDayButton().setEnabled(false);
    }
    
    // Once we reached the beginning of the list, we can re-enable the previous button.
    if (day_view_index == 0) {
      getPreviousDayButton().setEnabled(true);
    }
    
    // Render the new day items to the list view.
    doRefresh(service.getDayShows(++day_view_index));
  }

  /**
   * Helper to get the previous button view.
   * @return previous Button.
   */
  private Button getPreviousDayButton() {
    return (Button)findViewById(R.id.previous_day);
  }

  /**
   * Helper to get the next button view.
   * @return next Button.
   */
  private Button getNextDayButton() {
    return (Button)findViewById(R.id.next_day);
  }
  
  /**
   * Helper to get the schedule list view.
   * @return schedule ListView.
   */
  private ListView getListView() {
    return (ListView)findViewById(R.id.schedule_list);
  }
  
  /**
   * Helper to get the header for the day text.
   * @return title TextView.
   */
  private TextView getHeaderView() {
    return (TextView)findViewById(R.id.day_header);
  }

  /**
   * Inform the REST service that we need to fetch a brand new schedule.
   * @return the requested day of shows.
   */
  private TVDay getFreshSchedule() {
    SharedPreferences settings = getSharedPreferences(SETTINGS, 0);
    String lang = items[settings.getInt(PREF_COUNTRY, 0)];
    service.setLanguage(TVLanguage.valueOf(lang));
    service.fetchSchedule();
    return service.getDayShows(day_view_index);
  }

  /**
   * Inform the ListView adapter that we have new shows to render.
   * @param dayShows the day to render with new shows.
   */
  private void doRefresh(TVDay dayShows) {
    getHeaderView().setText(dayShows.getText());
    TVRageListAdapter adapter = (TVRageListAdapter)getListView().getAdapter();
    adapter.setShows(dayShows.getShows());
    
    // Disable the previous day button if we are starting at index 0.
    if (day_view_index == 0) {
      getPreviousDayButton().setEnabled(false);
    }
    
    // If no shows exist, that implies some sort of connection error.
    if (service.getShows().size() == 0) {
      getPreviousDayButton().setEnabled(false);
      getNextDayButton().setEnabled(false);
    }
  }

  /**
   * Display the about dialog.
   */
  private void displayAbout() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.about_title)
           .setMessage(R.string.about_description)
           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                 dialog.dismiss();
               }
           })
           .create();
    builder.show();
  }

  /**
   * Display the settings dialog.
   */
  private void displaySettings() {
    final SharedPreferences settings = getSharedPreferences(SETTINGS, 0);
    int selected_item = settings.getInt(PREF_COUNTRY, 0);
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.choose_schedule)
           .setSingleChoiceItems(items, selected_item, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                  // Persist the preferences.
                  SharedPreferences.Editor editor = settings.edit();
                  editor.putInt(PREF_COUNTRY, item);
                  editor.commit();
                  dialog.dismiss();

                  // Inform the ListView that we have new items.
                  doRefresh(getFreshSchedule());
                  
                  // Show a toast that we saved successfully.
                  String dismiss_toast = items[item] + " " +
                      getResources().getString(R.string.settings_saved);
                  Toast.makeText(getApplicationContext(),
                                 dismiss_toast,
                                 Toast.LENGTH_SHORT).show();
                }
           })
           .create();
    builder.show();
  }
}