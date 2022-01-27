package com.loan.loaneazy.activity.certificate;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.loan.loaneazy.R;
import com.loan.loaneazy.bottom_fragment.BottomSuccessDialog;
import com.loan.loaneazy.my_interface.CommonListener;
import com.loan.loaneazy.utility.Constants;
import com.loan.loaneazy.utility.MDateUtils;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.utility.UrlUtils;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.views.MyEditText;
import com.loan.loaneazy.views.MyRadioButton;
import com.loan.loaneazy.views.MyTextView;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class BasicInfoActivity extends AppCompatActivity implements View.OnClickListener, CommonListener {
    private static final String TAG = "BasicInfoActivity";
    private MyTextView tv1stRead, tv2ndRead, tv3rdRead, tv4thRead, tv5thRead;
    private MyTextView tv1stEmergent, tv2ndEmergent, tv3rdEmergent, tv4thEmergent, tv5thEmergent, edDobBasic;
    private MyEditText edFName, edLName, edEMailId, edUserIdb, edEducation, edChild, edAddress;
    private MyButton btnSubmitInfo;
    private MyEditText edBankAcNo, edIfscCode;
    private MyRadioButton rbtnMale, rbtnFemale;
    private MyTextView tvAlternate;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ProgressDialog mProgress;
    private BottomSuccessDialog mBottomSheetDialog;
    private int mYear, mMonth, mDay;
    private String mSendDob;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);
        tv1stRead = (MyTextView) findViewById(R.id.tv1stRead);
        tv2ndRead = (MyTextView) findViewById(R.id.tv2ndRead);
        tv3rdRead = (MyTextView) findViewById(R.id.tv3rdRead);
        tv4thRead = (MyTextView) findViewById(R.id.tv4thRead);
        tv5thRead = (MyTextView) findViewById(R.id.tv5thRead);
        tv1stEmergent = (MyTextView) findViewById(R.id.tv1stEmergent);
        tv2ndEmergent = (MyTextView) findViewById(R.id.tv2ndEmergent);
        tv3rdEmergent = (MyTextView) findViewById(R.id.tv3rdEmergent);
        tv4thEmergent = (MyTextView) findViewById(R.id.tv4thEmergent);
        tv5thEmergent = (MyTextView) findViewById(R.id.tv5thEmergent);
        edFName = (MyEditText) findViewById(R.id.edFName);
        edLName = (MyEditText) findViewById(R.id.edLName);
        edEMailId = (MyEditText) findViewById(R.id.edEMailId);
        edDobBasic = (MyTextView) findViewById(R.id.edDobBasic);
        btnSubmitInfo = (MyButton) findViewById(R.id.btnSubmitInfo);

        edBankAcNo = (MyEditText) findViewById(R.id.edBankAcNo);
        edIfscCode = (MyEditText) findViewById(R.id.edIfscCode);
        edUserIdb = (MyEditText) findViewById(R.id.edUserIdb);
        edEducation = (MyEditText) findViewById(R.id.edEducation);
        edChild = (MyEditText) findViewById(R.id.edChild);
        edAddress = (MyEditText) findViewById(R.id.edAddress);

        rbtnMale = (MyRadioButton) findViewById(R.id.rbtnMale);
        rbtnFemale = (MyRadioButton) findViewById(R.id.rbtnFemale);
        tvAlternate = (MyTextView) findViewById(R.id.tvAlternate);

        tv1stRead.setOnClickListener(this);
        tv2ndRead.setOnClickListener(this);
        tv3rdRead.setOnClickListener(this);
        tv4thRead.setOnClickListener(this);
        tv5thRead.setOnClickListener(this);
        tvAlternate.setOnClickListener(this);
        edDobBasic.setOnClickListener(this);
        btnSubmitInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyHelper.isNetworkAvailable(BasicInfoActivity.this)) {

                    verify();
                } else {
                    MyHelper.showToast(BasicInfoActivity.this, "No Internet Connection");
                }

            }
        });
        setupActionBar("Personal Details");

        String loginData = Prefs.getString(Constants.LOGINDATA);
        //Log.e(TAG, "onCreate: " + loginData);
        makeEditableFalse(loginData);


    }

    private void makeEditableFalse(String loginData) {
        String email = MyHelper.getEmail(loginData);
        String userId = MyHelper.getUserId(loginData);
        String name = MyHelper.getName(loginData);
        String[] mName = name.split(" ");
        edUserIdb.setText(userId);
        edUserIdb.setEnabled(false);
        edFName.setText(mName[0]);
        edFName.setEnabled(false);
        edLName.setText(mName[1]);
        edLName.setEnabled(false);
        edEMailId.setText(email);
        edEMailId.setEnabled(false);


    }

    private void verify() {
        if (edUserIdb.getText().toString().trim().isEmpty()) {
            edUserIdb.setError("enter user id");
            edUserIdb.requestFocus();
            return;
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
        if (edEMailId.getText().toString().trim().isEmpty()) {
            edEMailId.setError("enter email id");
            edEMailId.requestFocus();
            return;
        }
        if (!edEMailId.getText().toString().trim().matches(emailPattern)) {
            edEMailId.setFocusable(true);
            edEMailId.setError(getString(R.string.info_enter_emial_id));
            return;
        }

        if (edDobBasic.getText().toString().trim().equalsIgnoreCase("Select your DOB")) {
            MyHelper.showToast(BasicInfoActivity.this, "Please enter your DOB.");
            return;
        }
        if (!rbtnMale.isChecked() && !rbtnFemale.isChecked()) {
            MyHelper.showToast(this, "please select your Gender");
            return;
        }
        if (edEducation.getText().toString().trim().isEmpty()) {
            edEducation.setError("enter your education");
            edEducation.requestFocus();
            return;
        }
        if (edChild.getText().toString().trim().isEmpty()) {
            edChild.setError("enter total");
            edChild.requestFocus();
            return;
        }
        if (edAddress.getText().toString().trim().isEmpty()) {
            edAddress.setError("enter your address");
            edAddress.requestFocus();
            return;
        }
        if (edBankAcNo.getText().toString().trim().isEmpty()) {
            edBankAcNo.setError("enter account no");
            edBankAcNo.requestFocus();
            return;
        }

        if (edIfscCode.getText().toString().trim().isEmpty()) {
            edIfscCode.setError("enter ifsc code");
            edIfscCode.requestFocus();
            return;
        }

        makeJSONRequest();


    }

    public void showLoading() {
        mProgress = new ProgressDialog(BasicInfoActivity.this);
        mProgress.setMessage("Please wait.....");
        mProgress.show();
    }

    public void hide() {
        mProgress.dismiss();
    }

    private void makeJSONRequest() {
        //String mBasicUrl = "http:103.108.220.161:8080/user/login/basicinfoupdate";
        String mBasicUrl = UrlUtils.LOGIN_PORT + UrlUtils.URL_BASIC_INFO;
        JSONObject mJSON = new JSONObject();
        try {
            mJSON.put("userid", edUserIdb.getText().toString().trim());
            if (rbtnMale.isChecked()) {
                mJSON.put("gender", "M");
            } else {
                mJSON.put("gender", "F");
            }

            mJSON.put("education", edEducation.getText().toString().trim());
            mJSON.put("no_of_children", Integer.parseInt(edChild.getText().toString().trim()));
            if (tvAlternate.getText().toString().trim().equalsIgnoreCase("please select")) {
                mJSON.put("alternate_contact_number", "");
            } else {
                mJSON.put("alternate_contact_number", tvAlternate.getText().toString().trim());
            }

            mJSON.put("home_full_address", edAddress.getText().toString().trim());
            mJSON.put("bank_account_number", edBankAcNo.getText().toString().trim());
            mJSON.put("ifsc_code", edIfscCode.getText().toString().trim());
            mJSON.put("dob", mSendDob);
            //Log.e(TAG, "makeJSONRequest: " + mJSON);
            showLoading();
            AndroidNetworking.post(mBasicUrl)
                    .addHeaders("Content-Type", "application/json")
                    .addJSONObjectBody(mJSON)
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hide();
                            //Log.e(TAG, "onResponse: " + response);
                            /*{
                                "msg":"Basic User Info is updated.", "status_code":200
                            }*/
                            //{"msg":"User details is already exists. Latest user data is entered into the database","status_code":302}
                            try {
                                if (response.getInt("status_code") == 200) {
                                    showSuccessDialog("Request Status : Success", "Your request has been successfully saved.");
                                } else if (response.getInt("status_code") == 302) {
                                    showSuccessDialog("Request Status : Success", response.getString(Constants.MSG));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            hide();
                            /*Log.e(TAG, "onError: " + anError.getErrorDetail());
                            Log.e(TAG, "onError: " + anError.getErrorBody());*/
                            //String errorBody = anError.getErrorBody();
                            /*
                            2021-10-10 03:36:32.769 7885-7885/com.loan.loaneazy E/BasicInfoActivity: onError: connectionError
                            2021-10-10 03:36:32.769 7885-7885/com.loan.loaneazy E/BasicInfoActivity: onError: null
                             */
                            //Toast.makeText(BasicInfoActivity.this, "" + anError.getErrorBody(), Toast.LENGTH_LONG).show();
                            MyHelper.showErrorToast(anError,BasicInfoActivity.this);
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
        if (view.getId() == R.id.tv1stRead) {
            Intent intent1st = new Intent(BasicInfoActivity.this, SingleContactActivity.class);
            startActivityForResult(intent1st, 100);
        } else if (view.getId() == R.id.tv2ndRead) {
            Intent intent2nd = new Intent(BasicInfoActivity.this, SingleContactActivity.class);
            startActivityForResult(intent2nd, 101);
        } else if (view.getId() == R.id.tv3rdRead) {
            Intent intent3rd = new Intent(BasicInfoActivity.this, SingleContactActivity.class);
            startActivityForResult(intent3rd, 102);
        } else if (view.getId() == R.id.tv4thRead) {
            Intent intent4th = new Intent(BasicInfoActivity.this, SingleContactActivity.class);
            startActivityForResult(intent4th, 103);
        } else if (view.getId() == R.id.tv5thRead) {
            Intent intent5th = new Intent(BasicInfoActivity.this, SingleContactActivity.class);
            startActivityForResult(intent5th, 104);
        } else if (view.getId() == R.id.tvAlternate) {
            Intent intent5th = new Intent(BasicInfoActivity.this, SingleContactActivity.class);
            startActivityForResult(intent5th, 105);
        } else if (view.getId() == R.id.edDobBasic) {
            getDob();
        }
    }

    private void getDob() {
        Calendar c = Calendar.getInstance();
        c.set(1950, 0, 1);
        Calendar todayCalender = Calendar.getInstance();

        mYear = todayCalender.get(Calendar.YEAR);
        mMonth = todayCalender.get(Calendar.MONTH);
        mDay = todayCalender.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(BasicInfoActivity.this, AlertDialog.THEME_HOLO_DARK,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        //edDateTo.setText("To : " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        int mont = monthOfYear + 1;
                        String month = MDateUtils.getMonth(mont);
                        String dayOfMonth1 = "";
                        if (dayOfMonth < 10) {
                            dayOfMonth1 = "0" + dayOfMonth;
                        } else {
                            dayOfMonth1 = String.valueOf(dayOfMonth);
                        }
                        edDobBasic.setText(" " + dayOfMonth1 + "-" + month + "-" + year);
                        edDobBasic.setTextColor(Color.parseColor("#000000"));
                        mSendDob = "" + year + "/" + mont + "/" + dayOfMonth1;
                        Log.e(TAG, "onDateSet: " + mSendDob);


                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        //datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis() + 86400000);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

        datePickerDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String name, number;
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                name = data.getStringExtra("name");
                number = data.getStringExtra("number");
                tv1stEmergent.setText("1. " + name + " : " + number);
                tv1stEmergent.setVisibility(View.VISIBLE);

                return;
            }
            if (requestCode == 101) {
                name = data.getStringExtra("name");
                number = data.getStringExtra("number");
                /*if (Character.isDigit(name.charAt(0))){

                }*/
                tv2ndEmergent.setText("2. " + name + " : " + number);
                tv2ndEmergent.setVisibility(View.VISIBLE);

                return;
            }
            if (requestCode == 102) {
                name = data.getStringExtra("name");
                number = data.getStringExtra("number");
                tv3rdEmergent.setText("3. " + name + " : " + number);
                tv3rdEmergent.setVisibility(View.VISIBLE);

                return;
            }
            if (requestCode == 103) {
                name = data.getStringExtra("name");
                number = data.getStringExtra("number");
                tv4thEmergent.setText("4. " + name + " : " + number);
                tv4thEmergent.setVisibility(View.VISIBLE);

                return;
            }
            if (requestCode == 104) {
                name = data.getStringExtra("name");
                number = data.getStringExtra("number");
                tv5thEmergent.setText("5. " + name + " : " + number);
                tv5thEmergent.setVisibility(View.VISIBLE);
                return;
            }

            if (requestCode == 105) {
                name = data.getStringExtra("name");
                number = data.getStringExtra("number");
                tvAlternate.setText(number);
            }
        }
    }

    @Override
    public void onSuccess() {
        onBackPressed();
    }
}