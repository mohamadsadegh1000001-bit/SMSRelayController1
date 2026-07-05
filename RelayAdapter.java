package com.example.smsrelay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smsrelay.R;
import com.example.smsrelay.models.Relay;
import java.util.List;

public class RelayAdapter extends RecyclerView.Adapter<RelayAdapter.RelayViewHolder> {

    private Context context;
    private List<Relay> relayList;
    private OnRelayClickListener listener;
    private int lastPosition = -1;

    public interface OnRelayClickListener { void onRelayClick(Relay relay); }

    public RelayAdapter(Context context, List<Relay> relayList, OnRelayClickListener listener) {
        this.context = context; this.relayList = relayList; this.listener = listener;
    }

    @NonNull @Override public RelayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RelayViewHolder(LayoutInflater.from(context).inflate(R.layout.item_relay, parent, false));
    }

    @Override public void onBindViewHolder(@NonNull RelayViewHolder holder, int position) {
        Relay relay = relayList.get(position);
        boolean isOn = relay.getStatus();

        holder.txtRelayName.setText(relay.getName());
        holder.txtRelayNumber.setText(String.valueOf(relay.getNumber()));
        holder.txtRelayStatus.setText(isOn ? "ON" : "OFF");

        if (isOn) {
            holder.txtRelayStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.relay_on));
            holder.cardRelay.setCardBackgroundColor(ContextCompat.getColor(context, R.color.relay_on));
        } else {
            holder.txtRelayStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.relay_off));
            holder.cardRelay.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_background));
        }

        holder.cardRelay.setOnClickListener(v -> { if (listener != null) listener.onRelayClick(relay); });

        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override public int getItemCount() { return relayList.size(); }

    public static class RelayViewHolder extends RecyclerView.ViewHolder {
        CardView cardRelay; TextView txtRelayName, txtRelayNumber, txtRelayStatus;
        public RelayViewHolder(@NonNull View itemView) { super(itemView);
            cardRelay = itemView.findViewById(R.id.cardRelay);
            txtRelayName = itemView.findViewById(R.id.txtRelayName);
            txtRelayNumber = itemView.findViewById(R.id.txtRelayNumber);
            txtRelayStatus = itemView.findViewById(R.id.txtRelayStatus);
        }
    }
}