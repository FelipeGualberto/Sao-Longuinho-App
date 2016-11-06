package com.org.saolonguinho.object;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.org.saolonguinho.R;
import com.org.saolonguinho.databinding.ActivityObjectBinding;
import com.org.saolonguinho.shared.models.Objects;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;

public class ObjectActivity extends AppCompatActivity {
    ActivityObjectBinding activityObjectBinding;
    ProgressDialog progressDialog;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, ObjectActivity.class);
        return intent;
    }

    View.OnClickListener onNavigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            save();
            finish();
        }
    };

    private void save() {
        Objects objects = new Objects();
        objects.setNameObject(activityObjectBinding.itemName.getText().toString());
        objects.setLocation(activityObjectBinding.itemLocation.getText().toString(), new Date());
        objects.setUser(ParseUser.getCurrentUser());
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
        configureToolbar();
    }

    private void configureToolbar() {
        activityObjectBinding.toolbar.setTitle(R.string.app_name);
        activityObjectBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        activityObjectBinding.toolbar.setNavigationOnClickListener(onNavigationClickListener);
    }
}
