package com.geekofia.tinyurl.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);

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

        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                selectedFragment = new HomeFragment();
                break;
            case R.id.navigation_history:
                selectedFragment = new HistoryFragment();
                break;
            case R.id.navigation_settings:
                selectedFragment = new HistoryFragment();
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

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
