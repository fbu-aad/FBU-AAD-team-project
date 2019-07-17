package com.example.una.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.una.R;
import com.example.una.RecyclerViewModels.HorizontalModel;
import com.example.una.RecyclerViewModels.VerticalModel;
import com.example.una.adapters.VerticalRecyclerViewAdapter;

import java.util.ArrayList;

public class HomePageFragment extends Fragment {
    public RecyclerView verticalRecyclerView;
    public VerticalRecyclerViewAdapter adapter;
    ArrayList<VerticalModel> arrayListVertical;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        verticalRecyclerView = view.findViewById(R.id.rvCharitiesVertical);
        // makes verticalRecyclerView's height and width immutable no matter what is inserted or removed inside the recyclerView
        verticalRecyclerView.setHasFixedSize(true);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        arrayListVertical = new ArrayList<>();
        adapter = new VerticalRecyclerViewAdapter(getContext(), arrayListVertical);
        verticalRecyclerView.setAdapter(adapter);
        setData();
    }

    private void setData() {
        for (int index = 0; index <= 5; index++) {
            VerticalModel verticalModel = new VerticalModel();
            verticalModel.setTitle("Title: " + index);
            ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();

            for (int j = 0; j < 5; j++) {
                HorizontalModel horizontalModel = new HorizontalModel();
                horizontalModel.setDescription("Description" + j);
                horizontalModel.setName("Name: " + j);
                arrayListHorizontal.add(horizontalModel);
            }
            verticalModel.setArrayList(arrayListHorizontal);

            arrayListVertical.add(verticalModel);
        }
        adapter.notifyDataSetChanged();
    }
}


