package com.example.smsrelay;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerHistory;
    private HistoryAdapter historyAdapter;
    private List<CommandHistory> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerHistory = findViewById(R.id.recyclerHistory);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        loadHistory();
        historyAdapter = new HistoryAdapter(this, historyList);
        recyclerHistory.setAdapter(historyAdapter);
    }

    private void loadHistory() {
        SharedPreferences prefs = getSharedPreferences("history", MODE_PRIVATE);
        String historyJson = prefs.getString("history", "");
        if (!historyJson.isEmpty()) {
            String[] items = historyJson.split("\\|");
            for (String item : items) {
                if (!item.isEmpty()) {
                    String[] parts = item.split(",");
                    if (parts.length >= 4) historyList.add(new CommandHistory(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        }
    }
}