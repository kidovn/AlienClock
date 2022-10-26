package com.kido.alienclock.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kido.alienclock.R;
import com.kido.alienclock.clock.model.Alarm;
import com.kido.alienclock.databinding.FragmentDashboardBinding;

import java.util.Random;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    DashboardViewModel dashboardViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.btnScheduleAlarm.setOnClickListener(this::scheduleAlarm);
        binding.btnCancelAlarm.setOnClickListener(this::cancelAlarm);
        binding.tpTimePicker.setIs24HourView(true);
        dashboardViewModel.getAlarm().observe(getViewLifecycleOwner(), alarm -> {
            if(alarm!=null){
                binding.tpTimePicker.setHour(alarm.getHour());
                binding.tpTimePicker.setMinute(alarm.getMinute());
                binding.etAlarmName.setText(alarm.getAlarmName());
                binding.cbRecurringAlarm.setChecked(alarm.isRecurring());
                binding.btnScheduleAlarm.setText(getString(R.string.update_alarm));
                binding.btnCancelAlarm.setVisibility(View.VISIBLE);
            }
            else{
                binding.btnScheduleAlarm.setText(getString(R.string.btn_schedule_alarm_text));
                binding.btnCancelAlarm.setVisibility(View.GONE);
            }

        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void cancelAlarm(final View view) {
        dashboardViewModel.cancelAlarm();
    }
    private void scheduleAlarm(final View view) {

        final int alarmId = new Random().nextInt(Integer.MAX_VALUE);

        final Alarm alarm = new Alarm(
                alarmId,
                binding.etAlarmName.getText().toString(),
                binding.tpTimePicker.getHour(),
                binding.tpTimePicker.getMinute(),
                binding.cbRecurringAlarm.isChecked()
        );
        dashboardViewModel.updateAlarm(alarm);


    }
}