package com.example.karlo.stepcounter;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StepCounterService extends Service implements SensorEventListener {

    public static final String ACTION_UPDATE_TEXT = "action_update_text";
    public static final String EXTRA_COUNTER_VALUE = "counter_value";

    private SensorManager sensorManager;
    private static int value = 0;

    public static void startCounting(Context context) {
        if (isServiceRunning(context)) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, StepCounterService.class));
        } else {
            context.startService(new Intent(context, StepCounterService.class));
        }
    }

    public static void resetCounting(Context context) {
        if (!isServiceRunning(context)) {
            Intent intent = new Intent(context, StepCounterService.class);
            context.startService(intent);
        }
        value = SharedPrefsUtility.getInt(context, SharedPrefsUtility.COUNT);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            startForeground(1, new Notification());
        }
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Log.i("StepCounterService", "Create Service");
        value = SharedPrefsUtility.getInt(this, SharedPrefsUtility.COUNT);
        startCounter();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startCounter() {
        Sensor stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepCounter != null) {
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_FASTEST);
            Log.i("StepCounterService", "Listener registered");
            scheduleAlarm();
        } else {
            Log.i("StepCounterService", "No step counter sensor");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        if (values.length > 0) {
            value += (int) values[0];
        }

        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            SharedPrefsUtility.putInt(this, SharedPrefsUtility.COUNT, value);
            sendBroadcastMessage();
        }

        Log.i("StepCounterService", "Changed new value: " + value);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void sendBroadcastMessage() {
        Intent intent = new Intent(ACTION_UPDATE_TEXT);
        intent.putExtra(EXTRA_COUNTER_VALUE, value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public static boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (StepCounterService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void scheduleAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Intent intent = new Intent(this, ResetService.class);
        String timestamp = getFormattedDate();
        SharedPrefsUtility.putString(this, SharedPrefsUtility.DAY, timestamp);
        intent.setAction(timestamp);
        PendingIntent resetIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, resetIntent);
        Log.i("StepCounterService", "Start Alarm");
    }

    public static void cancelAlarm(Context context, String guid) {
        Intent intent = new Intent(context, ResetService.class);
        intent.setAction(guid);
        PendingIntent operation = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(operation);
    }

    private String getFormattedDate() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return format1.format(date);

    }
}
