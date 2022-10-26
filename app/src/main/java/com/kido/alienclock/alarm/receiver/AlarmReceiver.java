package com.kido.alienclock.alarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import com.kido.alienclock.alarm.service.AlarmService;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String TITLE = "TITLE";
    @Override
    public void onReceive(final Context context, final Intent intent) {
        startAlarmService(context,intent);

    }

    private void startAlarmService(Context context, Intent intent) {
        Intent intentService = new Intent(context, AlarmService.class);
        intentService.putExtra(TITLE, intent.getStringExtra(TITLE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

}