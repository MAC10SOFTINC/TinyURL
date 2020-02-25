package com.geekofia.tinyurl.interfaces;

import com.geekofia.tinyurl.models.ShortUrl;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ShortenApi {

    @GET("create.php")
    Call<ShortUrl> getShortURL(@Query("format") String format, @Query("url") String url);
}
