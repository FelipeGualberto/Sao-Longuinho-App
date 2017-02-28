package com.org.saolonguinho.object;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.org.saolonguinho.R;
import com.org.saolonguinho.databinding.ActivityObjectBinding;
import com.org.saolonguinho.shared.models.Objects;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.http.ParseHttpRequest;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class ObjectActivity extends AppCompatActivity {
    ActivityObjectBinding activityObjectBinding;
    ProgressDialog progressDialog;

    File photo;

    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, ObjectActivity.class);
        return intent;
    }

    View.OnClickListener onNavigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    CompoundButton.OnCheckedChangeListener onSwitchClickListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                activityObjectBinding.alarmView.setVisibility(View.VISIBLE);
            } else {
                activityObjectBinding.alarmView.setVisibility(View.GONE);
            }
        }
    };

    View.OnClickListener onClickAlarmViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DialogTimePicker dialogTimePicker = DialogTimePicker.newInstance();
            dialogTimePicker.setOnTimeSet(onTimeSet);
            dialogTimePicker.show(getSupportFragmentManager(), "ObjectActivity");
        }
    };

    MenuItem.OnMenuItemClickListener onMenuItemSaveClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            progressDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //save();
                    testConnection();
                }
            }).start();
            return false;
        }
    };

    View.OnClickListener onClickChangePhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            takePhoto();
        }
    };

    DialogTimePicker.OnTimeSet onTimeSet = new DialogTimePicker.OnTimeSet() {
        @Override
        public void onSet(int minute, int hour) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityObjectBinding = DataBindingUtil.setContentView(this, R.layout.activity_object);
        progressDialog = new ProgressDialog(ObjectActivity.this);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setCancelable(false);
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "SaoLonguinho");
        if (!directory.exists()) {
            directory.mkdir();
        }
        configureToolbar();
        configureTriggers();
    }

    private void configureToolbar() {
        activityObjectBinding.toolbar.setTitle(R.string.app_name);
        activityObjectBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        activityObjectBinding.toolbar.setNavigationOnClickListener(onNavigationClickListener);
        activityObjectBinding.toolbar.inflateMenu(R.menu.menu_object_activity);
    }

    private void configureTriggers() {
        activityObjectBinding.turnOnAlarm.setOnCheckedChangeListener(onSwitchClickListener);
        activityObjectBinding.alarmView.setOnClickListener(onClickAlarmViewListener);
        activityObjectBinding.toolbar.getMenu().findItem(R.id.action_save).setOnMenuItemClickListener(onMenuItemSaveClickListener);
        activityObjectBinding.btnChangePhoto.setOnClickListener(onClickChangePhotoListener);
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photo = new File(Environment.getExternalStorageDirectory() + File.separator + "SaoLonguinho", "temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    private void save(boolean connection) {
        Objects objects = new Objects();
        objects.setNameObject(activityObjectBinding.itemName.getText().toString());
        objects.setLocation(activityObjectBinding.itemLocation.getText().toString(), new Date());
        objects.setUser(ParseUser.getCurrentUser());
        if ((photo != null) &&  connection) {
            objects.setImageObject(photo);
        }
        objects.saveEventually();
        progressDialog.dismiss();
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bitImage = BitmapFactory.decodeFile(photo.getAbsolutePath());
                    activityObjectBinding.ivPhoto.setImageBitmap(bitImage);
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    photo.delete();
                    photo = null;
                }
                break;
            case RESULT_CANCELED:
                photo.delete();
                photo = null;
                break;
        }
    }

    private void testConnection() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.back4app.com/";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        save(true);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                save(false);
                Toast.makeText(getApplicationContext(),"Sem rede, tente enviar a foto posteriormente",Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }
}
