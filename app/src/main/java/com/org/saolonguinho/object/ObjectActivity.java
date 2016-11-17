package com.org.saolonguinho.object;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.org.saolonguinho.R;
import com.org.saolonguinho.databinding.ActivityObjectBinding;
import com.org.saolonguinho.shared.models.Objects;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.Date;

public class ObjectActivity extends AppCompatActivity {
    ActivityObjectBinding activityObjectBinding;
    ProgressDialog progressDialog;

    File photo ;

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
            dialogTimePicker.show(getSupportFragmentManager(), "ObjectActivity");
        }
    };

    MenuItem.OnMenuItemClickListener onMenuItemSaveClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            save();
            finish();
            return false;
        }
    };

   View.OnClickListener onClickChangePhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            takePhoto();
        }
    };

    private void save() {
        Objects objects = new Objects();
        objects.setNameObject(activityObjectBinding.itemName.getText().toString());
        objects.setLocation(activityObjectBinding.itemLocation.getText().toString(), new Date());
        objects.setUser(ParseUser.getCurrentUser());
        File photo = new File(Environment.getExternalStorageDirectory() +  File.separator + "SaoLonguinho", "temp.jpg");
        objects.setImageObject(photo);
        objects.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityObjectBinding = DataBindingUtil.setContentView(this, R.layout.activity_object);
        progressDialog = new ProgressDialog(getApplicationContext());
        photo = new File(Environment.getExternalStorageDirectory() +  File.separator + "SaoLonguinho", "temp.jpg");
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
        photo.mkdir();
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
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
        }
    }

}
