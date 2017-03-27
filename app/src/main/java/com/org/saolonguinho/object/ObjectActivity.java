package com.org.saolonguinho.object;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.org.saolonguinho.ApplicationSaoLonguinho;
import com.org.saolonguinho.R;
import com.org.saolonguinho.databinding.ActivityObjectBinding;
import com.org.saolonguinho.login.LoginActivity;
import com.org.saolonguinho.shared.models.Objects;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

public class ObjectActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 3;
    ActivityObjectBinding activityObjectBinding;
    ProgressDialog progressDialog;

    private File photo;
    private Objects object;

    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;

    public static RequestQueue queue = Volley.newRequestQueue(ApplicationSaoLonguinho.getAppContext());

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
            final String itemName = activityObjectBinding.itemName.getText().toString();
            final String itemLocation = activityObjectBinding.itemLocation.getText().toString();
            if (!(itemName.equals("")) && !(itemLocation.equals(""))) {
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        testConnection();
                    }
                }).start();
            } else {
                Toast.makeText(ObjectActivity.this, "Por favor preencha todos os campos", Toast.LENGTH_LONG).show();
            }
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
        progressDialog.setMessage("Isso pode demorar um pouco..");
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
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.displayImage("file://" + Environment.getExternalStorageDirectory() + File.separator + "SaoLonguinho" + File.separator + id_object + ".png", activityObjectBinding.ivPhoto);
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
        //activityObjectBinding.alarmView.setOnClickListener(onClickAlarmViewListener);
        activityObjectBinding.toolbar.getMenu().findItem(R.id.action_save).setOnMenuItemClickListener(onMenuItemSaveClickListener);
        activityObjectBinding.toolbar.getMenu().findItem(R.id.action_delete).setOnMenuItemClickListener(onMenuItemDeleteClickListener);
        activityObjectBinding.btnChangePhoto.setOnClickListener(onClickChangePhotoListener);
    }

    public void takePhoto() {
        photo = new File(Environment.getExternalStorageDirectory() + File.separator + "SaoLonguinho", "temp.jpg");
        //Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.org.saolonguinho", photo);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(intent, TAKE_PICTURE);
        } else {
            requestPermission();
        }
    }

    private void save(boolean connection) {
        final String id_object = getIntent().getStringExtra("id_object");
        if (id_object == null) {
            object = new Objects();
        }
        String itemName = activityObjectBinding.itemName.getText().toString();
        String itemLocation = activityObjectBinding.itemLocation.getText().toString();
        Boolean saveImage = false;
        object.setNameObject(itemName);
        object.setLocation(itemLocation, new Date());
        object.setUser(ParseUser.getCurrentUser());
        if ((photo != null) && connection) {
            if (id_object != null) {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "SaoLonguinho", id_object + ".png");
                file.delete();
            }
            object.setImageObject(photo);
            saveImage = true;
        }
        final Boolean finalSaveImage = saveImage;
        object.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    try {
                        object.pin();
                        if (finalSaveImage) {
                            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "SaoLonguinho", object.getObjectId() + ".png");
                            photo.renameTo(file);
                        }
                    } catch (ParseException er) {
                        er.printStackTrace();
                    }
                } else {
                    problemToast();
                }
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OutputStream outStream = null;
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    //  ImageLoader imageLoader = ImageLoader.getInstance();
                    //  imageLoader.displayImage("file://" + photo.getAbsolutePath(), activityObjectBinding.ivPhoto);
                    try {
                        activityObjectBinding.ivPhoto.setImageBitmap(imageBitmap);
                        outStream = new FileOutputStream(photo);
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        outStream.flush();
                        outStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        problemToast();
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    photo.delete(); // Deleta o arquivo vazio
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
        String url = "https://www.google.com.br/";
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
        int socketTimeout = 4000;//4 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }

    private void createDialogDeleteObject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ObjectActivity.this);
        builder.setMessage("Você deseja apagar o objeto?");
        builder.setCancelable(true);
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        progressDialog.show();
                        object.getLocation().deleteEventually(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                object.deleteEventually(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            finish();
                                        } else {
                                            problemToast();
                                        }
                                    }
                                });
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

    private void problemToast() {
        Toast.makeText(getApplicationContext(), "Ocorreu um erro, por favor tente novamente", Toast.LENGTH_LONG).show();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ObjectActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
