package com.example.una.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.R;
import com.example.una.RecyclerViewModels.HorizontalModel;

import java.util.ArrayList;

public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.HorizontalRVViewHolder> {

    private final ArrayList<HorizontalModel> arrayList;
    Context context;
    public HorizontalRecyclerViewAdapter(Context context, ArrayList<HorizontalModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public HorizontalRecyclerViewAdapter.HorizontalRVViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.charity_item_horizontal, viewGroup, false);
        return new HorizontalRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalRecyclerViewAdapter.HorizontalRVViewHolder holder, int currentPosition) {
        final HorizontalModel horizontalModel = arrayList.get(currentPosition);
        holder.tvCharityName.setText(horizontalModel.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, horizontalModel.getName(), Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class HorizontalRVViewHolder extends RecyclerView.ViewHolder {

        TextView tvCharityName;
        ImageView ivCharityImage;
        public HorizontalRVViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCharityName = itemView.findViewById(R.id.tvCharityName);
            ivCharityImage = itemView.findViewById(R.id.ivCharityImage);

        }
    }
}
