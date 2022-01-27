package com.loan.loaneazy.activity.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.loan.loaneazy.BuildConfig;
import com.loan.loaneazy.R;
import com.loan.loaneazy.activity.certificate.ContactsActivity;
import com.loan.loaneazy.bottom_fragment.BottomErrorDialog;
import com.loan.loaneazy.bottom_fragment.BottomSuccessDialog;
import com.loan.loaneazy.my_interface.CommonErrorListener;
import com.loan.loaneazy.my_interface.CommonListener;
import com.loan.loaneazy.utility.Constants;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.utility.UrlUtils;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.views.MyContactEditText;
import com.loan.loaneazy.views.MyEditText;
import com.loan.loaneazy.views.MyTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements CommonListener, CommonErrorListener, View.OnClickListener {

    private MyButton affirmBut;
    private MyButton btnGetConts;
    private static final String TAG = "RegisterActivity";
    private MyTextView tvContacts;
    private MyTextView tvContactInfo;
    private MyEditText edUserId;
    private MyContactEditText edPhoneNo;
    private MyEditText edFName;
    private MyEditText edPwd;
    private MyEditText edLName;
    private MyEditText edEmail;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private JSONArray mJsonArray;
    BottomSuccessDialog mBottomSheetDialog;
    private BottomErrorDialog mBottomErrorDialog;
    private ProgressDialog mProgress;
    private MyTextView tvGetOtp;
    String mOtp = "";
    //OTPBroadCastReceiver mOTPReceiver;
    BroadcastReceiver mOTPReceiver;
    private LinearLayout linearReg;
    private MyEditText edOtp;
    private String mStr;
    private MyTextView tvVerifyOtp;
    private ImageView swtOnOffReg;
    private int mCount = 0;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;

    private boolean mUserChangeSetting = false;
    private LocationCallback mLocationCallback;
    private Location mLocatoin;
    private static final int REQUEST_CHECK_SETTINGS = 9999;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_s_m_s);
        AndroidNetworking.initialize(getApplicationContext());
        swtOnOffReg = (ImageView) findViewById(R.id.swtOnOffReg);
        tvGetOtp = (MyTextView) findViewById(R.id.tvGetOtp);
        tvVerifyOtp = (MyTextView) findViewById(R.id.tvVerifyOtp);
        edOtp = (MyEditText) findViewById(R.id.edOtp);
        linearReg = (LinearLayout) findViewById(R.id.linearReg);
        affirmBut = (MyButton) findViewById(R.id.affirmBut);
        btnGetConts = (MyButton) findViewById(R.id.btnGetConts);
        tvContacts = (MyTextView) findViewById(R.id.tvContacts);
        tvContactInfo = (MyTextView) findViewById(R.id.tvContactInfo);
        edUserId = (MyEditText) findViewById(R.id.edUserId);
        edPhoneNo = (MyContactEditText) findViewById(R.id.edPhoneNo);
        edPwd = (MyEditText) findViewById(R.id.edPwd);
        edFName = (MyEditText) findViewById(R.id.edFName);
        edLName = (MyEditText) findViewById(R.id.edLName);
        edEmail = (MyEditText) findViewById(R.id.edEmail);
        //edPhoneNo.setText("9836683269");
        //mOtp = "12345";
        //tvGetOtp.setVisibility(View.GONE);
        //tvVerifyOtp.setVisibility(View.VISIBLE);
        swtOnOffReg.setOnClickListener(this);


        affirmBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intentHome = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intentHome);*/

                verify();
                /*if (MyHelper.isNetworkAvailable(RegisterActivity.this)) {
                    //verify();
                    if (isPermissionOk()) {
                        checkGpsStatus();
                    } else {
                        requestPermission();
                    }
                }*/

            }
        });

        btnGetConts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, ContactsActivity.class);
                startActivityForResult(intent, 100);

            }
        });

        tvVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edOtp.getText().toString().trim().isEmpty()) {
                    edOtp.setError("please enter otp");
                    edOtp.requestFocus();
                    return;
                }
                if (edOtp.getText().toString().trim().length() < 5) {
                    edOtp.setError("please enter valid otp");
                    edOtp.requestFocus();
                    return;
                }
                if (!mOtp.isEmpty()) {
                    if (mOtp.equals(edOtp.getText().toString().trim())) {
                        MyHelper.showToast(RegisterActivity.this, "OTP Successfully Verified");
                        edOtp.setEnabled(false);
                        tvVerifyOtp.setEnabled(false);
                    } else {
                        MyHelper.showToast(RegisterActivity.this, "OTP  Verification failed.");
                    }
                }
            }
        });

        tvGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edPhoneNo.getPhoneNumberNumber().isEmpty()) {
                    edPhoneNo.setError("enter phone number");
                    edPhoneNo.requestFocus();
                    return;
                }
                if (edPhoneNo.getPhoneNumberNumber().length() < 10) {
                    edPhoneNo.setError("enter valid phone number");
                    edPhoneNo.requestFocus();
                    return;
                }
                if (MyHelper.isNetworkAvailable(RegisterActivity.this)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkCameraPermission();
                    } else {
                        getOtp();
                    }
                } else {
                    MyHelper.showToast(RegisterActivity.this, "No Internet Connection.");
                }

            }
        });

        setupActionBar("Registration");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));

        /*mOTPReceiver = new OTPBroadCastReceiver();
        IntentFilter mFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        RegisterActivity.this.registerReceiver(mOTPReceiver, mFilter, Manifest.permission.BROADCAST_SMS, null);*/

        /*edOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mStr = mStr + charSequence.toString();
                if (mStr.length() >= 5) {
                    if (mStr.equals(mOtp)) {
                        tvGetOtp.setEnabled(false);
                        edOtp.setEnabled(false);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
       // init();

    }

    private void init() {
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mSettingsClient = LocationServices.getSettingsClient(this);
            mLocationRequest = new LocationRequest();
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    mLocatoin = locationResult.getLastLocation();
                    updateLocationUI("Update location");
                }
            };
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(5000);
            mLocationRequest.setFastestInterval(3000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            mLocationSettingsRequest = builder.build();
        } catch (Exception e) {
            Log.e(TAG, "init: EXCEPTION :: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateLocationUI(String from) {
        Log.e(TAG, "updateLocationUI: " + from);
        //Log.e(TAG, "updateLocationUI: LAT : " + mLocatoin.getLatitude());
        //Log.e(TAG, "updateLocationUI: LNG : " + mLocatoin.getLongitude());
        if (mUserChangeSetting) {
            try {
                hide();
                mUserChangeSetting = false;
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                Log.e(TAG, "updateLocationUI: successfully remove location updates...");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "updateLocationUI: removing location update error occur...");
            }
        }
        verify();
    }

    private boolean isPermissionOk() {
        boolean coursePermission = false, finePermission = false;
        coursePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        finePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        return coursePermission & finePermission;
    }

    private void checkGpsStatus() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.e(TAG, "All location settings are satisfied.");
                        showLoading();
                        mUserChangeSetting = true;
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.e(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(RegisterActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.e(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void requestPermission() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.LOCATION_REQUEST);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE}, Constants.LOCATION_REQUEST);
        }*/
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, Constants.LOCATION_REQUEST);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)


    private void showErrorDialogLocation(String title, String desc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Html.fromHtml("<font color='#28568A'> <b>" + title + "</font></b>"));
        builder.setMessage(Html.fromHtml("<font color='#2C2A2A'> <i>" + desc + "</font></i>"));
        builder.setPositiveButton(Html.fromHtml("<font color='#28568A' ><b>" + "OK" + "</font></b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isPermissionOk()) {
                    checkGpsStatus();
                } else {
                    requestPermission();
                }
            }
        });
        builder.setCancelable(false).create().show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.READ_SMS}, 987);
        } else {
            getOtp();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 987) {
            boolean readPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean writePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if (readPermission && writePermission) {
                getOtp();
            } else {
                forSMSReading(permissions, grantResults);
            }
        } else {
            if (requestCode == Constants.LOCATION_REQUEST) {
                if (isPermissionOk()) {
                    checkGpsStatus();
                } else {
                    boolean setting = false;
                    for (int i = 0; i < permissions.length; i++) {
                        String permission = permissions[i];
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            boolean showRational = shouldShowRequestPermissionRationale(permission);
                            if (!showRational) {
                                Log.e(TAG, "onRequestPermissionsResult: true : " + permission);
                                setting = true;
                                break;
                            }
                        }
                    }
                    if (setting) {
                        String permission_desc;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            permission_desc = getString(R.string.permission_desc_login);
                        } else {
                            permission_desc = getString(R.string.permission_desc_login);
                        }
                        Snackbar snackbar = Snackbar.make(linearReg, permission_desc, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.settings, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Build intent that displays the App settings screen.
                                        Intent intent = new Intent();
                                        intent.setAction(
                                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package",
                                                BuildConfig.APPLICATION_ID, null);
                                        intent.setData(uri);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });
                        snackbar.setActionTextColor(Color.RED);
                        View view = snackbar.getView();
                        TextView sbTextView = view.findViewById(com.google.android.material.R.id.snackbar_text);
                        sbTextView.setMaxLines(3);
                        sbTextView.setTextColor(Color.YELLOW);
                        snackbar.show();
                    } else {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String permission_desc;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    permission_desc = getString(R.string.permission_desc_login);
                                } else {
                                    permission_desc = getString(R.string.permission_desc_login);
                                }
                                showErrorDialogLocation(getResources().getString(R.string.permission_title), permission_desc);
                            }
                        }, 200);
                    }


                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void forSMSReading(String[] permissions, int[] grantResults) {
        boolean setting = false;
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                boolean showRational = shouldShowRequestPermissionRationale(permission);
                if (!showRational) {
                    Log.e(TAG, "onRequestPermissionsResult: true : " + permission);
                    setting = true;
                    break;
                }
            }
        }
        if (setting) {
            String permission_desc;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permission_desc = getString(R.string.permission_read_sms);
            } else {
                permission_desc = getString(R.string.permission_read_sms);
            }
            Snackbar snackbar = Snackbar.make(linearReg, permission_desc, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Build intent that displays the App settings screen.
                            //mSettingEnable = true;
                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View view = snackbar.getView();
            TextView sbTextView = view.findViewById(com.google.android.material.R.id.snackbar_text);
            sbTextView.setMaxLines(4);
            sbTextView.setTextColor(Color.YELLOW);
            snackbar.show();
        } else {
            Toast.makeText(RegisterActivity.this, "permission denied by user!", Toast.LENGTH_SHORT).show();
        }

    }

    private void getOtp() {
        showLoading();
        //String mOtpUrl = "http://103.108.220.161:8080/user/login/otpregis/" + edPhoneNo.getPhoneNumberNumber();
        //String mOtpUrl = UrlUtils.LOGIN_URL + UrlUtils.OTP_REG + edPhoneNo.getPhoneNumberNumber();
        String mOtpUrl = UrlUtils.LOGIN_PORT + UrlUtils.OTP_REG + edPhoneNo.getPhoneNumberNumber();
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
                            tvVerifyOtp.setVisibility(View.VISIBLE);
                            tvGetOtp.setVisibility(View.GONE);
                            Log.e(TAG, "onResponse: OTP :: " + response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        hide();
                        //MyHelper.showToast(RegisterActivity.this, anError.getErrorBody());
                        MyHelper.showErrorToast(anError, RegisterActivity.this);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        //unregisterReceiver(mOTPReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                String substring = message.substring(0, 6);
                Log.e("OTP MESSSGE", message);
                Log.e(TAG, "onReceive: " + substring);
                //final String sender = intent.getStringExtra("Sender");
                /*otpOnlyTextView.setText(message.replaceAll("\\D+", ""));
                fullmessageTextView.setText(sender + " : " + message);*/
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    };

    public void showLoading() {
        mProgress = new ProgressDialog(RegisterActivity.this);
        mProgress.setMessage("Please wait.....");
        mProgress.show();
    }

    public void hide() {
        mProgress.dismiss();
    }

    private void verify() {
        if (edUserId.getText().toString().trim().isEmpty()) {
            edUserId.setError("create user id");
            edUserId.requestFocus();
            return;
        }
        if (edPhoneNo.getPhoneNumberNumber().length() < 10) {
            edPhoneNo.setError("invalid phone number");
            edPhoneNo.requestFocus();
            return;
        }

        if (tvGetOtp.getVisibility() == View.VISIBLE) {
            MyHelper.showToast(this, "Please verify your phone number");
            return;
        }

        if (edPwd.getText().toString().trim().isEmpty()) {
            edPwd.setError("create login password");
            edPwd.requestFocus();
        }
        if (edFName.getText().toString().trim().isEmpty()) {
            edFName.setError("enter first name");
            edFName.requestFocus();
            return;
        }
        if (edLName.getText().toString().trim().isEmpty()) {
            edLName.setError("enter last name");
            edLName.requestFocus();
            return;
        }

        if (edOtp.getText().toString().trim().isEmpty()) {
            edOtp.setError("please verify OTP");
            edOtp.requestFocus();
            return;
        }

        if (!edEmail.getText().toString().trim().matches(emailPattern)) {
            edEmail.setFocusable(true);
            edEmail.setError(getString(R.string.info_enter_emial_id));
            return;
        }
        JSONObject mRoot = new JSONObject();
        try {
            mRoot.put("userid", edUserId.getText().toString().trim());
            mRoot.put("password", edPwd.getText().toString().trim());
            mRoot.put("first_name", edFName.getText().toString().trim());
            mRoot.put("last_name", edLName.getText().toString().trim());
            mRoot.put("mobile", "+91" + edPhoneNo.getPhoneNumberNumber());
            mRoot.put("email", edEmail.getText().toString().trim());
            String macAddress = getMacAddress();
            mRoot.put("deviceIP", macAddress);
            mRoot.put("deviceOs", "Android");
            mRoot.put("deviceId", MyHelper.getDeviceId(this));
            mRoot.put("relations", mJsonArray);
            //Log.e(TAG, "verify: " + mRoot);
            showLoading();
            //"http://103.108.220.161:8080/user/login/newregistion"
            String mRegUrl = UrlUtils.LOGIN_PORT + UrlUtils.URL_NEWREG;
            AndroidNetworking.post(mRegUrl)
                    .addHeaders("Content-Type", "application/json")
                    .addJSONObjectBody(mRoot)
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hide();
                            Log.e(TAG, "onResponse: " + response);
                            try {
                                if (response.getBoolean("isSuccess")) {
                                    showSuccessDialog("Registration Success : " + response.getString("userid"), response.getString("msg"));
                                } else if (!response.getBoolean("isSuccess")) {
                                    showErrorDialog("Registration Failed : " + response.getString("userid"), response.getString("msg"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onError(ANError anError) {
                            hide();
                            //MyHelper.showToast(RegisterActivity.this, anError.getErrorBody());
                            MyHelper.showErrorToast(anError, RegisterActivity.this);

                        }
                    });


        } catch (JSONException e) {
            e.printStackTrace();
        }


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

    private void showSuccessDialog(String title, String message) {
        mBottomSheetDialog = new BottomSuccessDialog();
        Bundle mBundle = new Bundle();
        mBundle.putString("title", title);
        mBundle.putString("message", message);
        mBottomSheetDialog.setArguments(mBundle);
        mBottomSheetDialog.show(getSupportFragmentManager(), mBottomSheetDialog.getTag());
        mBottomSheetDialog.setCancelable(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                String data1 = data.getStringExtra("data");
                Log.e(TAG, "onActivityResult: ok data" + data1);
                try {
                    mJsonArray = new JSONArray(data1);
                    StringBuilder finalString = new StringBuilder();
                    for (int i = 0; i < mJsonArray.length(); i++) {
                        JSONObject index = mJsonArray.getJSONObject(i);
                        String name = index.getString("name");
                        String contact = index.getString("contact");
                        finalString.append(i + 1).append(". ").append(name).append(" ").append(contact).append("\n");
                    }
                    Log.e(TAG, "onActivityResult: " + finalString.toString());
                    tvContactInfo.setVisibility(View.GONE);
                    tvContacts.setText(finalString);
                    tvContacts.setVisibility(View.VISIBLE);
                    btnGetConts.setVisibility(View.GONE);
                    affirmBut.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } else {
            switch (requestCode) {
                // Check for the integer request code originally supplied to startResolutionForResult().
                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            Log.e(TAG, "User agreed to make required location settings changes.");
                            showLoading();
                            mUserChangeSetting = true;
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                            break;
                        case Activity.RESULT_CANCELED:
                            Log.e(TAG, "User chose not to make required location settings changes.");
                            Log.e(TAG, "request again for allow for using device location ");
                            checkGpsStatus();
                            break;
                    }
                    break;

            }
        }
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

    //8A:7B:39:5B:13:EB
    /*public String getMacAddress() {
        try {
            List<NetworkInterface> networkInterfaceList = Collections.list(NetworkInterface.getNetworkInterfaces());
            if (networkInterfaceList == null) {
                return edUserId.getText().toString() + " has mac address null";
            }
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
    }*/

    public String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return edUserId.getText().toString() + " has mac address null";
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
            return edUserId.getText().toString() + " has mac address null";
        }
        return edUserId.getText().toString() + " has mac address null";
    }

    @Override
    public void onSuccess() {
        if (mBottomSheetDialog != null) {
            mBottomSheetDialog.dismiss();
        }
        Intent intentLogin = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intentLogin);
        finish();
    }

    @Override
    public void onRequestError() {
        if (mBottomErrorDialog != null) {
            mBottomErrorDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.swtOnOffReg) {
            if (mCount == 0) {
                edPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                if (Objects.requireNonNull(edPwd.getText()).toString().trim().length() > 0) {
                    edPwd.setSelection(edPwd.getText().toString().trim().length());
                }
                Glide.with(RegisterActivity.this).load(R.drawable.icon_eye_on).into(swtOnOffReg);
                mCount = 1;
            } else {
                edPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                if (Objects.requireNonNull(edPwd.getText()).toString().trim().length() > 0) {
                    edPwd.setSelection(edPwd.getText().toString().trim().length());
                }
                Glide.with(RegisterActivity.this).load(R.drawable.icon_eye_off).into(swtOnOffReg);
                mCount = 0;
            }
        }
    }
}
