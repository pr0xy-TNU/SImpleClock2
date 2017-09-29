package com.example.metaldude.simpleclock;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.Log;
import java.util.TimeZone;


/**
 * Created by metalDude on 29.09.2017.
 */

public class ClockService extends Service {

  private final Handler mHandler = new Handler();
  public static boolean hasSecondHand = false;
  public static String LOG_TAG = "TAG";

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(LOG_TAG, "onStartCommand");
    IntentFilter filter = new IntentFilter();

    filter.addAction(Intent.ACTION_TIME_CHANGED);
    filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

    ClockWidget.onDraw();

    getBaseContext().registerReceiver(mIntentReceiver, filter);
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(LOG_TAG, "onDestroy in service");
    stopSelf();
  }

  //Синхронизация с системным времененем
  private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
      Log.d(LOG_TAG, "onReceive in BroadcastReceiver");
      if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {
        String tz = intent.getStringExtra("time-zone");
        ClockWidget.mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
      }
      //ClockWidget.onTimeChanged();
      ClockWidget.broadcastTimeChanging();

      AppWidgetManager appWidgetManager = AppWidgetManager
          .getInstance(context);
      ClockWidget.updateAppWidget(context,
          appWidgetManager, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

  };
}
