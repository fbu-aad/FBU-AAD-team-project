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
import com.example.una.RecyclerViewModels.HomeFragmentSection;

import java.util.ArrayList;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;

public class VerticalRecyclerViewAdapter extends RecyclerView.Adapter<VerticalRecyclerViewAdapter.VerticalRVViewHolder> {
    Context context;
    ArrayList<HomeFragmentSection> homeFragmentSections;

    public VerticalRecyclerViewAdapter(Context context, ArrayList<HomeFragmentSection> homeFragmentSections) {
        this.homeFragmentSections = homeFragmentSections;
        this.context = context;
    }

    @NonNull
    @Override
    public VerticalRVViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.charity_item_vertical, viewGroup, false);
        return new VerticalRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalRVViewHolder holder, int index) {
        HomeFragmentSection section = homeFragmentSections.get(index);
        holder.sectionTitle.setText(section.getTitle());
        ArrayList<Object> currentItem = section.getArrayList();
        RecyclerView.Adapter recyclerViewAdapter = null;

        if (holder.getItemViewType() == HomeFragmentSection.CHARITY_LIST_TYPE || holder.getItemViewType() == HomeFragmentSection.RECOMMENDED_LIST_TYPE) {
            recyclerViewAdapter = new CharityAdapter(currentItem);
        } else if (holder.getItemViewType() == HomeFragmentSection.CATEGORIES_LIST_TYPE) {
            recyclerViewAdapter = new CategoryAdapter(currentItem);
        }
        holder.sectionItemsRV.setLayoutManager(new LinearLayoutManager(context, HORIZONTAL, false));
        holder.sectionItemsRV.setAdapter(recyclerViewAdapter);
    }

    @Override
    public int getItemCount() {
        return homeFragmentSections.size();
    }

    @Override
    public int getItemViewType(int position) {
        return homeFragmentSections.get(position).getViewType();
    }

    public class VerticalRVViewHolder extends RecyclerView.ViewHolder {
        RecyclerView sectionItemsRV;
        TextView sectionTitle;
        public VerticalRVViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionItemsRV = itemView.findViewById(R.id.rvSectionItems);
            sectionTitle = itemView.findViewById(R.id.tvSectionTitle);
        }
    }
}
