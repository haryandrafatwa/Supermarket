package com.example.supermarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.supermarket.Cart.CartFragment;
import com.example.supermarket.History.HistoryFragment;
import com.example.supermarket.Home.HomeFragment;
import com.example.supermarket.Ongoing.OngoingFragment;
import com.example.supermarket.Profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    public static Context contextOfApplication;
    private DatabaseReference databaseReference;

    private String search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contextOfApplication = getApplicationContext();

        setContentView(R.layout.activity_main);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavBar);

        final HomeFragment homeFragment = new HomeFragment();
        final OngoingFragment ongoingFragment = new OngoingFragment();
        final HistoryFragment historyFragment = new HistoryFragment();
        final CartFragment cartFragment = new CartFragment();
        final ProfileFragment profileFragment = new ProfileFragment();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {

                int id = menuItem.getItemId();
                if (id == R.id.menuProfile) {
                    setFragment(profileFragment);
                    return true;
                } else if (id == R.id.menuHome) {
                    setFragment(homeFragment);
                    return true;
                } else if (id == R.id.menuOngoing) {
                    setFragment(ongoingFragment);
                    return true;
                } else if (id == R.id.menuHistory) {
                    setFragment(historyFragment);
                    return true;
                } else if (id == R.id.menuCart) {
                    setFragment(cartFragment);
                    return true;
                }
                return  false;
            }

        });
        bottomNavigationView.setSelectedItemId(R.id.menuHome);

    }
    public void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment);
        fragmentTransaction.commit();
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSearch() {
        return search;
    }
}
