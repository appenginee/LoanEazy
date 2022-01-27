package com.loan.loaneazy.activity.profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.loan.loaneazy.R;
import com.loan.loaneazy.activity.login.LoginActivity;
import com.loan.loaneazy.utility.Constants;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.utility.UrlUtils;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.views.MyEditText;
import com.loan.loaneazy.views.MyTextView;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

public class UpdateBankActivity extends AppCompatActivity implements View.OnClickListener {
    MyEditText edBankAcNo, edIfscCode;
    MyButton btnSubmitDetials;
    private ProgressDialog mProgress;
    private static final String TAG = "UpdateBankActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bank);
        setupActionBar("Bank Account Info");
        edBankAcNo = (MyEditText) findViewById(R.id.edBankAcNo);
        edIfscCode = (MyEditText) findViewById(R.id.edIfscCode);
        btnSubmitDetials = (MyButton) findViewById(R.id.btnSubmitDetials);
        btnSubmitDetials.setOnClickListener(this);


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
    public void onClick(View view) {
        if (view.getId() == R.id.btnSubmitDetials) {
            if (edBankAcNo.getText().toString().trim().isEmpty()) {
                edBankAcNo.setError("enter account no");
                edBankAcNo.requestFocus();
                return;
            }

            if (edIfscCode.getText().toString().trim().isEmpty()) {
                edIfscCode.setError("enter ifsc code");
                edIfscCode.requestFocus();
                return;
            }

            if (MyHelper.isNetworkAvailable(this)) {
                makeParameters();
            } else {
                MyHelper.showToast(this, getString(R.string.info_no_internet));
            }


        }
    }

    private void makeParameters() {
        JSONObject mJSON = new JSONObject();
        try {
            mJSON.put(Constants.USERID, Prefs.getString(Constants.USERID));
            mJSON.put("bank_account_number", edBankAcNo.getText().toString().trim());
            mJSON.put("ifsc_code", edIfscCode.getText().toString().trim());
            String macAddress = getMacAddress();
            mJSON.put("deviceIP", macAddress);
            mJSON.put("deviceOs", "Android");
            mJSON.put("deviceId", MyHelper.getDeviceId(this));
            showLoading();
            //http://103.108.220.161:8080/support/v1/bankaccountupdate
            String Url_BankUpdate = UrlUtils.LOGIN_PORT + UrlUtils.URL_BANKUPDATE;
            AndroidNetworking.post(Url_BankUpdate)
                    .addHeaders("Content-Type", "application/json")
                    .addJSONObjectBody(mJSON)
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hide();
                            Log.e(TAG, "onResponse: " + response);
                            try {
                                /*if (response.getBoolean("isSuccess")) {
                                    showSuccessDialog("Registration Success : " + response.getString("userid"), response.getString("msg"));
                                } else if (!response.getBoolean("isSuccess")) {
                                    showErrorDialog("Registration Failed : " + response.getString("userid"), response.getString("msg"));
                                }*/
                                MyHelper.showToast(UpdateBankActivity.this, response.getString(Constants.MSG));
                                onBackPressed();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onError(ANError anError) {
                            hide();
                            //MyHelper.showToast(UpdateBankActivity.this, anError.getErrorBody());
                            MyHelper.showErrorToast(anError, UpdateBankActivity.this);

                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public String getMacAddress() {
        try {
            List<NetworkInterface> networkInterfaceList = Collections.list(NetworkInterface.getNetworkInterfaces());
            String stringMac = "";
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
    }

    public void showLoading() {
        mProgress = new ProgressDialog(UpdateBankActivity.this);
        mProgress.setMessage("Please wait.....");
        mProgress.show();
    }

    public void hide() {
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }
}