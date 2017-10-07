package com.example.metaldude.simpleclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by metalDude on 28.09.2017.
 */

public class ClockWidget extends AppWidgetProvider {

  public static Time mCalendar;
  private static RemoteViews views;
  private static Canvas dial_Canvas;
  private static int timeLeft = 0;
  protected static SharedPreferences ids;

  private static int secondCounter;
  private static int minuteCounter;
  private static int hourCounter;

  /*@Override
  public void onReceive(Context context, Intent intent) {
    String currentAction = intent.getAction();
    if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(currentAction)){
      ClockWidget.initTime();
    }
  }*/

  private static final int MINUTES = 108;
  private static final int SECONDS = 100;

  private static Bitmap bitmap;
  private static int DISPLAY_WIDTH, DISPLAY_HEIGHT;

  private static Drawable mMinuteHand, mSecondHand, mHourHand, mainDial;
  public static boolean isWidgetCreated;
  public static String LOG_TAG = "LOG_TAG";

  private static float mMinutes;
  private static float mHours;
  private static float mSeconds;
  public static boolean mChanged = false;
  public static String APP_WIDGET_ID = " WIDGET_ID";
  static int[] appWidgetIds = null;

  @Override
  public void onEnabled(Context context) {
    super.onEnabled(context);
    ComponentName name = new ComponentName(context, ClockWidget.class);
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    int[] test = appWidgetManager.getAppWidgetIds(name);
    ids = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = ids.edit();
    editor.putInt(APP_WIDGET_ID, test[0]);
    editor.apply();
    editor.commit();
    Log.d(LOG_TAG, String.valueOf(test[0]));
  }

  @Override
  public void onDisabled(Context context) {
    context.stopService(new Intent(context, ClockService.class));
    super.onDisabled(context);
  }

  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    Log.d(LOG_TAG, "THe widget with id " + appWidgetIds[0] + "was deleted");
    context.stopService(new Intent(context, ClockService.class));
    super.onDeleted(context, appWidgetIds);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    super.onUpdate(context, appWidgetManager, appWidgetIds);
    Log.d(LOG_TAG, "onUpdate");

    context.startService(new Intent(context, ClockService.class));
    ClockWidget.appWidgetIds = appWidgetIds;
    updateAppWidget(context, appWidgetManager, appWidgetIds);
  }

  public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
      int[] ids) {
    Log.d(LOG_TAG, "updateAppWidget");

    views = new RemoteViews(context.getPackageName(), R.layout.clock_widget_layout);
    //init resources

    mainDial = context.getResources().getDrawable(R.drawable.dial_294);
    mHourHand = context.getResources().getDrawable(R.drawable.hour_hand_yellow);
    mMinuteHand = context.getResources().getDrawable(R.drawable.minute_hand_yellow);

    mSecondHand = context.getResources().getDrawable(R.drawable.second_red);
    DISPLAY_HEIGHT = mainDial.getIntrinsicHeight();
    DISPLAY_WIDTH = mainDial.getIntrinsicWidth();

    bitmap = Bitmap.createBitmap(DISPLAY_WIDTH, DISPLAY_HEIGHT, Config.ARGB_8888);
    dial_Canvas = new Canvas(bitmap);

    onTimeChanged();
    if (appWidgetIds == null) {
      Log.e(LOG_TAG, "updateAppWidget: widget id null");
      return;
    }
    for (int item : ids) {
      appWidgetManager.updateAppWidget(item, views);
    }
  }

  public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int ids) {
    Log.d(LOG_TAG, "updateAppWidget");

    views = new RemoteViews(context.getPackageName(), R.layout.clock_widget_layout);
    //init resources

    mainDial = context.getResources().getDrawable(R.drawable.dial_294);
    mHourHand = context.getResources().getDrawable(R.drawable.hour_hand_yellow);
    mMinuteHand = context.getResources().getDrawable(R.drawable.minute_hand_yellow);

    mSecondHand = context.getResources().getDrawable(R.drawable.second_red);
    DISPLAY_HEIGHT = mainDial.getIntrinsicHeight();
    DISPLAY_WIDTH = mainDial.getIntrinsicWidth();

    bitmap = Bitmap.createBitmap(DISPLAY_WIDTH, DISPLAY_HEIGHT, Config.ARGB_8888);
    dial_Canvas = new Canvas(bitmap);

    onTimeChanged();
    appWidgetManager.updateAppWidget(ids, views);
  }

  public static void onDraw() {
    boolean changed = mChanged;
    if (changed) {
      mChanged = false;
    }
    Log.d(LOG_TAG, "onDraw");

    int availableHeight = DISPLAY_HEIGHT;
    int availableWidth = DISPLAY_WIDTH;

    int x = availableHeight / 2;
    int y = availableWidth / 2;

    final Drawable dial = mainDial;
    int w = mainDial.getIntrinsicWidth();
    int h = mainDial.getIntrinsicHeight();

    boolean scaled = false;

    if (availableWidth < w || availableHeight < h) {
      scaled = true;
      float scale = Math.min((float) availableWidth / (float) w,
          (float) availableHeight / (float) h);
      dial_Canvas.save();
      dial_Canvas.scale(scale, scale, x, y);
    }

    if (changed) {
      mainDial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
          + (h / 2));
    }
    dial.draw(dial_Canvas);
    dial_Canvas.save();

    dial_Canvas.rotate(mHours / 4.0f * 360, x, y);
    final Drawable hourHand = mHourHand;
    if (changed) {
      w = hourHand.getIntrinsicWidth();
      h = hourHand.getIntrinsicHeight();
      hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
          + (h / 2));
    }
    hourHand.draw(dial_Canvas);
    dial_Canvas.restore();
    dial_Canvas.save();

    dial_Canvas.rotate(mSeconds / 100 * 360, x, y);

    final Drawable secondHand = mSecondHand;
    if (changed) {
      w = secondHand.getIntrinsicWidth();
      h = secondHand.getIntrinsicHeight();
      secondHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
    }
    secondHand.draw(dial_Canvas);
    dial_Canvas.restore();
    dial_Canvas.save();

    dial_Canvas.rotate(mMinutes / 108.0f * 360.0f, x, y);
    final Drawable minuteHand = mMinuteHand;
    if (changed) {
      w = minuteHand.getIntrinsicWidth();
      h = minuteHand.getIntrinsicHeight();
      minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
    }
    minuteHand.draw(dial_Canvas);
    dial_Canvas.restore();

    if (scaled) {
      dial_Canvas.restore();
    }
    isWidgetCreated = true;
    //Log.d(LOG_TAG, "Clock was rendered...");
  }

  public static void initTime() {
    mCalendar = new Time();
    mCalendar.setToNow();
    int ourSeconds = mCalendar.second + mCalendar.minute * 60 + mCalendar.hour * 3600;
    hourCounter = ourSeconds / 10800;
    minuteCounter = (ourSeconds - hourCounter * (MINUTES * SECONDS)) / 108;
    secondCounter = (ourSeconds - hourCounter * MINUTES * SECONDS + minuteCounter * 108) / 100;
    Log.d(LOG_TAG, "Our seconds : " + ourSeconds);
    Log.e(LOG_TAG, "New time is: " + hourCounter + ":" + minuteCounter + ":" + secondCounter);
  }

  public static void onTimeChanged() {
    Log.d(LOG_TAG, "oTimeChanged");
    Log.d(LOG_TAG,
        "Time is(clock):\t" + hourCounter + "\t:\t" + minuteCounter + "\t:\t" + secondCounter
            + ". Time left:"
            + timeLeft);

    //Time logic
    secondCounter++;
    timeLeft++;

    if (secondCounter >= 100) {
      minuteCounter++;
      secondCounter = 1;
    }
    if (minuteCounter >= 108) {
      hourCounter++;
      minuteCounter = 1;
    }
    if (hourCounter >= 4) {
      hourCounter -= 4;
    }
    mSeconds = secondCounter;
    mMinutes = minuteCounter + mSeconds / 100.0f;
    mHours = hourCounter + mMinutes / 108.0f;
    mChanged = true;

    Log.d(LOG_TAG, "Time is(hands):\t " + mHours + "\t:\t" + mMinutes + "\t:\t" + mSeconds);
    bitmap.eraseColor(Color.TRANSPARENT);
    onDraw();
    views.setImageViewBitmap(R.id.ivClockScreen, bitmap);
  }

}
