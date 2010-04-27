package com.mindtechnologies.tvrage;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.mindtechnologies.tvrage.model.TVRageService;
import com.mindtechnologies.tvrage.model.TVShow;

public class TVRageAndroid extends ListActivity {
  
  private static final String SETTINGS = "MySettingsFile";
  private static final String PREF_COUNTRY = "country";
  private String[] items;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    items = getResources().getStringArray(R.array.countries_array);
    setListAdapter(new TVRageListAdapter(this,
                                         R.layout.list_item,
                                         getSchedule()));
    
    ListView lv = getListView();
    lv.setTextFilterEnabled(true);
    lv.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent,
                              View view,
                              int position,
                              long id) {
        Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
            Toast.LENGTH_SHORT).show();
      }
    });
  }
  
  private List<TVShow> getSchedule() {
    SharedPreferences settings = getSharedPreferences(SETTINGS, 0);
    String url = getResources().getString(R.string.tvrage_url);
    String lang = items[settings.getInt(PREF_COUNTRY, 0)];
    TVRageService service = new TVRageService(url, lang, false);
    service.fetchSchedule();
    return service.getDayShows();
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
      doRefresh();
      return true;
    case R.id.settings:
      displaySettings();
      return true;
    case R.id.about:
      displayAbout();
      return true;
    case R.id.quit:
      doQuit();
      return true;
    }
    return false;
  }
  
  private void doRefresh() {
    TVRageListAdapter adapter = (TVRageListAdapter)getListAdapter();
    adapter.clear();
    adapter.addList(getSchedule());
  }
  
  private void doQuit() {
    TVRageAndroid.this.finish();
  }

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
  
  private void displaySettings() {
    final SharedPreferences settings = getSharedPreferences(SETTINGS, 0);
    int selected_item = settings.getInt(PREF_COUNTRY, 0);
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.choose_schedule)
           .setSingleChoiceItems(items, selected_item, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                  SharedPreferences.Editor editor = settings.edit();
                  editor.putInt(PREF_COUNTRY, item);
                  editor.commit();
                  dialog.dismiss();

                  doRefresh();
                  
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