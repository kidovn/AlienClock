package com.kido.alienclock.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.kido.alienclock.R;
import com.kido.alienclock.clock.AlienClock;
import com.kido.alienclock.clock.Clock;
import com.kido.alienclock.clock.EarthClock;
import com.kido.alienclock.clock.enumaration.NumericFormat;
import com.kido.alienclock.clock.model.Config;
import com.kido.alienclock.databinding.FragmentHomeBinding;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    HomeViewModel homeViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final AlienClock alienclock = binding.alienclock;
        alienclock.setShowBorder(true);
        alienclock.setNumericShowSeconds(true);
        alienclock.setNumericFormat(NumericFormat.hour_24);

        homeViewModel.getAlienConfig().observe(getViewLifecycleOwner(), alienclock::setConfig);

        EarthClock earthclock = binding.earthclock;
        earthclock.setShowBorder(true);
        earthclock.setNumericShowSeconds(true);
        earthclock.setNumericFormat(NumericFormat.hour_24);

        homeViewModel.getAlienConfig().observe(getViewLifecycleOwner(), earthclock::setConfig);

        binding.btnEditDate.setOnClickListener(view -> showEditDateDialog());
        binding.btnEditTime.setOnClickListener(view -> showEditTimeDialog());
        binding.btnReset.setOnClickListener(view -> resetToCurrentEarth());

        return root;
    }

    private void resetToCurrentEarth() {
        homeViewModel.setAlienConfig(Config.getDefault());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showEditDateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView =  LayoutInflater.from(getContext()).inflate(R.layout.dialog_date_picker,null);
        TextInputLayout day = dialogView.findViewById(R.id.edtDay);
        TextInputLayout month = dialogView.findViewById(R.id.edtMonth);
        TextInputLayout year = dialogView.findViewById(R.id.edtYear);
        TextView error = dialogView.findViewById(R.id.tv_error);
        day.getEditText().setText(String.valueOf(binding.alienclock.getCurrentDayOfMonth()));
        month.getEditText().setText(String.valueOf(binding.alienclock.getCurrentMonth()));
        year.getEditText().setText(String.valueOf(binding.alienclock.getCurrentYear()));
        builder.setView(dialogView);
        builder.setPositiveButton(android.R.string.ok,(dialogInterface, i) -> {});
        builder.setNegativeButton(android.R.string.cancel,(dialogInterface, i) -> {dialogInterface.dismiss();});
        AlertDialog dialog  = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String monthText = month.getEditText().getText().toString();
            String dayText = day.getEditText().getText().toString();
            String yearText = year.getEditText().getText().toString();
            if(monthText.isEmpty() || dayText.isEmpty() || yearText.isEmpty()){
                error.setText(getString(R.string.error_invalid_data));
            }
            else{
                int dayNumber = Integer.parseInt(dayText);
                int monthNumber = Integer.parseInt(monthText);
                int yearNumber = Integer.parseInt(yearText);
                if(monthNumber>18 || monthNumber==0){
                    error.setText(getString(R.string.error_invalid_month));
                }
                else if(dayNumber == 0 || AlienClock.MONTH_LENGTH[monthNumber-1]<dayNumber){
                    error.setText(getString(R.string.error_invalid_day));
                }
                else {
                    int tmp =  yearNumber*AlienClock.getDayOfYear();
                    for(int j = 0; j<monthNumber-1;j++){
                        tmp+=AlienClock.MONTH_LENGTH[j];
                    }
                    tmp+=dayNumber-1;
                    long tmp2 = tmp*AlienClock.ONE_DAY+
                            (long) binding.alienclock.getCurrentHourOfDay() *AlienClock.ONE_HOUR+
                            (long) binding.alienclock.getCurrentMinute() *AlienClock.ONE_MINUTE+
                            (long) binding.alienclock.getCurrentSecond() *AlienClock.ONE_SECOND;
//                    if(tmp2<AlienClock.getEpochOffset()){
//                        error.setText(getString(R.string.error_invalid_day));
//                    }
//                    binding.alienclock.setOffset(tmp2);
//                    binding.alienclock.setEqualUnixTime(Calendar.getInstance().getTimeInMillis());
                    homeViewModel.setAlienConfig(new Config(tmp2,Calendar.getInstance().getTimeInMillis()));
                    dialog.dismiss();
                }
            }

        });
    }

    private void showEditTimeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView =  LayoutInflater.from(getContext()).inflate(R.layout.dialog_time_picker,null);
        TextInputLayout hour = dialogView.findViewById(R.id.edtHour);
        TextInputLayout minute = dialogView.findViewById(R.id.edtMin);
        TextInputLayout second = dialogView.findViewById(R.id.edtSecond);
        TextView error = dialogView.findViewById(R.id.tv_error);
        hour.getEditText().setText(String.valueOf(binding.alienclock.getCurrentHourOfDay()));
        minute.getEditText().setText(String.valueOf(binding.alienclock.getCurrentMinute()));
        second.getEditText().setText(String.valueOf(binding.alienclock.getCurrentSecond()));
        builder.setView(dialogView);
        builder.setPositiveButton(android.R.string.ok,(dialogInterface, i) -> {});
        builder.setNegativeButton(android.R.string.cancel,(dialogInterface, i) -> {dialogInterface.dismiss();});
        AlertDialog dialog  = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String hourText = hour.getEditText().getText().toString();
            String minText = minute.getEditText().getText().toString();
            String secondTExt = second.getEditText().getText().toString();
            if(hourText.isEmpty() || minText.isEmpty() || secondTExt.isEmpty()){
                error.setText(getString(R.string.error_invalid_data));
            }
            else{
                int hourNumber = Integer.parseInt(hourText);
                int minNumber = Integer.parseInt(minText);
                int secNumber = Integer.parseInt(secondTExt);
                if(hourNumber>35){
                    error.setText(getString(R.string.error_invalid_hour));
                }
                else if( minNumber>89){
                    error.setText(getString(R.string.error_invalid_min));
                }
                else if(secNumber>89){
                    error.setText(getString(R.string.error_invalid_second));
                }
                else{
                    int tmp =  binding.alienclock.getCurrentYear()*AlienClock.getDayOfYear();
                    for(int j = 0; j<binding.alienclock.getCurrentMonth()-1;j++){
                        tmp+=AlienClock.MONTH_LENGTH[j];
                    }
                    tmp+=binding.alienclock.getCurrentDayOfMonth()-1;
                    long tmp2 = tmp*AlienClock.ONE_DAY+ (long) hourNumber *AlienClock.ONE_HOUR+ (long) minNumber *AlienClock.ONE_MINUTE+ (long) secNumber *AlienClock.ONE_SECOND;
//                    if(tmp2<AlienClock.getEpochOffset()){
//                        error.setText(getString(R.string.error_invalid_day));
//                    }
//                    binding.alienclock.setOffset(tmp2);
//                    binding.alienclock.setEqualUnixTime(Calendar.getInstance().getTimeInMillis());
                    homeViewModel.setAlienConfig(new Config(tmp2,Calendar.getInstance().getTimeInMillis()));
                    dialog.dismiss();
                }
            }

        });
    }
}