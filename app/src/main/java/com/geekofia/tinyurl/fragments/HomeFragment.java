package com.geekofia.tinyurl.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import static com.geekofia.tinyurl.utils.Functions.clipURL;
import static com.geekofia.tinyurl.utils.Functions.initRetrofitIsGd;
import static com.geekofia.tinyurl.utils.Functions.initRetrofitVGd;
import static com.geekofia.tinyurl.utils.Functions.shareURL;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private TextInputEditText mEditTextLongURL, mEditTextShortURL, mEditTextCustomURL;
    private MaterialButton buttonShorten, buttonShare, buttonCopy;
    private ShortenApi shortenApiIsGd, shortenApiVGd;
    private String longUrl, shortUrl;
    private ShortUrlProfileRepo profileRepository;
    private MaterialCheckBox statsCheckBox;
    private AutoCompleteTextView mAutoCompleteDomain;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);

        Retrofit mRetrofitIsGd = initRetrofitIsGd();
        Retrofit mRetrofitVGd = initRetrofitVGd();
        shortenApiIsGd = mRetrofitIsGd.create(ShortenApi.class);
        shortenApiVGd = mRetrofitVGd.create(ShortenApi.class);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        profileRepository = new ShortUrlProfileRepo(getActivity().getApplication());
    }

    private void initViews(View view) {
        mEditTextLongURL = view.getRootView().findViewById(R.id.edit_text_long_url);
        mEditTextShortURL = view.getRootView().findViewById(R.id.edit_text_short_url);
        mAutoCompleteDomain = view.getRootView().findViewById(R.id.dropdown_custom_domain);
        mEditTextCustomURL = view.getRootView().findViewById(R.id.edit_text_custom_url);
        statsCheckBox = view.getRootView().findViewById(R.id.check_stats);
        buttonShorten = view.getRootView().findViewById(R.id.btn_shorten);
        buttonShare = view.getRootView().findViewById(R.id.btn_share);
        buttonCopy = view.getRootView().findViewById(R.id.btn_copy);

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
                    buttonShorten.setText(R.string.str_ready);
                } else {
                    buttonShorten.setEnabled(true);
                }

                String url = s.toString().trim();
                if (url.startsWith("is.gd") || url.contains("https://is.gd") || url.contains("https://is.gd/") ||
                        url.startsWith("v.gd") || url.contains("https://v.gd") || url.contains("https://v.gd/")) {
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

        // domain selection
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.domains, android.R.layout.simple_spinner_dropdown_item);
        mAutoCompleteDomain.setAdapter(adapter);

        mAutoCompleteDomain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                buttonShorten.setEnabled(true);
                buttonShorten.setText(R.string.str_ready);
                mEditTextShortURL.setText("");
            }
        });

        mEditTextCustomURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                buttonShorten.setEnabled(true);
                buttonShorten.setText(R.string.str_ready);
                mEditTextShortURL.setText("");
            }
        });

        buttonShorten.setOnClickListener(this);
        buttonShare.setOnClickListener(this);
        buttonCopy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_shorten:
                buttonShorten.setText(R.string.str_in_prog);
                buttonShorten.setEnabled(false);

                String domain = mAutoCompleteDomain.getText().toString();
                String customURL = mEditTextCustomURL.getText().toString();

                if (domain.equals("is.gd")) {
                    if (statsCheckBox.isChecked()) {
                        if (customURL.isEmpty()) {
                            getShortUrl(shortenApiIsGd, true, null);
                        } else {
                            getShortUrl(shortenApiIsGd, true, customURL);
                        }
                    } else {
                        if (customURL.isEmpty()) {
                            getShortUrl(shortenApiIsGd, false, null);
                        } else {
                            getShortUrl(shortenApiIsGd, false, customURL);
                        }
                    }
                } else {
                    if (statsCheckBox.isChecked()) {
                        if (customURL.isEmpty()) {
                            getShortUrl(shortenApiVGd, true, null);
                        } else {
                            getShortUrl(shortenApiVGd, true, customURL);
                        }
                    } else {
                        if (customURL.isEmpty()) {
                            getShortUrl(shortenApiVGd, false, null);
                        } else {
                            getShortUrl(shortenApiVGd, false, customURL);
                        }
                    }
                }
                break;

            case R.id.btn_share:
                shareURL(shortUrl, getActivity());
                break;

            case R.id.btn_copy:
                clipURL(shortUrl, getActivity(), getContext());
                break;
        }
    }

    private void getShortUrl(ShortenApi shortenApi, boolean statsEnabled, String customURL) {
        if (longUrl == null) {
            longUrl = Objects.requireNonNull(mEditTextLongURL.getText()).toString();
        }

        Call<ShortUrl> shortUrlCall;

        if (statsEnabled) {
            if (customURL != null) {
                shortUrlCall = shortenApi.getShortURLCustomStats("json", longUrl, customURL, 1);
            } else {
                shortUrlCall = shortenApi.getShortURLStats("json", longUrl, 1);
            }
        } else {
            if (customURL != null) {
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
                            mEditTextShortURL.setText(shortUrlProfile.getShortUrl());

                            buttonShorten.setText(R.string.str_done);
                            buttonShare.setEnabled(true);
                            buttonCopy.setEnabled(true);

                            profileRepository.insert(shortUrlProfile);
                        } else {
                            buttonShorten.setText(R.string.str_ready);
                            mEditTextShortURL.setText("");
                            Toast.makeText(getContext(), response.body().getErrorMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Response: " + response.code(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ShortUrl> call, Throwable t) {
                Toast.makeText(getContext(), "Throwable: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
