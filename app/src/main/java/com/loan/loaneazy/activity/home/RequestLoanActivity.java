package com.loan.loaneazy.activity.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.loan.loaneazy.R;
import com.loan.loaneazy.my_interface.ChangeFragmentListener;
import com.loan.loaneazy.my_interface.CommonListener;
import com.loan.loaneazy.my_interface.OtpConfirmListener;
import com.loan.loaneazy.utility.Constants;

public class RequestLoanActivity extends AppCompatActivity implements CommonListener, ChangeFragmentListener , OtpConfirmListener {


    private String mAmount, mTenure,mLastTenure;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_loan);
        mAmount = getIntent().getStringExtra(Constants.AMOUNT);
        mTenure = getIntent().getStringExtra(Constants.TENURE);
        mLastTenure=getIntent().getStringExtra(Constants.LASTTEANURE);
        mBundle = new Bundle();
        mBundle.putString(Constants.AMOUNT, mAmount);
        mBundle.putString(Constants.TENURE, mTenure);
        mBundle.putString(Constants.LASTTEANURE,mLastTenure);

        if (savedInstanceState == null) {
            LoanFirstFragment mLoanFirstFragment = new LoanFirstFragment();
            mLoanFirstFragment.setArguments(mBundle);
            initFragments(mLoanFirstFragment, mLoanFirstFragment.getTag());
        }

    }

    private void initFragments(Fragment mFragment, String tag) {
        FragmentManager mManager = getSupportFragmentManager();
        FragmentTransaction mTransaction = mManager.beginTransaction();
        mTransaction.replace(R.id.layoutMainLoan, mFragment, tag);
        mTransaction.addToBackStack(tag);
        mTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        verifyStack();
    }

    private void verifyStack() {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.layoutMainLoan);

            if (currentFragment instanceof LoanSecFragment) {
                //Log.d("message", "home fragment");
                getSupportFragmentManager().popBackStack();
                //toolbarTitle.setText(getString(R.string.info_nueva_cuenta));
            } else {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSuccess() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.layoutMainLoan);
        if (currentFragment instanceof LoanSecFragment) {
            ((LoanSecFragment)currentFragment).hideBottom();
        }
    }

    @Override
    public void onChangeRequest() {
        LoanSecFragment mLoanSecFragment = new LoanSecFragment();
        mLoanSecFragment.setArguments(mBundle);
        initFragments(mLoanSecFragment, mLoanSecFragment.getTag());
    }

    @Override
    public void onOtpConfirm() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.layoutMainLoan);
        if (currentFragment instanceof LoanSecFragment) {
            ((LoanSecFragment)currentFragment).hideBottomMakeRequest();
        }
    }
}