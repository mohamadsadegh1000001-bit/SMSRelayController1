package com.example.smsrelay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BackupActivity extends AppCompatActivity {

    private Button btnBackup, btnRestore, btnExport, btnImport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        btnBackup = findViewById(R.id.btnBackup);
        btnRestore = findViewById(R.id.btnRestore);
        btnExport = findViewById(R.id.btnExport);
        btnImport = findViewById(R.id.btnImport);

        btnBackup.setOnClickListener(v -> backupSettings());
        btnRestore.setOnClickListener(v -> restoreSettings());
        btnExport.setOnClickListener(v -> Toast.makeText(this, "📤 خروجی گرفتن از تنظیمات", Toast.LENGTH_SHORT).show());
        btnImport.setOnClickListener(v -> Toast.makeText(this, "📥 وارد کردن تنظیمات", Toast.LENGTH_SHORT).show());
    }

    private void backupSettings() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences backupPrefs = getSharedPreferences("backup", MODE_PRIVATE);
        SharedPreferences.Editor editor = backupPrefs.edit();
        editor.putString("device_number", prefs.getString("device_number", ""));
        editor.putString("device_password", prefs.getString("device_password", ""));
        editor.putString("auth_numbers", prefs.getString("auth_numbers", ""));
        editor.putString("schedules", prefs.getString("schedules", ""));
        editor.putString("scenes", prefs.getString("scenes", ""));
        editor.putString("timers", prefs.getString("timers", ""));
        editor.apply();
        Toast.makeText(this, "✅ پشتیبان‌گیری انجام شد", Toast.LENGTH_SHORT).show();
    }

    private void restoreSettings() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences backupPrefs = getSharedPreferences("backup", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("device_number", backupPrefs.getString("device_number", ""));
        editor.putString("device_password", backupPrefs.getString("device_password", ""));
        editor.putString("auth_numbers", backupPrefs.getString("auth_numbers", ""));
        editor.putString("schedules", backupPrefs.getString("schedules", ""));
        editor.putString("scenes", backupPrefs.getString("scenes", ""));
        editor.putString("timers", backupPrefs.getString("timers", ""));
        editor.apply();
        Toast.makeText(this, "✅ تنظیمات بازیابی شد", Toast.LENGTH_SHORT).show();
    }
}