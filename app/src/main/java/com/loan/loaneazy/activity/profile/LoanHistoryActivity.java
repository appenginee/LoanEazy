package com.loan.loaneazy.activity.profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.loan.loaneazy.R;
import com.loan.loaneazy.activity.login.LoginActivity;
import com.loan.loaneazy.adapter.LoanAdapter;
import com.loan.loaneazy.bottom_fragment.BottomErrorDialog;
import com.loan.loaneazy.bottom_fragment.BottomOtp;
import com.loan.loaneazy.bottom_fragment.BottomSuccessDialog;
import com.loan.loaneazy.model.loan.LoanRecord;
import com.loan.loaneazy.my_interface.CommonListener;
import com.loan.loaneazy.my_interface.ItemClickListener;
import com.loan.loaneazy.my_interface.OtpConfirmListener;
import com.loan.loaneazy.utility.Constants;
import com.loan.loaneazy.utility.HashGenerationUtils;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.utility.UrlUtils;
import com.loan.loaneazy.views.MyTextView;
import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class LoanHistoryActivity extends AppCompatActivity implements ItemClickListener, CommonListener, OtpConfirmListener {
    private static final String TAG = "LoanHistoryActivity";
    private ProgressDialog mProgress;
    private ArrayList<LoanRecord> mListLoan;
    private RecyclerView rvLoanHist;
    MyTextView toolbarTitle;
    private final String email = "snooze@payu.in";
    private final String phone = "9999999999";
    private final String merchantName = "RH Group";
    private final String surl = "https://payuresponse.firebaseapp.com/success";
    private final String furl = "https://payuresponse.firebaseapp.com/failure";
    private final String amount = "1.0";
    private long mLastClickTime = 0;
    private LinearLayout linearMain;
    //private String testKey = "rszDJj";//debug
    private String testKey = "1gt6Gg";//production
    //private String testSalt = "luFjAtL9bmZp6I0f05Mg5XiGAH5DhpxT";//debug
    private String testSalt = "pElg8Bkm";//production
    String udf1 = "udf1", udf2 = "udf2", udf3 = "udf3", udf4 = "udf4", udf5 = "udf5";
    String txnid;
    private String firstname;
    private String productInfo;
    private String hashFormala;
    private String userCredentila;
    private String payment_hash;
    private BottomErrorDialog mBottomErrorDialog;
    private BottomSuccessDialog mBottomSheetDialog;
    private Object mResponse;
    private Integer mAmount;
    private BottomOtp fragmentBottomSheet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_history);
        rvLoanHist = (RecyclerView) findViewById(R.id.rvLoanHist);
        linearMain = (LinearLayout) findViewById(R.id.linearMain);
        rvLoanHist.setLayoutManager(new LinearLayoutManager(LoanHistoryActivity.this, LinearLayoutManager.VERTICAL, false));
        setupActionBar(getString(R.string.info_loan_history));
        getHistory();
    }

    private void getHistory() {
        if (MyHelper.isNetworkAvailable(this)) {
            getLoanHistory();
        } else {
            MyHelper.showToast(this, getString(R.string.info_no_internet));
        }
    }

    private void getLoanHistory() {
        mListLoan = new ArrayList<>();
        //String mLoanHistory = "http://103.108.220.161:8091/admin/v1/loanHistories/";
        String mLoanHistory = UrlUtils.LOAN_PORT + UrlUtils.URL_LOAN_HIST;
        JSONObject mRequest = new JSONObject();
        String loginData = Prefs.getString(Constants.LOGINDATA);
        String string = Prefs.getString(Constants.USERID);
        String name = MyHelper.getName(loginData);
        Log.e(TAG, "getLoanHistory: " + mLoanHistory);
        try {
            mRequest.put("loanNumber", "");
            mRequest.put("userId", string);
            //mRequest.put("userId", "test1");
            mRequest.put("pageNumber", 1);
            mRequest.put("pageSize", 10);
            //Log.e(TAG, "getLoanHistory: " + mRequest);
            showLoading();
            AndroidNetworking.post(mLoanHistory)
                    .setPriority(Priority.IMMEDIATE)
                    .setTag("hist")
                    .addJSONObjectBody(mRequest)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Log.e(TAG, "onResponse: "+response );
                            hide();
                            try {
                                JSONArray records = response.getJSONArray("records");
                                if (response.length() > 0) {
                                    for (int i = 0; i < records.length(); i++) {
                                        LoanRecord loanRecord = new LoanRecord();
                                        JSONObject mIndex = records.getJSONObject(i);
                                        loanRecord.setLoanNumber(mIndex.getString("loanNumber"));
                                        loanRecord.setUserId(mIndex.getString("userId"));
                                        loanRecord.setUserName(mIndex.getString("userName"));
                                        loanRecord.setLoanApplicationDate(mIndex.getString("loanApplicationDate"));
                                        loanRecord.setLoanRepaymentDate(mIndex.getString("loanRepaymentDate"));
                                        loanRecord.setLoanAmount(mIndex.getInt("loanAmount"));
                                        loanRecord.setLoanTenure(mIndex.getString("loanTenure"));
                                        loanRecord.setServiceCharges(mIndex.getInt("serviceCharges"));
                                        loanRecord.setRepaymentAmount(mIndex.getInt("repaymentAmount"));
                                        loanRecord.setReminded(mIndex.getBoolean("reminded"));
                                        loanRecord.setLoanStatus(mIndex.getString("loanStatus"));
                                        mListLoan.add(loanRecord);
                                    }
                                    if (mListLoan.size() > 0) {
                                        String userName = mListLoan.get(0).getUserName();
                                        toolbarTitle.setText(getString(R.string.info_loan_history) + " of " + userName);
                                        LoanAdapter mAdapter = new LoanAdapter(mListLoan, LoanHistoryActivity.this);
                                        rvLoanHist.setAdapter(mAdapter);
                                    } else {

                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            hide();
                            //MyHelper.showToast(LoanHistoryActivity.this, anError.getErrorBody());
                            MyHelper.showErrorToast(anError, LoanHistoryActivity.this);

                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void showLoading() {
        mProgress = new ProgressDialog(LoanHistoryActivity.this);
        mProgress.setMessage("Please wait.....");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
    }

    public void hide() {
        mProgress.dismiss();
    }

    private void setupActionBar(String param) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);
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
    public void onItemClick(int pos) {
        startPayment(pos);
    }

    public void startPayment(int mPos) {
        // Preventing multiple clicks, using threshold of 1 second
        Log.e(TAG, "startPayment: called");
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000)
            return;

        mLastClickTime = SystemClock.elapsedRealtime();

        mAmount = mListLoan.get(mPos).getRepaymentAmount();

        //initUiSdk(preparePayUBizParams());
        getOtp();
        //showBottomDialog("12345");


        //getTransactionId();


    }

    private void showBottomDialog(String otp) {
        fragmentBottomSheet = new BottomOtp();
        //Prefs.putString("otp",otp);
        Bundle bundle = new Bundle();
        bundle.putString("otp", otp);
        fragmentBottomSheet.setArguments(bundle);
        fragmentBottomSheet.show(getSupportFragmentManager(), fragmentBottomSheet.getTag());
        fragmentBottomSheet.setCancelable(false);
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
                        //MyHelper.showToast(LoanHistoryActivity.this, anError.getErrorBody());
                        MyHelper.showErrorToast(anError, LoanHistoryActivity.this);
                    }
                });


    }

    /*private void getTransactionId() {
        Log.e(TAG, "getTransactionId: called" );
        AndroidNetworking.get("http://103.108.220.161:8080/payment/v1/gettrxnid")
                .setTag("trxid")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "onResponse: " + response);
                        txnid = response;
                        hashPrepare();

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: "+anError.getMessage() );
                        Log.e(TAG, "onError: "+anError.getErrorDetail() );
                        Log.e(TAG, "onError: "+anError.getResponse() );
                        MyHelper.showToast(LoanHistoryActivity.this, anError.getErrorBody());
                    }
                });
    }*/

    private PayUPaymentParams preparePayUBizParams() {


        HashMap<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(PayUCheckoutProConstants.CP_UDF1, "udf1");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF2, "udf2");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF3, "udf3");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF4, "udf4");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF5, "udf5");


        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount(String.valueOf(mAmount))
                .setIsProduction(false)
                .setProductInfo("Loan Eazy")
                .setKey(testKey)
                .setPhone(MyHelper.getPhoneNumber(Prefs.getString(Constants.LOGINDATA)))
                .setTransactionId(String.valueOf(System.currentTimeMillis()))
                .setFirstName(MyHelper.getName(Prefs.getString(Constants.LOGINDATA)))
                .setEmail(MyHelper.getEmail(Prefs.getString(Constants.LOGINDATA)))
                .setSurl(surl)
                .setFurl(furl)
                .setUserCredential(testKey + ":" + email)
                .setAdditionalParams(additionalParams);
        PayUPaymentParams payUPaymentParams = builder.build();
        return payUPaymentParams;
    }


    private void initUiSdk(PayUPaymentParams payUPaymentParams) {
        PayUCheckoutPro.open(
                this,
                payUPaymentParams,
                new PayUCheckoutProListener() {

                    @Override
                    public void onPaymentSuccess(Object response) {

                        mResponse = response;
                        showAlertDialog(response);
                        //showSuccessDialog("Status : Success", "Your amount has been successfully paid");

                    }

                    @Override
                    public void onPaymentFailure(Object response) {
                        showAlertDialog(response);
                    }

                    @Override
                    public void onPaymentCancel(boolean isTxnInitiated) {
                        showSnackBar(getResources().getString(R.string.transaction_cancelled_by_user));
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        //Log.e(TAG, "onError: " + errorResponse.getErrorMessage());
                        String errorMessage = errorResponse.getErrorMessage();
                        if (TextUtils.isEmpty(errorMessage))
                            errorMessage = getResources().getString(R.string.some_error_occurred);
                        showSnackBar(errorMessage);
                    }

                    @Override
                    public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                        //For setting webview properties, if any. Check Customized Integration section for more details on this
                    }

                    @Override
                    public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                        String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                        String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                        //Log.e(TAG, "generateHash: " + hashName);
                        //Log.e(TAG, "generateHash: " + hashData);
                        //IUIaFM|get_checkout_details|{"requestId":"1628856597399","transactionDetails":{"amount":1},"useCase":{"getAdditionalCharges":true,"getTaxSpecification":true,"checkDownStatus":true,"getExtendedPaymentDetails":true}}|
                        //String hash = HashGenerationUtils.generateHashFromSDK(hashData, testSalt);
                        String hash = HashGenerationUtils.calculateHash(hashData + testSalt);
                        HashMap<String, String> dataMap = new HashMap<>();
                        dataMap.put(hashName, hash);
                        hashGenerationListener.onHashGenerated(dataMap);
                    }

                }
        );
    }

    private void showSuccessDialog(String title, String message) {
        mBottomSheetDialog = new BottomSuccessDialog();
        Bundle mBundle = new Bundle();
        mBundle.putString("title", title);
        mBundle.putString("message", message);
        mBottomSheetDialog.setArguments(mBundle);
        mBottomSheetDialog.setCancelable(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.commit();
        mBottomSheetDialog.show(transaction, mBottomSheetDialog.getTag());

    }


    private String getSHA(String str) {

        MessageDigest md;
        String out = "";
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(str.getBytes());
            byte[] mb = md.digest();

            for (int i = 0; i < mb.length; i++) {
                byte temp = mb[i];
                String s = Integer.toHexString(new Byte(temp));
                while (s.length() < 2) {
                    s = "0" + s;
                }
                s = s.substring(s.length() - 2);
                out += s;
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return out;

    }


    private void showAlertDialog(Object response) {

        HashMap<String, Object> result = (HashMap<String, Object>) response;//Theme_AppCompat_Light_Dialog_Alert
        String s = "Payu's Data : " + result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE) + "\n\n\n Merchant's Data: " + result.get(
                PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
        Log.e(TAG, "showAlertDialog: response" + s);
        //showSuccessDialog("Status : Success", "Your amount has been successfully paid");
        String title = "<font color='#85C53B'>" + "Status : Success" + "</font>";
        String message = "<font color='#FF000000'>" + "Thank you, Your amount has been successfully paid." + "</font>";
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(Html.fromHtml(title))
                .setMessage(
                        Html.fromHtml(message)
                )
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        submitResponse(dialogInterface);

                    }
                }).show();
    }

    private void submitResponse(DialogInterface dialogInterface) {
        dialogInterface.dismiss();
        HashMap<String, Object> result = (HashMap<String, Object>) mResponse;
        String resultResponse = (String) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
        String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
        Log.e(TAG, "onSuccess: " + resultResponse);
        Log.e(TAG, "onSuccess: " + merchantResponse);

        try {
            JSONObject mJSON1 = new JSONObject(resultResponse);
            JSONObject mJSON2 = new JSONObject(merchantResponse);
            String loginData = Prefs.getString(Constants.LOGINDATA);
            String userId = MyHelper.getUserId(loginData);
            JSONObject mJSON = new JSONObject();
            mJSON.put(Constants.USERID, userId);
            int i = (int) Float.parseFloat(mJSON2.getString(Constants.AMOUNT));
            Log.e(TAG, "submitResponse: " + i);
            mJSON.put(Constants.LOAN_AMOUNT, i);
            mJSON.put(Constants.PAYU_RESPONSE_CODE, "200");
            mJSON.put(Constants.ID, mJSON1.getString(Constants.ID));
            mJSON.put(Constants.MODE, mJSON2.getString(Constants.MODE));
            mJSON.put(Constants.STATUS, mJSON2.getString(Constants.STATUS));
            mJSON.put(Constants.UNMAPPEDSTATUS, mJSON2.getString(Constants.UNMAPPEDSTATUS));
            mJSON.put(Constants.KEY, mJSON2.getString(Constants.KEY));
            mJSON.put(Constants.TXNID, mJSON2.getString(Constants.TXNID));
            mJSON.put(Constants.TRANSACTION_FEE, mJSON1.getString(Constants.TRANSACTION_FEE));
            mJSON.put(Constants.AMOUNT, mJSON1.getString(Constants.AMOUNT));
            mJSON.put(Constants.CARDCATEGORY, mJSON1.getString(Constants.CARDCATEGORY));
            mJSON.put(Constants.DISCOUNT, mJSON1.getString(Constants.DISCOUNT));
            mJSON.put(Constants.ADDEDON, mJSON1.getString(Constants.ADDEDON));
            mJSON.put(Constants.PRODUCTINFO, mJSON1.getString(Constants.PRODUCTINFO));
            mJSON.put(Constants.FIRSTNAME, mJSON1.getString(Constants.FIRSTNAME));
            mJSON.put(Constants.EMAIL, mJSON1.getString(Constants.EMAIL));
            mJSON.put(Constants.PHONE, mJSON1.getString(Constants.PHONE));
            mJSON.put(Constants.UDF1, mJSON1.getString(Constants.UDF1));
            mJSON.put(Constants.UDF2, mJSON1.getString(Constants.UDF2));
            mJSON.put(Constants.UDF3, mJSON1.getString(Constants.UDF3));
            mJSON.put(Constants.UDF4, mJSON1.getString(Constants.UDF4));
            mJSON.put(Constants.UDF5, mJSON1.getString(Constants.UDF5));
            mJSON.put(Constants.HASH, mJSON1.getString(Constants.HASH));
            mJSON.put(Constants.FIELD1, mJSON1.getString(Constants.FIELD1));
            mJSON.put(Constants.FIELD2, mJSON1.getString(Constants.FIELD2));
            mJSON.put(Constants.FIELD3, mJSON1.getString(Constants.FIELD3));
            mJSON.put(Constants.FIELD4, mJSON1.getString(Constants.FIELD4));
            mJSON.put(Constants.FIELD5, mJSON1.getString(Constants.FIELD5));
            mJSON.put(Constants.FIELD6, mJSON1.getString(Constants.FIELD6));
            mJSON.put(Constants.FIELD7, mJSON1.getString(Constants.FIELD7));
            mJSON.put(Constants.FIELD8, mJSON1.getString(Constants.FIELD8));
            mJSON.put(Constants.FIELD9, mJSON1.getString(Constants.FIELD9));
            mJSON.put(Constants.PAYMENT_SOURCE, mJSON1.getString(Constants.PAYMENT_SOURCE));
            mJSON.put(Constants.PG_TYPE, mJSON1.getString(Constants.PG_TYPE));
            mJSON.put(Constants.BANK_REF_NUM, mJSON2.getString(Constants.BANK_REF_NUM));
            mJSON.put(Constants.BANKCODE, mJSON2.getString(Constants.BANKCODE));
            mJSON.put(Constants.ERROR, mJSON2.getString(Constants.ERROR));
            mJSON.put(Constants.ERROR_MESSAGE, mJSON2.getString(Constants.ERROR_MESSAGE));
            mJSON.put(Constants.NAME_ON_CARD, mJSON2.getString(Constants.NAME_ON_CARD));
            mJSON.put(Constants.CARDNUM, mJSON2.getString(Constants.CARDNUM));
            mJSON.put(Constants.IS_SEAMLESS, mJSON1.getString(Constants.IS_SEAMLESS));
            mJSON.put(Constants.SURL, mJSON1.getString(Constants.SURL));
            mJSON.put(Constants.FURL, mJSON1.getString(Constants.FURL));


            //'http://103.108.220.161:8082/payment/v1/payureturn'
            Log.e(TAG, "submitResponse: " + mJSON);
            showLoading();
            //"http://103.108.220.161:8082/payment/v1/payureturn"
            String URL_PAYURETURN = UrlUtils.PAY_RETURN_PORT + UrlUtils.URL_PAYURETURN;
            AndroidNetworking.post(URL_PAYURETURN)
                    .setPriority(Priority.IMMEDIATE)
                    .addJSONObjectBody(mJSON)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hide();
                            Log.e(TAG, "onResponse: " + response);
                            MyHelper.showToast(LoanHistoryActivity.this, "Successfully Updated");
                            onBackPressed();
                        }

                        @Override
                        public void onError(ANError anError) {
                            hide();
                            //Log.e(TAG, "onError: " + anError.getErrorBody());
                            //MyHelper.showToast(LoanHistoryActivity.this, anError.getMessage());
                            MyHelper.showErrorToast(anError, LoanHistoryActivity.this);

                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showSnackBar(String message) {
        Snackbar.make(linearMain, message, Snackbar.LENGTH_LONG).show();
    }

    private static String calculateHash(String hashString) {
        try {
            StringBuilder hash = new StringBuilder();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            for (byte hashByte : mdbytes) {
                hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }

            return hash.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
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

    @Override
    public void onSuccess() {
        HashMap<String, Object> result = (HashMap<String, Object>) mResponse;
        String resultResponse = (String) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
        String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
        //Log.e(TAG, "onSuccess: " + resultResponse);
        //Log.e(TAG, "onSuccess: " + merchantResponse);


    }

    @Override
    public void onOtpConfirm() {
        //when otp is confirm need to call below sdk for payment
        if (fragmentBottomSheet != null) {
            fragmentBottomSheet.dismiss();
        }
        initUiSdk(preparePayUBizParams());
    }
}