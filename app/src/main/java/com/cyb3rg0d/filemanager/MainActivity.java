package com.cyb3rg0d.filemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.cyb3rg0d.filemanager.ui.DCIMUI;
import com.cyb3rg0d.filemanager.ui.DownloadUI;
import com.cyb3rg0d.filemanager.ui.HomeUI;
import com.cyb3rg0d.filemanager.ui.InternalUI;
import com.cyb3rg0d.filemanager.ui.MoviesUI;
import com.cyb3rg0d.filemanager.ui.MusicUI;
import com.cyb3rg0d.filemanager.ui.SdCardUI;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeUI()).commit();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                HomeUI homeUI = new HomeUI();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,homeUI).addToBackStack(null).commit();
                break;
            case R.id.nav_interal:
                InternalUI internalUI = new InternalUI();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,internalUI).addToBackStack(null).commit();
                break;
            case R.id.nav_sd:
                SdCardUI sdCardUI = new SdCardUI();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,sdCardUI).addToBackStack(null).commit();
                break;
            case R.id.nav_download:
                DownloadUI DownloadUI = new DownloadUI();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,DownloadUI).addToBackStack(null).commit();
                break;
            case R.id.nav_music:
                MusicUI MusicUI = new MusicUI();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,MusicUI).addToBackStack(null).commit();
                break;
            case R.id.nav_dcim:
                DCIMUI DCIMUI = new DCIMUI();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,DCIMUI).addToBackStack(null).commit();
                break;
            case R.id.nav_movies:
                MoviesUI MoviesUI = new MoviesUI();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,MoviesUI).addToBackStack(null).commit();
                break;
            case R.id.nav_about:
                startActivity(new Intent(MainActivity.this,About.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStackImmediate();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}