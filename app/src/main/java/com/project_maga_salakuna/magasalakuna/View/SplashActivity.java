package com.project_maga_salakuna.magasalakuna.View;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.project_maga_salakuna.magasalakuna.R;

public class SplashActivity extends AppCompatActivity {
    ImageView imageView;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splach);
        imageView = (ImageView) findViewById(R.id.loadingimage);
        context = this;

        final Handler updateBackground = new Handler(){

            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == 21){
                    if(isLoggedIn()){
                        Intent loginIntent = new Intent(SplashActivity.this,MainActivity.class);
                        loginIntent.putExtra("loginmethod","fb");
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.pullright_transition, R.anim.pushleft_transition);
                    }else{
                        Intent loginIntent = new Intent(SplashActivity.this,LoginActivity.class);

                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.pullright_transition, R.anim.pushleft_transition);
                    }

                }else{
                    int resourceId = context.getResources().getIdentifier("loadingcircle"+msg.what, "drawable", "com.project_maga_salakuna.magasalakuna");
                    imageView.setImageResource(resourceId);
                }
            }
        };

        Thread animationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 1;
                int count2 = 0;
                while(count < 21 && count2<5){

                    updateBackground.obtainMessage(count).sendToTarget();
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count++;
                    if(count==21){
                        count = 1;
                        count2++;
                    }
                }
                updateBackground.obtainMessage(21).sendToTarget();
            }
        });
        animationThread.start();

    }
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
}
