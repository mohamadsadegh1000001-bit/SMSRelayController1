package com.example.smsrelay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StatsActivity extends AppCompatActivity {

    private TextView txtUptime, txtTotalCommands, txtTotalResets, txtTotalSMS;
    private TextView txtAvgVoltage, txtTotalScenes, txtTotalTimers, txtTotalSchedules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        SharedPreferences prefs = getSharedPreferences("stats", MODE_PRIVATE);

        txtUptime = findViewById(R.id.txtUptime);
        txtTotalCommands = findViewById(R.id.txtTotalCommands);
        txtTotalResets = findViewById(R.id.txtTotalResets);
        txtTotalSMS = findViewById(R.id.txtTotalSMS);
        txtAvgVoltage = findViewById(R.id.txtAvgVoltage);
        txtTotalScenes = findViewById(R.id.txtTotalScenes);
        txtTotalTimers = findViewById(R.id.txtTotalTimers);
        txtTotalSchedules = findViewById(R.id.txtTotalSchedules);

        txtUptime.setText("آپ‌تایم: " + prefs.getLong("uptime", 0) + " ثانیه");
        txtTotalCommands.setText("دستورات اجرا شده: " + prefs.getInt("total_commands", 0));
        txtTotalResets.setText("تعداد ریست‌ها: " + prefs.getInt("total_resets", 0));
        txtTotalSMS.setText("پیامک‌های ارسال: " + prefs.getInt("total_sms", 0));
        txtAvgVoltage.setText("میانگین ولتاژ: " + prefs.getFloat("avg_voltage", 12.0f) + "V");
        txtTotalScenes.setText("سناریوها: " + prefs.getInt("total_scenes", 0));
        txtTotalTimers.setText("تایمرها: " + prefs.getInt("total_timers", 0));
        txtTotalSchedules.setText("زمان‌بندی‌ها: " + prefs.getInt("total_schedules", 0));
    }
}