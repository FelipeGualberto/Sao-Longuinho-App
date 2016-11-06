package com.org.saolonguinho.login;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.org.saolonguinho.MainActivity;
import com.org.saolonguinho.R;
import com.org.saolonguinho.databinding.ActivityLoginBinding;
import com.org.saolonguinho.signup.SignupActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding activityLoginBinding;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        setTriggers();
    }

    private void setTriggers() {
        activityLoginBinding.buttonCreateAcc.setOnClickListener(onClickCreatAccListener);
        activityLoginBinding.buttonLogin.setOnClickListener(OnClickLoginListener);
    }

    View.OnClickListener onClickCreatAccListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(SignupActivity.createIntent(getApplicationContext()));
        }
    };
    View.OnClickListener OnClickLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ParseUser.logInInBackground(activityLoginBinding.loginText.getText().toString(), activityLoginBinding.senhaText.getText().toString(), new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Intent intent = MainActivity.createIntent(getApplicationContext());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                    }
                }
            });
        }
    };
}