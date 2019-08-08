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
import com.example.una.CharitySearchListActivity;
import com.example.una.R;
import com.example.una.models.Category;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    // list of categories
    ArrayList<Object> categories;
    // context for rendering
    Context context;

    // initialize with list
    public CategoryAdapter(ArrayList<Object> categories) { this.categories = categories; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // get the context and create the inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // create the view using the item_category layout
        View categoryView = inflater.inflate(R.layout.item_category, parent, false);
        // return a new ViewHolder
        return new ViewHolder(categoryView);
    }

    // binds an inflated view to a new item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        // get the category data at the specified position
        Category category = (Category) categories.get(i);
        // populate the view with the category data
        viewHolder.tvCategoryName.setText(category.categoryName);

        // set ivCategoryImage
        String imageUrl = category.categoryImage;
        Glide.with(context)
                .load(imageUrl)
                .into(viewHolder.ivCategoryImage);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCategoryImage;
        TextView tvCategoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryImage = (ImageView) itemView.findViewById(R.id.ivCategoryImage);
            tvCategoryName = (TextView) itemView.findViewById(R.id.tvCategoryName);
            itemView.setOnClickListener(new CategoryClickListener());
        }

        class CategoryClickListener implements View.OnClickListener {
            @Override
            public void onClick(View view) {
                int categoryItemPosition = getAdapterPosition();
                // getting the category name based on which the user clicked on
                Category category = (Category) categories.get(categoryItemPosition);
                // fire an intent to an activity with a recycler view populated with a list of charities matching each category
                Intent categoryList = new Intent(view.getContext(), CharitySearchListActivity.class);
                categoryList.putExtra("query", category.categoryName);
                view.getContext().startActivity(categoryList);
            }
        }
    }
}