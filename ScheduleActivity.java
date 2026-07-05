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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerSchedules;
    private ScheduleAdapter scheduleAdapter;
    private List<Schedule> scheduleList = new ArrayList<>();
    private SharedPreferences prefs;
    private String devicePhoneNumber = "";
    private TimePicker timePicker;
    private Spinner spinnerRelay, spinnerAction, spinnerRepeat, spinnerDayOfWeek;
    private EditText edtScheduleName;
    private Button btnAddSchedule;
    private LinearLayout layoutDayOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        devicePhoneNumber = prefs.getString("device_number", "+989121234567");
        initViews();
        loadSchedules();
        btnAddSchedule.setOnClickListener(v -> addSchedule());
    }

    private void initViews() {
        recyclerSchedules = findViewById(R.id.recyclerSchedules);
        recyclerSchedules.setLayoutManager(new LinearLayoutManager(this));
        scheduleAdapter = new ScheduleAdapter(this, scheduleList, this::deleteSchedule);
        recyclerSchedules.setAdapter(scheduleAdapter);

        timePicker = findViewById(R.id.timePicker);
        spinnerRelay = findViewById(R.id.spinnerRelay);
        spinnerAction = findViewById(R.id.spinnerAction);
        spinnerRepeat = findViewById(R.id.spinnerRepeat);
        spinnerDayOfWeek = findViewById(R.id.spinnerDayOfWeek);
        edtScheduleName = findViewById(R.id.edtScheduleName);
        btnAddSchedule = findViewById(R.id.btnAddSchedule);
        layoutDayOfWeek = findViewById(R.id.layoutDayOfWeek);

        String[] relays = {"رله 1", "رله 2", "رله 3", "رله 4", "رله 5", "رله 6", "رله 7", "رله 8"};
        String[] actions = {"روشن", "خاموش"};
        String[] repeats = {"یک بار", "روزانه", "هفتگی"};
        String[] days = {"شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه"};

        ArrayAdapter<String> relayAd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, relays);
        relayAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRelay.setAdapter(relayAd);

        ArrayAdapter<String> actionAd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, actions);
        actionAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAction.setAdapter(actionAd);

        ArrayAdapter<String> repeatAd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, repeats);
        repeatAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepeat.setAdapter(repeatAd);

        ArrayAdapter<String> dayAd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        dayAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDayOfWeek.setAdapter(dayAd);

        spinnerRepeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { layoutDayOfWeek.setVisibility(position == 2 ? View.VISIBLE : View.GONE); }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        Calendar cal = Calendar.getInstance();
        timePicker.setHour(cal.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(cal.get(Calendar.MINUTE));
    }

    private void loadSchedules() {
        scheduleList.clear();
        String schedulesJson = prefs.getString("schedules", "");
        if (!schedulesJson.isEmpty()) {
            String[] items = schedulesJson.split("\\|");
            for (String item : items) {
                if (!item.isEmpty()) {
                    String[] parts = item.split(",");
                    if (parts.length >= 6) {
                        scheduleList.add(new Schedule(parts[0], Integer.parseInt(parts[1]), parts[2], parts[3], parts[4], parts.length > 5 ? parts[5] : ""));
                    }
                }
            }
        }
        scheduleAdapter.notifyDataSetChanged();
    }

    private void saveSchedules() {
        StringBuilder sb = new StringBuilder();
        for (Schedule s : scheduleList) {
            if (sb.length() > 0) sb.append("|");
            sb.append(s.getName()).append(",").append(s.getRelay()).append(",").append(s.getTime()).append(",")
              .append(s.getAction()).append(",").append(s.getRepeat()).append(",").append(s.getDayOfWeek());
        }
        prefs.edit().putString("schedules", sb.toString()).apply();
    }

    private void addSchedule() {
        int relay = spinnerRelay.getSelectedItemPosition() + 1;
        String action = spinnerAction.getSelectedItem().toString();
        String repeat = spinnerRepeat.getSelectedItem().toString();
        String dayOfWeek = spinnerDayOfWeek.getSelectedItem().toString();
        int hour = timePicker.getHour(); int minute = timePicker.getMinute();
        String time = String.format(Locale.US, "%02d:%02d", hour, minute);
        String name = edtScheduleName.getText().toString().trim();
        if (name.isEmpty()) name = "زمان‌بندی " + (scheduleList.size() + 1);

        int actionCode = action.equals("روشن") ? 1 : 0;
        int repeatCode = repeat.equals("روزانه") ? 1 : repeat.equals("هفتگی") ? 2 : 0;
        int dayCode = 0;
        if (repeat.equals("هفتگی")) {
            String[] days = {"شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه"};
            for (int i = 0; i < days.length; i++) { if (days[i].equals(dayOfWeek)) { dayCode = i + 1; break; } }
        }
        sendCommand(String.format("ADDSCHEDULE:%d,%d,%d,%d,%d,%d", relay, hour, minute, actionCode, repeatCode, dayCode));
        scheduleList.add(new Schedule(name, relay, time, actionCode == 1 ? "ON" : "OFF", repeat, dayOfWeek));
        saveSchedules();
        scheduleAdapter.notifyDataSetChanged();
        Toast.makeText(this, "زمان‌بندی افزوده شد", Toast.LENGTH_LONG).show();
        edtScheduleName.setText("");
    }

    private void deleteSchedule(int position) {
        Schedule s = scheduleList.get(position);
        new MaterialAlertDialogBuilder(this).setTitle("حذف زمان‌بندی").setMessage("آیا از حذف \"" + s.getName() + "\" مطمئن هستید؟")
            .setPositiveButton("حذف", (dialog, which) -> { sendCommand("DELSCHEDULE:" + position); scheduleList.remove(position); saveSchedules(); scheduleAdapter.notifyDataSetChanged(); Toast.makeText(this, "زمان‌بندی حذف شد", Toast.LENGTH_SHORT).show(); })
            .setNegativeButton("انصراف", null).show();
    }

    private void sendCommand(String command) {
        try { SmsManager smsManager = SmsManager.getDefault(); ArrayList<String> parts = smsManager.divideMessage(command); smsManager.sendMultipartTextMessage(devicePhoneNumber, null, parts, null, null); }
        catch (Exception e) { Toast.makeText(this, "❌ خطا: " + e.getMessage(), Toast.LENGTH_SHORT).show(); }
    }
}