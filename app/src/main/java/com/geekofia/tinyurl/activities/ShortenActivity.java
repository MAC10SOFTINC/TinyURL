package com.geekofia.tinyurl.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.geekofia.tinyurl.R;
import com.geekofia.tinyurl.interfaces.ShortenApi;
import com.geekofia.tinyurl.models.ShortUrl;
import com.geekofia.tinyurl.models.ShortUrlProfile;
import com.geekofia.tinyurl.repositories.ShortUrlProfileRepo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.geekofia.tinyurl.utils.Functions.clipURL;
import static com.geekofia.tinyurl.utils.Functions.initRetrofitIsGd;
import static com.geekofia.tinyurl.utils.Functions.initRetrofitVGd;
import static com.geekofia.tinyurl.utils.Functions.shareURL;

public class ShortenActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private ImageView mQrCodeView;
    private TextView mTVShortURL;
    private TextInputLayout mTextInputLayoutCustomURL;
    private String mCustomURL;
    private MaterialButton mButtonShorten, mButtonShare, mButtonCopy;
    private ShortenApi shortenApiIsGd;
    private String longUrl, shortUrl;
    private ShortUrlProfileRepo profileRepository;
    private MaterialCheckBox mCheckBoxCustomURL, mCheckBoxStats;
    private boolean isCustomURLChecked, isStatsChecked;
    private final GsonConverterFactory gsonFactory = GsonConverterFactory.create();
    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shorten);

        // get the share intent
        Intent receivedIntent = getIntent();
        String receivedType = receivedIntent.getType();

        if (receivedType.startsWith("text/")) {
            String data = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);

            if (data != null) {
                this.longUrl = data;
                Retrofit mRetrofitIsGd = initRetrofitIsGd(gsonFactory, okHttpClient);
                shortenApiIsGd = mRetrofitIsGd.create(ShortenApi.class);
            }
        }

        profileRepository = new ShortUrlProfileRepo(this.getApplication());

        // initialize views
        if (longUrl != null) {
            initViews();
        }
    }

    private void initViews() {
        // find views
        mQrCodeView = findViewById(R.id.iv_qr);
        TextView mTVLongURL = findViewById(R.id.tv_long_url);
        mTVShortURL = findViewById(R.id.tv_short_url);
        mTextInputLayoutCustomURL = findViewById(R.id.ti_custom_url);
        TextInputEditText mTextInputEditCustomURL = findViewById(R.id.tie_custom_url);
        mButtonShorten = findViewById(R.id.btn_shorten);
        mButtonShare = findViewById(R.id.btn_share);
        mButtonCopy = findViewById(R.id.btn_copy);
        mCheckBoxCustomURL = findViewById(R.id.cb_custom_url);
        mCheckBoxStats = findViewById(R.id.cb_stats);

        // listeners
        mButtonShorten.setOnClickListener(this);
        mButtonShare.setOnClickListener(this);
        mButtonCopy.setOnClickListener(this);
        mCheckBoxCustomURL.setOnCheckedChangeListener(this);
        mCheckBoxStats.setOnCheckedChangeListener(this);

        mTextInputEditCustomURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 5 && editable.length() < 30) {
                    mCustomURL = editable.toString();
                    mButtonShorten.setText(R.string.str_ready);
                    mButtonShorten.setEnabled(true);
                } else {
                    mButtonShorten.setText(R.string.str_ready);
                    mButtonShorten.setEnabled(false);
                }
            }
        });

        // render data
        mTVLongURL.setText(longUrl);
        mButtonShorten.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_shorten) {
            mButtonShorten.setText(R.string.str_in_progress);
            mButtonShorten.setEnabled(false);

            if (isStatsChecked) {
                if (isCustomURLChecked && !mCustomURL.isEmpty()) {
                    getShortUrl(shortenApiIsGd, true, mCustomURL);
                } else {
                    getShortUrl(shortenApiIsGd, true, null);
                }
            } else {
                if (isCustomURLChecked && !mCustomURL.isEmpty()) {
                    getShortUrl(shortenApiIsGd, false, mCustomURL);
                } else {
                    getShortUrl(shortenApiIsGd, false, null);
                }
            }

        }

        if (v.getId() == R.id.btn_share) {
            shareURL(shortUrl, this);
        }

        if (v.getId() == R.id.btn_copy) {
            clipURL(shortUrl, this, this);
        }
    }

    private void getShortUrl(ShortenApi shortenApi, boolean statsEnabled, String customURL) {
        Call<ShortUrl> shortUrlCall;

        if (isStatsChecked) {
            if (isCustomURLChecked && !customURL.isEmpty()) {
                shortUrlCall = shortenApi.getShortURLCustomStats("json", longUrl, customURL, 1);
            } else {
                shortUrlCall = shortenApi.getShortURLStats("json", longUrl, 1);
            }
        } else {
            if (isCustomURLChecked && !customURL.isEmpty()) {
                shortUrlCall = shortenApi.getShortURLCustom("json", longUrl, customURL);
            } else {
                shortUrlCall = shortenApi.getShortURL("json", longUrl);
            }
        }


        shortUrlCall.enqueue(new Callback<ShortUrl>() {
            @Override
            public void onResponse(Call<ShortUrl> call, Response<ShortUrl> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        shortUrl = response.body().getShortenedURL();

                        if (shortUrl != null) {
                            ShortUrlProfile shortUrlProfile = new ShortUrlProfile(shortUrl, longUrl, statsEnabled);
                            profileRepository.insert(shortUrlProfile);

                            mTVShortURL.setVisibility(View.VISIBLE);
                            mTVShortURL.setText(shortUrlProfile.getShortUrl());

                            mQrCodeView.setVisibility(View.VISIBLE);
                            Glide.with(getBaseContext())
                                    .load("https://api.qrserver.com/v1/create-qr-code/?data=" + shortUrlProfile.getShortUrl())
                                    .placeholder(R.drawable.ic_qr)
                                    .into(mQrCodeView);

                            mCheckBoxStats.setVisibility(View.GONE);
                            mCheckBoxCustomURL.setVisibility(View.GONE);
                            mTextInputLayoutCustomURL.setVisibility(View.GONE);
                            mButtonShorten.setVisibility(View.GONE);

                            mButtonShare.setVisibility(View.VISIBLE);
                            mButtonShare.setEnabled(true);
                            mButtonCopy.setVisibility(View.VISIBLE);
                            mButtonCopy.setEnabled(true);
                        } else {
                            mButtonShorten.setText(R.string.str_ready);
                            Toast.makeText(getBaseContext(), response.body().getErrorMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (compoundButton.getId() == R.id.cb_custom_url) {
            if (isChecked) {
                mTextInputLayoutCustomURL.setVisibility(View.VISIBLE);
                isCustomURLChecked = true;
            } else {
                mTextInputLayoutCustomURL.setVisibility(View.GONE);
                isCustomURLChecked = false;
            }
        }

        if (compoundButton.getId() == R.id.cb_stats) {
            isStatsChecked = isChecked;
        }
    }
}
