package com.geekofia.tinyurl.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.geekofia.tinyurl.models.ShortUrlProfile;

import java.util.List;

@Dao
public interface ShortUrlProfileDao {

    @Insert
    void insert(ShortUrlProfile profile);

    @Update
    void update(ShortUrlProfile profile);

    @Delete
    void delete(ShortUrlProfile profile);

    @Query("DELETE FROM short_urls_table")
    void deleteAllProfiles();

    @Query("SELECT * FROM short_urls_table ORDER BY id ASC")
    LiveData<List<ShortUrlProfile>> getAllProfiles();
}
