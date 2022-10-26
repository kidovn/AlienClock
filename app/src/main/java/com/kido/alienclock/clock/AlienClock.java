package com.kido.alienclock.clock;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import com.kido.alienclock.clock.Clock;
import com.kido.alienclock.clock.model.Config;

import java.time.OffsetDateTime;
import java.util.Calendar;

public class AlienClock extends Clock {
    public static final int MONTH_LENGTH[]
            = {44,42,48,40,48,44,40,44,42,40,40,42,44,48,42,40,44,38}; // 0-based

    public static final int  ONE_SECOND = 500;
    public static final int  ONE_MINUTE = 90*ONE_SECOND;
    public static final int  ONE_HOUR   = 90*ONE_MINUTE;
    public static final long ONE_DAY    = 36*ONE_HOUR;

    private static int EPOCH_YEAR = 2804;
    private static int EPOCH_MONTH = 18;
    private static int EPOCH_DAY = 31;      //de bai sai. Thang thu 18 chi co toi da 38 ngay. Tam set la ngay 31
    private static int EPOCH_HOUR = 2;
    private static int EPOCH_MINUTE = 2;
    private static int EPOCH_SECOND = 88;


    private static long Offset = 0;
    private static long equalUnixTime = 0;

    public static long getDefaultEpochOffset(){
        int day =  EPOCH_YEAR*getDayOfYear();
        for(int i = 0; i<EPOCH_MONTH-1;i++){
            day+=MONTH_LENGTH[i];
        }
        day+=EPOCH_DAY-1;
        long tmp = day*ONE_DAY+ (long) EPOCH_HOUR *ONE_HOUR+ (long) EPOCH_MINUTE *ONE_MINUTE+ (long) EPOCH_SECOND *ONE_SECOND;
//        Log.d("KIDO " ,"getDefaultEpochOffset " + tmp);
        return tmp;
    }
    public static int getDayOfYear(){
        int sum = 0;
        for (int value : MONTH_LENGTH) {
            sum += value;
        }
        return sum;
    }

    public AlienClock(Context context) {
        super(context);
    }

    public AlienClock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlienClock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void calField(){

    }

    @Override
    public int getCurrentMinute() {
        int hourOfDay = (int) ((mCalendar.getTimeInMillis() - equalUnixTime + Offset)%ONE_HOUR);
        return hourOfDay/ONE_MINUTE;
    }

    @Override
    public int getCurrentSecond() {
        int hourOfDay = (int) ((mCalendar.getTimeInMillis() - equalUnixTime+ Offset)%ONE_MINUTE);
        return hourOfDay/ONE_SECOND;
    }

    @Override
    public int getCurrentHour() {
        return getCurrentHourOfDay();
    }

    @Override
    public int getCurrentHourOfDay() {
        int hourOfDay = (int) ((mCalendar.getTimeInMillis() - equalUnixTime+ Offset)%ONE_DAY);
        return hourOfDay/ONE_HOUR;
    }

    @Override
    public int getCurrentDayOfMonth() {
        long dayOfYear = ((mCalendar.getTimeInMillis() - equalUnixTime+ Offset)/ONE_DAY)%getDayOfYear();
//        Log.d("KIDO","dayOfYear " + dayOfYear);
        int sum = 0;
        for (int i= 0; i< MONTH_LENGTH.length;i++) {
            sum += MONTH_LENGTH[i];
            if(dayOfYear<sum){
                return (int) ((dayOfYear-sum+MONTH_LENGTH[i])+1);
            }
        }
        return 1;
    }

    @Override
    public int getCurrentMonth() {
        int dayOfYear = (int) ((mCalendar.getTimeInMillis() - equalUnixTime+ Offset)/ONE_DAY)%getDayOfYear();
        int sum = 0;
        for (int i= 0; i< MONTH_LENGTH.length;i++ ) {
            if(dayOfYear<=sum){
                return i; //start from 1
            }
            sum += MONTH_LENGTH[i];
        }
        return MONTH_LENGTH.length;
    }

    @Override
    public int getCurrentYear() {
        long year = (mCalendar.getTimeInMillis() - equalUnixTime + Offset)/(ONE_DAY*getDayOfYear());
        return (int) year;
    }

    @Override
    int getCurrentAM_PM() {
        return 0;
    }

    public void setConfig(Config config) {
        Offset = config.getOffset();
        equalUnixTime = config.getEqualUnixTime();
    }

//    public void setEqualUnixTime(long milisecond){
//        equalUnixTime = milisecond;
//    }
}
