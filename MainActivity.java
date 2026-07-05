package com.example.smsrelay;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SmsReceiver.SmsListener {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private String devicePhoneNumber = "";
    private SharedPreferences prefs;
    private boolean nightMode = false;

    private TextView txtResponse, txtSimStatus, txtSignal, txtVoltage, txtNightMode;
    private ImageView imgSimStatus;
    private RecyclerView recyclerRelays;
    private RelayAdapter relayAdapter;
    private List<Relay> relayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        devicePhoneNumber = prefs.getString("device_number", "+989121234567");
        nightMode = prefs.getBoolean("night_mode", false);

        checkPermissions();
        initViews();
        initRelays();
        initButtons();
        initTopButtons();
        initBottomNavigation();
        applyNightMode();

        SmsReceiver.bindListener(this);

        appendResponse("--- SMS Relay Controller v4.0 ---");
        appendResponse("📱 کنترل هوشمند با سناریو و تایمر");
        appendResponse("📞 شماره دستگاه: " + devicePhoneNumber);
        appendResponse("در انتظار دستورات شما...");
    }

    private void initViews() {
        txtResponse = findViewById(R.id.txtResponse);
        txtResponse.setMovementMethod(new ScrollingMovementMethod());
        txtSimStatus = findViewById(R.id.txtSimStatus);
        txtSignal = findViewById(R.id.txtSignal);
        txtVoltage = findViewById(R.id.txtVoltage);
        txtNightMode = findViewById(R.id.txtNightMode);
        imgSimStatus = findViewById(R.id.imgSimStatus);
        recyclerRelays = findViewById(R.id.recyclerRelays);
        recyclerRelays.setLayoutManager(new GridLayoutManager(this, 4));
    }

    private void initRelays() {
        String[] relayNames = new String[8];
        for (int i = 0; i < 8; i++) {
            relayNames[i] = prefs.getString("relay_name_" + i, "رله " + (i + 1));
        }

        relayList.clear();
        for (int i = 0; i < 8; i++) {
            boolean isOn = prefs.getBoolean("relay_state_" + i, false);
            relayList.add(new Relay(i + 1, relayNames[i], isOn));
        }

        relayAdapter = new RelayAdapter(this, relayList, relay -> {
            String command = "TOGGLE" + relay.getNumber();
            sendCommand(command);
            relay.setStatus(!relay.getStatus());
            prefs.edit().putBoolean("relay_state_" + (relay.getNumber() - 1), relay.getStatus()).apply();
            relayAdapter.notifyDataSetChanged();
        });

        recyclerRelays.setAdapter(relayAdapter);
    }

    private void initButtons() {
        Button btnAllOn = findViewById(R.id.btnAllOn);
        Button btnAllOff = findViewById(R.id.btnAllOff);
        Button btnPing = findViewById(R.id.btnPing);
        Button btnStatus = findViewById(R.id.btnStatus);

        btnAllOn.setOnClickListener(v -> { sendCommand("ALLON"); updateAllRelays(true); });
        btnAllOff.setOnClickListener(v -> { sendCommand("ALLOFF"); updateAllRelays(false); });
        btnPing.setOnClickListener(v -> { sendCommand("PING"); appendResponse("> ارسال PING..."); });
        btnStatus.setOnClickListener(v -> { sendCommand("STATUS"); appendResponse("> ارسال STATUS..."); });
    }

    private void initTopButtons() {
        findViewById(R.id.btnNightMode).setOnClickListener(v -> {
            nightMode = !nightMode;
            prefs.edit().putBoolean("night_mode", nightMode).apply();
            applyNightMode();
            Toast.makeText(this, nightMode ? "🌙 حالت شب" : "☀️ حالت روز", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btnScenes).setOnClickListener(v -> startActivity(new Intent(this, ScenesActivity.class)));
        findViewById(R.id.btnTimer).setOnClickListener(v -> startActivity(new Intent(this, TimerActivity.class)));
        findViewById(R.id.btnStats).setOnClickListener(v -> startActivity(new Intent(this, StatsActivity.class)));
        findViewById(R.id.btnSettings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void initBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            else if (id == R.id.nav_scenes) { startActivity(new Intent(this, ScenesActivity.class)); return true; }
            else if (id == R.id.nav_timer) { startActivity(new Intent(this, TimerActivity.class)); return true; }
            else if (id == R.id.nav_schedule) { startActivity(new Intent(this, ScheduleActivity.class)); return true; }
            else if (id == R.id.nav_more) return true;
            return false;
        });
    }

    private void applyNightMode() {
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            txtNightMode.setVisibility(View.VISIBLE);
            txtNightMode.setText("🌙");
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            txtNightMode.setVisibility(View.GONE);
        }
    }

    private void sendCommand(String command) {
        if (devicePhoneNumber.isEmpty()) {
            Toast.makeText(this, "لطفا شماره دستگاه را تنظیم کنید", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(command);
            smsManager.sendMultipartTextMessage(devicePhoneNumber, null, parts, null, null);
            appendResponse("> " + command + " (ارسال)");
            Toast.makeText(this, "✅ " + command, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "❌ خطا: " + e.getMessage(), Toast.LENGTH_LONG).show();
            appendResponse("! خطا: " + e.getMessage());
        }
    }

    private void updateAllRelays(boolean status) {
        for (Relay relay : relayList) { relay.setStatus(status); prefs.edit().putBoolean("relay_state_" + (relay.getNumber() - 1), status).apply(); }
        relayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSmsReceived(String sender, String message) {
        runOnUiThread(() -> { appendResponse("< " + message); processResponse(message); });
    }

    private void processResponse(String message) {
        if (message.contains("Status:") || message.contains("=== Status ===")) parseStatusResponse(message);
        else if (message.contains("PONG")) parsePingResponse(message);
        else if (message.matches("R\\d ON") || message.matches("R\\d OFF")) parseRelayResponse(message);
    }

    private void parseStatusResponse(String message) {
        String[] lines = message.split("\n");
        for (String line : lines) {
            if (line.contains("SIM:")) runOnUiThread(() -> txtSimStatus.setText(line.trim()));
            if (line.contains("dBm")) runOnUiThread(() -> txtSignal.setText(extractSignal(line)));
            if (line.contains("Volt:")) runOnUiThread(() -> txtVoltage.setText(extractVoltage(line)));
            if (line.contains("Relay:")) updateRelaysFromStatus(line);
        }
    }

    private void parsePingResponse(String message) { /* نمایش پاسخ PING */ }
    private void parseRelayResponse(String message) { /* به‌روزرسانی رله */ }

    private void updateRelaysFromStatus(String line) {
        int start = line.indexOf('['); int end = line.indexOf(']');
        if (start >= 0 && end > start) {
            String relayStr = line.substring(start + 1, end);
            String[] parts = relayStr.split(",");
            for (int i = 0; i < Math.min(parts.length, 8); i++) {
                boolean status = parts[i].trim().equals("1");
                relayList.get(i).setStatus(status);
                prefs.edit().putBoolean("relay_state_" + i, status).apply();
            }
            runOnUiThread(() -> relayAdapter.notifyDataSetChanged());
        }
    }

    private String extractSignal(String line) { return ""; }
    private String extractVoltage(String line) { return ""; }

    private void appendResponse(String text) {
        runOnUiThread(() -> {
            String current = txtResponse.getText().toString();
            txtResponse.setText(current + "\n" + text);
            ((ScrollView) txtResponse.getParent()).fullScroll(View.FOCUS_DOWN);
        });
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = { Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS, Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.VIBRATE };
            List<String> needed = new ArrayList<>();
            for (String p : permissions) {
                if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) needed.add(p);
            }
            if (!needed.isEmpty()) ActivityCompat.requestPermissions(this, needed.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int r : grantResults) { if (r != PackageManager.PERMISSION_GRANTED) { allGranted = false; break; } }
            Toast.makeText(this, allGranted ? "✅ همه مجوزها اعطا شدند" : "⚠️ برخی مجوزها اعطا نشدند", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() { super.onResume(); SmsReceiver.bindListener(this); devicePhoneNumber = prefs.getString("device_number", "+989121234567"); initRelays(); }
    @Override
    protected void onDestroy() { super.onDestroy(); SmsReceiver.bindListener(null); }
}