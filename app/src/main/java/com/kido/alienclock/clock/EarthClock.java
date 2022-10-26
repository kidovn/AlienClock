package com.kido.alienclock.clock;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import com.kido.alienclock.clock.model.Config;

import java.util.Calendar;
import java.util.TimeZone;

public class EarthClock extends Clock{
    private long offset;
    private long equalUnixTime;
    public EarthClock(Context context) {
        super(context);
    }

    public EarthClock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EarthClock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    int getCurrentMinute() {
        return mCalendar.get(Calendar.MINUTE);
    }

    @Override
    int getCurrentSecond() {
        return mCalendar.get(Calendar.SECOND);
    }

    @Override
    int getCurrentHour() {
        return mCalendar.get(Calendar.HOUR);
    }

    @Override
    int getCurrentHourOfDay() {
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }

    @Override
    int getCurrentDayOfMonth() {
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    int getCurrentMonth() {
        return mCalendar.get(Calendar.MONTH) + 1; //calendar start at 0
    }

    @Override
    int getCurrentYear() {
        return mCalendar.get(Calendar.YEAR);
    }

    @Override
    int getCurrentAM_PM() {
        return this.mCalendar.get(Calendar.AM_PM);
    }

    public void setConfig(Config config) {
        offset = config.getOffset() - AlienClock.getDefaultEpochOffset();
        equalUnixTime = config.getEqualUnixTime();
    }

    @Override
    protected Calendar getCalendar() {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        long currentTime = calendar.getTimeInMillis() - equalUnixTime;
        calendar.setTimeInMillis(offset);
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
        Log.d("KIDO","OFFSET " + offset + " " + equalUnixTime);
        return calendar;
    }
}
