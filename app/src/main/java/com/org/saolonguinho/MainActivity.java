package com.org.saolonguinho;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.org.saolonguinho.about.AboutActivity;
import com.org.saolonguinho.databinding.ActivityMainBinding;
import com.org.saolonguinho.help.HelpActivity;
import com.org.saolonguinho.list.ListObjectsFragment;
import com.org.saolonguinho.login.LoginActivity;
import com.parse.Parse;
import com.parse.ParseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding activityMainBinding;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        configureToolbar();
        configureNavigation();
        if (ParseUser.getCurrentUser() == null) {
            Intent intent = LoginActivity.createIntent(getApplicationContext());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            setFragment();
        }

        try {
            PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }

    private void configureToolbar() {
        activityMainBinding.toolbar.setTitle(R.string.app_name);
        activityMainBinding.toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        activityMainBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityMainBinding.drwLytMain.isDrawerOpen(GravityCompat.START)) {
                    activityMainBinding.drwLytMain.closeDrawer(GravityCompat.START);
                    activityMainBinding.toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
                } else {
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

    private void configureNavigation() {
        activityMainBinding.nvgtVw.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
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
                    case R.id.mn_exit:
                        createDialogLogout();
                        return true;
                }
                return false;
            }
        });
    }

    private void setFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frm_lyt_container, new ListObjectsFragment()).commit();
    }

    private void createDialogLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Você deseja sair da sua conta?");
        builder.setCancelable(true);
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ParseUser.logOut();
                        Intent intent = LoginActivity.createIntent(getApplicationContext());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("Não",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void printHashKey(Context pContext) {

    }
}
