package com.geekofia.tinyurl.interfaces;

import com.geekofia.tinyurl.models.ShortUrl;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ShortenApi {

    @GET("create.php")
    Call<ShortUrl> getShortURL(@Query("format") String format, @Query("url") String url);

    @GET("create.php")
    Call<ShortUrl> getShortURLStats(@Query("format") String format, @Query("url") String url, @Query("logstats") int statsEnabled);

    @GET("create.php")
    Call<ShortUrl> getShortURLCustom(@Query("format") String format, @Query("url") String url, @Query("shorturl") String shorturl);

    @GET("create.php")
    Call<ShortUrl> getShortURLCustomStats(@Query("format") String format, @Query("url") String url, @Query("shorturl") String shorturl, @Query("logstats") int statsEnabled);
}
