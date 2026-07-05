package com.example.smsrelay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smsrelay.R;
import com.example.smsrelay.models.CommandHistory;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<CommandHistory> historyList;

    public HistoryAdapter(Context context, List<CommandHistory> historyList) {
        this.context = context; this.historyList = historyList;
    }

    @NonNull @Override public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_history, parent, false));
    }

    @Override public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        CommandHistory item = historyList.get(position);
        holder.txtTime.setText(item.getTime());
        holder.txtCommand.setText(item.getCommand());
        holder.txtResponse.setText(item.getResponse());
        holder.txtStatus.setText(item.getStatus());
        holder.txtStatus.setTextColor(ContextCompat.getColor(context, item.getStatus().equals("OK") ? R.color.relay_on : R.color.relay_off));
    }

    @Override public int getItemCount() { return historyList.size(); }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtTime, txtCommand, txtResponse, txtStatus;
        public HistoryViewHolder(@NonNull View itemView) { super(itemView);
            txtTime = itemView.findViewById(R.id.txtHistoryTime);
            txtCommand = itemView.findViewById(R.id.txtHistoryCommand);
            txtResponse = itemView.findViewById(R.id.txtHistoryResponse);
            txtStatus = itemView.findViewById(R.id.txtHistoryStatus);
        }
    }
}