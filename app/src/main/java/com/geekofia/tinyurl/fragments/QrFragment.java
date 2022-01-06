package com.geekofia.tinyurl.fragments;

import static com.geekofia.tinyurl.utils.Functions.clipURL;
import static com.geekofia.tinyurl.utils.Functions.shareURL;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.geekofia.tinyurl.R;
import com.google.android.material.button.MaterialButton;

public class QrFragment extends Fragment implements View.OnClickListener {

    private ImageView qrCodeView;
    private TextView shortUrlTextView;
    private MaterialButton buttonShare, buttonCopy;
    private String shortUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            shortUrl = bundle.getString("SHORT_URL", "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        qrCodeView = view.getRootView().findViewById(R.id.iv_qr);
        shortUrlTextView = view.getRootView().findViewById(R.id.tv_short_url);
        buttonShare = view.getRootView().findViewById(R.id.btn_share);
        buttonShare.setOnClickListener(this);

        buttonCopy = view.getRootView().findViewById(R.id.btn_copy);
        buttonCopy.setOnClickListener(this);

        if (shortUrl.contains(".gd")) {
            Glide.with(this)
                    .load("https://api.qrserver.com/v1/create-qr-code/?data=" + shortUrl)
                    .placeholder(R.drawable.ic_qr)
                    .into(qrCodeView);
            shortUrlTextView.setText(shortUrl);
            buttonShare.setEnabled(true);
            buttonCopy.setEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_copy) {
            clipURL(shortUrl, requireActivity(), getContext());
        }

        if (view.getId() == R.id.btn_share) {
            shareURL(shortUrl, requireActivity());
        }
    }
}
