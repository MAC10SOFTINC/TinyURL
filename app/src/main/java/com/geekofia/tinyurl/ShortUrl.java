package com.geekofia.tinyurl;

import com.google.gson.annotations.SerializedName;

public class ShortUrl {

    @SerializedName("shorturl")
    private String url;

    public ShortUrl(String shortenedURL) {
        this.url = shortenedURL;
    }

    public String getShortenedURL() {
        return url;
    }

    public void setShortenedURL(String shortenedURL) {
        this.url = shortenedURL;
    }

    @Override
    public String toString() {
        return "ShortUrl{" +
                "url='" + url + '\'' +
                '}';
    }
}