package com.loan.loaneazy.activity.profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.loan.loaneazy.R;
import com.loan.loaneazy.activity.login.LoginActivity;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.utility.UrlUtils;
import com.loan.loaneazy.views.MyTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HelpActivity";
    private MyTextView tvTogether;
    private RadioButton rEnglish, rHindi, rBengali;
    private String mType;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        tvTogether = (MyTextView) findViewById(R.id.tvTogether);
        rEnglish = (RadioButton) findViewById(R.id.rEnglish);
        rHindi = (RadioButton) findViewById(R.id.rHindi);
        rBengali = (RadioButton) findViewById(R.id.rBengali);

        rEnglish.setOnClickListener(this);
        rHindi.setOnClickListener(this);
        rBengali.setOnClickListener(this);

        setupActionBar("Help");

        mType = "ENG";
        rEnglish.setChecked(true);
        makeRequest(mType);

    }

    public void showLoading() {
        mProgress = new ProgressDialog(HelpActivity.this);
        mProgress.setMessage("Please wait.....");
        mProgress.show();
    }

    public void hide() {
        mProgress.dismiss();
    }

    private void makeRequest(String mType) {


        showLoading();
        //http://103.108.220.161:8080/support/v1/help/+ mType
        String Url_Help = UrlUtils.LOGIN_PORT + UrlUtils.URL_HELP + mType;
        AndroidNetworking.get(Url_Help)
                .setPriority(Priority.IMMEDIATE)
                .setTag("rating")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hide();
                        //Log.e(TAG, "onResponse: " + response);
                        //String message = response.getString(Constants.MESSAGE);
                          /*  Map<String, Object> stringObjectMap = jsonToMap(response);
                            Log.e(TAG, "onResponse: 2 :: " + stringObjectMap);*/

                        Map<String, String> map = new Gson()
                                .fromJson(response.toString(), new TypeToken<HashMap<String, String>>() {
                                }.getType());
                        //Log.e(TAG, "onResponse: 4 :: " + map);
                        String lineBreak = "<br>";
                        String boldStart = "<b>";
                        String boldEnd = "</b>";
                        String param = "";
                        for (Map.Entry<String, String> mMap : map.entrySet()) {
                            String key = mMap.getKey();
                            String value = mMap.getValue();
                            param = param + boldStart + key + boldEnd + lineBreak + value + lineBreak;



                            /*Log.e(TAG, "onResponse: key :: " + key);
                            Log.e(TAG, "onResponse: value :: " + value);*/

                        }
                        tvTogether.setText(Html.fromHtml(param));


                    }

                    @Override
                    public void onError(ANError anError) {
                        //String errorDetail = anError.getErrorDetail();
                        //Log.e(TAG, "onError: " + errorDetail);
                        hide();
                        //MyHelper.showToast(HelpActivity.this, anError.getErrorBody());
                        MyHelper.showErrorToast(anError, HelpActivity.this);
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
    public void onClick(View view) {
        if (view.getId() == R.id.rEnglish) {
            mType = "ENG";
            makeRequest(mType);

        } else if (view.getId() == R.id.rHindi) {
            mType = "HIND";
            makeRequest(mType);

        } else if (view.getId() == R.id.rBengali) {
            mType = "BENG";
            makeRequest(mType);


        }
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

}