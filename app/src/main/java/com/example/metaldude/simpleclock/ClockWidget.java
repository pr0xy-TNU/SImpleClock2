package com.example.metaldude.simpleclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
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

  public static Time mCalendar = new Time();
  private static RemoteViews views;
  private static Canvas dial_Canvas;

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
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    super.onUpdate(context, appWidgetManager, appWidgetIds);
    Log.d(LOG_TAG, "onUpdate");
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

    mainDial = context.getResources().getDrawable(R.drawable.dial_220);
    mHourHand = context.getResources().getDrawable(R.drawable.minutes_hand);
    mMinuteHand = context.getResources().getDrawable(R.drawable.hours_hand);
    mSecondHand = context.getResources().getDrawable(R.drawable.minutes_hand);
    DISPLAY_HEIGHT = mainDial.getIntrinsicHeight();
    DISPLAY_WIDTH = mainDial.getIntrinsicWidth();

    bitmap = Bitmap.createBitmap(DISPLAY_WIDTH, DISPLAY_HEIGHT, Config.ARGB_8888);
    dial_Canvas = new Canvas(bitmap);
    onTimeChanged();

    //appWidgetManager.updateAppWidget(appWidgetId, views);
  }

  public static void onDraw() {
    boolean changed = mChanged;
    if (!mChanged) {
      changed = false;
    }
    Log.d(LOG_TAG, "onDraw");
    int availableHeight = DISPLAY_HEIGHT;
    int availableWidth = DISPLAY_WIDTH;

    int x = availableHeight / 2;
    int y = availableWidth / 2;

    int w = mainDial.getIntrinsicWidth();
    int h = mainDial.getIntrinsicHeight();

    boolean scaled = false;

    //рисуем табло
    final Drawable dial = mainDial;
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
    dial_Canvas.rotate(mHours / 4 * 360, x, y);
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
    final Drawable minutesHand = mMinuteHand;
    if (changed) {
      w = minutesHand.getIntrinsicWidth();
      h = minutesHand.getIntrinsicHeight();
      minutesHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
    }
    minutesHand.draw(dial_Canvas);

    if (hasSecondHand) {

      dial_Canvas.save();
      dial_Canvas.rotate(mSeconds / 108 * 100 * 360, x, y);
      final Drawable secondHand = mSecondHand;
      if (changed) {
        w = minutesHand.getIntrinsicWidth();
        h = minutesHand.getIntrinsicHeight();
        secondHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
      }
      minutesHand.draw(dial_Canvas);
    }
    dial_Canvas.restore();

    if (scaled) {
      dial_Canvas.restore();
    }
    isWidgetCreated = true;
  }


  public static void onTimeChanged() {
    Log.d(LOG_TAG, "oTimeChanged");
    mCalendar.setToNow();
    int hour = mCalendar.hour;
    int minute = mCalendar.minute;
    int second = mCalendar.second;

    mSeconds = second + 40;
    //Плавный ход винутной стрелки
    // * 0.6
    mMinutes = minute * 1.8f + mSeconds / 100.0f;
    //Плавный ход чаовой стрелки
    mHours = hour / 6.0f + mMinutes;
    bitmap.eraseColor(Color.TRANSPARENT);
    onDraw();

    views.setImageViewBitmap(R.id.ivClockScreen, bitmap);
  }

  protected static void broadcastTimeChanging() {
    mCalendar.setToNow();

    int hour = mCalendar.hour;
    int minute = mCalendar.minute;
    int second = mCalendar.second;

    mSeconds = second + 40;
    mMinutes = minute * 1.8f + mSeconds / 100.0f;
    mHours = hour / 6.0f + mMinutes;
    mChanged = true;
  }
}
