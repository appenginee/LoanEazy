package com.loan.loaneazy.activity.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.loan.loaneazy.R;
import com.loan.loaneazy.activity.certificate.BasicInfoActivity;
import com.loan.loaneazy.activity.certificate.SingleContactActivity;
import com.loan.loaneazy.activity.login.LoginActivity;
import com.loan.loaneazy.bottom_fragment.BottomSuccessDialog;
import com.loan.loaneazy.my_interface.CommonListener;
import com.loan.loaneazy.utility.Constants;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.utility.UrlUtils;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.views.MyContactEditText;
import com.loan.loaneazy.views.MyEditText;
import com.loan.loaneazy.views.MyTextView;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class ReferActivity extends AppCompatActivity implements View.OnClickListener, CommonListener {
    private static final String TAG = "ReferActivity";
    private MyEditText edFullName;
    private MyContactEditText edPhoneNo;
    private MyTextView tvSelect;
    private MyEditText edEmail;
    private MyButton btnRefer;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ProgressDialog mProgress;
    private BottomSuccessDialog mBottomSheetDialog;
    private PhoneNumberUtil phoneUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);
        setupActionBar("Referer");
        edFullName = (MyEditText) findViewById(R.id.edFullName);
        edPhoneNo = (MyContactEditText) findViewById(R.id.edPhoneNo);
        tvSelect = (MyTextView) findViewById(R.id.tvSelect);
        edEmail = (MyEditText) findViewById(R.id.edEmail);
        btnRefer = (MyButton) findViewById(R.id.btnRefer);
        btnRefer.setOnClickListener(this);
        tvSelect.setOnClickListener(this);

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
        if (view.getId() == R.id.btnRefer) {
            verify();
        } else if (view.getId() == R.id.tvSelect) {
            Intent intent1st = new Intent(ReferActivity.this, SingleContactActivity.class);
            startActivityForResult(intent1st, 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                String name = data.getStringExtra("name");
                //String number = data.getStringExtra("number");
                edFullName.setText(name);
                //edPhoneNo.setText(number);
                String mMobileNumber = data.getStringExtra("number");
                edPhoneNo.setText(mMobileNumber);
                try {
                    // phone must begin with '+'
                    if (phoneUtil == null) {
                        phoneUtil = PhoneNumberUtil.createInstance(ReferActivity.this);
                    }
                    Phonenumber.PhoneNumber numberProto = phoneUtil.parse(mMobileNumber, "");
                    int countryCode = numberProto.getCountryCode();
                    long nationalNumber = numberProto.getNationalNumber();
                    edPhoneNo.setText(String.valueOf(nationalNumber));
                    //  Log.e("code", "code " + countryCode);
                    //  Log.e("code", "national number " + nationalNumber);
                } catch (NumberParseException e) {
                    System.err.println("NumberParseException was thrown: " + e.toString());
                }
            }
        }
    }

    private void verify() {
        if (edFullName.getText().toString().trim().isEmpty()) {
            edFullName.setError("enter name");
            edFullName.requestFocus();
            return;
        }
        if (edPhoneNo.getText().toString().trim().isEmpty()) {
            //edPhoneNo.setError("Either phone no or email is required");
            edPhoneNo.setError("enter phone number");
            edPhoneNo.requestFocus();
            return;
        }

        if (edPhoneNo.getText().toString().trim().length() < 10) {
            edPhoneNo.setError("enter valid phone number");
            edPhoneNo.requestFocus();
        }

        if (edEmail.getText().toString().trim().isEmpty()) {
            edEmail.setError("enter email");
            edEmail.requestFocus();
            return;
        }
        if (!edEmail.getText().toString().trim().matches(emailPattern)) {
            edEmail.setFocusable(true);
            edEmail.setError(getString(R.string.info_enter_emial_id));
            return;
        }

        submitRequest();


    }

    private void submitRequest() {
        JSONObject mRequest = new JSONObject();
        String loginData = Prefs.getString(Constants.LOGINDATA);
        String userId = MyHelper.getUserId(loginData);
        try {
            mRequest.put(Constants.USERID, userId);
            mRequest.put(Constants.REFERAL_NAME, edFullName.getText().toString().trim());
            mRequest.put(Constants.REFERAL_EMAIL, edEmail.getText().toString().trim());
            mRequest.put(Constants.REFERAL_PHONE, edPhoneNo.getText().toString().trim());
            showLoading();
            String mReferUrl = UrlUtils.LOGIN_PORT + UrlUtils.URL_REFERENCE;
            AndroidNetworking.post(mReferUrl)
                    .setPriority(Priority.IMMEDIATE)
                    .addJSONObjectBody(mRequest)
                    .setTag("refer")
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hide();
                            Log.e(TAG, "onResponse: " + response);
                            try {
                                String message = response.getString(Constants.MSG);
                                showSuccessDialog("Status : Success", message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            hide();
                            //MyHelper.showToast(ReferActivity.this, anError.getErrorDetail());
                            MyHelper.showErrorToast(anError, ReferActivity.this);
                        }
                    });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showSuccessDialog(String title, String message) {
        mBottomSheetDialog = new BottomSuccessDialog();
        Bundle mBundle = new Bundle();
        mBundle.putString("title", title);
        mBundle.putString("message", message);
        mBottomSheetDialog.setArguments(mBundle);
        mBottomSheetDialog.show(getSupportFragmentManager(), mBottomSheetDialog.getTag());
        mBottomSheetDialog.setCancelable(false);
    }


    public void showLoading() {
        mProgress = new ProgressDialog(ReferActivity.this);
        mProgress.setMessage("Please wait.....");
        mProgress.show();
    }

    public void hide() {
        mProgress.dismiss();
    }

    @Override
    public void onSuccess() {
        if (mBottomSheetDialog != null)
            mBottomSheetDialog.dismiss();
        onBackPressed();
    }
}