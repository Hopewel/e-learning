package com.example.chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class IndexActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);


        drawerLayout = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new ProfileFragment());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.naviation_drawer_open,R.string.naviation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState==null){

            getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new ProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.profile);
        }


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, new ProfileFragment()).commit();
                break;

            case R.id.message:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, new MessageFragment()).commit();
                break;

            case R.id.chat:
                Intent intent = new Intent(this,GroupChatActivity.class);
                startActivity(intent);
                break;

            case R.id.live_session:
                Intent intent2 = new Intent(this,ConferenceActivity.class);
                startActivity(intent2);
                break;

                //include other cases for other fragments or activities

            case R.id.menuLogout:
                Intent intent1 = new Intent(this,MainActivity.class);
                startActivity(intent1);
                break;


        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }








    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }


}