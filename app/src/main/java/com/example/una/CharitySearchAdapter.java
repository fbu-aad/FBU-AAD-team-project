package com.example.una;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.una.models.Charity;

import java.util.ArrayList;

public class CharitySearchAdapter extends RecyclerView.Adapter<CharitySearchAdapter.ViewHolder> {
    private LayoutInflater inflater;
    ArrayList<Charity> charities;
    Context context;

    // initialize with list
    public CharitySearchAdapter(Context context, ArrayList<Charity> charities) {
        this.charities = charities;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // get the context and create the inflater
        context = parent.getContext();
        inflater = LayoutInflater.from(context);
        // create the view using the item_charity_search layout
        View charityView = inflater.inflate(R.layout.item_charity_search, parent, false);
        // return a new ViewHolder
        return new ViewHolder(charityView);
    }

    // binds an inflated view to a new item
    @Override
    public void onBindViewHolder(@NonNull CharitySearchAdapter.ViewHolder viewHolder, int i) {
        // get the charity data at the specified position
        Charity charity = (Charity) charities.get(i);
        // populate the view with the category data
        viewHolder.tvCharityName.setText(charity.getName());

        // TODO set charity image
    }

    @Override
    public int getItemCount() {
        return charities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCharityImage;
        TextView tvCharityName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCharityImage = (ImageView) itemView.findViewById(R.id.ivCharityImage);
            tvCharityName = (TextView) itemView.findViewById(R.id.charityName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Charity charity = charities.get(position);
                        Intent charityDetail = new Intent(context, CharityDetailsActivity.class);
                        charityDetail.putExtra("ein", charity.getEin());
                        context.startActivity(charityDetail);
                    }
                }
            });
        }
    }
}