package com.example.smsrelay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.List;

public class AuthActivity extends AppCompatActivity {

    private RecyclerView recyclerAuth;
    private AuthAdapter authAdapter;
    private List<AuthNumber> authList = new ArrayList<>();
    private SharedPreferences prefs;
    private String devicePhoneNumber = "";
    private EditText edtPhoneNumber;
    private Button btnAddNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        devicePhoneNumber = prefs.getString("device_number", "+989121234567");
        initViews();
        loadAuthNumbers();
        btnAddNumber.setOnClickListener(v -> addNumber());
    }

    private void initViews() {
        recyclerAuth = findViewById(R.id.recyclerAuth);
        recyclerAuth.setLayoutManager(new LinearLayoutManager(this));
        authAdapter = new AuthAdapter(this, authList, this::deleteNumber);
        recyclerAuth.setAdapter(authAdapter);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        btnAddNumber = findViewById(R.id.btnAddNumber);
    }

    private void loadAuthNumbers() {
        authList.clear();
        String numbersJson = prefs.getString("auth_numbers", "");
        if (!numbersJson.isEmpty()) {
            String[] items = numbersJson.split("\\|");
            for (String item : items) {
                if (!item.isEmpty()) {
                    String[] parts = item.split(",");
                    if (parts.length >= 2) authList.add(new AuthNumber(parts[0], Boolean.parseBoolean(parts[1])));
                }
            }
        }
        authAdapter.notifyDataSetChanged();
    }

    private void saveAuthNumbers() {
        StringBuilder sb = new StringBuilder();
        for (AuthNumber auth : authList) {
            if (sb.length() > 0) sb.append("|");
            sb.append(auth.getNumber()).append(",").append(auth.isActive());
        }
        prefs.edit().putString("auth_numbers", sb.toString()).apply();
    }

    private void addNumber() {
        String number = edtPhoneNumber.getText().toString().trim();
        if (number.isEmpty()) { Toast.makeText(this, "لطفا شماره را وارد کنید", Toast.LENGTH_SHORT).show(); return; }
        if (!number.startsWith("+")) { if (number.startsWith("0")) number = "+98" + number.substring(1); else if (number.startsWith("9")) number = "+98" + number; else if (number.length() == 10) number = "+98" + number; }
        for (AuthNumber auth : authList) { if (auth.getNumber().equals(number)) { Toast.makeText(this, "این شماره قبلاً اضافه شده است", Toast.LENGTH_SHORT).show(); return; } }
        sendCommand("ADDAUTH:" + number);
        authList.add(new AuthNumber(number, true));
        saveAuthNumbers();
        authAdapter.notifyDataSetChanged();
        Toast.makeText(this, "شماره مجاز افزوده شد", Toast.LENGTH_SHORT).show();
        edtPhoneNumber.setText("");
    }

    private void deleteNumber(int position) {
        AuthNumber auth = authList.get(position);
        new MaterialAlertDialogBuilder(this).setTitle("حذف شماره مجاز").setMessage("آیا از حذف شماره \"" + auth.getNumber() + "\" مطمئن هستید؟")
            .setPositiveButton("حذف", (dialog, which) -> { sendCommand("DELAUTH:" + auth.getNumber()); authList.remove(position); saveAuthNumbers(); authAdapter.notifyDataSetChanged(); Toast.makeText(this, "شماره مجاز حذف شد", Toast.LENGTH_SHORT).show(); })
            .setNegativeButton("انصراف", null).show();
    }

    private void sendCommand(String command) {
        try { SmsManager smsManager = SmsManager.getDefault(); ArrayList<String> parts = smsManager.divideMessage(command); smsManager.sendMultipartTextMessage(devicePhoneNumber, null, parts, null, null); }
        catch (Exception e) { Toast.makeText(this, "❌ خطا: " + e.getMessage(), Toast.LENGTH_SHORT).show(); }
    }
}