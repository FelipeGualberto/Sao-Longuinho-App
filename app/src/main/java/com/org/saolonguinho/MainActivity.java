package com.org.saolonguinho;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.view.MenuItem;
import android.view.View;

import com.org.saolonguinho.about.AboutActivity;
import com.org.saolonguinho.databinding.ActivityMainBinding;
import com.org.saolonguinho.help.HelpActivity;
import com.org.saolonguinho.list.ListObjectsFragment;
import com.org.saolonguinho.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        configureToolbar();
        configureNavigation();
        setFragment();
        startActivity(LoginActivity.createIntent(getApplicationContext()));

    }


    private void configureToolbar(){
        activityMainBinding.toolbar.setTitle(R.string.app_name);
        activityMainBinding.toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        activityMainBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(activityMainBinding.drwLytMain.isDrawerOpen(GravityCompat.START)){
                    activityMainBinding.drwLytMain.closeDrawer(GravityCompat.START);
                    activityMainBinding.toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
                }else{
                    activityMainBinding.drwLytMain.openDrawer(GravityCompat.START);
                    activityMainBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
                }
            }
        });
        activityMainBinding.drwLytMain.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                activityMainBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                activityMainBinding.toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
            }
        });
    }
    private void configureNavigation(){
        activityMainBinding.nvgtVw.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mn_settings:
                        return true;
                    case R.id.mn_feedback:
                        return true;
                    case R.id.mn_help:
                        startActivity(HelpActivity.createIntent(getApplicationContext()));
                        return true;
                    case R.id.mn_about:
                       startActivity(AboutActivity.createIntent(getApplicationContext()));
                        return true;
                }
                return false;
            }
        });
    }
    private void setFragment(){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frm_lyt_container, new ListObjectsFragment()).commit();
    }
}
