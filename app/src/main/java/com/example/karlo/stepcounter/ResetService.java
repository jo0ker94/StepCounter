package com.example.karlo.stepcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ResetService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("StepCounterService", "Reset service");
        int value = SharedPrefsUtility.getInt(context, SharedPrefsUtility.COUNT);
        SharedPrefsUtility.addToList(context, SharedPrefsUtility.DAY_COUNTS,
                String.format(Locale.getDefault(), "%s - %d", SharedPrefsUtility.getString(context, SharedPrefsUtility.DAY), value));
        SharedPrefsUtility.putInt(context, SharedPrefsUtility.COUNT, 0);
        String timestamp = getFormattedDate();
        SharedPrefsUtility.putString(context, SharedPrefsUtility.DAY, timestamp);
        StepCounterService.resetCounting(context);
    }

    private String getFormattedDate() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return format1.format(date);

    }
}
