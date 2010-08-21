package com.mindtechnologies.tvrage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mindtechnologies.tvrage.model.TVDay;
import com.mindtechnologies.tvrage.model.TVLanguage;
import com.mindtechnologies.tvrage.model.TVShow;

/**
 * Main TV Rage Guide activity. This Android application only shows the new TV
 * episodes airing through the whole week navigable per day.
 * 
 * @author Mohamed Mansour
 * @since 2010-04-30
 */
public class TVRageAndroid extends Activity implements OnItemClickListener,
                                                       OnClickListener {
  private static final String TAG = "TVRageAndroid";
  private TVRageServiceExecutor executor;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
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

    // The service executor that takes care of the progress.
    executor = new TVRageServiceExecutor(this);

    // Make sure we fetch the schedule just once.
    TVRageApplication appState = getApp();
    if (!appState.isInitialized()) {
      executor.runAsync();
      appState.setInitialized(true);
    } else {
      onScheduleRefresh(appState.getViewIndexDay());
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
    case R.id.today:
      onScheduleRefresh(0);
      return true;
    case R.id.refresh:
      doRefresh();
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
  public void onItemClick(AdapterView<?> parent, View view, int position,
                          long id) {
    // Show the toast which shows extra information such as show's title.
    TVShow show = ((TVRageListAdapter)parent.getAdapter()).getShow(position);
    Toast.makeText(getApplicationContext(),
                   show.getTitle(),
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
   * Inform the ListView adapter that we have new shows to render.
   * @param day_view_index The day to refresh to.
   */
  public void onScheduleRefresh(int day_view_index) {
    TVRageApplication appState = getApp();
    
    // Save the day index we are about to see.
    appState.setViewIndexDay(day_view_index);
    
    // Show the TV shows for that day.
    TVDay dayShows = appState.getService().getDayShows(day_view_index);
   
    // If any case, something went wrong fetching shows for that day.
    // Just show an empty view instead.
    if (dayShows == null) {
      getHeaderView().setText("Invalid data, refresh!");
    } else {
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
    }
    
    // Control visibility.
    if (day_view_index == 0) {
      getPreviousDayButton().setEnabled(false);
      getNextDayButton().setEnabled(true);
    } else if (day_view_index == 6) {
      getPreviousDayButton().setEnabled(true);
      getNextDayButton().setEnabled(false);
    } else {
      getPreviousDayButton().setEnabled(true);
      getNextDayButton().setEnabled(true);
    }
    
    // If no shows exist, that implies some sort of connection error.
    if (appState.getService().getShows().size() == 0) {
      getPreviousDayButton().setEnabled(false);
      getNextDayButton().setEnabled(false);
    }
  }
  
  /**
   * Refresh the list view with the previous day of shows if exists.
   */
  private void onPreviousDay() {
    int day_view_index = getApp().getViewIndexDay();
    
    // We can't go back anymore.
    if (day_view_index == 0) {
      return;
    }
    
    // Render the new day items to the list view.
    onScheduleRefresh(day_view_index - 1);
  }

  /**
   * Refresh the list view with the next day of shows if exists.
   */
  private void onNextDay() {
    int day_view_index = getApp().getViewIndexDay();
    
    // We can't go forwards anymore.
    if (day_view_index == 6) {
      return;
    }
    
    // Render the new day items to the list view.
    onScheduleRefresh(day_view_index + 1);
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
   * Display the about dialog.
   */
  private void displayAbout() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.about_title)
           .setMessage(R.string.about_description)
           .setPositiveButton(R.string.ok,
                              new DialogInterface.OnClickListener() {
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
    final SharedPreferences settings = 
        getSharedPreferences(TVRageApplication.SETTINGS, 0);
    int selected_item = settings.getInt(TVRageApplication.PREF_COUNTRY, 0);
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.choose_schedule)
           .setSingleChoiceItems(getApp().getItems(),
                                 selected_item,
                                 new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                  SharedPreferences.Editor editor = settings.edit();
                  editor.putInt(TVRageApplication.PREF_COUNTRY, item);
                  editor.commit();
                  dialog.dismiss();
                  doRefresh();
                }
           })
           .create()
           .show();
  }
  
  /**
   * By triggering a langauge change, we are invalidating the cache
   * and forcing a refresh for all the shows.
   */
  private void doRefresh() {
    getApp().getService().setLanguage(getCurrentLanguage());
    executor.runAsync();
  }
  
  /**
   * The current language loaded.
   * @return value of the language.
   */
  public TVLanguage getCurrentLanguage() {
    SharedPreferences settings = 
        getSharedPreferences(TVRageApplication.SETTINGS, 0);
    int position = settings.getInt(TVRageApplication.PREF_COUNTRY, 0);
    return TVLanguage.valueOf(getApp().getItem(position));
  }
  
  /**
   * The main TV Rage application that holds the global constructs.
   * @return Application context.
   */
  public TVRageApplication getApp() {
    return (TVRageApplication)getApplicationContext();
  }
}