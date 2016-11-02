package com.project_maga_salakuna.magasalakuna.View;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.project_maga_salakuna.magasalakuna.Controller.JSONParser;
import com.project_maga_salakuna.magasalakuna.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SignInActivity extends Activity {

    EditText email,password;
    Button signin;

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    private static final String LOGIN_URL = "http://176.32.230.51/pathmila.com/maga_salakuna/login.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email=(EditText)findViewById(R.id.emailtxt);
        password= (EditText) findViewById(R.id.passwordtxt);
        signin= (Button) findViewById(R.id.signinbtn);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AttemptLogin().execute();
            }
        });
    }

    public String getEmail(){
        return email.getText().toString();
    }
    public String getPassword(){
        return password.getText().toString();
    }

    class AttemptLogin extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignInActivity.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String emailVal = getEmail();
            String passwordVal = getPassword();
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", emailVal));
                params.add(new BasicNameValuePair("password", passwordVal));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                JSONObject user = json.getJSONObject("user");
                String id = user.getString("id");
                String firstname = user.getString("firstname");
                String lastname = user.getString("lastname");
                String email = user.getString("email");
                String phone = user.getString("phone");
                String picture = user.getString("picture");

                if (success == 1) {
                    Log.d("Login Successful!", json.toString());
                    Intent i = new Intent(SignInActivity.this, MainActivity.class);
                    i.putExtra("id",id);
                    i.putExtra("loginmethod","email");
                    i.putExtra("firstname", firstname);
                    i.putExtra("lastname", lastname);
                    i.putExtra("email", email);
                    i.putExtra("phone", phone);
                    i.putExtra("picture", picture);
                    SharedPreferences prefs = getSharedPreferences(
                            "com.project_maga_salakuna.magasalakuna", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("id",id);
                    editor.putString("loginmethod","email");
                    editor.putString("firstname", firstname);
                    editor.putString("lastname", lastname);
                    editor.putString("email", email);
                    editor.putString("phone", phone);
                    editor.putString("picture", picture);
                    editor.commit();
                    finish();
                    startActivity(i);
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    //Toast.makeText(Login.this, "Invalid login details", Toast.LENGTH_LONG).show();
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(SignInActivity.this, file_url, Toast.LENGTH_LONG).show();
            }

        }
    }
}
