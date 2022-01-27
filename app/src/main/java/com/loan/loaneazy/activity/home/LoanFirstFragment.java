package com.loan.loaneazy.activity.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.loan.loaneazy.R;
import com.loan.loaneazy.model.request.RequestLoan;
import com.loan.loaneazy.my_interface.ChangeFragmentListener;
import com.loan.loaneazy.utility.Constants;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.utility.UrlUtils;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.views.MyRadioButton;
import com.loan.loaneazy.views.MyTextView;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class LoanFirstFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LoanFirstFragment";
    private View mView;
    private MyTextView tvReqAmt, tvReqDate, tvLoanAmt, tvDisAmt, tvProcessFee, tvGst, tvTotalInts, tvTotReAmt;
    private MyButton btnConfirm;
    private ChangeFragmentListener mCommonListener;
    private MyTextView tvHeader2;
    private ProgressDialog mProgress;
    private String mAmount, mTenure;
    private RequestLoan mRequestLoan;
    private MyTextView tvTenure;
    private MyRadioButton rbConfirm;
    private ArrayList<RequestLoan.RepaymentDetails> mListPayments;
    private StringBuilder mDate;
    private int mTotalAmount = 0;
    private String mLastTenure;
    private StringBuilder mRepaymentAmount;
    private StringBuilder mTotalTenaure;
    private MyTextView scrolltext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = this.getArguments();
        if (arguments != null) {
            mAmount = arguments.getString(Constants.AMOUNT);
            mTenure = arguments.getString(Constants.TENURE);
            mLastTenure = arguments.getString(Constants.LASTTEANURE);
        }

    }

    public LoanFirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_loan_first, container, false);
            scrolltext = (MyTextView) mView.findViewById(R.id.scrolltext);
            rbConfirm = (MyRadioButton) mView.findViewById(R.id.rbConfirm);
            tvReqAmt = (MyTextView) mView.findViewById(R.id.tvReqAmt);
            tvReqDate = (MyTextView) mView.findViewById(R.id.tvReqDate);
            tvLoanAmt = (MyTextView) mView.findViewById(R.id.tvLoanAmt);
            tvTenure = (MyTextView) mView.findViewById(R.id.tvTenure);

            tvDisAmt = (MyTextView) mView.findViewById(R.id.tvDisAmt);
            tvProcessFee = (MyTextView) mView.findViewById(R.id.tvProcessFee);
            tvGst = (MyTextView) mView.findViewById(R.id.tvGst);
            tvTotalInts = (MyTextView) mView.findViewById(R.id.tvTotalInts);
            tvTotReAmt = (MyTextView) mView.findViewById(R.id.tvTotReAmt);
            tvHeader2 = (MyTextView) mView.findViewById(R.id.tvHeader2);
            btnConfirm = (MyButton) mView.findViewById(R.id.btnConfirm);
            tvHeader2.setTypeface(tvHeader2.getTypeface(), Typeface.BOLD);
            tvHeader2.setTextColor(Color.parseColor("#FFFF00"));
            scrolltext.setSelected(true);
            scrolltext.setTextColor(Color.parseColor("#FF0000"));

            /*tvLoanAmt.setText("\u20B9" + " 1,150");
            tvDisAmt.setText("\u20B9" + " 1,000");
            tvProcessFee.setText("\u20B9" + " 115");
            tvGst.setText("\u20B9" + " 15");
            tvTotalInts.setText("\u20B9" + " 20");
            tvTotReAmt.setText("\u20B9" + " 1,150");

            tvReqAmt.setText("\u20B9" + " 1,150");
            tvReqDate.setText(MyHelper.getTodayDate());*/
            rbConfirm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        btnConfirm.setEnabled(true);
                    } else {
                        btnConfirm.setEnabled(false);
                    }
                }
            });

            getTexDetails();

            btnConfirm.setOnClickListener(this);
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

    public void showLoading() {
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Please wait.....");
        mProgress.show();
    }

    public void hide() {
        mProgress.dismiss();
    }

    private void getTexDetails() {
        String string = Prefs.getString(Constants.USERID);
        JSONObject mJSON = new JSONObject();
        try {
            int mFinalAmount = (int) Float.parseFloat(mAmount);
            mJSON.put(Constants.USERID, string);
            mJSON.put("loanAmount", String.valueOf(mFinalAmount));
            mJSON.put("teanure", mTenure);
            mJSON.put("sixtyoneteanure", mLastTenure);
            //Log.e(TAG, "getTexDetails: " + mJSON);
            showLoading();
            //"http://103.108.220.161:8080/kesh/v1/repaymentcalculation"
            String URL_REPAYMENT_CAL = UrlUtils.LOGIN_PORT + UrlUtils.URL_REPAYMENT_CALCULATION;
            AndroidNetworking.post(URL_REPAYMENT_CAL)
                    .setTag("details")
                    .setPriority(Priority.IMMEDIATE)
                    .addJSONObjectBody(mJSON)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Log.e(TAG, "onResponse: " + response);
                            hide();
                            try {
                                String mScrollMessage = response.getString(Constants.MESSAGE);
                                scrolltext.setText(mScrollMessage);
                                mRequestLoan = new RequestLoan(
                                        response.getString("disbursement_amount"),
                                        response.getString("gst_interest"),
                                        response.getString("loan_amount"),
                                        response.getString("processing_fees"),
                                        response.getString("total_interest")

                                );
                                mListPayments = new ArrayList<RequestLoan.RepaymentDetails>();
                                JSONArray repaymentDetails = response.getJSONArray("repaymentDetails");
                                for (int i = 0; i < repaymentDetails.length(); i++) {
                                    JSONObject mIndex = repaymentDetails.getJSONObject(i);
                                    RequestLoan.RepaymentDetails mPayDetails = new RequestLoan.RepaymentDetails(
                                            mIndex.getInt(Constants.AMOUNT),
                                            mIndex.getString(Constants.REPAYMENT_DATE),
                                            mIndex.getString(Constants.TEANURE)
                                    );
                                    mListPayments.add(mPayDetails);

                                }

                                updateValue();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onError(ANError anError) {
                            hide();
                            //MyHelper.showToast(getActivity(), anError.getErrorBody());
                            MyHelper.showErrorToast(anError, getActivity());

                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void updateValue() {


        tvLoanAmt.setText("\u20B9" + " " + mRequestLoan.getLoan_amount());
        tvProcessFee.setText("\u20B9" + " " + mRequestLoan.getProcessing_fees());
        String mTeanure = "";
        mRepaymentAmount = new StringBuilder();

        mDate = new StringBuilder();
        for (int i = 0; i < mListPayments.size(); i++) {
            if (!mListPayments.get(i).getTeanure().equalsIgnoreCase(Constants.ALL)) {
                mTeanure = mTeanure + mListPayments.get(i).getTeanure() + " Days" + " and ";
                mDate.append(mListPayments.get(i).getRepayment_date()).append("(").append(mListPayments.get(i).getTeanure()).append(" days)").append("\n");
                mRepaymentAmount.append("\u20B9").append(" ").append(mListPayments.get(i).getAmount()).append("\n");
                //mTotalTenaure.append().append(" ");
            } else {
                mTotalAmount = mListPayments.get(i).getAmount();
            }
        }


        tvTenure.setText(mTeanure.trim().substring(0, mTeanure.trim().length() - 3));
        tvReqDate.setText(mDate.toString().trim());
        tvDisAmt.setText("\u20B9" + " " + mRequestLoan.getDisbursement_amount());
        String total_interest = mRequestLoan.getTotal_interest();
        String round2Places = MyHelper.getRound2Places(total_interest);
        tvGst.setText("\u20B9" + " " + mRequestLoan.getGst_interest());
        tvTotalInts.setText("\u20B9" + " " + round2Places);
        //tvReqAmt.setText("\u20B9" + " " + mRequestLoan.getTotal_repayment_amount());
        //tvTotReAmt.setText("\u20B9" + " " + mRequestLoan.getTotal_repayment_amount());
        //tvReqAmt.setText("\u20B9" + " " + mTotalAmount);
        tvReqAmt.setText(mRepaymentAmount.toString());
        tvTotReAmt.setText("\u20B9" + " " + mTotalAmount);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnConfirm) {
            if (rbConfirm.isChecked()) {
                Prefs.putString("repay", String.valueOf(mTotalAmount));
                Prefs.putString("redate", mDate.toString().trim());
                Prefs.putString("repay_part", mRepaymentAmount.toString());
                mCommonListener.onChangeRequest();
            } else {
                MyHelper.showToast(getActivity(), "please click on agree");
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCommonListener = (ChangeFragmentListener) context;
    }
}