package com.example.smsrelay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";
    private static SmsListener listener;

    public interface SmsListener {
        void onSmsReceived(String sender, String message);
    }

    public static void bindListener(SmsListener listener) {
        SmsReceiver.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;
        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) return;
        for (Object pdu : pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String message = smsMessage.getDisplayMessageBody();
            Log.d(TAG, "SMS from: " + sender + " - " + message);
            if (isDeviceResponse(message) && listener != null) {
                listener.onSmsReceived(sender, message);
            }
        }
    }

    private boolean isDeviceResponse(String message) {
        String[] keywords = {"Status:", "PONG", "Version:", "=== Status ===", "R", "OK", "ERROR", "Voltage:", "SIM:", "Signal:"};
        for (String keyword : keywords) { if (message.contains(keyword)) return true; }
        return false;
    }
}