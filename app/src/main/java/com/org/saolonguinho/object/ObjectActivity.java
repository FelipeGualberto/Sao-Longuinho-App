package com.org.saolonguinho.object;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
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
import com.org.saolonguinho.login.LoginActivity;
import com.org.saolonguinho.shared.models.Objects;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.Date;
import java.util.List;

public class ObjectActivity extends AppCompatActivity {
    ActivityObjectBinding activityObjectBinding;
    ProgressDialog progressDialog;

    private File photo;
    private Objects object;

    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;

    public static Intent createIntent(Context context, String id) {
        Intent intent = new Intent(context, ObjectActivity.class);
        intent.putExtra("id_object", id);
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
                //        activityObjectBinding.alarmView.setVisibility(View.VISIBLE);
            } else {
                //        activityObjectBinding.alarmView.setVisibility(View.GONE);
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
                    testConnection();
                }
            }).start();
            return true;
        }
    };
    MenuItem.OnMenuItemClickListener onMenuItemDeleteClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            createDialogDeleteObject();
            return true;
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
        if (getIntent().getStringExtra("id_object") != null) {
            populateFields();
        }
        configureToolbar();
        configureTriggers();
    }

    private void populateFields() {
        final String id_object = getIntent().getStringExtra("id_object");
        ParseQuery<Objects> query = ParseQuery.getQuery(Objects.class);
        query.include(Objects.LOCATION);
        query.whereEqualTo("objectId", id_object);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Objects>() {
            @Override
            public void done(List<Objects> list, ParseException e) {
                if (e == null) {
                    try {
                        object = list.get(0);
                        activityObjectBinding.itemName.setText(list.get(0).getNameObject());
                        activityObjectBinding.itemLocation.setText(list.get(0).getLocation().getDescription());
                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "SaoLonguinho", id_object + ".png");
                        Bitmap bitImage = BitmapFactory.decodeFile(file.getAbsolutePath());
                        activityObjectBinding.ivPhoto.setImageBitmap(bitImage);
                    } catch (Exception er) {
                    }
                } else {
                }
            }
        });
    }

    private void configureToolbar() {
        activityObjectBinding.toolbar.setTitle(R.string.app_name);
        activityObjectBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        activityObjectBinding.toolbar.setNavigationOnClickListener(onNavigationClickListener);
        activityObjectBinding.toolbar.inflateMenu(R.menu.menu_object_activity);
        if (getIntent().getStringExtra("id_object") == null) {
            activityObjectBinding.toolbar.getMenu().findItem(R.id.action_delete).setVisible(false);
        }
    }

    private void configureTriggers() {
        //activityObjectBinding.turnOnAlarm.setOnCheckedChangeListener(onSwitchClickListener);
        //  activityObjectBinding.alarmView.setOnClickListener(onClickAlarmViewListener);
        activityObjectBinding.toolbar.getMenu().findItem(R.id.action_save).setOnMenuItemClickListener(onMenuItemSaveClickListener);
        activityObjectBinding.toolbar.getMenu().findItem(R.id.action_delete).setOnMenuItemClickListener(onMenuItemDeleteClickListener);
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
        final String id_object = getIntent().getStringExtra("id_object");
        if (id_object == null) {
            object = new Objects();
        }
        object.setNameObject(activityObjectBinding.itemName.getText().toString());
        object.setLocation(activityObjectBinding.itemLocation.getText().toString(), new Date());
        object.setUser(ParseUser.getCurrentUser());
        if ((photo != null) && connection) {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "SaoLonguinho", id_object + ".png");
            file.delete();
            object.setImageObject(photo);
        }
        object.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    try {
                        object.pin();
                    } catch (ParseException er) {
                        er.printStackTrace();
                    }
                }
            }
        });
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
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                save(true);
                            }
                        }).start();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                save(false);
                Toast.makeText(getApplicationContext(), "Sem rede, tente enviar a foto posteriormente", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

    private void createDialogDeleteObject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ObjectActivity.this);
        builder.setMessage("Você deseja apagar o objeto?");
        builder.setCancelable(true);
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        object.deleteEventually();
                        finish();
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
}
