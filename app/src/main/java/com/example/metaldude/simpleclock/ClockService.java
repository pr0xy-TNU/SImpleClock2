package com.example.metaldude.simpleclock;

import static com.example.metaldude.simpleclock.ClockWidget.APP_WIDGET_ID;
import static com.example.metaldude.simpleclock.ClockWidget.LOG_TAG;

import android.app.Notification;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * Created by metalDude on 29.09.2017.
 */

public class ClockService extends Service {

  private final Handler mHandler = new Handler();
  protected boolean isServiceRun = false;
  private int id;
  public static int FOREGROUND_SERVICE = 101;

  @Override
  public void onCreate() {
    super.onCreate();
    isServiceRun = true;
    SharedPreferences ids = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    id = ids.getInt(APP_WIDGET_ID, 1);
    ClockWidget.initTime();
    Log.d(LOG_TAG, String.valueOf(id));
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(ClockWidget.LOG_TAG, "onStartCommand");
    Notification notification = new NotificationCompat.Builder(this)
        .setContentTitle("Runic watches")
        .setContentText("Runic watches is start")
        .setSmallIcon(R.drawable.preload_icon)
        .setOngoing(true).build();
    startForeground(ClockService.FOREGROUND_SERVICE, notification);
    mHandler.postDelayed(updateTimeRun, 1000);
    return START_REDELIVER_INTENT;
  }

  @RequiresApi(api = VERSION_CODES.N)
  @Override
  public void onDestroy() {
    super.onDestroy();
    mHandler.removeCallbacks(updateTimeRun);
    Log.d(ClockWidget.LOG_TAG, "onDestroy in service");
    stopSelf();
  }

  private Runnable updateTimeRun = new Runnable() {
    @Override
    public void run() {
      AppWidgetManager appWidgetManager = AppWidgetManager
          .getInstance(getApplicationContext());
      ClockWidget.updateAppWidget(getBaseContext(),
          appWidgetManager, id);
      mHandler.postDelayed(this, 1000);
    }
  };
}
