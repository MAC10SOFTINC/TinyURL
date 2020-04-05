package com.geekofia.tinyurl.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "short_urls_table")
public class ShortUrlProfile {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    private String shortUrl, longUrl;
    private boolean statsEnabled;

    public ShortUrlProfile(String shortUrl, String longUrl, boolean statsEnabled) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.statsEnabled = statsEnabled;
    }

    public int getId() {
        return id;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public boolean isStatsEnabled() {return statsEnabled; }

    public void setId(int id) {
        this.id = id;
    }

//    public void setShortUrl(String shortUrl) {
//        this.shortUrl = shortUrl;
//    }
//
//    public void setLongUrl(String longUrl) {
//        this.longUrl = longUrl;
//    }
}
