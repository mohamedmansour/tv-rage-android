package com.mindtechnologies.tvrage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class TVRageServiceExecutor implements Runnable {
  private static final String ERROR_TOAST = "error";
  
  private ProgressDialog progressDialog;
  private TVRageAndroid ctx;
  
  public TVRageServiceExecutor(TVRageAndroid ctx) {
    this.ctx = ctx;
  }
  
  /**
   * Shows an error dialog on the view.
   * @param msg The error to show.
   */
  private void showError(String msg) {
    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
    builder.setMessage(msg)
           .setPositiveButton("OK", null)
           .create()
           .show();
  }
  
  /**
   * Handle a progress handler in to the Message thread.
   */
  private Handler progressHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      progressDialog.dismiss();
      
      // Check if an error occurred while fetching schedule.
      if (msg.getData() != null && msg.getData().containsKey(ERROR_TOAST)) {
        showError(msg.getData().getString(ERROR_TOAST));
      } else {
        ctx.onScheduleRefresh(ctx.getApp().getViewIndexDay());
      }
    };
  };

  
  @Override
  public void run() {
    try {
      ctx.getApp().getService().fetchSchedule();
      progressHandler.sendEmptyMessage(0);
    } catch (RuntimeException ex) {
      Bundle data = new Bundle();
      data.putString(ERROR_TOAST, ex.getMessage());
      Message msg = new Message();
      msg.setData(data);
      progressHandler.sendMessage(msg);
    }    
  }
  
  /**
   * Inform the REST service that we need to fetch a brand new schedule.
   */
  public void runAsync() {
    progressDialog = ProgressDialog.show(ctx, "",
        ctx.getResources().getString(R.string.loading_schedule),
        true, false);
    Thread thread = new Thread(this);
    thread.start();
  }
}
