package com.example.metaldude.simpleclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
  private static int secondCounter = 0;
  private static float minuteCounter = 0;
  private static float hourCounter = 0;

  private static Bitmap bitmap;
  private static int DISPLAY_WIDTH, DISPLAY_HEIGHT;

  private static Drawable mMinuteHand, mSecondHand, mHourHand, mainDial;
  public static boolean isWidgetCreated;
  public static String LOG_TAG = "TAG";

  private static float mMinutes;
  private static float mHours;
  private static float mSeconds;
  public static boolean mChanged = false;
  public static boolean hasSecondHand = false;

  @Override
  public void onEnabled(Context context) {
    super.onEnabled(context);
    context.startService(new Intent(context, ClockService.class));
    initTime();
  }

  @Override
  public void onDisabled(Context context) {
    context.stopService(new Intent(context, ClockService.class));
    super.onDisabled(context);
  }

  static int[] appWidgetIds = null;

  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    super.onDeleted(context, appWidgetIds);
    Log.d(LOG_TAG, "THe widget was deleted");
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    super.onUpdate(context, appWidgetManager, appWidgetIds);
    Log.d(LOG_TAG, "onUpdate");
    ClockWidget.appWidgetIds = appWidgetIds;
    final int N = appWidgetIds.length;
    for (int i = 0; i < N; i++) {
      int appWidgetId = appWidgetIds[i];
      updateAppWidget(context, appWidgetManager, appWidgetId);
    }
  }

  public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
      int appWidgetId) {
    Log.d(LOG_TAG, "updateAppWidget");

    views = new RemoteViews(context.getPackageName(), R.layout.clock_widget_layout);
    //init resources

    mainDial = context.getResources().getDrawable(R.drawable.new_dial);
    mHourHand = context.getResources().getDrawable(R.drawable.hour_hand_new);
    mMinuteHand = context.getResources().getDrawable(R.drawable.minute_hand_new);

    mSecondHand = context.getResources().getDrawable(R.drawable.second_hand_new);
    DISPLAY_HEIGHT = mainDial.getIntrinsicHeight();
    DISPLAY_WIDTH = mainDial.getIntrinsicWidth();

    bitmap = Bitmap.createBitmap(DISPLAY_WIDTH, DISPLAY_HEIGHT, Config.ARGB_8888);
    dial_Canvas = new Canvas(bitmap);

    onTimeChanged();
    if (appWidgetIds == null) {
      Log.e(LOG_TAG, "updateAppWidget: widget id null");
      return;
    }
    appWidgetManager.updateAppWidget(appWidgetIds[0], views);
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
    // Рисуем компонентый
    //Рисуем часовую трелку
    dial_Canvas.rotate(mHours / 4.0f * 360, x, y);
    //dial_Canvas.rotate(mHours / 12.0f * 360.0f, x, y);
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

    dial_Canvas.rotate(mMinutes / 108 * 360, x, y);
    //dial_Canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);
    final Drawable minutesHand = mMinuteHand;
    if (changed) {
      w = minutesHand.getIntrinsicWidth();
      h = minutesHand.getIntrinsicHeight();
      minutesHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
    }
    minutesHand.draw(dial_Canvas);
    dial_Canvas.restore();
    dial_Canvas.save();

    //dial_Canvas.rotate(mSeconds / 108 * 100 * 360, x, y);
    dial_Canvas.rotate(mSeconds / 100.0f * 360.0f, x, y);
    final Drawable secondHand = mSecondHand;
    if (changed) {
      w = secondHand.getIntrinsicWidth();
      h = secondHand.getIntrinsicHeight();
      secondHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
    }
    secondHand.draw(dial_Canvas);
    dial_Canvas.restore();

    if (scaled) {
      dial_Canvas.restore();
    }
    isWidgetCreated = true;
  }

  public void initTime() {
    mCalendar = new Time();
    mCalendar.setToNow();
    secondCounter = mCalendar.second;
    minuteCounter = mCalendar.minute * 0.6f;
    int startHourValue = 0;
    if (mCalendar.hour > 12) {
      startHourValue = mCalendar.hour - 12;
    } else {
      startHourValue = mCalendar.hour;
    }
    hourCounter = startHourValue / 3.0f;
  }

  public static void onTimeChanged() {
    Log.d(LOG_TAG, "oTimeChanged");
    Log.d(LOG_TAG, "Time is " + hourCounter + " : " + minuteCounter + " : " + secondCounter + ". ");
    //mCalendar.setToNow();
    //Time logic
    secondCounter++;
    if (secondCounter == 100) {
      secondCounter = 0;
      minuteCounter++;
    }
    if (minuteCounter == 108) {
      minuteCounter = 0;
      hourCounter++;
    }

    mSeconds = secondCounter;
    mMinutes = minuteCounter + secondCounter / 100.0f;
    mHours = hourCounter + minuteCounter / 108.0f;

    mChanged = true;
    bitmap.eraseColor(Color.TRANSPARENT);
    onDraw();
    views.setImageViewBitmap(R.id.ivClockScreen, bitmap);
  }

}
