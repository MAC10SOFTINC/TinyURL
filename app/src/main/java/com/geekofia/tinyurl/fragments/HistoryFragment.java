package com.geekofia.tinyurl.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.geekofia.tinyurl.R;
import com.geekofia.tinyurl.adapters.ShortUrlProfileAdapter;
import com.geekofia.tinyurl.models.ShortUrlProfile;
import com.geekofia.tinyurl.viewmodels.ShortUrlProfileViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import static com.geekofia.tinyurl.utils.Functions.clipURL;
import static com.geekofia.tinyurl.utils.Functions.shareURL;
import static com.geekofia.tinyurl.utils.Functions.showStats;

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
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                ShortUrlProfile shortUrlProfile = shortUrlProfileAdapter.getProfileAt(viewHolder.getAdapterPosition());

                shortUrlProfileViewModel.delete(shortUrlProfile);
                Toast.makeText(getContext(), shortUrlProfile.getShortUrl() + " deleted from history", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(mRecyclerView);


        shortUrlProfileAdapter.setOnProfileClickListener(profile -> {
            final String shortURL = profile.getShortUrl();
            final String longURL = profile.getLongUrl();
            final boolean statsEnabled = profile.isStatsEnabled();

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.short_url_profile_click, null);

            // long url
            TextView longUrl = view.findViewById(R.id.tv_long_url);
            longUrl.setText(longURL);

            // qr code
            ImageView qrCodeView = view.findViewById(R.id.iv_qr);
            Glide.with(this)
                    .load("https://api.qrserver.com/v1/create-qr-code/?data=" + shortURL)
                    .placeholder(R.drawable.ic_qr)
                    .into(qrCodeView);

            // short url
            TextView shortUrl = view.findViewById(R.id.tv_short_url);
            shortUrl.setText(shortURL);
            Button copyShort = view.findViewById(R.id.btn_copy);
            Button shareShort = view.findViewById(R.id.btn_share);

            copyShort.setOnClickListener(v -> clipURL(shortURL, getActivity(), getContext()));
            shareShort.setOnClickListener(v -> shareURL(shortURL, getActivity()));

            // stats button
            MaterialButton statsButton = view.findViewById(R.id.btn_stats);

            if (statsEnabled) {
                statsButton.setVisibility(View.VISIBLE);
                statsButton.setOnClickListener(v -> showStats(requireActivity(), shortURL));
            } else  {
                statsButton.setVisibility(View.GONE);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(view);

            builder.show();
        });
    }
}
