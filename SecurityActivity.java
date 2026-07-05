package com.example.smsrelay;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SecurityActivity extends AppCompatActivity {

    private Switch switchBiometric, switchPinLock, switchAutoLock;
    private Button btnChangePin;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        prefs = getSharedPreferences("security", MODE_PRIVATE);

        switchBiometric = findViewById(R.id.switchBiometric);
        switchPinLock = findViewById(R.id.switchPinLock);
        switchAutoLock = findViewById(R.id.switchAutoLock);
        btnChangePin = findViewById(R.id.btnChangePin);

        switchBiometric.setChecked(prefs.getBoolean("biometric", false));
        switchPinLock.setChecked(prefs.getBoolean("pin_lock", false));
        switchAutoLock.setChecked(prefs.getBoolean("auto_lock", true));

        switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("biometric", isChecked).apply();
            if (isChecked) setupBiometric();
        });

        switchPinLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("pin_lock", isChecked).apply();
        });

        switchAutoLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("auto_lock", isChecked).apply();
        });

        btnChangePin.setOnClickListener(v -> Toast.makeText(this, "🔑 تغییر PIN", Toast.LENGTH_SHORT).show());
    }

    private void setupBiometric() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Executor executor = Executors.newSingleThreadExecutor();
            BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(SecurityActivity.this, "✅ احراز هویت موفق", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(SecurityActivity.this, "❌ احراز هویت ناموفق", Toast.LENGTH_SHORT).show();
                    }
                });

            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("احراز هویت")
                .setSubtitle("برای ادامه اثر انگشت خود را قرار دهید")
                .setNegativeButtonText("انصراف")
                .build();
            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(this, "⚠️ اثر انگشت در این نسخه پشتیبانی نمی‌شود", Toast.LENGTH_LONG).show();
        }
    }
}