package com.org.saolonguinho.edit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.org.saolonguinho.R;

public class EditObjectActivity extends AppCompatActivity {
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, EditObjectActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_object);
    }
}
