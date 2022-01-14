package com.geekofia.tinyurl.models;

import com.google.gson.annotations.SerializedName;

public class ShortUrl extends ErrorResponse {

    @SerializedName("shorturl")
    private String shorturl;

    public ShortUrl(int errorCode, String errorMessage,  String shortenedURL) {
        super(errorCode, errorMessage);
        this.shorturl = shortenedURL;
    }

    public String getShortenedURL() {
        return shorturl;
    }

    @Override
    public String toString() {
        return "ShortUrlProfile{" +
                "url='" + shorturl + '\'' +
                '}';
    }
}
