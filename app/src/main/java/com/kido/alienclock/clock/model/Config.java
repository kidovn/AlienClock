package com.kido.alienclock.clock.model;

import android.content.SharedPreferences;

import com.kido.alienclock.clock.AlienClock;

public class Config {
    private long offset;
    private long equalUnixTime;
    public Config(long _offset, long unix ){
        this.offset = _offset;
        this.equalUnixTime = unix;
    }

    public long getOffset(){
        return offset;
    }
    public long getEqualUnixTime(){
        return equalUnixTime;
    }

    public static Config getSavedConfig(SharedPreferences prefs){
        long tmp1 = prefs.getLong("offset", AlienClock.getDefaultEpochOffset());
        long tmp2 = prefs.getLong("equalUnixTime",0);
        return new Config(tmp1,tmp2);
    }

    public static Config getDefault(){
        return new Config(AlienClock.getDefaultEpochOffset(),0);
    }

    public static void saveConfig(SharedPreferences prefs,Config config){
        prefs.edit().putLong("offset",config.offset).putLong("equalUnixTime",config.equalUnixTime).apply();
    }
}
