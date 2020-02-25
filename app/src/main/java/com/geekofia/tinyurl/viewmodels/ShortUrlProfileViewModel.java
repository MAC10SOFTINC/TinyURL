package com.geekofia.tinyurl.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.geekofia.tinyurl.models.ShortUrlProfile;
import com.geekofia.tinyurl.repositories.ShortUrlProfileRepo;

import java.util.List;

public class ShortUrlProfileViewModel extends AndroidViewModel {

    private ShortUrlProfileRepo profileRepository;
    private LiveData<List<ShortUrlProfile>> allProfiles;

    public ShortUrlProfileViewModel(@NonNull Application application) {
        super(application);
        profileRepository = new ShortUrlProfileRepo(application);
        allProfiles = profileRepository.getAllProfiles();
    }

    public void insert(ShortUrlProfile profile) {
        profileRepository.insert(profile);
    }

    public void update(ShortUrlProfile profile) {
        profileRepository.update(profile);
    }

    public void delete(ShortUrlProfile profile) {
        profileRepository.delete(profile);
    }

    public void deleteAllProfiles() {
        profileRepository.deleteAllProfiles();
    }

    public LiveData<List<ShortUrlProfile>> getAllProfiles() {
        return allProfiles;
    }
}
