package com.geekofia.tinyurl.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.geekofia.tinyurl.R;
import com.geekofia.tinyurl.interfaces.ShortenApi;
import com.geekofia.tinyurl.models.ShortUrl;
import com.geekofia.tinyurl.models.ShortUrlProfile;
import com.geekofia.tinyurl.repositories.ShortUrlProfileRepo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.geekofia.tinyurl.utils.Functions.clipURL;
import static com.geekofia.tinyurl.utils.Functions.shareURL;

public class ShortenActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText mEditTextLongURL;
    private MaterialButton buttonShorten, buttonShare, buttonCopy;
    private ShortenApi shortenApi;
    private String longUrl, shortUrl;
    private ShortUrlProfileRepo profileRepository;
    private MaterialCheckBox statsCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shorten);

        Intent receivedIntent = getIntent();
        String receivedType = receivedIntent.getType();

        initViews();

        if (receivedType.startsWith("text/")) {
            String longUrl = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);

            if (longUrl != null) {
                mEditTextLongURL.setText(longUrl);
                Retrofit mRetrofit = initRetrofit();
                shortenApi = mRetrofit.create(ShortenApi.class);
            }
        }

        profileRepository = new ShortUrlProfileRepo(this.getApplication());
    }

    private void initViews() {
        mEditTextLongURL = findViewById(R.id.input_url);

        mEditTextLongURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonShare.setEnabled(false);
                buttonCopy.setEnabled(false);

                if (s.toString().trim().length() == 0) {
                    buttonShorten.setEnabled(false);
                    buttonShorten.setText(R.string.str_done);
                } else {
                    buttonShorten.setEnabled(true);
                }

                String url = s.toString().trim();
                if (url.startsWith("is.gd") || url.contains("https://is.gd") || url.contains("https://is.gd/")) {
                    buttonShorten.setEnabled(false);
                    statsCheckBox.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        statsCheckBox = findViewById(R.id.check_d_stats);
        buttonShorten = findViewById(R.id.btn_d_shorten);
        buttonShare = findViewById(R.id.btn_d_share);
        buttonCopy = findViewById(R.id.btn_d_copy);

        buttonShorten.setOnClickListener(this);
        buttonShare.setOnClickListener(this);
        buttonCopy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_d_shorten:
                buttonShorten.setText(R.string.str_in_prog);
                buttonShorten.setEnabled(false);

                if (statsCheckBox.isChecked()) {
                    getShortUrl(true);
                } else {
                    getShortUrl(false);
                }

                break;

            case R.id.btn_d_share:
                shareURL(shortUrl, this);
                break;

            case R.id.btn_d_copy:
                clipURL(shortUrl, this, this);
                break;
        }
    }

    private Retrofit initRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://is.gd/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void getShortUrl(boolean statsEnabled) {
        if (longUrl == null) {
            longUrl = Objects.requireNonNull(mEditTextLongURL.getText()).toString();
        }

        Call<ShortUrl> shortUrlCall;

        if (statsEnabled) {
            shortUrlCall = shortenApi.getShortURLStats("json", longUrl, 1);
        } else {
            shortUrlCall = shortenApi.getShortURL("json", longUrl);
        }


        shortUrlCall.enqueue(new Callback<ShortUrl>() {
            @Override
            public void onResponse(Call<ShortUrl> call, Response<ShortUrl> response) {
                if (response.isSuccessful()) {
//                    Log.d("SHORT_RESPONSE", response.body().toString());
                    if (response.body() != null) {
                        shortUrl = response.body().getShortenedURL();

                        if (shortUrl != null) {
                            ShortUrlProfile shortUrlProfile = new ShortUrlProfile(shortUrl, longUrl, statsEnabled);
                            mEditTextLongURL.setText(shortUrlProfile.getShortUrl());

                            statsCheckBox.setVisibility(View.GONE);
                            buttonShorten.setVisibility(View.GONE);
                            buttonShare.setVisibility(View.VISIBLE);
                            buttonCopy.setVisibility(View.VISIBLE);
                            buttonShare.setEnabled(true);
                            buttonCopy.setEnabled(true);

                            profileRepository.insert(shortUrlProfile);
                        } else {
                            Toast.makeText(getBaseContext(), "Can't short this URL ;(", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Response: " + response.code(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ShortUrl> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Throwable: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
