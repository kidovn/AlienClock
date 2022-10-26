package com.kido.alienclock.ui.home;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kido.alienclock.clock.AlienClock;
import com.kido.alienclock.clock.model.Config;

import java.util.Calendar;

public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<Config> config; //start time by user
    SharedPreferences sharedPre;
    public HomeViewModel(Application application) {
        super(application);
        this.sharedPre = application.getSharedPreferences("pref_key", Context.MODE_PRIVATE);
        config = new MutableLiveData<>();
        config.setValue(Config.getSavedConfig(sharedPre));
    }

    public LiveData<Config> getAlienConfig() {
        return config;
    }

    public void setAlienConfig(Config value){
        config.postValue(value);
        Config.saveConfig(sharedPre,value);
    }
}