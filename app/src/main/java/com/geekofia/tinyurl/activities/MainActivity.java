package com.geekofia.tinyurl.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.geekofia.tinyurl.R;
import com.geekofia.tinyurl.fragments.HistoryFragment;
import com.geekofia.tinyurl.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{

    public static final String HOME_FRAGMENT = "HOME_FRAGMENT";
    public static final String HISTORY_FRAGMENT = "HISTORY_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        String receivedType = receivedIntent.getType();

        switch (receivedAction) {
            //app has been launched from share list
            case Intent.ACTION_SEND:
                if (receivedType.startsWith("text/")) {
                    String longUrl = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);

                    if (longUrl != null){
                        HomeFragment homeFragment = new HomeFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("LONG_URL", longUrl);
                        homeFragment.setArguments(bundle);
                        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment, HOME_FRAGMENT).commit();
                    }
                }
                break;
            //app has been launched directly, not from share list
            case Intent.ACTION_MAIN:
                break;
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        initViews();

        if (savedInstanceState == null) {
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment, HOME_FRAGMENT).commit();
        }
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
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, FRAG_TAG).commit();
        }

        return true;
    };

    private void initViews() {}


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Confirmation")
                .setMessage("Do you really want to close the app ?")
                .setPositiveButton("Yeh", (dialog, which) -> finish())
                .setNegativeButton("Nope", null)
                .show();
    }


}
