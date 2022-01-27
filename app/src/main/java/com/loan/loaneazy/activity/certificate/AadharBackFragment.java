package com.loan.loaneazy.activity.certificate;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.loan.loaneazy.R;
import com.loan.loaneazy.my_interface.AadharPrevNextListener;
import com.loan.loaneazy.my_interface.CameraCaptureListener;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.utility.MyHelper;


public class AadharBackFragment extends Fragment implements View.OnClickListener {


    private MyButton btnAadhaarNext, btnAadhaarPrev;
    AadharPrevNextListener mListener;
    View mView;
    private ImageView ivAadharBack;
    private LinearLayout linearBack;
    CameraCaptureListener mCameraCaptureListener;
    private String mfilePath;

    public AadharBackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_aadhar_back, container, false);
            btnAadhaarNext = (MyButton) mView.findViewById(R.id.btnAadhaarNext);
            btnAadhaarPrev = (MyButton) mView.findViewById(R.id.btnAadhaarPrev);
            ivAadharBack = (ImageView) mView.findViewById(R.id.ivAadharBack);
            linearBack = (LinearLayout) mView.findViewById(R.id.linearBack);
            btnAadhaarNext.setOnClickListener(this);
            btnAadhaarPrev.setOnClickListener(this);
            linearBack.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAadhaarNext) {
            if (TextUtils.isEmpty(mfilePath)){
                MyHelper.showToast(getActivity(),"please capture aadhar back image");
                return;
            }
            mListener.onAadharBackNextClick();
        } else if (view.getId() == R.id.btnAadhaarPrev) {
            mListener.onAadharBackPrevClick();
        } else if (view.getId() == R.id.linearBack) {

                mCameraCaptureListener.onCaptureRequest(1);

        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (AadharPrevNextListener) context;
        mCameraCaptureListener = (CameraCaptureListener) context;
    }

    public void updateImageFilePath(String absolutePath) {
        mfilePath=absolutePath;
        ivAadharBack.setPadding(0, 0, 0, 0);
        Glide.with(getActivity()).load(absolutePath).centerCrop().into(ivAadharBack);
    }
}