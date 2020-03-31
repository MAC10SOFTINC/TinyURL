package com.geekofia.tinyurl.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.material.textfield.TextInputEditText;

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


        shortUrlProfileAdapter.setOnProfileClickListener(new ShortUrlProfileAdapter.onProfileClickListener() {

            @Override
            public void onProfileClick(ShortUrlProfile profile) {
//                Toast.makeText(getContext(), "Profile is clicked", Toast.LENGTH_SHORT).show();
                final String shortURL = profile.getShortUrl();
                final String longURL = profile.getLongUrl();

                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.short_url_profile_click, null);

                // long url
                TextInputEditText longUrl = view.findViewById(R.id.dialog_long_url);
                longUrl.setText(profile.getLongUrl());
                Button copyLong = view.findViewById(R.id.dialog_copy_long);
                Button shareLong = view.findViewById(R.id.dialog_share_long);

                copyLong.setOnClickListener(v -> clipURL(longURL));
                shareLong.setOnClickListener(v -> {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, longURL);
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, "Share " + longURL + " with");
                    startActivity(shareIntent);
                });

                // short url
                TextInputEditText shortUrl = view.findViewById(R.id.dialog_short_url);
                shortUrl.setText(profile.getShortUrl());
                Button copyShort = view.findViewById(R.id.dialog_copy_short);
                Button shareShort = view.findViewById(R.id.dialog_share_short);

                copyShort.setOnClickListener(v -> clipURL(shortURL));
                shareShort.setOnClickListener(v -> {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shortURL);
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, "Share " + shortURL + " with");
                    startActivity(shareIntent);
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(view);
//                builder.setTitle(String.valueOf(profile.getId()));
//                builder.setIcon(R.drawable.ic_launcher_foreground);

                builder.show();
            }

//            @Override
//            public void onButtonClick(ShortUrlProfile profile) {
//                clipURL(profile.getShortUrl());
//            }
        });
    }

    private void clipURL(String shortUrl) {
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("Shortened URL", shortUrl);

        // Set the clipboard's primary clip.
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Copied: " + shortUrl, Toast.LENGTH_SHORT).show();
        }
    }
}
