package com.example.jiongyi.foodemblem;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiongyi.foodemblem.fragment.HomeFragment;
import com.example.jiongyi.foodemblem.fragment.ReservationFragment;
import com.example.jiongyi.foodemblem.fragment.RestaurantMenuFragment;
import com.example.jiongyi.foodemblem.room.CustomerOrder;

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
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerlayout = navigationView.getHeaderView(0);
        TextView textView = headerlayout.findViewById(R.id.headerEmail);
        SharedPreferences sp = getSharedPreferences("FoodEmblem",MODE_PRIVATE);
        String useremail = sp.getString("UserEmail","");
        textView.setText(useremail);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.emblemtoolbar);
        setSupportActionBar(myToolbar);
        fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        //Home Fragment
        homeFragment = HomeFragment.newInstance("","");
        homeFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.fragment_frame,homeFragment).commit();

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        Boolean gotoReservation = sp.getBoolean("promoreserve",false);
        final Boolean isordering = sp.getBoolean("IsOrdering",false);
        if (isordering == true){
            int restid = sp.getInt("IsOrderingRestId",0);
            CustomerOrder customerOrder = (CustomerOrder) getIntent().getSerializableExtra("CustomerOrder");
            Bundle orderbundle = new Bundle();
            orderbundle.putSerializable("CustomerOrder",customerOrder);
            orderbundle.putInt("RestaurantId",restid);
            orderbundle.putBoolean("fromOrder",true);
            try {
                Fragment fragment = (Fragment) RestaurantMenuFragment.class.newInstance();
                fragment.setArguments(orderbundle);
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment).commit();
            }
            catch (Exception ex){

            }
        }
//        if (gotoReservation == true){
//            try {
//                int restid = getIntent().getIntExtra("RestaurantId",0);
//                bundle.putInt("RestaurantId",restid);
//                Log.i("WhatRestId",String.valueOf(restid));
//                Fragment fragment = (Fragment) HomeFragment.class.newInstance();
//                fragment.setArguments(bundle);
//                fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment).commit();
//            }
//            catch (Exception ex){
//
//            }
//        }
//        if (attable == false){
//            orderitem.setEnabled(false);
//            invalidateOptionsMenu();
//        }
//        else {
//            orderitem.setEnabled(true);
//            invalidateOptionsMenu();
//        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    SharedPreferences sp = getSharedPreferences("FoodEmblem",MODE_PRIVATE);
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        int id = menuItem.getItemId();
                        Class fragclass = null;
                        Fragment fragment = null;
                        Bundle orderbundle = new Bundle();
                        if (id == R.id.nav_reservation){
                            fragclass = ReservationFragment.class;
                            try {
                                fragment = (Fragment)fragclass.newInstance();
                                fragment.setArguments(orderbundle);
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                            fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment,"ReservationFragment").commit();
                            return true;
                        }
                        else if (id == R.id.nav_home){
                            fragclass = HomeFragment.class;
                            try {
                                fragment = (Fragment)fragclass.newInstance();
                                fragment.setArguments(orderbundle);
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                            fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment, "HomeFragment").commit();
                            return true;
                        }
                        else if (id == R.id.nav_order){
                            final boolean attable = sp.getBoolean("AtTable",false);
                            Log.i("AtTable", String.valueOf(attable));
                            if (attable == true){
                                fragclass = RestaurantMenuFragment.class;
                                //Retrieve reservations if any and pass restID
                                orderbundle.putBoolean("fromOrder",true);
                                orderbundle.putInt("RestaurantId",1);
                                if (isordering == true) {
                                    CustomerOrder customerOrder = (CustomerOrder) getIntent().getSerializableExtra("CustomerOrder");
                                    orderbundle.putSerializable("CustomerOrder",customerOrder);
                                }
                                try {
                                    fragment = (Fragment)fragclass.newInstance();
                                    fragment.setArguments(orderbundle);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                                fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment, "OrderFragment").commit();
                                return true;
                            }
                            else {
                                Fragment currentfrag = fragmentManager.findFragmentByTag("OrderFragment");
                                if (currentfrag != null && currentfrag.isVisible()){
                                    fragclass = HomeFragment.class;
                                    try {
                                        fragment = (Fragment)fragclass.newInstance();
                                        fragment.setArguments(orderbundle);
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment, "OrderFragment").commit();
                                }
                                Toast toast = Toast.makeText(getApplicationContext(),"You must be seated at your reserved table before making orders!",Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                        else if (id == R.id.nav_logout){
                            Intent logout = new Intent(HomeActivity.this, StartupPageActivity.class);
                            sp.edit().clear().apply();
                            startActivity(logout);
                        }
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
