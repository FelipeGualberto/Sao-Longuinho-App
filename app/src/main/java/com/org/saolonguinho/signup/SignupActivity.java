package com.org.saolonguinho.signup;

import android.app.ProgressDialog;
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
    private ProgressDialog progressDialog;
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
        progressDialog = new ProgressDialog(SignupActivity.this);
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
            emailIsCorrect();
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

    private void emailIsCorrect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setMessage("O email: " + activitySignupBinding.emailText.getText() + " está correto?");
        builder.setCancelable(true);
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String email = activitySignupBinding.emailText.getText().toString();
                        String password = activitySignupBinding.passwordText.getText().toString();
                        String password_again = activitySignupBinding.passwordConfirmText.getText().toString();
                        if (isValidEmail(email) && password.equals(password_again)) {
                            if (activitySignupBinding.passwordText.getText().toString().length() >= 5) {
                                progressDialog.show();
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
                                            progressDialog.dismiss();
                                            if (e.getCode() == ParseException.EMAIL_TAKEN || e.getCode() == ParseException.USERNAME_TAKEN) {
                                                Toast.makeText(getApplicationContext(), "Usuário já existe (Recupere sua senha em ''Esqueci a senha'')", Toast.LENGTH_LONG).show();
                                            }
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

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }
}
