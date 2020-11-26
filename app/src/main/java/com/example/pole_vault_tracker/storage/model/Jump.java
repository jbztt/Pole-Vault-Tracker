package com.example.pole_vault_tracker.storage.model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

public class Jump {
    public static DiffUtil.ItemCallback<Jump> DIFF_CALLBACK = new DiffUtil.ItemCallback<Jump>() {
        @Override
        public boolean areItemsTheSame(@NonNull Jump oldItem, @NonNull Jump newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Jump oldItem, @NonNull Jump newItem) {
            return oldItem.equals(newItem);
        }
    };
    private long id;
    private long gameID;
    private double jumpHeight;
    private boolean success;
    private String description;

    public Jump(long id, long gameID, double jumpHeight, boolean success, String description) {
        this.id = id;
        this.gameID = gameID;
        this.jumpHeight = jumpHeight;
        this.success = success;
        this.description = description;
    }

    public Jump(long gameID) {
        this(0, gameID, 0, false, "");
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGameID() {
        return gameID;
    }

    public void setGameID(long gameID) {
        this.gameID = gameID;
    }

    public double getJumpHeight() {
        return jumpHeight;
    }

    public void setJumpHeight(double jumpHeight) {
        this.jumpHeight = jumpHeight;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Jump jump = (Jump) o;

        if (id != jump.id) return false;
        if (gameID != jump.gameID) return false;
        if (Double.compare(jump.jumpHeight, jumpHeight) != 0) return false;
        if (success != jump.success) return false;
        return Objects.equals(description, jump.description);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (gameID ^ (gameID >>> 32));
        temp = Double.doubleToLongBits(jumpHeight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (success ? 1 : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
