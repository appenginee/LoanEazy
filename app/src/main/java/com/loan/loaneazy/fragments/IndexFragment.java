package com.loan.loaneazy.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.loan.loaneazy.R;
import com.loan.loaneazy.activity.home.RequestLoanActivity;
import com.loan.loaneazy.adapter.RangeAdapter;
import com.loan.loaneazy.bottom_fragment.BottomErrorDialog;
import com.loan.loaneazy.model.general.AmountRange;
import com.loan.loaneazy.model.general.LoanCheck;
import com.loan.loaneazy.utility.Constants;
import com.loan.loaneazy.utility.UrlUtils;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.views.MyEditText;
import com.loan.loaneazy.views.MyTextView;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


public class IndexFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "IndexFragment";
    private MyButton btnApply;
    private MyTextView tvHeader1, tvTenureTitle, tvNote, tvMinAmt, tvMaxAmt;
    private MyEditText edLoanAmt;
    private LinearLayout linearTenure, linearMinMax;
    private ProgressDialog mProgress;
    private ArrayList<LoanCheck> mListCheck;
    //private Slider mSlider;
    private MyTextView tvCenterAmount;
    private float mValue;
    private ArrayList<AmountRange> mAmountList;
    private RecyclerView mAmountSlider;
    private RangeAdapter mRangeAdapter;
    private BottomErrorDialog mBottomErrorDialog;

    public IndexFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_index, container, false);
        edLoanAmt = (MyEditText) mView.findViewById(R.id.edLoanAmt);
        btnApply = (MyButton) mView.findViewById(R.id.btnApply);
        tvHeader1 = (MyTextView) mView.findViewById(R.id.tvHeader1);
        tvNote = (MyTextView) mView.findViewById(R.id.tvNote);
        tvTenureTitle = (MyTextView) mView.findViewById(R.id.tvTenureTitle);
        linearTenure = (LinearLayout) mView.findViewById(R.id.linearTenure);
        linearMinMax = (LinearLayout) mView.findViewById(R.id.linearMinMax);
        tvMinAmt = (MyTextView) mView.findViewById(R.id.tvMinAmt);
        tvMaxAmt = (MyTextView) mView.findViewById(R.id.tvMaxAmt);
        tvCenterAmount = (MyTextView) mView.findViewById(R.id.tvCenterAmount);
        tvHeader1.setTypeface(tvHeader1.getTypeface(), Typeface.BOLD);
        String string = Prefs.getString(Constants.LOGINDATA);
        //Log.e(TAG, "onCreateView: " + string);

        tvHeader1.setTextColor(Color.parseColor("#FFFF00"));
        //mSlider = (Slider) mView.findViewById(R.id.mSlider);
        mAmountSlider = (RecyclerView) mView.findViewById(R.id.mAmountSlider);
        mAmountSlider.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        btnApply.setOnClickListener(this);
        mListCheck = new ArrayList<LoanCheck>();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getCheckStatus();
            }
        }, 500);


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

    private void getCheckStatus() {
        showLoading();
        String mUserId = Prefs.getString(Constants.USERID);
        //"http://103.108.220.161:8080/kesh/v1/userloancheck/" + mUserId
        String mLOANCHECKED = UrlUtils.LOGIN_PORT + UrlUtils.URL_USER_CHECKED + mUserId;
        AndroidNetworking.get(mLOANCHECKED)
                .setPriority(Priority.IMMEDIATE)
                .doNotCacheResponse()
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hide();
                        //Log.e(TAG, "onResponse: " + response);
                        try {
                            LoanCheck mLoanCheck = new LoanCheck(response.getBoolean(Constants.ISALLOWEDTOPRCEED),
                                    response.getString(Constants.MESSAGE),
                                    response.getString(Constants.TEANURE),
                                    response.getString(Constants.USERID),
                                    response.getString(Constants.LASTTEANURE)
                            );
                            mListCheck.add(mLoanCheck);
                            mAmountList = new ArrayList<AmountRange>();

                            JSONObject jsonRangeOBJ = response.getJSONObject(Constants.LOAN_AMOUNT_RANGE);
                            Iterator<String> mIter = jsonRangeOBJ.keys();
                            while (mIter.hasNext()) {
                                AmountRange mAmountRange = new AmountRange();
                                String next = mIter.next();
                                boolean status = (boolean) jsonRangeOBJ.get(next);
                                mAmountRange.setAmount(next);
                                mAmountRange.setStatus(status);
                                mAmountList.add(mAmountRange);
                            }
                            updateApply();


                            /*JSONArray jsonArray = response.getJSONArray(Constants.LOAN_AMOUNT_RANGE);
                            mAmountList = new ArrayList<String>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                *//*JSONObject index = jsonArray.getJSONObject(i);
                                String mAmount = index.getString(String.valueOf(i));*//*

                                String mAmount = "" + jsonArray.get(i);
                                mAmountList.add(mAmount);
                            }

                            updateApply();*/


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }

    private void updateApply() {
        if (mListCheck.get(0).isAllowedToProceed()) {
            //Collections.sort(mAmountList);
            Collections.sort(mAmountList, new Comparator<AmountRange>() {
                @Override
                public int compare(AmountRange lhs, AmountRange rhs) {
                    return lhs.getAmount().compareTo(rhs.getAmount());
                }
            });
            linearTenure.setVisibility(View.VISIBLE);
            //tvTenureTitle.setText(mListCheck.get(0).getTeanure() + " days "+" & " + mListCheck.get(0).getLastTeanure() + " days");
            tvTenureTitle.setText(mListCheck.get(0).getLastTeanure() + " days");

            //String note = "<font color='#85C53B'>" + "Note : " + "</font>" + " " + mListCheck.get(0).getMessage() + ".";
            String note = "<font color='#58F815'>" + "Note : " + "</font>" + " " + mListCheck.get(0).getMessage() + ".";
            tvNote.setText(Html.fromHtml(note));
            tvMinAmt.setText("Min " + "\u20B9" + " " + mAmountList.get(0).getAmount());
            tvMaxAmt.setText("Max " + "\u20B9" + " " + mAmountList.get(mAmountList.size() - 1).getAmount());
            linearMinMax.setVisibility(View.VISIBLE);
            mRangeAdapter = new RangeAdapter(getActivity(), mAmountList, 1);
            mAmountSlider.setAdapter(mRangeAdapter);
            mAmountSlider.setVisibility(View.VISIBLE);
            btnApply.setEnabled(false);
        } else {
            mAmountSlider.setVisibility(View.GONE);
            linearTenure.setVisibility(View.GONE);
            btnApply.setEnabled(false);
            String note = "<font color='#85C53B'>" + "Note : " + "</font>" + " " + mListCheck.get(0).getMessage();
            tvNote.setText(Html.fromHtml(note));
            linearMinMax.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnApply) {

            String loanAmt = String.valueOf(mValue);
            float mInputAmt = (int) Float.parseFloat(loanAmt);
            float mMinAmount = Float.parseFloat(mAmountList.get(0).getAmount());
            float mMaxAmount = Float.parseFloat(mAmountList.get(mAmountList.size() - 1).getAmount());
            if (mInputAmt >= mMinAmount && mInputAmt <= mMaxAmount) {
                Intent intentLoanReq = new Intent(getActivity(), RequestLoanActivity.class);
                intentLoanReq.putExtra(Constants.AMOUNT, loanAmt);
                intentLoanReq.putExtra(Constants.LASTTEANURE, mListCheck.get(0).getLastTeanure());
                intentLoanReq.putExtra(Constants.TENURE, mListCheck.get(0).getTeanure());
                startActivity(intentLoanReq);
            } else {
                String error = "Amount should be between " + "\u20B9" + mAmountList.get(0) + " to " + "\u20B9" + mAmountList.get(mAmountList.size() - 1);
                showErrorDialog("Status : Error", error);
            }


        }
    }

    private void showErrorDialog(String title, String message) {
        mBottomErrorDialog = new BottomErrorDialog();
        Bundle mBundle = new Bundle();
        mBundle.putString("title", title);
        mBundle.putString("message", message);
        mBottomErrorDialog.setArguments(mBundle);
        mBottomErrorDialog.show(getChildFragmentManager(), mBottomErrorDialog.getTag());
        mBottomErrorDialog.setCancelable(false);
    }

    public void updateData(int pos) {
        mRangeAdapter.selected(pos);
        if (mAmountList.get(pos).isStatus()) {
            mValue = Float.parseFloat(mAmountList.get(pos).getAmount());
            btnApply.setEnabled(true);
        } else {
            btnApply.setEnabled(false);
        }
        //mAmountSlider.setVisibility(View.VISIBLE);

    }

    public void hideBottomDialog() {
        if (mBottomErrorDialog != null) {
            mBottomErrorDialog.dismiss();
        }
    }
}