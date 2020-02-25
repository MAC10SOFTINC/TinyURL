package com.geekofia.tinyurl.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.geekofia.tinyurl.R;
import com.geekofia.tinyurl.adapters.ShortUrlAdapter;
import com.geekofia.tinyurl.interfaces.ShortenApi;
import com.geekofia.tinyurl.models.ShortUrl;
import com.geekofia.tinyurl.models.ShortUrlProfile;
import com.geekofia.tinyurl.viewmodels.ShortUrlProfileViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText mEditTextLongURL, mEditTextShortURL;
    private MaterialButton buttonShorten, buttonShare, buttonCopy;
    private Retrofit mRetrofit;
    private ShortenApi shortenApi;
    private String shortUrl;
    private RecyclerView mRecyclerView;
    private ShortUrlAdapter profileAdapter = new ShortUrlAdapter();
    private ShortUrlProfileViewModel shortUrlProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);

        initViews();
        loadConnectionProfiles();

        mRetrofit = initRetrofit();
        shortenApi = mRetrofit.create(ShortenApi.class);
    }

    private void getShortUrl() {
        String mLongURL = Objects.requireNonNull(mEditTextLongURL.getText()).toString();

        Call<ShortUrl> shortUrlCall = shortenApi.getShortURL("json", mLongURL);

        shortUrlCall.enqueue(new Callback<ShortUrl>() {
            @Override
            public void onResponse(Call<ShortUrl> call, Response<ShortUrl> response) {
                if (response.isSuccessful()) {
//                    Log.d("SHORT_RESPONSE", response.body().toString());
                    if (response.body() != null) {
                        shortUrl = response.body().getShortenedURL();

                        ShortUrlProfile shortUrlProfile = new ShortUrlProfile(shortUrl, mLongURL);
                        mEditTextShortURL.setText(shortUrlProfile.getShortUrl());

                        buttonShare.setEnabled(true);
                        buttonCopy.setEnabled(true);

                        shortUrlProfileViewModel.insert(shortUrlProfile);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Response: " + response.code(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ShortUrl> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Throwable: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Retrofit initRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://is.gd/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void initViews() {
        mEditTextLongURL = findViewById(R.id.edit_text_long_url);
        mEditTextShortURL = findViewById(R.id.edit_text_short_url);

        mEditTextLongURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditTextShortURL.setText("");
                buttonShare.setEnabled(false);
                buttonCopy.setEnabled(false);

                if (s.toString().trim().length() == 0) {
                    buttonShorten.setEnabled(false);
                } else {
                    buttonShorten.setEnabled(true);
                }

                String url = s.toString().trim();
                if (url.startsWith("is.gd") || url.contains("https://is.gd") || url.contains("https://is.gd/")){
                    buttonShorten.setEnabled(false);
                    buttonShare.setEnabled(false);
                    buttonCopy.setEnabled(false);

                    mEditTextShortURL.setText("Can't short our own urls");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonShorten = findViewById(R.id.btn_shorten);
        buttonShare = findViewById(R.id.btn_share);
        buttonCopy = findViewById(R.id.btn_copy);

        buttonShorten.setOnClickListener(this);
        buttonShare.setOnClickListener(this);
        buttonCopy.setOnClickListener(this);

        mRecyclerView = findViewById(R.id.recycler_view_urls);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(profileAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_shorten:
                getShortUrl();
                break;

            case R.id.btn_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shortUrl);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                break;

            case R.id.btn_copy:
                // Gets a handle to the clipboard service.
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);

                // Creates a new text clip to put on the clipboard
                ClipData clip = ClipData.newPlainText("Shortened URL", shortUrl);

                // Set the clipboard's primary clip.
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Copied: " + shortUrl, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Confirmation")
                .setMessage("Do you really want to close the app ?")
                .setPositiveButton("Yeh", (dialog, which) -> finish())
                .setNegativeButton("Nope", null)
                .show();
    }

    private void loadConnectionProfiles() {
        shortUrlProfileViewModel = new ViewModelProvider(this).get(ShortUrlProfileViewModel.class);
        shortUrlProfileViewModel.getAllProfiles().observe(this, profiles -> {
//                Toast.makeText(getContext(), "ON CHANGE CALLED", Toast.LENGTH_SHORT).show();
            profileAdapter.submitList(profiles);
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                shortUrlProfileViewModel.delete(profileAdapter.getProfileAt(viewHolder.getAdapterPosition()));
//                Toast.makeText(this, profileAdapter.getProfileAt(viewHolder.getAdapterPosition()).getId() + " deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(mRecyclerView);


        profileAdapter.setOnProfileClickListener(new ShortUrlAdapter.onProfileClickListener() {

            @Override
            public void onProfileClick(ShortUrlProfile profile) { }

            @Override
            public void onConnectClick(ShortUrlProfile profile) {
                Toast.makeText(MainActivity.this, "Working ...", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
