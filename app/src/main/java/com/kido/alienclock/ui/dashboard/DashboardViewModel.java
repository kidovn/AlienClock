package com.kido.alienclock.ui.dashboard;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.kido.alienclock.clock.model.Alarm;

public class DashboardViewModel extends AndroidViewModel {

    private final MutableLiveData<Alarm> alarm;
    private static final String PREF_KEY_ALARM = "Alarm";
    SharedPreferences sharedPre;
    public DashboardViewModel(Application application) {
        super(application);
        this.sharedPre = application.getSharedPreferences("pref_key", Context.MODE_PRIVATE);
        alarm = new MutableLiveData<>();
        Gson gson = new Gson();
        String json = sharedPre.getString(PREF_KEY_ALARM, "");
        if(!json.isEmpty()){
            alarm.setValue(gson.fromJson(json, Alarm.class));
        }
        else{
            alarm.setValue(null);
        }
    }

    public LiveData<Alarm> getAlarm() {
        return alarm;
    }

    public void updateAlarm(Alarm al) {
        if(alarm.getValue()!=null){
            alarm.getValue().cancel(getApplication());
        }
        al.schedule(getApplication());
        alarm.postValue(al);
        sharedPre.edit().putString(PREF_KEY_ALARM,new Gson().toJson(al)).apply();

    }

    public void cancelAlarm() {
        if(alarm.getValue()!=null){
            alarm.getValue().cancel(getApplication());
        }
        sharedPre.edit().putString(PREF_KEY_ALARM,"").apply();
        alarm.postValue(null);
    }
}