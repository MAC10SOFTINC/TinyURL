package com.geekofia.tinyurl.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.geekofia.tinyurl.databases.ShortUrlProfileDatabase;
import com.geekofia.tinyurl.interfaces.ShortUrlProfileDao;
import com.geekofia.tinyurl.models.ShortUrlProfile;

import java.util.List;

public class ShortUrlProfileRepo {
    private ShortUrlProfileDao shortUrlProfileDao;
    private LiveData<List<ShortUrlProfile>> allShortUrls;

    public ShortUrlProfileRepo(Application application) {
        ShortUrlProfileDatabase database = ShortUrlProfileDatabase.getInstance(application);
        shortUrlProfileDao = database.shortUrlProfileDao();
        allShortUrls = shortUrlProfileDao.getAllProfiles();
    }

    public void insert(ShortUrlProfile profile) {
        new InsertProfileAsyncTask(shortUrlProfileDao).execute(profile);
    }

    public void update(ShortUrlProfile profile) {
        new UpdateProfileAsyncTask(shortUrlProfileDao).execute(profile);
    }

    public void delete(ShortUrlProfile profile) {
        new DeleteProfileAsyncTask(shortUrlProfileDao).execute(profile);
    }

    public void deleteAllProfiles() {
        new DeleteAllProfilesAsyncTask(shortUrlProfileDao).execute();
    }

    public LiveData<List<ShortUrlProfile>> getAllProfiles() {
        return allShortUrls;
    }

    private static class InsertProfileAsyncTask extends AsyncTask<ShortUrlProfile, Void, Void> {

        private ShortUrlProfileDao profileDao;

        private InsertProfileAsyncTask(ShortUrlProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(ShortUrlProfile... profiles) {
            profileDao.insert(profiles[0]);
            return null;
        }
    }

    private static class UpdateProfileAsyncTask extends AsyncTask<ShortUrlProfile, Void, Void> {

        private ShortUrlProfileDao profileDao;

        private UpdateProfileAsyncTask(ShortUrlProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(ShortUrlProfile... profiles) {
            profileDao.update(profiles[0]);
            return null;
        }
    }

    private static class DeleteProfileAsyncTask extends AsyncTask<ShortUrlProfile, Void, Void> {

        private ShortUrlProfileDao profileDao;

        private DeleteProfileAsyncTask(ShortUrlProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(ShortUrlProfile... profiles) {
            profileDao.delete(profiles[0]);
            return null;
        }
    }

    private static class DeleteAllProfilesAsyncTask extends AsyncTask<Void, Void, Void> {

        private ShortUrlProfileDao profileDao;

        private DeleteAllProfilesAsyncTask(ShortUrlProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            profileDao.deleteAllProfiles();
            return null;
        }
    }
}
