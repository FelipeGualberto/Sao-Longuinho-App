package com.org.saolonguinho.signup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.org.saolonguinho.MainActivity;
import com.org.saolonguinho.R;
import com.org.saolonguinho.databinding.ActivitySignupBinding;
import com.org.saolonguinho.login.LoginActivity;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding activitySignupBinding;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SignupActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignupBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        setToolbar();
        setTriggers();
    }

    private void setTriggers() {
        activitySignupBinding.buttonSignup.setOnClickListener(onClickSignup);
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

    View.OnClickListener onClickSignup = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = activitySignupBinding.emailText.getText().toString();
            String password = activitySignupBinding.passwordText.getText().toString();
            String password_again = activitySignupBinding.passwordConfirmText.getText().toString();
            if (isValidEmail(email) && password.equals(password_again)) {
                if (activitySignupBinding.passwordText.getText().toString().length() >= 5) {
                    ParseUser user = new ParseUser();
                    user.setUsername(email);
                    user.setPassword(password);
                    user.setEmail(email);
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                startSaoLonguinho();
                                // Hooray! Let them use the app now.
                            } else {
                                problemToast();
                                activitySignupBinding.emailText.setText("");
                                activitySignupBinding.passwordText.setText("");
                                activitySignupBinding.passwordConfirmText.setText("");
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Por favor digite uma senha maior que 5 dígitos", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Verifique seu email e senha", Toast.LENGTH_LONG).show();
            }
        }
    };

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    void startSaoLonguinho() {
        Intent intent = MainActivity.createIntent(getApplicationContext());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    void problemToast() {
        Toast.makeText(getApplicationContext(), "Um problema ocorreu, por favor tente novamente", Toast.LENGTH_LONG).show();
    }

}
