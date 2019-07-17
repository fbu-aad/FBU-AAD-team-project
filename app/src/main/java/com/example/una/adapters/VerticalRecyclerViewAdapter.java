package com.example.una.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.R;
import com.example.una.RecyclerViewModels.HorizontalModel;
import com.example.una.RecyclerViewModels.VerticalModel;

import java.util.ArrayList;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;

public class VerticalRecyclerViewAdapter extends RecyclerView.Adapter<VerticalRecyclerViewAdapter.VerticalRVViewHolder> {
    Context context;
    ArrayList<VerticalModel> arrayList;
    public VerticalRecyclerViewAdapter(Context context, ArrayList<VerticalModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public VerticalRVViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.charity_item_vertical, viewGroup, false);
        return new VerticalRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalRVViewHolder holder, int index) {
        VerticalModel charity = arrayList.get(index);
        String charityName = charity.getTitle();
        ArrayList<HorizontalModel> currentItem = charity.getArrayList();
        HorizontalRecyclerViewAdapter horizontalRecyclerViewAdapter = new HorizontalRecyclerViewAdapter(context, currentItem);
        holder.rvCharitiesVertical.setLayoutManager(new LinearLayoutManager(context, HORIZONTAL, false));
        holder.rvCharitiesVertical.setAdapter(horizontalRecyclerViewAdapter);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class VerticalRVViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rvCharitiesVertical;
        TextView tvCharityFeatured;
        public VerticalRVViewHolder(@NonNull View itemView) {
            super(itemView);
            rvCharitiesVertical = itemView.findViewById(R.id.rvCharitiesVertical);
            tvCharityFeatured = itemView.findViewById(R.id.tvFeatured);
        }
    }
}
