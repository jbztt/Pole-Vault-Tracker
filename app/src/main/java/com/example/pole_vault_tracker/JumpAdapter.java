package com.example.pole_vault_tracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pole_vault_tracker.storage.model.Jump;

import java.util.Locale;
import java.util.function.BiConsumer;

public class JumpAdapter extends ListAdapter<Jump, JumpAdapter.JumpViewHolder> {
    private BiConsumer<Integer, View> onItemClickListener;

    public JumpAdapter() {
        super(Jump.DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public JumpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        JumpViewHolder jumpViewHolder = new JumpViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.jump_list_row, parent, false));
        jumpViewHolder.itemView.setOnClickListener(v -> this.onItemClickListener.accept(jumpViewHolder.getAdapterPosition(), v));
        return jumpViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull JumpViewHolder holder, int position) {
        Jump jump = getItem(position);
        if (jump != null) holder.bindTo(jump);
    }

    public void setOnItemClickListener(BiConsumer<Integer, View> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class JumpViewHolder extends RecyclerView.ViewHolder {
        private final TextView height;
        private final TextView successful;

        public JumpViewHolder(@NonNull View itemView) {
            super(itemView);
            height = itemView.findViewById(R.id.jump_list_row_jump_height_text_view);
            successful = itemView.findViewById(R.id.jump_list_row_jump_success_text_view);
        }

        void bindTo(Jump jump) {
            height.setText(String.format(Locale.getDefault(), "%.2f %s", jump.getJumpHeight(), itemView.getResources().getString(R.string.meters_text)));
            successful.setText(jump.isSuccess() ? R.string.successful_text : R.string.unsuccessful_text);
        }
    }
}
