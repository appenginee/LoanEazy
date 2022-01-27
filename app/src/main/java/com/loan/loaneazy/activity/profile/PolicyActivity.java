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

import org.json.JSONException;
import org.json.JSONObject;

public class PolicyActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PolicyActivity";
    private ProgressDialog mProgress;
    private MyTextView tvTogether;
    private RadioButton rEnglish, rHindi, rBengali;
    private String mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        setupActionBar("Privacy Agreement");
        tvTogether = (MyTextView) findViewById(R.id.tvTogether);
        rEnglish = (RadioButton) findViewById(R.id.rEnglish);
        rHindi = (RadioButton) findViewById(R.id.rHindi);
        rBengali = (RadioButton) findViewById(R.id.rBengali);

        rEnglish.setOnClickListener(this);
        rHindi.setOnClickListener(this);
        rBengali.setOnClickListener(this);

        mType = "ENG";
        rEnglish.setChecked(true);
        makeRequest(mType);


    }

    private void makeRequest(String mType) {


        showLoading();
        //"http://103.108.220.161:8080/support/v1/privacyagree/" + mType
        String Url_Privacy = UrlUtils.LOGIN_PORT + UrlUtils.URL_PRIVACY + mType;
        AndroidNetworking.get(Url_Privacy)
                .setPriority(Priority.IMMEDIATE)
                .setTag("rating")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hide();
                        try {
                            String lineBreak = "<br>";
                            String boldStart = "<b>";
                            String boldEnd = "</b>";
                            String param = "";
                            String header_msg = response.getString("header_msg");
                            String aggrement = response.getString("aggrement");

                            String mFinalHeader=boldStart+header_msg+boldEnd+lineBreak+aggrement;
                            tvTogether.setText(Html.fromHtml(mFinalHeader));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        //String errorDetail = anError.getErrorDetail();
                        //Log.e(TAG, "onError: " + errorDetail);
                        hide();
                        //MyHelper.showToast(PolicyActivity.this, anError.getErrorBody());
                        MyHelper.showErrorToast(anError, PolicyActivity.this);
                    }
                });


    }

    public void showLoading() {
        mProgress = new ProgressDialog(PolicyActivity.this);
        mProgress.setMessage("Please wait.....");
        mProgress.show();
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


}