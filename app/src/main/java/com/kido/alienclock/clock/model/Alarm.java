package com.kido.alienclock.clock.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.kido.alienclock.alarm.receiver.AlarmReceiver;
import com.kido.alienclock.clock.AlienClock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class Alarm {
    private int alarmId;

    private String alarmName;

    private int hour;
    private int minute;

    private boolean recurring;
    private boolean enabled;


    public Alarm(final int alarmId, final String alarmName, final int hour, final int minute, final boolean recurring) {

        this.alarmId = alarmId;
        this.alarmName = alarmName;
        this.hour = hour;
        this.minute = minute;
        this.recurring = recurring;

    }


    public void schedule(final Context context) {

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("RECURRING", recurring);
        intent.putExtra("ALARM_NAME", alarmName);

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);

        SharedPreferences prefs = context.getSharedPreferences("pref_key", Context.MODE_PRIVATE);
        Config config = Config.getSavedConfig(prefs);

        Calendar calendar =getCurrentCalendar(config);

        Log.d("KIDO", "calendar.getTimeInMillis() " + calendar.getTimeInMillis() + " " +
                getCurrentCalendar(config).getTimeInMillis() + " " + getCurrentCalendar(config).get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() <= getCurrentCalendar(config).getTimeInMillis()) {
            calendar.add(Calendar.DATE,1);
        }

        long millisecond = System.currentTimeMillis()+(calendar.getTimeInMillis()-getCurrentCalendar(config).getTimeInMillis());

        if (recurring) {

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,millisecond , 86400000, pendingIntent); //everyday

        } else {

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millisecond, pendingIntent);

        }

        enabled = true;

    }

    private Calendar getCurrentCalendar(Config config) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        long currentTime = calendar.getTimeInMillis() - config.getEqualUnixTime();
        calendar.setTimeInMillis(config.getOffset()- AlienClock.getDefaultEpochOffset());
        if(currentTime>0) {
            while (currentTime > Integer.MAX_VALUE) {
                calendar.add(Calendar.MILLISECOND, Integer.MAX_VALUE);
                currentTime = currentTime - Integer.MAX_VALUE;
            }
        }
        else if(currentTime<0){
            while (currentTime < Integer.MIN_VALUE) {
                calendar.add(Calendar.MILLISECOND, Integer.MIN_VALUE);
                currentTime = currentTime - Integer.MIN_VALUE;
            }
        }
        calendar.add(Calendar.MILLISECOND, (int) (currentTime));

        return calendar;
    }

    public void cancel(final Context context) {

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(PendingIntent.getBroadcast(context, alarmId, new Intent(context, AlarmReceiver.class), 0));

        enabled = false;

    }


    public int getAlarmId() {

        return alarmId;

    }

    public void setAlarmId(final int alarmId) {

        this.alarmId = alarmId;

    }

    public String getAlarmName() {

        return alarmName;

    }

    public void setAlarmName(final String alarmName) {

        this.alarmName = alarmName;

    }

    public boolean isRecurring() {

        return recurring;

    }

    public void setRecurring(final boolean recurring) {

        this.recurring = recurring;

    }

    public int getHour() {

        return hour;

    }

    public void setHour(final int hour) {

        this.hour = hour;

    }

    public int getMinute() {

        return minute;

    }

    public void setMinute(final int minute) {

        this.minute = minute;

    }

}
