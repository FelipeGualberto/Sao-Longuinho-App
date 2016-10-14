package com.org.saolonguinho.signup;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.org.saolonguinho.R;
import com.org.saolonguinho.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding activitySignupBinding;
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SignupActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignupBinding = DataBindingUtil.setContentView(this,R.layout.activity_signup);
        setToolbar();
    }

    private void setToolbar() {
        activitySignupBinding.toolbar.setTitle(R.string.app_name);
        activitySignupBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        activitySignupBinding.toolbar.setNavigationOnClickListener(onClickNavigationListener);
    }

    View.OnClickListener onClickNavigationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
