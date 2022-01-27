package com.loan.loaneazy.activity.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.loan.loaneazy.R;
import com.loan.loaneazy.bottom_fragment.BottomErrorDialog;
import com.loan.loaneazy.bottom_fragment.BottomSuccessDialog;
import com.loan.loaneazy.my_interface.CommonErrorListener;
import com.loan.loaneazy.my_interface.CommonListener;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.utility.UrlUtils;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.views.MyContactEditText;
import com.loan.loaneazy.views.MyEditText;
import com.loan.loaneazy.views.MyTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ForgotPwdActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, CommonErrorListener, CommonListener, View.OnClickListener {

    private MyButton btnConfirmForgot;
    private ImageView swtOnOffTyped, swtOnOffReTyped;
    private MyEditText edNewPwd, edNewRePwd;
    private MyContactEditText edPhoneNoF;
    private MyEditText edOtpF;
    private MyTextView tvGetOtpF, tvVerifyOtpF;
    private ProgressDialog mProgress;
    private static final String TAG = "ForgotPwdActivity";
    private String mOtp;
    private MyEditText edUserIdF;
    private BottomErrorDialog mBottomErrorDialog;
    private BottomSuccessDialog mBottomSheetDialog;
    private int mCountNew = 0, mCountRe = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pwd);
        edUserIdF = (MyEditText) findViewById(R.id.edUserIdF);
        edPhoneNoF = (MyContactEditText) findViewById(R.id.edPhoneNoF);
        edOtpF = (MyEditText) findViewById(R.id.edOtpF);
        tvGetOtpF = (MyTextView) findViewById(R.id.tvGetOtpF);
        tvVerifyOtpF = (MyTextView) findViewById(R.id.tvVerifyOtpF);
        btnConfirmForgot = (MyButton) findViewById(R.id.btnConfirmForgot);
        edNewPwd = (MyEditText) findViewById(R.id.edNewPwd);
        edNewRePwd = (MyEditText) findViewById(R.id.edNewRePwd);
        swtOnOffTyped = (ImageView) findViewById(R.id.swtOnOffTyped);
        swtOnOffReTyped = (ImageView) findViewById(R.id.swtOnOffReTyped);

        swtOnOffTyped.setOnClickListener(this);
        swtOnOffReTyped.setOnClickListener(this);


        btnConfirmForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intentHome = new Intent(ForgotPwdActivity.this, HomeActivity.class);
                startActivity(intentHome);*/

                verify();

            }
        });

        tvGetOtpF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edPhoneNoF.getPhoneNumberNumber().isEmpty()) {
                    edPhoneNoF.setError("enter phone number");
                    edPhoneNoF.requestFocus();
                    return;
                }
                if (edPhoneNoF.getPhoneNumberNumber().length() < 10) {
                    edPhoneNoF.setError("enter valid phone number");
                    edPhoneNoF.requestFocus();
                    return;
                }
                if (MyHelper.isNetworkAvailable(ForgotPwdActivity.this)) {
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkCameraPermission();
                    } else {
                        getOtp();
                    }*/
                    getOtp();
                } else {
                    MyHelper.showToast(ForgotPwdActivity.this, "No Internet Connection.");
                }

            }
        });
        setupActionBar("SMS quick login");
    }

    private void verify() {
        if (edUserIdF.getText().toString().trim().isEmpty()) {
            edUserIdF.setError("enter user id");
            edUserIdF.requestFocus();
            return;
        }

        if (edPhoneNoF.getPhoneNumberNumber().isEmpty()) {
            edPhoneNoF.setError("enter phone number");
            edPhoneNoF.requestFocus();
            return;
        }

        if (edPhoneNoF.getPhoneNumberNumber().length() < 10) {
            edPhoneNoF.setError("invalid phone number");
            edPhoneNoF.requestFocus();
            return;
        }

        if (tvGetOtpF.getVisibility() == View.VISIBLE) {
            MyHelper.showToast(this, "Please verify your phone number");
            return;
        }
        if (edNewPwd.getText().toString().trim().isEmpty()) {
            edNewPwd.setError("enter your password");
            edNewPwd.requestFocus();
            return;

        }
        if (edNewRePwd.getText().toString().trim().isEmpty()) {
            edNewRePwd.setError("confirm your password");
            edNewRePwd.requestFocus();
            return;
        }
        if (!edNewPwd.getText().toString().trim().equals(edNewRePwd.getText().toString().trim())) {
            showErrorDialog("Password Mismatch", "Your enter password and re-enter password is different.");
            return;
        }


        if (edOtpF.getText().toString().trim().isEmpty()) {
            edOtpF.setError("please enter OTP");
            edOtpF.requestFocus();
            return;
        }

        if (tvVerifyOtpF.isEnabled()) {
            edOtpF.setError("please verify OTP");
            edOtpF.requestFocus();
            return;
        }
        edOtpF.setEnabled(false);
        tvVerifyOtpF.setEnabled(false);

        makeRequest();

    }

    private void makeRequest() {
        JSONObject mJSON = new JSONObject();
        try {
            mJSON.put("userid", edUserIdF.getText().toString().trim());
            mJSON.put("newpassword", edNewPwd.getText().toString().trim());
            mJSON.put("renewpassword", edNewRePwd.getText().toString().trim());
            mJSON.put("phonenumner", "+91" + edPhoneNoF.getPhoneNumberNumber());
            //String mForgotUrl = "http://103.108.220.161:8080/user/login/forgetpassword";
            String mForgotUrl = UrlUtils.LOGIN_PORT + UrlUtils.FORGOT_PASSWORD;

            showLoading();

            AndroidNetworking.post(mForgotUrl)
                    .addHeaders("Content-Type", "application/json")
                    .addJSONObjectBody(mJSON)
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hide();
                            Log.e(TAG, "onResponse: " + response);
                            //{"msg":"Password updated successfully. Wasim07","status_code":200}
                            try {
                                if (response.getInt("status_code") == 200) {
                                    showSuccessDialog("Request Status : Success", "Your password has been updated successfully.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            hide();
                            Log.e(TAG, "onError: " + anError.getErrorBody());
                            MyHelper.showErrorToast(anError, ForgotPwdActivity.this);
                            //Toast.makeText(ForgotPwdActivity.this, "" + anError.getErrorBody(), Toast.LENGTH_LONG).show();

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

    private void showErrorDialog(String title, String message) {
        mBottomErrorDialog = new BottomErrorDialog();
        Bundle mBundle = new Bundle();
        mBundle.putString("title", title);
        mBundle.putString("message", message);
        mBottomErrorDialog.setArguments(mBundle);
        mBottomErrorDialog.show(getSupportFragmentManager(), mBottomErrorDialog.getTag());
        mBottomErrorDialog.setCancelable(false);
    }

    public void showLoading() {
        mProgress = new ProgressDialog(ForgotPwdActivity.this);
        mProgress.setMessage("Please wait.....");
        mProgress.show();
    }

    public void hide() {
        mProgress.dismiss();
    }

    private void getOtp() {
        showLoading();
        //String mOtpUrl = "http://103.108.220.161:8080/user/login/otpregis/" + edPhoneNoF.getPhoneNumberNumber();
        String mOtpUrl = UrlUtils.LOGIN_PORT + UrlUtils.OTP_REG + edPhoneNoF.getPhoneNumberNumber();
        AndroidNetworking.get(mOtpUrl)
                .setTag("otp")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hide();
                        try {
                            mOtp = response.getString("otp");
                            tvVerifyOtpF.setVisibility(View.VISIBLE);
                            tvGetOtpF.setVisibility(View.GONE);
                            Log.e(TAG, "onResponse: OTP :: " + response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        hide();
                        //MyHelper.showToast(ForgotPwdActivity.this, anError.getErrorBody());
                        MyHelper.showErrorToast(anError, ForgotPwdActivity.this);
                    }
                });

        tvVerifyOtpF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edOtpF.getText().toString().trim().isEmpty()) {
                    edOtpF.setError("please enter otp");
                    edOtpF.requestFocus();
                    return;
                }
                if (edOtpF.getText().toString().trim().length() < 5) {
                    edOtpF.setError("please enter valid otp");
                    edOtpF.requestFocus();
                    return;
                }
                if (!mOtp.isEmpty()) {
                    if (mOtp.equals(edOtpF.getText().toString().trim())) {
                        MyHelper.showToast(ForgotPwdActivity.this, "OTP Successfully Verified");
                        edOtpF.setEnabled(false);
                        tvVerifyOtpF.setEnabled(false);
                    } else {
                        MyHelper.showToast(ForgotPwdActivity.this, "OTP  Verification failed.");
                    }
                }
            }
        });
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
        if (compoundButton.getId() == R.id.swtOnOffTyped) {
            if (b) {
                edNewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                if (Objects.requireNonNull(edNewPwd.getText()).toString().trim().length() > 0) {
                    edNewPwd.setSelection(edNewPwd.getText().toString().trim().length());
                }
            } else {
                edNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                if (Objects.requireNonNull(edNewPwd.getText()).toString().trim().length() > 0) {
                    edNewPwd.setSelection(edNewPwd.getText().toString().trim().length());
                }
            }
        } else if (compoundButton.getId() == R.id.swtOnOffReTyped) {
            if (b) {
                edNewRePwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                if (Objects.requireNonNull(edNewRePwd.getText()).toString().trim().length() > 0) {
                    edNewRePwd.setSelection(edNewRePwd.getText().toString().trim().length());
                }
            } else {
                edNewRePwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                if (Objects.requireNonNull(edNewRePwd.getText()).toString().trim().length() > 0) {
                    edNewRePwd.setSelection(edNewRePwd.getText().toString().trim().length());
                }
            }
        }
    }

    @Override
    public void onRequestError() {
        if (mBottomErrorDialog != null)
            mBottomErrorDialog.dismiss();
    }

    @Override
    public void onSuccess() {
        if (mBottomSheetDialog != null) {
            mBottomSheetDialog.dismiss();
        }
        onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.swtOnOffTyped) {
            if (mCountNew == 0) {
                edNewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                if (Objects.requireNonNull(edNewPwd.getText()).toString().trim().length() > 0) {
                    edNewPwd.setSelection(edNewPwd.getText().toString().trim().length());
                }
                Glide.with(ForgotPwdActivity.this).load(R.drawable.icon_eye_on).into(swtOnOffTyped);
                mCountNew = 1;
            } else {
                edNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                if (Objects.requireNonNull(edNewPwd.getText()).toString().trim().length() > 0) {
                    edNewPwd.setSelection(edNewPwd.getText().toString().trim().length());
                }
                Glide.with(ForgotPwdActivity.this).load(R.drawable.icon_eye_off).into(swtOnOffTyped);
                mCountNew = 0;
            }
        } else if (view.getId() == R.id.swtOnOffReTyped) {
            if (mCountRe == 0) {
                edNewRePwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                if (Objects.requireNonNull(edNewRePwd.getText()).toString().trim().length() > 0) {
                    edNewRePwd.setSelection(edNewRePwd.getText().toString().trim().length());
                }
                Glide.with(ForgotPwdActivity.this).load(R.drawable.icon_eye_on).into(swtOnOffReTyped);
                mCountRe = 1;
            } else {
                edNewRePwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                if (Objects.requireNonNull(edNewRePwd.getText()).toString().trim().length() > 0) {
                    edNewRePwd.setSelection(edNewRePwd.getText().toString().trim().length());
                }
                Glide.with(ForgotPwdActivity.this).load(R.drawable.icon_eye_off).into(swtOnOffReTyped);
                mCountRe = 0;
            }
        }
    }
}