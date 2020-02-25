package com.geekofia.tinyurl.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geekofia.tinyurl.R;
import com.geekofia.tinyurl.adapters.ShortUrlProfileAdapter;
import com.geekofia.tinyurl.models.ShortUrlProfile;
import com.geekofia.tinyurl.viewmodels.ShortUrlProfileViewModel;

public class HistoryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ShortUrlProfileAdapter shortUrlProfileAdapter = new ShortUrlProfileAdapter();
    private ShortUrlProfileViewModel shortUrlProfileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        initViews(view);

        return view;
    }

    private void initViews(View view) {
        mRecyclerView = view.getRootView().findViewById(R.id.recycler_view_urls);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(shortUrlProfileAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        shortUrlProfileViewModel = new ViewModelProvider(getActivity()).get(ShortUrlProfileViewModel.class);
        shortUrlProfileViewModel.getAllProfiles().observe(getViewLifecycleOwner(), shortUrlProfiles -> shortUrlProfileAdapter.submitList(shortUrlProfiles));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                shortUrlProfileViewModel.delete(shortUrlProfileAdapter.getProfileAt(viewHolder.getAdapterPosition()));
//                Toast.makeText(this, profileAdapter.getProfileAt(viewHolder.getAdapterPosition()).getId() + " deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(mRecyclerView);


        shortUrlProfileAdapter.setOnProfileClickListener(new ShortUrlProfileAdapter.onProfileClickListener() {

            @Override
            public void onProfileClick(ShortUrlProfile profile) { }

            @Override
            public void onConnectClick(ShortUrlProfile profile) {
                Toast.makeText(getContext(), "Working ...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
