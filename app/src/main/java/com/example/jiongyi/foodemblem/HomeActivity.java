package com.example.jiongyi.foodemblem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.jiongyi.foodemblem.fragment.HomeFragment;
import com.example.jiongyi.foodemblem.fragment.ReservationFragment;
import com.example.jiongyi.foodemblem.fragment.RestaurantMenuFragment;

/**
 * Created by JiongYi on 4/3/2018.
 */

public class HomeActivity extends AppCompatActivity implements ReservationFragment.OnFragmentInteractionListener,
HomeFragment.OnFragmentInteractionListener, RestaurantMenuFragment.OnFragmentInteractionListener {

    private FragmentManager fragmentManager;
    private DrawerLayout mDrawerLayout;
    private ReservationFragment reservationFragment;
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_home);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.emblemtoolbar);
        setSupportActionBar(myToolbar);
        fragmentManager = getSupportFragmentManager();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        //Home Fragment
        homeFragment = HomeFragment.newInstance("","");
        fragmentManager.beginTransaction().replace(R.id.fragment_frame,homeFragment).commit();
        
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        int id = menuItem.getItemId();
                        Class fragclass = null;
                        Fragment fragment = null;
                        if (id == R.id.nav_reservation){
                            fragclass = ReservationFragment.class;
                        }
                        else if (id == R.id.nav_home){
                            fragclass = HomeFragment.class;
                        }
                        try {
                            fragment = (Fragment)fragclass.newInstance();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                        fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment).commit();
                        return true;
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {
    }

    @Override
    public void onBackPressed(){

    }
}
