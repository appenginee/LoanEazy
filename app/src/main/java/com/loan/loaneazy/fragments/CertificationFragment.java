package com.loan.loaneazy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.loan.loaneazy.R;
import com.loan.loaneazy.activity.certificate.BasicInfoActivity;
import com.loan.loaneazy.activity.certificate.KycActivity;
import com.loan.loaneazy.activity.certificate.VerificationStatusActivity;

public class CertificationFragment extends Fragment implements View.OnClickListener {


    private RelativeLayout mRelKyc;
    private RelativeLayout relativeStatus;
    private RelativeLayout relativeBasic;

    public CertificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_certification, container, false);
        mRelKyc = (RelativeLayout) mView.findViewById(R.id.mRelKyc);
        mRelKyc.setOnClickListener(this);
        relativeStatus = (RelativeLayout) mView.findViewById(R.id.relativeStatus);
        relativeBasic = (RelativeLayout) mView.findViewById(R.id.relativeBasic);
        relativeBasic.setOnClickListener(this);
        relativeStatus.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onClick(View view) {
        /*if (view.getId() == R.id.contactInfoRel) {
            Intent intentAdd = new Intent(getActivity(), AddContacntActivity.class);
            startActivity(intentAdd);
        } else*/
        if (view.getId() == R.id.mRelKyc) {
            Intent intentKyc = new Intent(getActivity(), KycActivity.class);
            startActivity(intentKyc);
        } else if (view.getId() == R.id.relativeStatus) {
            Intent intentStatus = new Intent(getActivity(), VerificationStatusActivity.class);
            startActivity(intentStatus);
        } else if (view.getId() == R.id.relativeBasic) {
            Intent intentBasic = new Intent(getActivity(), BasicInfoActivity.class);
            startActivity(intentBasic);
        }
    }
}