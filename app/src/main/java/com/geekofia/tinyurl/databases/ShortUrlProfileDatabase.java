package com.geekofia.tinyurl.databases;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.geekofia.tinyurl.interfaces.ShortUrlProfileDao;
import com.geekofia.tinyurl.models.ShortUrlProfile;

@Database(entities = {ShortUrlProfile.class}, version = 2, exportSchema = false)
public abstract class ShortUrlProfileDatabase extends RoomDatabase {

    private static ShortUrlProfileDatabase instance;
    public abstract ShortUrlProfileDao shortUrlProfileDao();


    public static synchronized ShortUrlProfileDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ShortUrlProfileDatabase.class, "short_urls_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private ShortUrlProfileDao profileDao;

        private PopulateDbAsyncTask(ShortUrlProfileDatabase db) {
            this.profileDao = db.shortUrlProfileDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            profileDao.insert(new ShortUrlProfile("https://is.gd/dsUwVY", "https://google.com", false));
            profileDao.insert(new ShortUrlProfile("https://is.gd/dPPJw2",  "https://youtu.be/WFr2WgN9_xE", false));
            profileDao.insert(new ShortUrlProfile("https://is.gd/lPDjQA",  "https://blog.geekofia.in/ctf/2019/12/28/inferno-ctf-writeup.html", false));
            return null;
        }
    }


}
