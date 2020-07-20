package com.geekofia.tinyurl.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Functions {
    public static void clipURL(String url, Activity activity, Context context) {
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);

        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("Shortened URL", url);

        // Set the clipboard's primary clip.
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copied: " + url, Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareURL(String url, Activity activity) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share " + url + " with");
        activity.startActivity(shareIntent);
    }

    public static Retrofit initRetrofitIsGd(GsonConverterFactory gsonFactory, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl("https://is.gd/")
                .client(okHttpClient)
                .addConverterFactory(gsonFactory)
                .build();
    }

    public static Retrofit initRetrofitVGd(GsonConverterFactory gsonFactory, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl("https://v.gd/")
                .client(okHttpClient)
                .addConverterFactory(gsonFactory)
                .build();
    }
}
