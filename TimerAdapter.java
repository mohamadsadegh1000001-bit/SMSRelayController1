package com.example.smsrelay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smsrelay.R;
import com.example.smsrelay.models.Timer;
import java.util.List;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.TimerViewHolder> {

    private Context context;
    private List<Timer> timerList;
    private OnTimerDeleteListener listener;

    public interface OnTimerDeleteListener { void onDelete(int position); }

    public TimerAdapter(Context context, List<Timer> timerList, OnTimerDeleteListener listener) {
        this.context = context; this.timerList = timerList; this.listener = listener;
    }

    @NonNull @Override public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TimerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_timer, parent, false));
    }

    @Override public void onBindViewHolder(@NonNull TimerViewHolder holder, int position) {
        Timer t = timerList.get(position);
        holder.txtRelay.setText("رله " + t.getRelay());
        holder.txtAction.setText(t.isAction() ? "روشن" : "خاموش");
        holder.txtAction.setTextColor(ContextCompat.getColor(context, t.isAction() ? R.color.relay_on : R.color.relay_off));
        holder.txtMinutes.setText(String.valueOf(t.getMinutes()));
        holder.txtStatus.setText(t.isActive() ? "فعال" : "غیرفعال");
        holder.txtStatus.setTextColor(ContextCompat.getColor(context, t.isActive() ? R.color.relay_on : R.color.relay_off));
        holder.btnDelete.setOnClickListener(v -> { if (listener != null) listener.onDelete(position); });
    }

    @Override public int getItemCount() { return timerList.size(); }

    public static class TimerViewHolder extends RecyclerView.ViewHolder {
        CardView cardTimer; TextView txtRelay, txtAction, txtMinutes, txtStatus; ImageButton btnDelete;
        public TimerViewHolder(@NonNull View itemView) { super(itemView);
            cardTimer = itemView.findViewById(R.id.cardTimer);
            txtRelay = itemView.findViewById(R.id.txtTimerRelay);
            txtAction = itemView.findViewById(R.id.txtTimerAction);
            txtMinutes = itemView.findViewById(R.id.txtTimerMinutes);
            txtStatus = itemView.findViewById(R.id.txtTimerStatus);
            btnDelete = itemView.findViewById(R.id.btnDeleteTimer);
        }
    }
}