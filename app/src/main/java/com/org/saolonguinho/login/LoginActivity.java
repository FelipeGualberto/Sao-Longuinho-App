package com.org.saolonguinho.login;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.org.saolonguinho.MainActivity;
import com.org.saolonguinho.R;
import com.org.saolonguinho.databinding.ActivityLoginBinding;
import com.org.saolonguinho.signup.SignupActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
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
        activityLoginBinding.buttonFacebook.setOnClickListener(OnClickFacebookListener);
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
                        Toast.makeText(getApplicationContext(), "Usuário ou senha errados", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    };
    View.OnClickListener OnClickFacebookListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException err) {
                    err.getCode();
                    if (user == null) {
                        Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                    } else if (user.isNew()) {
                        Log.d("MyApp", "User signed up and logged in through Facebook!");
                        getUserDetailFromFB();
                    } else {
             //           Log.d("MyApp", user.getEmail()); ;
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

    void getUserDetailFromFB(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),new GraphRequest.GraphJSONObjectCallback(){
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try{
                    object.getString("name");
                }catch(JSONException e){
                    e.printStackTrace();
                }
                try{
                   object.getString("email");
                }catch(JSONException e){
                    e.printStackTrace();
                }
                saveNewUser();
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields","name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }
    void saveNewUser(){
        ParseUser user = ParseUser.getCurrentUser();
      //  user.setUsername(t_username.getText().toString());
      //  user.setEmail(t_email.getText().toString());
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
            //    alertDisplayer("First Time Login Welcome", "User:" + t_username.getText().toString() + " Login.Email:" + t_email.getText().toString());
            }
        });
    }
    void getUserDetailFromParse(){
        ParseUser user = ParseUser.getCurrentUser();
      //  t_username.setText(user.getUsername());
      //  t_email.setText(user.getEmail());
      //  alertDisplayer("Welcome Back","User:"+t_username.getText().toString()+" Login.Email:"+t_email.getText().toString());

    }
}