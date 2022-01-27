package com.loan.loaneazy.activity.certificate;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loan.loaneazy.R;
import com.loan.loaneazy.adapter.ReportAdapter;
import com.loan.loaneazy.model.ReportStatus;
import com.loan.loaneazy.utility.Constants;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.utility.UrlUtils;
import com.loan.loaneazy.views.MyTextView;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class VerificationStatusActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "VerificationStatusActiv";
    String[] mStatusParam = {"Submitted", "On Process", "KYC Verified", "Risk Verified", "Successful", "Failed", "Pending"};
    private ImageView ivRefresh;
    int mCount = 0;
    private MyTextView tvStatus;
    private ProgressDialog mProgress;
    private ReportStatus mReportStatus;
    private ArrayList<ReportStatus.LoanHistory> mLoanHistory;
    private MyTextView tvAadharKyc, tvImageKyc, tvPanKyc, tvTotalLoanRequest, tvDisbursed, tvAmountRepay;
    private MyTextView toolbarTitle;
    private RecyclerView rvLoanReport;
    private ReportAdapter mReportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_status);
        ivRefresh = (ImageView) findViewById(R.id.ivRefresh);
        tvStatus = (MyTextView) findViewById(R.id.tvStatus);
        tvAadharKyc = (MyTextView) findViewById(R.id.tvAadharKyc);
        tvImageKyc = (MyTextView) findViewById(R.id.tvImageKyc);
        tvPanKyc = (MyTextView) findViewById(R.id.tvPanKyc);
        tvTotalLoanRequest = (MyTextView) findViewById(R.id.tvTotalLoanRequest);
        tvDisbursed = (MyTextView) findViewById(R.id.tvDisbursed);
        tvAmountRepay = (MyTextView) findViewById(R.id.tvAmountRepay);
        rvLoanReport = (RecyclerView) findViewById(R.id.rvLoanReport);
        setupActionBar(getString(R.string.verification_status));
        ivRefresh.setOnClickListener(this);
        rvLoanReport.setNestedScrollingEnabled(false);
        rvLoanReport.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        getStatus();
    }

    public void showLoading() {
        mProgress = new ProgressDialog(VerificationStatusActivity.this);
        mProgress.setMessage("Please wait.....");
        mProgress.show();
    }

    public void hide() {
        mProgress.dismiss();
    }

    private void getStatus() {
        showLoading();
        String loginData = Prefs.getString(Constants.LOGINDATA);
        String userId = MyHelper.getUserId(loginData);
        String mStatusURL = UrlUtils.LOGIN_PORT + UrlUtils.URL_USER_REPORT + userId;
        //String mStatusURL = UrlUtils.LOGIN_PORT + UrlUtils.URL_USER_REPORT + "test1";
        AndroidNetworking.get(mStatusURL)
                .setPriority(Priority.IMMEDIATE)
                .setTag("status")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hide();
                        //Log.e(TAG, "onResponse: " + response);
                        mReportStatus = new ReportStatus();
                        try {
                            mReportStatus.setAadhar_kyc_is_done(response.getBoolean("aadhar_kyc_is_done"));
                            mReportStatus.setImage_verification_is_done(response.getBoolean("image_verification_is_done"));
                            mReportStatus.setKycStatus(response.getBoolean("kycStatus"));
                            mReportStatus.setKyc_message(response.getString("kyc_message"));
                            mReportStatus.setName(response.getString("name"));
                            mReportStatus.setPan_kyc_is_done(response.getBoolean("pan_kyc_is_done"));
                            mReportStatus.setTotal_amount_of_loan_applied(response.getLong("total_amount_of_loan_applied"));
                            mReportStatus.setTotal_amount_of_loan_disbursed(response.getLong("total_amount_of_loan_disbursed"));
                            mReportStatus.setTotal_amount_of_loan_repayed(response.getLong("total_amount_of_loan_repayed"));
                            mReportStatus.setTotal_number_loan_taken(response.getLong("total_number_loan_taken"));
                            upDateKycInfo();

                            if (response.has("_userLoanHistory")) {
                                JSONArray userLoanHistory = response.getJSONArray("_userLoanHistory");
                                mLoanHistory = new ArrayList<ReportStatus.LoanHistory>();
                                Gson mGSon = new Gson();
                                Type userListType = new TypeToken<ArrayList<ReportStatus.LoanHistory>>() {
                                }.getType();
                                rvLoanReport.setVisibility(View.VISIBLE);
                                mLoanHistory = mGSon.fromJson(String.valueOf(userLoanHistory), userListType);
                                mReportAdapter = new ReportAdapter(mLoanHistory, VerificationStatusActivity.this);
                                rvLoanReport.setAdapter(mReportAdapter);

                            } else {
                                //Log.e(TAG, "onResponse: user does not have loan history");
                                rvLoanReport.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        hide();
                        //Log.e(TAG, "onError: " + anError.getErrorBody());
                        //Log.e(TAG, "onError: " + anError.getErrorDetail());
                        MyHelper.showErrorToast(anError,VerificationStatusActivity.this);
                    }
                });

    }

    private void upDateKycInfo() {
        toolbarTitle.setText(getString(R.string.verification_status) + " of " + mReportStatus.getName());
        tvAadharKyc.setText("" + mReportStatus.isAadhar_kyc_is_done());
        tvImageKyc.setText("" + mReportStatus.isImage_verification_is_done());
        tvPanKyc.setText("" + mReportStatus.isPan_kyc_is_done());
        tvTotalLoanRequest.setText("\u20B9" + mReportStatus.getTotal_amount_of_loan_applied());
        tvDisbursed.setText("\u20B9" + mReportStatus.getTotal_amount_of_loan_disbursed());
        tvAmountRepay.setText("\u20B9" + mReportStatus.getTotal_amount_of_loan_repayed());
    }

    private void setupActionBar(String param) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = (MyTextView) toolbar.findViewById(R.id.toolbarTitle);
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
        if (view.getId() == R.id.ivRefresh) {
            /*mCount = mCount + 1;
            if (mCount >= mStatusParam.length - 1) {
                mCount = 0;
                tvStatus.setText(mStatusParam[mCount]);
            } else {
                tvStatus.setText(mStatusParam[mCount]);
            }*/
            getStatus();
        }
    }
}