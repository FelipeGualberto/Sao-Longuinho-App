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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.org.saolonguinho.about.AboutActivity;
import com.org.saolonguinho.databinding.ActivityMainBinding;
import com.org.saolonguinho.help.HelpActivity;
import com.org.saolonguinho.list.ListObjectsFragment;
import com.org.saolonguinho.login.LoginActivity;
import com.parse.ParseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding activityMainBinding;
    private InterfaceMain interfaceMain;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    MenuItem.OnMenuItemClickListener onMenuItemSearchClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            activityMainBinding.layoutSearch.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
            activityMainBinding.search.requestFocus();
            return true;
        }
    };

    MenuItem.OnMenuItemClickListener onMenuItemUpdateClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            interfaceMain.update();
            item.setVisible(false);
            Toast.makeText(getApplicationContext(), "Atualizando dados...", Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        configureToolbar();
        configureNavigation();
        setTriggers();
        if (ParseUser.getCurrentUser() == null) {
            Intent intent = LoginActivity.createIntent(getApplicationContext());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            setFragment();
        }
    }

    private void setTriggers() {
        activityMainBinding.toolbar.getMenu().findItem(R.id.action_search).setOnMenuItemClickListener(onMenuItemSearchClickListener);
        activityMainBinding.toolbar.getMenu().findItem(R.id.action_update).setOnMenuItemClickListener(onMenuItemUpdateClickListener);
        activityMainBinding.search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    activityMainBinding.layoutSearch.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        });
        activityMainBinding.cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMainBinding.layoutSearch.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                if (interfaceMain != null) {
                    interfaceMain.onSearch("");
                    activityMainBinding.search.setText("");
                }
            }
        });
        activityMainBinding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (interfaceMain != null) {
                    interfaceMain.onSearch(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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
        activityMainBinding.toolbar.inflateMenu(R.menu.menu_list_activity);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        activityMainBinding.layoutSearch.clearFocus();
        activityMainBinding.layoutSearch.setVisibility(View.GONE);
    }

    public interface InterfaceMain {
        public void onSearch(String text);

        public void update();
    }

    public void setInterface(InterfaceMain interface_main) {
        interfaceMain = interface_main;
    }
}
