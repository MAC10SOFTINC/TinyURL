package com.geekofia.tinyurl.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.geekofia.tinyurl.R;
import com.geekofia.tinyurl.models.ShortUrlProfile;

public class ShortUrlProfileAdapter extends ListAdapter<ShortUrlProfile, ShortUrlProfileAdapter.ProfileHolder> {
    private onProfileClickListener profileClickListener;

    public ShortUrlProfileAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<ShortUrlProfile> DIFF_CALLBACK = new DiffUtil.ItemCallback<ShortUrlProfile>() {

        @Override
        public boolean areItemsTheSame(@NonNull ShortUrlProfile oldItem, @NonNull ShortUrlProfile newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ShortUrlProfile oldItem, @NonNull ShortUrlProfile newItem) {
            return oldItem.getShortUrl().equals(newItem.getShortUrl()) &&
                    oldItem.getLongUrl().equals(newItem.getLongUrl()) &&
                    oldItem.getId() == newItem.getId();
        }
    };

    @NonNull
    @Override
    public ProfileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.short_url_profile, parent, false);
        return new ProfileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileHolder holder, int position) {
        ShortUrlProfile currentProfile = getItem(position);
        holder.tvShortUrl.setText(currentProfile.getShortUrl());
        holder.tvLongUrl.setText(currentProfile.getLongUrl());
    }

    public ShortUrlProfile getProfileAt(int position){
        return getItem(position);
    }

    class ProfileHolder extends RecyclerView.ViewHolder {
        private TextView tvShortUrl, tvLongUrl;


        ProfileHolder(@NonNull View itemView) {
            super(itemView);
            tvShortUrl = itemView.findViewById(R.id.tv_short_url);
            tvLongUrl = itemView.findViewById(R.id.tv_long_url);
//            Button connectButton = itemView.findViewById(R.id.btn_copy);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (profileClickListener != null && position != RecyclerView.NO_POSITION) {
                    profileClickListener.onProfileClick(getItem(position));
                }
            });

//            connectButton.setOnClickListener(v -> {
//                int position = getAdapterPosition();
//                if (profileClickListener != null && position != RecyclerView.NO_POSITION) {
//                    profileClickListener.onButtonClick(getItem(position));
//                }
//            });
        }
    }

    public interface onProfileClickListener {
        void onProfileClick(ShortUrlProfile profile);
//        void onButtonClick(ShortUrlProfile profile);
    }

    public void setOnProfileClickListener(onProfileClickListener profileClickListener) {
        this.profileClickListener = profileClickListener;
    }
}
