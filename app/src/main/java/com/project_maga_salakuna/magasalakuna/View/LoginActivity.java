package com.project_maga_salakuna.magasalakuna.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.project_maga_salakuna.magasalakuna.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    Button emailloginbtn;
    Button signinbtn;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_login);
        setTitle("Log In");
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.fb_login_btn);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.project_maga_salakuna.magasalakuna",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        emailloginbtn = (Button) findViewById(R.id.emailloginbtn);
        emailloginbtn.setTransformationMethod(null);
        signinbtn = (Button) findViewById(R.id.signinbtn);
        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        emailloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast toast = Toast.makeText(context, "Successfully Logged In", Toast.LENGTH_LONG);
                toast.show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("loginmethod","fb");
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                Toast toast = Toast.makeText(context, "Login Cancelled", Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast toast = Toast.makeText(context, "Login Unsuccessful", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        SharedPreferences prefs = getSharedPreferences(
                "com.project_maga_salakuna.magasalakuna", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if(prefs.getBoolean("isLoggedIn", false)){
            String id = prefs.getString("id","Null");
            String firstname = prefs.getString("firstname","Null");
            String lastname = prefs.getString("lastname","Null");
            String email = prefs.getString("email","Null");
            String phone = prefs.getString("phone","Null");
            String picture = prefs.getString("picture","Null");
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.putExtra("id",id);
            i.putExtra("loginmethod","email");
            i.putExtra("firstname", firstname);
            i.putExtra("lastname", lastname);
            i.putExtra("email", email);
            i.putExtra("phone", phone);
            i.putExtra("picture", picture);
            finish();
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
