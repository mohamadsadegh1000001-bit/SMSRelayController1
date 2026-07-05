package com.example.smsrelay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.List;

public class TimerActivity extends AppCompatActivity {

    private RecyclerView recyclerTimers;
    private TimerAdapter timerAdapter;
    private List<Timer> timerList = new ArrayList<>();
    private SharedPreferences prefs;
    private String devicePhoneNumber = "";
    private Spinner spinnerRelay, spinnerAction;
    private NumberPicker numberPickerMinutes;
    private Button btnAddTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        devicePhoneNumber = prefs.getString("device_number", "+989121234567");
        initViews();
        loadTimers();
        btnAddTimer.setOnClickListener(v -> addTimer());
    }

    private void initViews() {
        recyclerTimers = findViewById(R.id.recyclerTimers);
        recyclerTimers.setLayoutManager(new LinearLayoutManager(this));
        timerAdapter = new TimerAdapter(this, timerList, this::deleteTimer);
        recyclerTimers.setAdapter(timerAdapter);

        spinnerRelay = findViewById(R.id.spinnerRelay);
        spinnerAction = findViewById(R.id.spinnerAction);
        numberPickerMinutes = findViewById(R.id.numberPickerMinutes);
        btnAddTimer = findViewById(R.id.btnAddTimer);

        String[] relays = {"رله 1", "رله 2", "رله 3", "رله 4", "رله 5", "رله 6", "رله 7", "رله 8"};
        String[] actions = {"روشن بعد از", "خاموش بعد از"};
        ArrayAdapter<String> relayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, relays);
        relayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRelay.setAdapter(relayAdapter);
        ArrayAdapter<String> actionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, actions);
        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAction.setAdapter(actionAdapter);
        numberPickerMinutes.setMinValue(1); numberPickerMinutes.setMaxValue(120); numberPickerMinutes.setValue(30);
    }

    private void loadTimers() {
        timerList.clear();
        String timersJson = prefs.getString("timers", "");
        if (!timersJson.isEmpty()) {
            String[] items = timersJson.split("\\|");
            for (String item : items) {
                if (!item.isEmpty()) {
                    String[] parts = item.split(",");
                    if (parts.length >= 4) {
                        timerList.add(new Timer(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts[2].equals("1"), parts[3].equals("1")));
                    }
                }
            }
        }
        timerAdapter.notifyDataSetChanged();
    }

    private void saveTimers() {
        StringBuilder sb = new StringBuilder();
        for (Timer t : timerList) {
            if (sb.length() > 0) sb.append("|");
            sb.append(t.getRelay()).append(",").append(t.getMinutes()).append(",").append(t.isAction() ? "1" : "0").append(",").append(t.isActive() ? "1" : "0");
        }
        prefs.edit().putString("timers", sb.toString()).apply();
    }

    private void addTimer() {
        int relay = spinnerRelay.getSelectedItemPosition() + 1;
        int minutes = numberPickerMinutes.getValue();
        boolean action = spinnerAction.getSelectedItemPosition() == 0;
        sendCommand(String.format("TIMER:%d,%d,%s", relay, minutes, action ? "ON" : "OFF"));
        timerList.add(new Timer(relay, minutes, action, true));
        saveTimers();
        timerAdapter.notifyDataSetChanged();
        Toast.makeText(this, "⏱️ تایمر تنظیم شد", Toast.LENGTH_SHORT).show();
    }

    private void deleteTimer(int position) {
        Timer t = timerList.get(position);
        new MaterialAlertDialogBuilder(this).setTitle("لغو تایمر").setMessage("آیا از لغو تایمر رله " + t.getRelay() + " مطمئن هستید؟")
            .setPositiveButton("لغو", (dialog, which) -> { sendCommand("CANCELTIMER:" + t.getRelay()); timerList.remove(position); saveTimers(); timerAdapter.notifyDataSetChanged(); Toast.makeText(this, "⏱️ تایمر لغو شد", Toast.LENGTH_SHORT).show(); })
            .setNegativeButton("انصراف", null).show();
    }

    private void sendCommand(String command) {
        try { SmsManager smsManager = SmsManager.getDefault(); ArrayList<String> parts = smsManager.divideMessage(command); smsManager.sendMultipartTextMessage(devicePhoneNumber, null, parts, null, null); }
        catch (Exception e) { Toast.makeText(this, "❌ خطا: " + e.getMessage(), Toast.LENGTH_SHORT).show(); }
    }
}