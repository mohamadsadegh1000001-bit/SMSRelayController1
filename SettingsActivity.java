package com.example.smsrelay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText edtPhoneNumber, edtPassword;
    private EditText[] edtRelayNames = new EditText[8];
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtPassword = findViewById(R.id.edtPassword);

        int[] nameIds = {R.id.edtRelay1, R.id.edtRelay2, R.id.edtRelay3, R.id.edtRelay4,
                         R.id.edtRelay5, R.id.edtRelay6, R.id.edtRelay7, R.id.edtRelay8};
        for (int i = 0; i < 8; i++) edtRelayNames[i] = findViewById(nameIds[i]);

        loadSettings();
        findViewById(R.id.btnSave).setOnClickListener(v -> saveSettings());
    }

    private void loadSettings() {
        edtPhoneNumber.setText(prefs.getString("device_number", "+989121234567"));
        edtPassword.setText(prefs.getString("device_password", "1234"));
        for (int i = 0; i < 8; i++) edtRelayNames[i].setText(prefs.getString("relay_name_" + i, "رله " + (i + 1)));
    }

    private void saveSettings() {
        String phone = edtPhoneNumber.getText().toString().trim();
        if (phone.isEmpty()) { Toast.makeText(this, "لطفا شماره را وارد کنید", Toast.LENGTH_SHORT).show(); return; }
        if (!phone.startsWith("+98")) { if (phone.startsWith("0")) phone = "+98" + phone.substring(1); else if (phone.startsWith("9")) phone = "+98" + phone; else if (phone.length() == 10) phone = "+98" + phone; }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("device_number", phone);
        String password = edtPassword.getText().toString().trim();
        if (!password.isEmpty()) editor.putString("device_password", password);
        for (int i = 0; i < 8; i++) {
            String name = edtRelayNames[i].getText().toString().trim();
            if (name.isEmpty()) name = "رله " + (i + 1);
            editor.putString("relay_name_" + i, name);
        }
        editor.apply();
        Toast.makeText(this, "تنظیمات ذخیره شد", Toast.LENGTH_SHORT).show();
        finish();
    }
}