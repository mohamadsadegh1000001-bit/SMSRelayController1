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

public class ScenesActivity extends AppCompatActivity {

    private RecyclerView recyclerScenes;
    private SceneAdapter sceneAdapter;
    private List<Scene> sceneList = new ArrayList<>();
    private SharedPreferences prefs;
    private String devicePhoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenes);
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        devicePhoneNumber = prefs.getString("device_number", "+989121234567");
        initViews();
        loadScenes();
        findViewById(R.id.btnAddScene).setOnClickListener(v -> showAddSceneDialog());
    }

    private void initViews() {
        recyclerScenes = findViewById(R.id.recyclerScenes);
        recyclerScenes.setLayoutManager(new LinearLayoutManager(this));
        sceneAdapter = new SceneAdapter(this, sceneList, this::executeScene, this::deleteScene);
        recyclerScenes.setAdapter(sceneAdapter);
    }

    private void loadScenes() {
        sceneList.clear();
        String scenesJson = prefs.getString("scenes", "");
        if (!scenesJson.isEmpty()) {
            String[] items = scenesJson.split("\\|");
            for (String item : items) {
                if (!item.isEmpty()) {
                    String[] parts = item.split(",");
                    if (parts.length >= 3) {
                        List<Integer> relays = new ArrayList<>();
                        List<Boolean> states = new ArrayList<>();
                        for (int i = 1; i < parts.length; i += 2) {
                            if (i + 1 < parts.length) { relays.add(Integer.parseInt(parts[i])); states.add(parts[i + 1].equals("1")); }
                        }
                        sceneList.add(new Scene(parts[0], relays, states));
                    }
                }
            }
        }
        sceneAdapter.notifyDataSetChanged();
    }

    private void saveScenes() {
        StringBuilder sb = new StringBuilder();
        for (Scene scene : sceneList) {
            if (sb.length() > 0) sb.append("|");
            sb.append(scene.getName());
            for (int i = 0; i < scene.getRelayCount(); i++) {
                sb.append(",").append(scene.getRelayAt(i)).append(",").append(scene.getStateAt(i) ? "1" : "0");
            }
        }
        prefs.edit().putString("scenes", sb.toString()).apply();
    }

    private void showAddSceneDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("افزودن سناریو جدید");
        final EditText nameInput = new EditText(this);
        nameInput.setHint("نام سناریو");
        builder.setView(nameInput);

        String[] relayNames = new String[8];
        for (int i = 0; i < 8; i++) relayNames[i] = prefs.getString("relay_name_" + i, "رله " + (i + 1));
        boolean[] checkedItems = new boolean[8];

        builder.setMultiChoiceItems(relayNames, checkedItems, (dialog, which, isChecked) -> checkedItems[which] = isChecked);

        builder.setPositiveButton("افزودن", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            if (name.isEmpty()) name = "سناریو " + (sceneList.size() + 1);
            List<Integer> relays = new ArrayList<>(); List<Boolean> states = new ArrayList<>();
            for (int i = 0; i < 8; i++) { if (checkedItems[i]) { relays.add(i + 1); states.add(true); } }
            if (relays.isEmpty()) { Toast.makeText(this, "حداقل یک رله را انتخاب کنید", Toast.LENGTH_SHORT).show(); return; }
            StringBuilder cmd = new StringBuilder("SCENE:");
            for (int i = 0; i < relays.size(); i++) cmd.append(relays.get(i)).append(states.get(i) ? "1" : "0").append(",");
            sendCommand(cmd.toString());
            sceneList.add(new Scene(name, relays, states)); saveScenes(); sceneAdapter.notifyDataSetChanged();
            Toast.makeText(this, "✅ سناریو افزوده شد", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("انصراف", null);
        builder.show();
    }

    private void executeScene(int position) {
        Scene scene = sceneList.get(position);
        StringBuilder cmd = new StringBuilder("SCENE:");
        for (int i = 0; i < scene.getRelayCount(); i++) cmd.append(scene.getRelayAt(i)).append(scene.getStateAt(i) ? "1" : "0").append(",");
        sendCommand(cmd.toString());
        Toast.makeText(this, "▶️ اجرای سناریو: " + scene.getName(), Toast.LENGTH_SHORT).show();
    }

    private void deleteScene(int position) {
        Scene scene = sceneList.get(position);
        new MaterialAlertDialogBuilder(this).setTitle("حذف سناریو").setMessage("آیا از حذف \"" + scene.getName() + "\" مطمئن هستید؟")
            .setPositiveButton("حذف", (dialog, which) -> { sceneList.remove(position); saveScenes(); sceneAdapter.notifyDataSetChanged(); Toast.makeText(this, "🗑️ سناریو حذف شد", Toast.LENGTH_SHORT).show(); })
            .setNegativeButton("انصراف", null).show();
    }

    private void sendCommand(String command) {
        try { SmsManager smsManager = SmsManager.getDefault(); ArrayList<String> parts = smsManager.divideMessage(command); smsManager.sendMultipartTextMessage(devicePhoneNumber, null, parts, null, null); }
        catch (Exception e) { Toast.makeText(this, "❌ خطا: " + e.getMessage(), Toast.LENGTH_SHORT).show(); }
    }
}