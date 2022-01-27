package com.loan.loaneazy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.loan.loaneazy.R;
import com.loan.loaneazy.utility.Constants;
import com.pixplicity.easyprefs.library.Prefs;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //String localIpAddress = getLocalIpAddress();
        //Log.e("TAG", "onCreate: "+localIpAddress );
        Handler mHandler = new Handler();
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                //boolean aBoolean = Prefs.getBoolean(Constants.hasCoupon, false);
                String mUserId = Prefs.getString(Constants.USERID);
                if (TextUtils.isEmpty(mUserId)) {
                    Intent intentIntro = new Intent(SplashActivity.this, IntroActivity.class);
                    startActivity(intentIntro);
                    finish();
                } else {
                    Intent intentHome = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intentHome);
                    finish();
                }

                /*Intent intentIntro = new Intent(SplashActivity.this, IntroActivity.class);
                startActivity(intentIntro);*/

            }
        };
        mHandler.postDelayed(mRunnable, 5000);
    }


}