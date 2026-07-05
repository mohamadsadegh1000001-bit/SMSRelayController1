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
import com.example.smsrelay.models.Schedule;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private Context context;
    private List<Schedule> scheduleList;
    private OnScheduleDeleteListener listener;

    public interface OnScheduleDeleteListener { void onDelete(int position); }

    public ScheduleAdapter(Context context, List<Schedule> scheduleList, OnScheduleDeleteListener listener) {
        this.context = context; this.scheduleList = scheduleList; this.listener = listener;
    }

    @NonNull @Override public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ScheduleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false));
    }

    @Override public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule s = scheduleList.get(position);
        holder.txtName.setText(s.getName());
        holder.txtRelay.setText("رله " + s.getRelay());
        holder.txtTime.setText(s.getTime());
        holder.txtAction.setText(s.getAction().equals("ON") ? "روشن" : "خاموش");
        holder.txtAction.setTextColor(ContextCompat.getColor(context, s.getAction().equals("ON") ? R.color.relay_on : R.color.relay_off));
        String repeatText = s.getRepeat();
        if (s.getRepeat().equals("هفتگی") && !s.getDayOfWeek().isEmpty()) repeatText += " (" + s.getDayOfWeek() + ")";
        holder.txtRepeat.setText(repeatText);
        holder.btnDelete.setOnClickListener(v -> { if (listener != null) listener.onDelete(position); });
    }

    @Override public int getItemCount() { return scheduleList.size(); }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        CardView cardSchedule; TextView txtName, txtRelay, txtTime, txtAction, txtRepeat; ImageButton btnDelete;
        public ScheduleViewHolder(@NonNull View itemView) { super(itemView);
            cardSchedule = itemView.findViewById(R.id.cardSchedule);
            txtName = itemView.findViewById(R.id.txtScheduleName);
            txtRelay = itemView.findViewById(R.id.txtScheduleRelay);
            txtTime = itemView.findViewById(R.id.txtScheduleTime);
            txtAction = itemView.findViewById(R.id.txtScheduleAction);
            txtRepeat = itemView.findViewById(R.id.txtScheduleRepeat);
            btnDelete = itemView.findViewById(R.id.btnDeleteSchedule);
        }
    }
}