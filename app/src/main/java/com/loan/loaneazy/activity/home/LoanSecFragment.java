package com.loan.loaneazy.activity.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.loan.loaneazy.R;
import com.loan.loaneazy.activity.login.LoginActivity;
import com.loan.loaneazy.bottom_fragment.BottomOtp;
import com.loan.loaneazy.bottom_fragment.BottomSuccessDialog;
import com.loan.loaneazy.model.general.VerifyStatus;
import com.loan.loaneazy.utility.Constants;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.utility.UrlUtils;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.views.MyEditText;
import com.loan.loaneazy.views.MyTextView;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class LoanSecFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private MyTextView tvReqAmt, tvReqDate, tvHeader3, tvAadharVerified, tvPanVerified, tvBankVerified, tvNameVerified, tvCivilVerified;
    private String mAmount, mTenure;
    private MyEditText edPurpose;
    private MyButton btnApply;
    private ProgressDialog mProgress;
    private static final String TAG = "LoanSecFragment";
    private BottomSuccessDialog mBottomSheetDialog;
    private Handler updateUIHandler = null;
    private ArrayList<VerifyStatus> mListVerify;
    private MyButton btnESign;
    private BottomOtp fragmentBottomSheet;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = this.getArguments();
        if (arguments != null) {
            mAmount = arguments.getString(Constants.AMOUNT);
            mTenure = arguments.getString(Constants.TENURE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_loan_sec, container, false);
            tvReqAmt = (MyTextView) mView.findViewById(R.id.tvReqAmt);
            tvReqDate = (MyTextView) mView.findViewById(R.id.tvReqDate);
            tvHeader3 = (MyTextView) mView.findViewById(R.id.tvHeader3);
            tvHeader3.setTypeface(tvHeader3.getTypeface(), Typeface.BOLD);
            tvHeader3.setTextColor(Color.parseColor("#FFFF00"));
            tvAadharVerified = (MyTextView) mView.findViewById(R.id.tvAadharVerified);
            tvPanVerified = (MyTextView) mView.findViewById(R.id.tvPanVerified);
            tvBankVerified = (MyTextView) mView.findViewById(R.id.tvBankVerified);
            tvNameVerified = (MyTextView) mView.findViewById(R.id.tvNameVerified);
            tvCivilVerified = (MyTextView) mView.findViewById(R.id.tvCivilVerified);

            edPurpose=(MyEditText)mView.findViewById(R.id.edPurpose);

            btnApply = (MyButton) mView.findViewById(R.id.btnApply);
            btnESign = (MyButton) mView.findViewById(R.id.btnESign);

            btnApply.setOnClickListener(this);
            btnESign.setOnClickListener(this);
            //tvReqAmt.setText("\u20B9" + " " + Prefs.getString("repay"));

            tvReqAmt.setText(Prefs.getString("repay_part"));
            tvReqDate.setText(Prefs.getString("redate"));
            mListVerify = new ArrayList<VerifyStatus>();

            updateUiHandler();
            startThread();

        } else {
            // Do not inflate the layout again.
            // The returned View of onCreateView will be added into the fragment.
            // However it is not allowed to be added twice even if the parent is same.
            // So we must remove view from the existing parent view group
            // (it will be added back).
            //((ViewGroup)view.getParent()).removeView(view);
        }

        return mView;
    }

    /* Create Handler object in main thread. */
    private void updateUiHandler() {
        if (updateUIHandler == null) {
            updateUIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // Means the message is sent from child thread.
                    getVerificationStatus();

                }
            };
        }
    }


    private void getVerificationStatus() {
        //Log.e(TAG, "getVerificationStatus: called" );
        //Log.e(TAG, "getVerificationStatus: "+Prefs.getString(Constants.LOGINDATA) );
        String mUserId = Prefs.getString(Constants.USERID);
        //"http://103.108.220.161:8080/kesh/v1/verificationstatus/" + mUserId
        String mVERIFICATION_STATUS = UrlUtils.LOGIN_PORT + UrlUtils.URL_VERIFICATION_STATUS + mUserId;
        AndroidNetworking.get(mVERIFICATION_STATUS)
                .setPriority(Priority.IMMEDIATE)
                .setTag("verification")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse: "+response );
                        try {
                            VerifyStatus mVerify = new VerifyStatus(response.getBoolean(Constants.ISAADHARVERIFIED)
                                    , response.getBoolean(Constants.ISALLOWEDTOPRCEED),
                                    response.getBoolean(Constants.ISBANKACCOUNTVERIFIED),
                                    response.getBoolean(Constants.ISCIBILVERIFIED),
                                    response.getBoolean(Constants.ISNAMEVERIFIED),
                                    response.getBoolean(Constants.ISPANVERIFIED),
                                    response.getBoolean(Constants.ISSIGNVERIFIED),
                                    response.getString(Constants.MESSAGE));
                            mListVerify.add(mVerify);
                            updateUI();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: "+anError.getErrorBody() );
                        //MyHelper.showToast(getActivity(), anError.getErrorBody());
                        MyHelper.showErrorToast(anError, getActivity());
                    }
                });

    }

    private void updateUI() {
        if (mListVerify.get(0).isAadharVerified()) {
            tvAadharVerified.setText(R.string.info_verified);
        } else {
            tvAadharVerified.setText(R.string.info_not_verified);
            tvAadharVerified.setTextColor(Color.parseColor("#FF0000"));
        }
        if (mListVerify.get(0).isBankAccountVerified()) {
            tvBankVerified.setText(R.string.info_verified);
        } else {
            tvBankVerified.setText(R.string.info_not_verified);
            tvBankVerified.setTextColor(Color.parseColor("#FF0000"));
        }
        if (mListVerify.get(0).isCibilVerified()) {
            tvCivilVerified.setText(R.string.info_verified);
        } else {
            tvCivilVerified.setText(R.string.info_not_verified);
            tvCivilVerified.setTextColor(Color.parseColor("#FF0000"));
        }
        if (mListVerify.get(0).isNameVerified()) {
            tvNameVerified.setText(R.string.info_verified);
        } else {
            tvNameVerified.setText(R.string.info_not_verified);
            tvNameVerified.setTextColor(Color.parseColor("#FF0000"));
        }
        if (mListVerify.get(0).isPanVerified()) {
            tvPanVerified.setText(R.string.info_verified);
        } else {
            tvPanVerified.setText(R.string.info_not_verified);
            tvPanVerified.setTextColor(Color.parseColor("#FF0000"));
        }
        if (mListVerify.get(0).isAllowedToProceed()) {
            btnApply.setEnabled(true);
        } else {
            btnApply.setEnabled(false);
        }
    }

    public void startThread() {
        Thread workerThread = new Thread() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                // Send message to main thread Handler.
                updateUIHandler.sendMessage(message);
            }
        };
        workerThread.start();
    }


    public void showLoading() {
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Please wait.....");
        mProgress.show();
    }

    public void hide() {
        mProgress.dismiss();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnApply) {
            if (edPurpose.getText().toString().trim().isEmpty()){
                edPurpose.setError("enter loan purpose");
                edPurpose.requestFocus();
                return;
            }

            getOtp();
        } else if (view.getId() == R.id.btnESign) {
            makeSignRequest();
        }
    }

    private void getOtp() {
        showLoading();
        //String mOtpUrl = "http://103.108.220.161:8080/user/login/otpregis/" + edPhoneNoF.getPhoneNumberNumber();

        String phoneNumber = MyHelper.getPhoneNumber(Prefs.getString(Constants.LOGINDATA));
        String mOtpUrl = UrlUtils.LOGIN_PORT + UrlUtils.OTP_REG + phoneNumber;
        AndroidNetworking.get(mOtpUrl)
                .setTag("otp")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hide();
                        try {
                            String mOtp = response.getString("otp");
                            Log.e(TAG, "onResponse: OTP :: " + response);
                            showBottomDialog(mOtp);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                        //Log.e(TAG, "onError: " + anError.getErrorDetail());
                        //Log.e(TAG, "onError: " + anError.getErrorBody());
                        //Log.e(TAG, "onError: " + anError.getErrorCode());
                        hide();
                        //MyHelper.showToast(getActivity(), anError.getErrorBody());
                        MyHelper.showErrorToast(anError, getActivity());
                    }
                });


    }

    private void showBottomDialog(String otp) {
        fragmentBottomSheet = new BottomOtp();
        //Prefs.putString("otp",otp);
        Bundle bundle = new Bundle();
        bundle.putString("otp", otp);
        fragmentBottomSheet.setArguments(bundle);
        fragmentBottomSheet.show(getChildFragmentManager(), fragmentBottomSheet.getTag());
        fragmentBottomSheet.setCancelable(false);
    }

    private void makeSignRequest() {
        String loginData = Prefs.getString(Constants.LOGINDATA);
        String userId = MyHelper.getUserId(loginData);
        String phoneNumber = MyHelper.getPhoneNumber(loginData);
        String name = MyHelper.getName(loginData);
        String email = MyHelper.getEmail(loginData);
        JSONObject mREQUEST = new JSONObject();
        JSONObject mInitRequest = new JSONObject();
        JSONObject mConfigRequest = new JSONObject();
        JSONObject mPositionRequest = new JSONObject();
        JSONObject mPreFillRequest = new JSONObject();
        try {

            mREQUEST.put("userId", userId);
            mInitRequest.put("pdf_pre_uploaded", true);
            mInitRequest.put("esign_type", "suresign");
            mConfigRequest.put("auth_mode", "1");
            mConfigRequest.put("reason", "Contract");
            JSONArray mArray1 = new JSONArray();
            JSONObject mIndex1 = new JSONObject();
            mIndex1.put("x", 10);
            mIndex1.put("y", 20);

            JSONArray mArray2 = new JSONArray();
            JSONObject mIndex2 = new JSONObject();
            mIndex2.put("x", 0);
            mIndex2.put("y", 0);

            mArray1.put(0, mIndex1);
            mArray2.put(0, mIndex2);

            mPositionRequest.put("1", mArray1);
            mPositionRequest.put("2", mArray2);

            mConfigRequest.put("positions", mPositionRequest);

            mPreFillRequest.put("full_name", name);
            mPreFillRequest.put("mobile_number", phoneNumber);
            mPreFillRequest.put("user_email", email);

            mInitRequest.put("config", mConfigRequest);
            mInitRequest.put("prefill_options", mPreFillRequest);

            mREQUEST.put("initializedRequest", mInitRequest);
            //Log.e(TAG, "makeSignRequest: " + mREQUEST);
            showLoading();
            String mSignUrl = UrlUtils.ESIGN_PORT + UrlUtils.URL_ESIGN;
            AndroidNetworking.post(mSignUrl)
                    .setPriority(Priority.IMMEDIATE)
                    .setTag("e-sign")
                    .addJSONObjectBody(mREQUEST)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hide();
                            //Log.e(TAG, "onResponse: " + response);
                            try {
                                if (response.getInt(Constants.STATUS_CODE) == 200) {
                                    Intent intentESign = new Intent(getActivity(), E_SignActivity.class);
                                    intentESign.putExtra(Constants.URL, response.getString(Constants.URL));
                                    startActivity(intentESign);
                                } else {
                                    MyHelper.showToast(getActivity(), "Request failed, please try again");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            hide();
                            //MyHelper.showToast(getActivity(), anError.getErrorDetail());
                            MyHelper.showErrorToast(anError, getActivity());

                        }
                    });


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void submitRequest() {

        String string = Prefs.getString(Constants.USERID);
        JSONObject mJSON = new JSONObject();
        try {
            int mFinalAmount = (int) Float.parseFloat(mAmount);
            mJSON.put(Constants.USERID, string);
            mJSON.put("loanAmount", String.valueOf(mFinalAmount));
            mJSON.put("teanure", mTenure);
            mJSON.put("purpose",edPurpose.getText().toString());
            showLoading();
            //"http://103.108.220.161:8080/kesh/v1/submitloanrequest"
            String URL_SUBMIT_LOAN = UrlUtils.LOGIN_PORT + UrlUtils.URL_SUBMIT_LOAN_REQUEST;
            AndroidNetworking.post(URL_SUBMIT_LOAN)
                    .setTag("details")
                    .setPriority(Priority.IMMEDIATE)
                    .addJSONObjectBody(mJSON)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hide();
                            //Log.e(TAG, "onResponse: "+response );
                            try {
                                showSuccessDialog("Loan Status : Success", response.getString(Constants.MESSAGE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            hide();
                            //MyHelper.showToast(getActivity(), anError.getErrorBody());
                            MyHelper.showErrorToast(anError,getActivity());

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
        mBottomSheetDialog.show(getChildFragmentManager(), mBottomSheetDialog.getTag());
        mBottomSheetDialog.setCancelable(false);
    }

    public void hideBottom() {
        if (mBottomSheetDialog != null)
            mBottomSheetDialog.dismiss();
        getActivity().finish();
    }

    public void hideBottomMakeRequest() {
        if (mBottomSheetDialog != null)
            mBottomSheetDialog.dismiss();
        submitRequest();
    }
}