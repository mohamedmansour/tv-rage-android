package com.mindtechnologies.tvrage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.mindtechnologies.tvrage.service.TVRageCachedServiceImpl;
import com.mindtechnologies.tvrage.service.TVRageService;

/**
 * Main TV Rage Guide activity. This Android application only shows the new TV
 * episodes airing through the whole week navigable per day.
 * 
 * @author Mohamed Mansour
 * @since 2010-04-30
 */
public class TVRageAndroid extends Activity implements OnItemClickListener,
                                                       OnClickListener,
                                                       Runnable {
  private static final String TAG = "TVRageAndroid";
  private static final String SETTINGS = "MySettingsFile";
  private static final String PREF_COUNTRY = "country";
  private String[] items;
  private TVRageService service;
  private ProgressDialog progressDialog;
  private int day_view_index = 0;
  private boolean loaded = false;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    // Instantiate a new REST service and array of countries for settings.
    service = new TVRageCachedServiceImpl(this);
    items = getResources().getStringArray(R.array.countries_array);

    // Setup the ListView of shows.
    ListView lv = getListView();
    lv.setAdapter(new TVRageListAdapter(this, lv.getId()));
    lv.setTextFilterEnabled(true);
    lv.setOnItemClickListener(this);
    
    // Add click listeners for the next/previous buttons. 
    getPreviousDayButton().setOnClickListener(this);
    getNextDayButton().setOnClickListener(this);
    
    // Fetch a brand new schedule from the TV Rage REST service.
    getHeaderView().setText(getResources().getString(R.string.loading));
    
    if (!loaded) {
      fetchNewSchedule();
      loaded = true;
    }
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
      fetchNewSchedule();
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
    day_view_index--;
    doRefresh();
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
    day_view_index++;
    doRefresh();
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
   */
  private void fetchNewSchedule() {
    SharedPreferences settings = getSharedPreferences(SETTINGS, 0);
    String lang = items[settings.getInt(PREF_COUNTRY, 0)];
    service.setLanguage(TVLanguage.valueOf(lang));
    progressDialog = ProgressDialog.show(this, "",
        getResources().getString(R.string.loading_schedule),
        true, false);
    Thread thread = new Thread(this);
    thread.start();
  }

  /**
   * Inform the ListView adapter that we have new shows to render.
   */
  private void doRefresh() {
    TVDay dayShows = service.getDayShows(day_view_index);
    String dateText = dayShows.getDate();
    try {
      Date date = new SimpleDateFormat("yyyy-M-d").parse(dateText);
      dateText = new SimpleDateFormat("EEEE, d MMM yyyy").format(date);
    } catch (ParseException e) {
      Log.e(TAG, "Cannot format the date", e);
    }
    getHeaderView().setText(dateText);
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
                  fetchNewSchedule();
                }
           })
           .create();
    builder.show();
  }

  @Override
  public void run() {
    service.fetchSchedule();
    progressHandler.sendEmptyMessage(0);
  }
  
  /**
   * Handle a progress handler in to the Message thread.
   */
  private Handler progressHandler = new Handler() {
    @Override
    public void handleMessage(android.os.Message msg) {
      progressDialog.dismiss();
      doRefresh();
    };
  };
}