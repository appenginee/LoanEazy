package com.loan.loaneazy.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.loan.loaneazy.Apps;
import com.loan.loaneazy.R;
import com.loan.loaneazy.activity.login.LoginActivity;
import com.loan.loaneazy.activity.profile.AboutUsActivity;
import com.loan.loaneazy.activity.profile.FeedbackActivity;
import com.loan.loaneazy.activity.profile.HelpActivity;
import com.loan.loaneazy.activity.profile.LoanHistoryActivity;
import com.loan.loaneazy.activity.profile.PolicyActivity;
import com.loan.loaneazy.activity.profile.ReferActivity;
import com.loan.loaneazy.activity.profile.UpdateBankActivity;
import com.loan.loaneazy.bottom_fragment.BottomDeleteAccount;
import com.loan.loaneazy.utility.Constants;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.utility.UrlUtils;
import com.loan.loaneazy.views.MyTextView;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import io.customerly.Callback;
import io.customerly.Customerly;
import kotlin.Unit;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout relativeAbout, relativeLoan, bankRel, feedbackRel, helpRel, privacyPolicyRel, relativeSupport, friendRef;
    private RelativeLayout rLayoutDelete;
    private Handler updateUIHandler = null;
    private MyTextView tvCoupons, tvPoints;
    private static final String TAG = "ProfileFragment";
    private long mLastClickTime = 0;
    private long mLastClickTimeDelete = 0;
    BottomDeleteAccount mDeleteAccount;
    private ProgressDialog progressDialog;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_profile, container, false);
        relativeAbout = (RelativeLayout) mView.findViewById(R.id.relativeAbout);
        relativeLoan = (RelativeLayout) mView.findViewById(R.id.relativeLoan);
        bankRel = (RelativeLayout) mView.findViewById(R.id.bankRel);
        friendRef = (RelativeLayout) mView.findViewById(R.id.friendRef);
        feedbackRel = (RelativeLayout) mView.findViewById(R.id.feedbackRel);
        relativeSupport = (RelativeLayout) mView.findViewById(R.id.relativeSupport);
        privacyPolicyRel = (RelativeLayout) mView.findViewById(R.id.privacyPolicyRel);
        rLayoutDelete = (RelativeLayout) mView.findViewById(R.id.rLayoutDelete);
        helpRel = (RelativeLayout) mView.findViewById(R.id.helpRel);
        tvCoupons = (MyTextView) mView.findViewById(R.id.tvCoupons);
        tvPoints = (MyTextView) mView.findViewById(R.id.tvPoints);

        relativeAbout.setOnClickListener(this);
        rLayoutDelete.setOnClickListener(this);
        relativeLoan.setOnClickListener(this);
        bankRel.setOnClickListener(this);
        feedbackRel.setOnClickListener(this);
        helpRel.setOnClickListener(this);
        privacyPolicyRel.setOnClickListener(this);
        relativeSupport.setOnClickListener(this);
        friendRef.setOnClickListener(this);

        updateUiHandler();
        startThread();


        return mView;
    }


    public void startThread() {
        Thread workerThread = new Thread() {
            @Override
            public void run() {
                boolean aBoolean = Prefs.getBoolean(Constants.hasCoupon, false);
                Message message = new Message();
                if (aBoolean) {
                    message.what = 1;
                } else {
                    message.what = 0;
                }
                // Send message to main thread Handler.
                updateUIHandler.sendMessage(message);
            }
        };
        workerThread.start();
    }


    /* Create Handler object in main thread. */
    private void updateUiHandler() {
        if (updateUIHandler == null) {
            updateUIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // Means the message is sent from child thread.
                    if (msg.what == 1) {
                        // Update ui in main thread.
                        setCoponPoints();
                    } else {
                        getCouponPoints();
                    }
                }
            };
        }
    }

    private void setCoponPoints() {
        String mCoupon = Prefs.getString(Constants.COUPONS);
        String mPoints = Prefs.getString(Constants.POINTS);
        tvCoupons.setText("(" + mCoupon + ")");
        tvPoints.setText("(" + mPoints + ")");
    }

    private void getCouponPoints() {
        //"http://103.108.220.161:8080/support/v1/pointsandcoupons/user1"
        String mUserId = Prefs.getString(Constants.USERID);
        String mPintsUrl = UrlUtils.LOGIN_PORT + UrlUtils.URL_POINT_COUPON + mUserId;
        //http://103.108.220.161:8080/support/v1/pointsandcoupons/user1'
        //Log.e(TAG, "getCouponPoints: "+mPintsUrl );
        //AndroidNetworking.initialize(getContext(),Apps.getClient());


        AndroidNetworking.get(mPintsUrl)
                //.setOkHttpClient(Apps.getClient())
                .setPriority(Priority.IMMEDIATE)
                .setTag("coupon")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.e(TAG, "onResponse: "+response );
                        try {
                            Prefs.putString(Constants.COUPONS, response.getString(Constants.COUPONS));
                            Prefs.putString(Constants.POINTS, response.getString(Constants.POINTS));
                            Prefs.putBoolean(Constants.hasCoupon, true);
                            setCoponPoints();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        //Log.e(TAG, "onError: "+anError.getErrorDetail() );
                        Log.e(TAG, "onError: " + anError.getErrorBody());
                        MyHelper.showToast(getActivity(), anError.getErrorBody());
                    }
                });
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rLayoutDelete) {
            if (SystemClock.elapsedRealtime() - mLastClickTimeDelete < 1000)
                return;
            mLastClickTimeDelete = SystemClock.elapsedRealtime();
            showBottomDialog();

        } else if (view.getId() == R.id.relativeAbout) {
            Intent intentAboutUs = new Intent(getActivity(), AboutUsActivity.class);
            startActivity(intentAboutUs);
        } else if (view.getId() == R.id.relativeLoan) {
            Intent intentLoan = new Intent(getActivity(), LoanHistoryActivity.class);
            startActivity(intentLoan);
        } else if (view.getId() == R.id.bankRel) {
            Intent intentUpDateBank = new Intent(getActivity(), UpdateBankActivity.class);
            startActivity(intentUpDateBank);
        } else if (view.getId() == R.id.privacyPolicyRel) {
            Intent intentPolicy = new Intent(getActivity(), PolicyActivity.class);
            startActivity(intentPolicy);
        } else if ((view.getId() == R.id.feedbackRel)) {
            Intent intentFeedBack = new Intent(getActivity(), FeedbackActivity.class);
            startActivity(intentFeedBack);
        } else if (view.getId() == R.id.helpRel) {
            Intent intentHelp = new Intent(getActivity(), HelpActivity.class);
            startActivity(intentHelp);
        } else if (view.getId() == R.id.relativeSupport) {
            Log.e(TAG, "startPayment: called");
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000)
                return;

            mLastClickTime = SystemClock.elapsedRealtime();

            String loginData = Prefs.getString(Constants.LOGINDATA);
            String email = MyHelper.getEmail(loginData);
            Log.e(TAG, "onClick: " + email);
            if (email != null) {

                Customerly.registerUser(
                        email,
                        null,
                        null,
                        null,
                        null,
                        new Callback() {
                            @Override
                            public Unit invoke() {
                                //Called if the task completes successfully
                                Customerly.openSupport(getActivity());
                                return null;
                            }
                        },
                        new Callback() {
                            @Override
                            public Unit invoke() {
                                //Called if the task fails
                                return null;
                            }
                        });
            }

        } else if (view.getId() == R.id.friendRef) {
            Intent intentRefer = new Intent(getActivity(), ReferActivity.class);
            startActivity(intentRefer);
        }
    }

    private void showBottomDialog() {

        mDeleteAccount = new BottomDeleteAccount();
        mDeleteAccount.show(getChildFragmentManager(), mDeleteAccount.getTag());
        mDeleteAccount.setCancelable(false);
    }

    public void hideBottomDialog() {
        if (mDeleteAccount != null)
            deleteRequest();
    }

    private void deleteRequest() {
        showLoading();
        String loginData = Prefs.getString(Constants.LOGINDATA);
        String userId = MyHelper.getUserId(loginData);
        AndroidNetworking.get(UrlUtils.LOGIN_PORT + UrlUtils.URL_DELETE_USER + userId)
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        hideDialog();
                        try {
                            if (response.getBoolean(Constants.DELETED)) {
                                Toast.makeText(getActivity(), "" + response.getString(Constants.MSG), Toast.LENGTH_LONG).show();
                                if (mDeleteAccount != null) {
                                    mDeleteAccount.dismiss();
                                }
                                Prefs.putString(Constants.USERID, "");
                                Prefs.putString(Constants.LOGINDATA, "");
                                Intent intentLogin = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intentLogin);
                                getActivity().finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideDialog();
                        MyHelper.showErrorToast(anError, getActivity());
                    }
                });
    }

    private void showLoading() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait deleting your account.");
        progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing() && progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}