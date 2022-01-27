package com.loan.loaneazy.activity.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import com.loan.loaneazy.activity.HomeActivity;
import com.loan.loaneazy.utility.Constants;
import com.loan.loaneazy.utility.MDateUtils;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.views.MyContactEditText;
import com.loan.loaneazy.views.MyEditText;
import com.loan.loaneazy.views.MyTextView;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class LoginActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private MyTextView tvLoginPwd;
    private MyTextView SMSQuickLoginTex;
    private LinearLayout linearGmail;

    private GoogleSignInAccount mAccount;
    private LinearLayout faceBookLoginBut;
    private MyButton loginBut;
    private MyTextView forget_passwordTex;
    private MyEditText edUserID;
    private MyContactEditText edPhoneNo;
    private MyEditText edPwd;
    private ProgressDialog mProgress;
    private ImageView swtOnOff;
    private int mCount;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    LinearLayout rLayout;
    private boolean mUserChangeSetting = false;
    private LocationCallback mLocationCallback;
    private Location mLocatoin;
    private static final int REQUEST_CHECK_SETTINGS = 9999;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edUserID = (MyEditText) findViewById(R.id.edUserId);
        edPhoneNo = (MyContactEditText) findViewById(R.id.edPhoneNo);
        swtOnOff = (ImageView) findViewById(R.id.swtOnOff);
        edPwd = (MyEditText) findViewById(R.id.edPwd);
        rLayout = (LinearLayout) findViewById(R.id.rLayout);

        forget_passwordTex = (MyTextView) findViewById(R.id.forget_passwordTex);
        loginBut = (MyButton) findViewById(R.id.btnLoginPwd);
        //swtOnOff.setOnCheckedChangeListener(this);
        swtOnOff.setOnClickListener(this);
        loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (MyHelper.isNetworkAvailable(LoginActivity.this)) {
                    if (isPermissionOk()) {
                        checkGpsStatus();
                    } else {
                        requestPermission();
                    }
                }*/
                /*String mCEST = "07/10/2021 21:54:28";
                String timeDate = getTimeDate(mCEST);
                Log.e(TAG, "onClick: CEST :: " + mCEST);
                Log.e(TAG, "onClick:  IST :: " + timeDate);*/
                verify();


            }
        });

        proceed();

    }

    public void proceed() {
        forget_passwordTex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntentSMS = new Intent(LoginActivity.this, ForgotPwdActivity.class);
                startActivity(mIntentSMS);
            }
        });
        tvLoginPwd = (MyTextView) findViewById(R.id.tvLoginPwd);
        SMSQuickLoginTex = (MyTextView) findViewById(R.id.SMSQuickLoginTex);
        linearGmail = (LinearLayout) findViewById(R.id.linearGmail);
        faceBookLoginBut = (LinearLayout) findViewById(R.id.faceBookLoginBut);
        faceBookLoginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentHome = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intentHome);
            }
        });
        setupActionBar(getString(R.string.index_title_tex));

        tvLoginPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntentPwd = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(mIntentPwd);
            }
        });

        SMSQuickLoginTex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*checkAndRequestPermissions();
                if (checkAndRequestPermissions()){
                    Intent mIntentSMS=new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(mIntentSMS);
                }*/
                Intent mIntentSMS = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(mIntentSMS);
            }
        });

        linearGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Prefs.putString(Constants.HEIGHT, String.valueOf(height));
        Prefs.putString(Constants.WIDTH, String.valueOf(width));
        //init();
    }

    private String getTimeDate(String dateString) {
        //  DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String ourDate = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            TimeZone utc = TimeZone.getTimeZone("CEST");
            formatter.setTimeZone(utc);
            Date value = formatter.parse(dateString);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            ourDate = dateFormatter.format(value);
            return ourDate;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ourDate;
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
       // Log.e(TAG, "updateLocationUI: " + from);
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
                                    rae.startResolutionForResult(LoginActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.e(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                    Snackbar snackbar = Snackbar.make(rLayout, permission_desc, Snackbar.LENGTH_INDEFINITE)
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
                            showErrorDialog(getResources().getString(R.string.permission_title), permission_desc);
                        }
                    }, 200);
                }


            }
        }
    }

    private void showErrorDialog(String title, String desc) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
//CEST time zone
        try {
            MyHelper.hideKeyboard(LoginActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "verify: need to called below method");
        getToken();

    }

    private void getToken() {
        String currentDate = MDateUtils.getCurrentDate();
        //Log.e(TAG, "getToken: " + currentDate);
        String trim = edUserID.getText().toString().trim();
       // Log.e(TAG, "getToken: " + trim);
        //"http://103.108.220.161:8084/risk/user?username=test1&email=2121212121@emunity.in&birthdate=01/01/2000"
        //String url = "http://103.108.220.161:8084/risk/user?" + "username=" + edUserID.getText().toString().trim() + "&email=2121212121@emunity.in&birthdate=" + currentDate;
        String url = "http://103.174.102.113:8084/risk/user?" + "username=" + edUserID.getText().toString().trim() + "&email=2121212121@emunity.in&birthdate=" + currentDate;
        AndroidNetworking.post(url)
                /*.addBodyParameter("username", "Wasim02")
                .addBodyParameter("email,", "2121212121@emunity.in")
                .addBodyParameter("birthdate", currentDate)*/
                .setPriority(Priority.IMMEDIATE)
                .setTag("token")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.e(TAG, "onResponse: " + response);
                        Prefs.putString(Constants.TOKEN, response.toString());
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = response.getJSONObject(Constants.TOKEN);
                            //Prefs.putString(Constants.CREATIONDTM,response);
                            if (jsonObject.getBoolean(Constants.SERVICE)) {
                                requestLogin();
                            } else {
                                if (jsonObject.has("msg")) {
                                    MyHelper.showToast(LoginActivity.this, jsonObject.getString(Constants.MSG));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (jsonObject.has(Constants.SERVICE)) {

                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        //Log.e(TAG, "onError: " + anError.getResponse());
                        //Toast.makeText(LoginActivity.this, "" + anError.getErrorDetail(), Toast.LENGTH_SHORT).show();
                        MyHelper.showErrorToast(anError, LoginActivity.this);

                    }
                });


    }

    private void requestLogin() {

        JSONObject mRoot = new JSONObject();
        try {
            mRoot.put("userid", edUserID.getText().toString().trim());
            mRoot.put("pswd", edPwd.getText().toString().trim());
            mRoot.put("mobile", "+91" + edPhoneNo.getPhoneNumberNumber());
            String macAddress = getMacAddress();
            mRoot.put("deviceIP", macAddress);
            mRoot.put("deviceOs", "Android");
            double latitude;
            double longitude;

            if (mLocatoin != null) {
                latitude = mLocatoin.getLatitude();
                longitude = mLocatoin.getLongitude();
            } else {
                latitude = 0.0;
                longitude = 0.0;
            }
            mRoot.put("location", "" + latitude + ":" + longitude);
            mRoot.put("deviceID", MyHelper.getDeviceId(this));
            //Log.e(TAG, "requestLogin: " + mRoot);

            showLoading();
            //AndroidNetworking.post("http://103.108.220.161:8080/user/login/userlogin")
            AndroidNetworking.post("http://103.174.102.113:8080/user/login/userlogin")
                    .addHeaders("Content-Type", "application/json")
                    .addJSONObjectBody(mRoot)
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hide();
                            //Log.e(TAG, "onResponse: " + response);
                            try {
                                if (response.getBoolean("isSuccess")) {
                                    Prefs.putString(Constants.USERID, response.getString(Constants.USERID));
                                    Prefs.putString(Constants.LOGINDATA, response.toString());
                                    Toast.makeText(LoginActivity.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                                    Intent intentHome = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intentHome);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            hide();
                            Log.e(TAG, "onError: "+anError.getErrorBody() );
                            //MyHelper.showToast(LoginActivity.this, anError.getErrorBody());
                            MyHelper.showErrorToast(anError, LoginActivity.this);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void showLoading() {
        mProgress = new ProgressDialog(LoginActivity.this);
        mProgress.setMessage("Please wait.....");
        mProgress.show();
    }

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
        if (view.getId() == R.id.swtOnOff) {
            //mCount = mCount + 1;
            if (mCount == 0) {
                edPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                if (Objects.requireNonNull(edPwd.getText()).toString().trim().length() > 0) {
                    edPwd.setSelection(edPwd.getText().toString().trim().length());
                }
                Glide.with(LoginActivity.this).load(R.drawable.icon_eye_on).into(swtOnOff);
                mCount = 1;
            } else {
                edPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                if (Objects.requireNonNull(edPwd.getText()).toString().trim().length() > 0) {
                    edPwd.setSelection(edPwd.getText().toString().trim().length());
                }
                Glide.with(LoginActivity.this).load(R.drawable.icon_eye_off).into(swtOnOff);
                mCount = 0;
            }
        }
    }
}