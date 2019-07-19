package com.example.una.Viewholders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.R;

import butterknife.BindView;

public class StreaksViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ivStreak) ImageView ivStreak;
    public StreaksViewHolder(@NonNull View itemView) {
        super(itemView);
        ivStreak = itemView.findViewById(R.id.ivStreak);
    }

    public ImageView getIvCharityImage() {
        return ivStreak;
    }

    public void setImageView(ImageView ivStreak) {
        this.ivStreak = ivStreak;
    }
}
