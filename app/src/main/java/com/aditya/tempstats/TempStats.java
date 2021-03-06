package com.aditya.tempstats;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.RemoteViews;

import java.math.BigDecimal;

/**
 * Implementation of App Widget functionality.
 */
public class TempStats extends AppWidgetProvider {

    private static final String SYNC_CLICKED = "automaticWidgetSyncButtonClick";
    private static float prevTemp = 0;
    private static boolean initial = true;
    private static float diff = 0;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        float batteryTempF = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
        String batteryTemp = Float.toString(batteryTempF);

        float cpuTempF = 0;
        String cpuTemp = Float.toString(cpuTempF);


        CharSequence widgetText = "Battery: " + batteryTemp + " " + (char) 0x00B0 + "C";
        CharSequence widgetText2 = "CPU: " + cpuTemp + "C";
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.temp_stats);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews remoteViews;
        ComponentName watchWidget;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.temp_stats);
        watchWidget = new ComponentName(context, TempStats.class);

        remoteViews.setOnClickPendingIntent(R.id.button, getPendingSelfIntent(context, SYNC_CLICKED));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        if (SYNC_CLICKED.equals(intent.getAction())) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews;
            ComponentName watchWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.temp_stats);
            watchWidget = new ComponentName(context, TempStats.class);

            //Get temperature
            intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            float batteryTempF = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;

            //When update is tapped first time set prevTemp equal to current
            if (initial) {
                prevTemp = batteryTempF;
                initial = false;
            } else {
                //Calculate difference and round to 2 places
                if (batteryTempF - prevTemp != 0) {
                    diff = batteryTempF - prevTemp;
                    diff = BigDecimal.valueOf(diff).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                }

                //Update temp
                prevTemp = batteryTempF;
            }

            String batteryTemp = "";

            if (diff > 0) {
                batteryTemp = "Battery: " + batteryTempF + " " + (char) 0x00B0 + "C" + (char) 0x2191 + " " + Math.abs(diff);

            } else if (diff < 0) {
                batteryTemp = "Battery: " + batteryTempF + " " + (char) 0x00B0 + "C" + (char) 0x2193 + " " + Math.abs(diff);

            } else {
                batteryTemp = "Battery: " + batteryTempF + " " + (char) 0x00B0 + "C | 0";
            }
            remoteViews.setTextViewText(R.id.appwidget_text, batteryTemp);

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);

        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

}