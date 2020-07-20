package com.geekofia.tinyurl.interfaces;

import com.geekofia.tinyurl.models.ShortUrl;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ShortenApi {

    @GET("create.php")
    @Headers("Cache-control: no-cache")
    Call<ShortUrl> getShortURL(@Query("format") String format, @Query("url") String url);

    @GET("create.php")
    @Headers("Cache-control: no-cache")
    Call<ShortUrl> getShortURLStats(@Query("format") String format, @Query("url") String url, @Query("logstats") int statsEnabled);

    @GET("create.php")
    @Headers("Cache-control: no-cache")
    Call<ShortUrl> getShortURLCustom(@Query("format") String format, @Query("url") String url, @Query("shorturl") String shorturl);

    @GET("create.php")
    @Headers("Cache-control: no-cache")
    Call<ShortUrl> getShortURLCustomStats(@Query("format") String format, @Query("url") String url, @Query("shorturl") String shorturl, @Query("logstats") int statsEnabled);
}
