package com.org.saolonguinho.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionPropagation;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.org.saolonguinho.MainActivity;
import com.org.saolonguinho.R;
import com.org.saolonguinho.about.AboutActivity;
import com.org.saolonguinho.databinding.ActivityLoginBinding;
import com.org.saolonguinho.help.HelpActivity;
import com.org.saolonguinho.signup.SignupActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.ParseFacebookUtils;
import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding activityLoginBinding;
    final List<String> permissions = Arrays.asList("public_profile", "email");
    ProgressDialog progressDialog;
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logando");
        setTriggers();
        isFirstTime();
    }

    private void isFirstTime() {
        SharedPreferences sharedPref =  LoginActivity.this.getPreferences(Context.MODE_PRIVATE);
        boolean firstTime = sharedPref.getBoolean("firstTime", true);
        if(firstTime) {
            startActivity(HelpActivity.createIntent(getApplicationContext()));
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("firstTime", false);
            editor.apply();
        }
    }

    private void setTriggers() {
        activityLoginBinding.buttonCreateAcc.setOnClickListener(onClickCreatAccListener);
        activityLoginBinding.buttonLogin.setOnClickListener(OnClickLoginListener);
        activityLoginBinding.buttonFacebook.setOnClickListener(OnClickFacebookListener);
        activityLoginBinding.forgetPassword.setOnClickListener(OnClickForgetPassword);
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
            progressDialog.show();
            ParseUser.logInInBackground(activityLoginBinding.loginText.getText().toString(), activityLoginBinding.senhaText.getText().toString(), new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        progressDialog.dismiss();
                        startSaoLonguinho();
                    } else {
                        Toast.makeText(getApplicationContext(), "Usuário ou senha errados", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    };
    View.OnClickListener OnClickForgetPassword = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createDialogSendResetEmail();
        }
    };

    View.OnClickListener OnClickFacebookListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException err) {
                    if (user == null) {
                        Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                    } else if (user.isNew()) {
                        Log.d("MyApp", "User signed up and logged in through Facebook!");
                        getUserDetailFromFB();
                    } else {
                        startSaoLonguinho();
                        Log.d("MyApp", "User logged in through Facebook!");
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    void getUserDetailFromFB() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                String email = "";
                boolean isOk = true;
                try {
                    email = object.getString("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                    isOk = false;
                    problemToast();
                }
                if (isOk) {
                    saveNewUser(email);
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    void saveNewUser(String email) {
        ParseUser user = ParseUser.getCurrentUser();
        user.setEmail(email);
        user.setUsername(email);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    startSaoLonguinho();
                } else {
                    problemToast();
                }
            }
        });
    }

    void startSaoLonguinho() {
        Intent intent = MainActivity.createIntent(getApplicationContext());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    void problemToast() {
        Toast.makeText(getApplicationContext(), "Um problema ocorreu, por favor tente novamente. (Verifique se escreveu o email corretamente)", Toast.LENGTH_LONG).show();
    }

    private void createDialogSendResetEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Você deseja mudar a senha?");
        builder.setCancelable(true);
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ParseUser.requestPasswordResetInBackground(activityLoginBinding.loginText.getText().toString(),
                                new RequestPasswordResetCallback() {
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(getApplicationContext(), "Um email foi enviado para você!", Toast.LENGTH_LONG).show();
                                        } else {
                                            problemToast();
                                        }
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
}