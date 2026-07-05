package com.example.smsrelay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smsrelay.R;
import com.example.smsrelay.models.Scene;
import java.util.List;

public class SceneAdapter extends RecyclerView.Adapter<SceneAdapter.SceneViewHolder> {

    private Context context;
    private List<Scene> sceneList;
    private OnSceneActionListener listener;

    public interface OnSceneActionListener {
        void onExecute(int position);
        void onDelete(int position);
    }

    public SceneAdapter(Context context, List<Scene> sceneList, OnSceneActionListener listener) {
        this.context = context; this.sceneList = sceneList; this.listener = listener;
    }

    @NonNull @Override public SceneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SceneViewHolder(LayoutInflater.from(context).inflate(R.layout.item_scene, parent, false));
    }

    @Override public void onBindViewHolder(@NonNull SceneViewHolder holder, int position) {
        Scene scene = sceneList.get(position);
        holder.txtSceneName.setText(scene.getName());
        StringBuilder relays = new StringBuilder("رله‌ها: ");
        for (int i = 0; i < scene.getRelayCount(); i++) {
            if (i > 0) relays.append(", ");
            relays.append(scene.getRelayAt(i));
        }
        holder.txtSceneRelays.setText(relays.toString());
        holder.btnExecute.setOnClickListener(v -> { if (listener != null) listener.onExecute(position); });
        holder.btnDelete.setOnClickListener(v -> { if (listener != null) listener.onDelete(position); });
    }

    @Override public int getItemCount() { return sceneList.size(); }

    public static class SceneViewHolder extends RecyclerView.ViewHolder {
        CardView cardScene; TextView txtSceneName, txtSceneRelays; Button btnExecute; ImageButton btnDelete;
        public SceneViewHolder(@NonNull View itemView) { super(itemView);
            cardScene = itemView.findViewById(R.id.cardScene);
            txtSceneName = itemView.findViewById(R.id.txtSceneName);
            txtSceneRelays = itemView.findViewById(R.id.txtSceneRelays);
            btnExecute = itemView.findViewById(R.id.btnExecuteScene);
            btnDelete = itemView.findViewById(R.id.btnDeleteScene);
        }
    }
}