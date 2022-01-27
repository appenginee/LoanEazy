package com.loan.loaneazy.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.loan.loaneazy.R;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.utility.UrlUtils;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.views.MyContactEditText;
import com.loan.loaneazy.views.MyEditText;
import com.loan.loaneazy.views.MyTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LoginPasswordActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private MyButton loginBut;
    private MyTextView forget_passwordTex;
    private MyEditText edUserID;
    private MyContactEditText edPhoneNo;
    private MyEditText edPwd;
    private ProgressDialog mProgress;
    private SwitchCompat swtOnOff;
    private static final String TAG = "LoginPasswordActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);
        edUserID = (MyEditText) findViewById(R.id.edUserId);
        edPhoneNo = (MyContactEditText) findViewById(R.id.edPhoneNo);
        swtOnOff = (SwitchCompat) findViewById(R.id.swtOnOff);
        edPwd = (MyEditText) findViewById(R.id.edPwd);

        forget_passwordTex = (MyTextView) findViewById(R.id.forget_passwordTex);
        loginBut = (MyButton) findViewById(R.id.btnLoginPwd);
        swtOnOff.setOnCheckedChangeListener(this);
        loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyHelper.isNetworkAvailable(LoginPasswordActivity.this)) {
                    verify();
                }

                /*Intent intentHome = new Intent(LoginPasswordActivity.this, HomeActivity.class);
                startActivity(intentHome);*/

            }
        });
        forget_passwordTex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntentSMS = new Intent(LoginPasswordActivity.this, ForgotPwdActivity.class);
                startActivity(mIntentSMS);
            }
        });
        setupActionBar("Login via password");

    }

    private void verify() {
        if (edUserID.getText().toString().trim().isEmpty()) {
            //MyHelper.showToast();
            edUserID.setError("please enter user id");
            edUserID.requestFocus();
            return;
        }
        if (edPhoneNo.getPhoneNumberNumber().isEmpty()) {
            edPhoneNo.setError("please enter your phone number");
            edPhoneNo.requestFocus();
            return;
        }
        if (edPhoneNo.getPhoneNumberNumber().length() < 10) {
            edPhoneNo.setError("please enter valid phone number");
            edPhoneNo.requestFocus();
            return;
        }
        if (edPwd.getText().toString().trim().isEmpty()) {
            edPwd.setError("please enter login password");
            edPwd.requestFocus();
            return;
        }


        requestLogin();

    }

    private void requestLogin() {

        JSONObject mRoot = new JSONObject();
        try {
            mRoot.put("userid", edUserID.getText().toString().trim());
            mRoot.put("pswd", edPwd.getText().toString().trim());
            mRoot.put("mobile", edPhoneNo.getPhoneNumberNumber());
            String macAddress = getMacAddress();
            mRoot.put("deviceIP", macAddress);
            mRoot.put("deviceOs", "Android");
            mRoot.put("location", "INDIA");
            mRoot.put("deviceID", MyHelper.getDeviceId(this));
            Log.e(TAG, "requestLogin: " + mRoot);

            showLoading();
            String urlLogin = UrlUtils.LOGIN_PORT + UrlUtils.LOGIN_URL;
            AndroidNetworking.post(urlLogin)
                    .addHeaders("Content-Type", "application/json")
                    .addJSONObjectBody(mRoot)
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hide();
                            Log.e(TAG, "onResponse: " + response);

                        }

                        @Override
                        public void onError(ANError anError) {
                            hide();
                            MyHelper.showToast(LoginPasswordActivity.this, anError.getErrorBody());
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void showLoading() {
        mProgress = new ProgressDialog(LoginPasswordActivity.this);
        mProgress.setMessage("Please wait.....");
        mProgress.show();
    }

    /*public String getMacAddress() {
        try {
            List<NetworkInterface> networkInterfaceList = Collections.list(NetworkInterface.getNetworkInterfaces());
            String stringMac = "";
            if (networkInterfaceList==null){
                return edUserID.getText().toString() + " has mac address null";
            }
            for (NetworkInterface networkInterface : networkInterfaceList) {
                if (networkInterface.getName().equalsIgnoreCase("wlon0")) ;
                {
                    for (int i = 0; i < networkInterface.getHardwareAddress().length; i++) {
                        String stringMacByte = Integer.toHexString(networkInterface.getHardwareAddress()[i] & 0xFF);
                        if (stringMacByte.length() == 1) {
                            stringMacByte = "0" + stringMacByte;
                        }
                        if (i >= networkInterface.getHardwareAddress().length - 1) {
                            stringMac = stringMac + stringMacByte.toUpperCase();
                        } else {
                            stringMac = stringMac + stringMacByte.toUpperCase() + ":";
                        }

                    }
                    break;
                }
            }
            return stringMac;
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "0";
    }*/
    public String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return edUserID.getText().toString() + " has mac address null";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            return edUserID.getText().toString() + " has mac address null";
        }
        return edUserID.getText().toString() + " has mac address null";
    }

    public void hide() {
        mProgress.dismiss();
    }

    private void setupActionBar(String param) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MyTextView toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText(param);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayUseLogoEnabled(false);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // action bar menu behaviour
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            edPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            if (Objects.requireNonNull(edPwd.getText()).toString().trim().length() > 0) {
                edPwd.setSelection(edPwd.getText().toString().trim().length());
            }
        } else {
            edPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            if (Objects.requireNonNull(edPwd.getText()).toString().trim().length() > 0) {
                edPwd.setSelection(edPwd.getText().toString().trim().length());
            }
        }
    }
}