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
import com.example.smsrelay.models.AuthNumber;
import java.util.List;

public class AuthAdapter extends RecyclerView.Adapter<AuthAdapter.AuthViewHolder> {

    private Context context;
    private List<AuthNumber> authList;
    private OnAuthDeleteListener listener;

    public interface OnAuthDeleteListener { void onDelete(int position); }

    public AuthAdapter(Context context, List<AuthNumber> authList, OnAuthDeleteListener listener) {
        this.context = context; this.authList = authList; this.listener = listener;
    }

    @NonNull @Override public AuthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AuthViewHolder(LayoutInflater.from(context).inflate(R.layout.item_auth, parent, false));
    }

    @Override public void onBindViewHolder(@NonNull AuthViewHolder holder, int position) {
        AuthNumber auth = authList.get(position);
        holder.txtNumber.setText(auth.getNumber());
        holder.txtStatus.setText(auth.isActive() ? "فعال" : "غیرفعال");
        holder.txtStatus.setTextColor(ContextCompat.getColor(context, auth.isActive() ? R.color.relay_on : R.color.relay_off));
        holder.btnDelete.setOnClickListener(v -> { if (listener != null) listener.onDelete(position); });
    }

    @Override public int getItemCount() { return authList.size(); }

    public static class AuthViewHolder extends RecyclerView.ViewHolder {
        CardView cardAuth; TextView txtNumber, txtStatus; ImageButton btnDelete;
        public AuthViewHolder(@NonNull View itemView) { super(itemView);
            cardAuth = itemView.findViewById(R.id.cardAuth);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}