package com.org.saolonguinho.login;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.org.saolonguinho.R;
import com.org.saolonguinho.databinding.ActivityLoginBinding;
import com.org.saolonguinho.signup.SignupActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding activityLoginBinding;
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        setTriggers();
    }

    private void setTriggers(){
        activityLoginBinding.buttonCreateAcc.setOnClickListener(onClickCreatAccListener);
    }

   View.OnClickListener onClickCreatAccListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(SignupActivity.createIntent(getApplicationContext()));
        }
    };
}
