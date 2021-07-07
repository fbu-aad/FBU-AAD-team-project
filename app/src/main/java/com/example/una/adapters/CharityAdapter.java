package com.example.una.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.una.CharityDetailsActivity;
import com.example.una.CharityNavigatorClient;
import com.example.una.R;
import com.example.una.models.Charity;

import java.util.ArrayList;

public class CharityAdapter extends RecyclerView.Adapter<CharityAdapter.ViewHolder> {
    ArrayList<Object> charities;
    Context context;
    CharityNavigatorClient client;
    String ein;

    // initialize with list
    public CharityAdapter(ArrayList<Object> charities) { this.charities = charities; }

    @NonNull
    @Override
    public CharityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // get the context and create the inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // create the view using the item_category layout
        View charityView = inflater.inflate(R.layout.charity_item_horizontal, parent, false);
        // return a new ViewHolder
        return new CharityAdapter.ViewHolder(charityView);
    }

    // binds an inflated view to a new item
    @Override
    public void onBindViewHolder(@NonNull CharityAdapter.ViewHolder viewHolder, int i) {
        // get the charity data at the specified position
        Charity charity = (Charity) charities.get(i);
        viewHolder.tvCharityName.setText(charity.getName());

        Glide.with(context)
                .load("https://picsum.photos" + "/id/" + charity.getPictureInt() + "/200")
                .into(viewHolder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return charities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCharityName;
        ImageView ivProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCharityName = (TextView) itemView.findViewById(R.id.charityName);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);

            itemView.setOnClickListener(new CharityClickListener());
        }

        class CharityClickListener implements View.OnClickListener {
            @Override
            public void onClick(View view) {
                client = new CharityNavigatorClient(view.getContext());
                int position = getAdapterPosition();
                Charity charity = (Charity) charities.get(position);
                ein = charity.getEin();
                Intent charityDetailsIntent = new Intent(view.getContext(), CharityDetailsActivity.class);
                charityDetailsIntent.putExtra("ein", ein);
                view.getContext().startActivity(charityDetailsIntent);
            }
        }
    }
}
