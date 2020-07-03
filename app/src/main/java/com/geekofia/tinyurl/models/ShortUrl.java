package com.geekofia.tinyurl.models;

import com.google.gson.annotations.SerializedName;

public class ShortUrl extends ErrorResponse {

    @SerializedName("shorturl")
    private String url;

    public ShortUrl(int errorCode, String errorMessage,  String shortenedURL) {
        super(errorCode, errorMessage);
        this.url = shortenedURL;
    }

    public String getShortenedURL() {
        return url;
    }

    @Override
    public String toString() {
        return "ShortUrlProfile{" +
                "url='" + url + '\'' +
                '}';
    }
}
