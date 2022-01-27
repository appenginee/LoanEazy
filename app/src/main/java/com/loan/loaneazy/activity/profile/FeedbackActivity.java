package com.loan.loaneazy.activity.profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.loan.loaneazy.R;
import com.loan.loaneazy.activity.login.LoginActivity;
import com.loan.loaneazy.bottom_fragment.BottomSuccessDialog;
import com.loan.loaneazy.my_interface.CommonListener;
import com.loan.loaneazy.utility.Constants;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.utility.UrlUtils;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.views.MyEditText;
import com.loan.loaneazy.views.MyTextView;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener, CommonListener {
    private static final String TAG = "FeedbackActivity";
    private RatingBar ratingBar;
    private MyEditText edComments;
    private MyButton btnSubmitRating;
    private ProgressDialog mProgress;
    private BottomSuccessDialog mBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        edComments = (MyEditText) findViewById(R.id.edComments);
        btnSubmitRating = (MyButton) findViewById(R.id.btnSubmitRating);
        btnSubmitRating.setOnClickListener(this);
        setupActionBar("Feedback");
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
        if (view.getId() == R.id.btnSubmitRating) {
            if (ratingBar.getRating() == 0) {
                MyHelper.showToast(FeedbackActivity.this, "please select rating");
                return;
            }
            if (edComments.getText().toString().trim().isEmpty()) {
                edComments.setError("enter your comments");
                edComments.requestFocus();
                return;
            }

            if (MyHelper.isNetworkAvailable(this)) {
                MyHelper.hideKeyboard(this);
                makeRequest();
            } else {
                MyHelper.showToast(FeedbackActivity.this, getString(R.string.info_no_internet));
            }

        }
    }

    public void showLoading() {
        mProgress = new ProgressDialog(FeedbackActivity.this);
        mProgress.setMessage("Please wait.....");
        mProgress.show();
    }

    public void hide() {
        mProgress.dismiss();
    }

    private void makeRequest() {
        JSONObject mJSON = new JSONObject();
        try {
            mJSON.put(Constants.USERID, Prefs.getString(Constants.USERID));
            mJSON.put(Constants.USER_MESSAGE, edComments.getText().toString().trim());
            mJSON.put(Constants.START, ratingBar.getRating());
            showLoading();
            //"http://103.108.220.161:8080/support/v1/feedback"
            String mFeedBackUrl= UrlUtils.LOGIN_PORT+UrlUtils.URL_FEEDBACK;

            AndroidNetworking.post(mFeedBackUrl)
                    .setPriority(Priority.IMMEDIATE)
                    .setTag("rating")
                    .addJSONObjectBody(mJSON)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hide();
                            Log.e(TAG, "onResponse: " + response);
                            try {
                                String message = response.getString(Constants.MESSAGE);
                                showSuccessDialog("Status : " + "Success", message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onError(ANError anError) {
                            //String errorDetail = anError.getErrorDetail();
                            //Log.e(TAG, "onError: " + errorDetail);
                            hide();
                            MyHelper.showErrorToast(anError, FeedbackActivity.this);
                            //MyHelper.showToast(FeedbackActivity.this, anError.getErrorBody());
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

    @Override
    public void onSuccess() {
        if (mBottomSheetDialog != null)
            mBottomSheetDialog.dismiss();
        onBackPressed();
        finish();

    }
}