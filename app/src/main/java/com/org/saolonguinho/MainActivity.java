package com.org.saolonguinho;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.appnext.ads.interstitial.Interstitial;
import com.appnext.appnextsdk.API.AppnextAPI;
import com.appnext.appnextsdk.API.AppnextAd;
import com.appnext.appnextsdk.API.AppnextAdRequest;

import com.appnext.core.callbacks.OnAdLoaded;
import com.facebook.login.LoginManager;
import com.org.saolonguinho.about.AboutActivity;
import com.org.saolonguinho.databinding.ActivityMainBinding;
import com.org.saolonguinho.help.HelpActivity;
import com.org.saolonguinho.list.ListObjectsFragment;
import com.org.saolonguinho.login.LoginActivity;
import com.org.saolonguinho.shared.models.Objects;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST = 5;
    private ActivityMainBinding activityMainBinding;
    private InterfaceMain interfaceMain;

    Interstitial interstitial_Ad;
    private AppnextAPI appnextAPI;
    private AppnextAd ad;
    private int interstitial_Ad_time = 0;
    private boolean isActivityVisible = true;

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
            startSimpleAds();
        }
    }

    private void setTriggers() {
        activityMainBinding.toolbar.getMenu().findItem(R.id.action_search).setOnMenuItemClickListener(onMenuItemSearchClickListener);
        //     activityMainBinding.toolbar.getMenu().findItem(R.id.action_update).setOnMenuItemClickListener(onMenuItemUpdateClickListener);
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
                    case R.id.mn_help:
                        startActivity(HelpActivity.createIntent(getApplicationContext()));
                        return true;
                    case R.id.mn_about:
                        startActivity(AboutActivity.createIntent(getApplicationContext()));
                        return true;
                    case R.id.mn_exit:
                        createDialogLogout();
                        return true;
                    case R.id.mn_delete:
                        createDialogDelete();
                        return true;
                }
                return false;
            }
        });
    }

    private void setFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frm_lyt_container, new ListObjectsFragment()).commit();
        int permissionCheckWriteExternal = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheckInternet = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        if (permissionCheckWriteExternal == PackageManager.PERMISSION_DENIED || permissionCheckInternet == PackageManager.PERMISSION_DENIED) {
            requestPermission();
        }
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

    private void createDialogDelete() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Você deseja deletar sua conta?");
        builder.setCancelable(true);
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setTitle("Deletando...");
                        progressDialog.show();
                    /*    ParseFacebookUtils.unlinkInBackground(ParseUser.getCurrentUser(), new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                            }
                        });*/
                        ParseUser.getCurrentUser().deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            deleteItens();
                                        }
                                    }).start();
                                    LoginManager.getInstance().logOut();
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Conta deletada", Toast.LENGTH_LONG).show();
                                    startActivity(LoginActivity.createIntent(getApplicationContext()));
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Um erro ocorreu, tente novamente mais tarde", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
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

    private void deleteItens() {
        ParseQuery<Objects> query = ParseQuery.getQuery(Objects.class);
        query.include(Objects.LOCATION);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Objects>() {
            @Override
            public void done(List<Objects> list, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).deleteEventually();
                        list.get(i).getLocation().deleteEventually();
                    }
                } else {
                }
            }
        });
    }

    public void setInterface(InterfaceMain interface_main) {
        interfaceMain = interface_main;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET},
                MY_PERMISSIONS_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    interfaceMain.update();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void startSimpleAds() {
        appnextAPI = new AppnextAPI(this, getString(R.string.placement_id));
        appnextAPI.setAdListener(new AppnextAPI.AppnextAdListener() {
            @Override
            public void onAdsLoaded(ArrayList<AppnextAd> arrayList) {
                ad = arrayList.get(0);
                activityMainBinding.nvgtVw.getMenu().findItem(R.id.mn_ads).setTitle(ad.getAdTitle());
                activityMainBinding.nvgtVw.getMenu().findItem(R.id.mn_ads).setIcon(R.drawable.ic_ads);
                activityMainBinding.nvgtVw.getMenu().findItem(R.id.mn_ads).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        appnextAPI.adClicked(ad);
                        appnextAPI.adImpression(ad);
                        return false;
                    }
                });
                activityMainBinding.nvgtVw.getMenu().findItem(R.id.mn_ads_privacy).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        appnextAPI.privacyClicked(ad);
                        return false;
                    }
                });
            }

            @Override
            public void onError(String s) {
            }
        });
        appnextAPI.setCreativeType(AppnextAPI.TYPE_MANAGED);
        appnextAPI.loadAds(new AppnextAdRequest());
    }

    private void startInteristialAds() {
        if(interstitial_Ad == null) {
            interstitial_Ad = new Interstitial(this, getString(R.string.placement_id));
            interstitial_Ad.loadAd();
            interstitial_Ad.setBackButtonCanClose(true);
            interstitial_Ad.setSkipText(getString(R.string.skip_ads));
            interstitial_Ad.setOnAdLoadedCallback(new OnAdLoaded() {
                @Override
                public void adLoaded() {
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        public void run() {
                            if(isActivityVisible) {
                                interstitial_Ad.showAd();
                            }
                        }
                    };
                    handler.postDelayed(r, 5000);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (appnextAPI != null) {
            appnextAPI.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityVisible = true;
        if(interstitial_Ad_time < 3) {
            interstitial_Ad_time++;
        }else{
            startInteristialAds();
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        isActivityVisible = false;
    }
}
