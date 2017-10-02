package com.example.metaldude.simpleclock;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by metalDude on 29.09.2017.
 */

public class ClockService extends Service {

    private final Handler mHandler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(ClockWidget.LOG_TAG, "onStartCommand");
        mHandler.postDelayed(updateTimeRun, 1000);
        return START_REDELIVER_INTENT;
    }

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
            Log.d(ClockWidget.LOG_TAG, "updateTimeRun");
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(getApplicationContext());
            ClockWidget.updateAppWidget(getApplicationContext(),
                    appWidgetManager, AppWidgetManager.INVALID_APPWIDGET_ID);
            mHandler.postDelayed(this, 1000);
        }
    };

}
