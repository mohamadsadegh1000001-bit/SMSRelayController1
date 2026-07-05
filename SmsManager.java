package com.example.smsrelay;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;
import java.util.ArrayList;

public class SmsManager {

    private Context context;
    private String phoneNumber;

    public SmsManager(Context context, String phoneNumber) {
        this.context = context;
        this.phoneNumber = phoneNumber;
    }

    public void sendCommand(String command) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Toast.makeText(context, "لطفا شماره دستگاه را تنظیم کنید", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(command);
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
            Toast.makeText(context, "✅ " + command, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "❌ خطا: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}