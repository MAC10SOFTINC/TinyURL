package com.geekofia.tinyurl.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import com.geekofia.tinyurl.R;
import com.geekofia.tinyurl.fragments.AboutFragment;
import com.geekofia.tinyurl.fragments.HistoryFragment;
import com.geekofia.tinyurl.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import hotchemi.android.rate.AppRate;

import static com.geekofia.tinyurl.utils.Functions.updateTheme;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final String HOME_FRAGMENT = "HOME_FRAGMENT";
    public static final String HISTORY_FRAGMENT = "HISTORY_FRAGMENT";
    public static final String ABOUT_FRAGMENT = "ABOUT_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        setContentView(R.layout.activity_main);

        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment, HOME_FRAGMENT).commit();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        initViews();
        showRateApp();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = menuItem -> {

        Fragment selectedFragment = null;
        String FRAG_TAG = "";

        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                selectedFragment = new HomeFragment();
                FRAG_TAG = HOME_FRAGMENT;
                break;
            case R.id.navigation_history:
                selectedFragment = new HistoryFragment();
                FRAG_TAG = HISTORY_FRAGMENT;
                break;
            case R.id.navigation_about:
                selectedFragment = new AboutFragment();
                FRAG_TAG = ABOUT_FRAGMENT;
                break;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, FRAG_TAG).commit();
        }

        return true;
    };

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isDarkEnabled = sharedPreferences.getBoolean("dark_theme", false);
        updateTheme(isDarkEnabled);
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Confirmation")
                .setMessage("Do you really want to close the app ?")
                .setPositiveButton("Yeh", (dialog, which) -> finish())
                .setNegativeButton("Nope", null)
                .show();
    }

    private void showRateApp() {
        AppRate.with(this)
                .setInstallDays(1)
                .setLaunchTimes(3)
                .setRemindInterval(2)
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);
    }
}
