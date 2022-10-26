package com.kido.alienclock.clock.calendar;


import android.icu.util.Calendar;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class AlienCalendar extends Calendar {

    static final int MONTH_LENGTH[]
            = {44,42,48,40,48,44,40,44,42,40,40,42,44,48,42,40,44,38}; // 0-based

    private static final int  ONE_SECOND = 500;
    private static final int  ONE_MINUTE = 90*ONE_SECOND;
    private static final int  ONE_HOUR   = 90*ONE_MINUTE;
    private static final long ONE_DAY    = 36*ONE_HOUR;

    @Override
    protected int handleGetLimit(int i, int i1) {
        return 0;
    }

    @Override
    protected int handleComputeMonthStart(int i, int i1, boolean b) {
        return 0;
    }

    @Override
    protected int handleGetExtendedYear() {
        return 0;
    }
//    private static final long ONE_WEEK   = 7*ONE_DAY;

//    private transient BaseCalendar.Date gdate;

}
