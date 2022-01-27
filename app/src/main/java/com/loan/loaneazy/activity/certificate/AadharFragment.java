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
import com.loan.loaneazy.my_interface.AadhaarNextListener;
import com.loan.loaneazy.my_interface.CameraCaptureListener;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.views.AadhaarNumberEditText;
import com.loan.loaneazy.views.MyButton;


public class AadharFragment extends Fragment implements View.OnClickListener {
    AadhaarNextListener mAadhaarNextListener;
    CameraCaptureListener mCameraCaptureListener;

    private MyButton btnAadhaarNext;
    private LinearLayout linearFront;
    private ImageView ivAadharFront;
    View mView;
    private String mFilePath;
    private AadhaarNumberEditText edAadharNo;

    public AadharFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_aadhar, container, false);
            btnAadhaarNext = (MyButton) mView.findViewById(R.id.btnAadhaarNext);
            edAadharNo = (AadhaarNumberEditText) mView.findViewById(R.id.edAadharNo);
            linearFront = (LinearLayout) mView.findViewById(R.id.linearFront);
            ivAadharFront = (ImageView) mView.findViewById(R.id.ivAadharFront);
            btnAadhaarNext.setOnClickListener(this);
            linearFront.setOnClickListener(this);
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mAadhaarNextListener = (AadhaarNextListener) context;
        mCameraCaptureListener = (CameraCaptureListener) context;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAadhaarNext) {
            if (TextUtils.isEmpty(mFilePath)) {
                MyHelper.showToast(getActivity(), "please capture aadhar font image");
            } else {
                if (edAadharNo.getCardNumber().isEmpty()) {
                    edAadharNo.setError("enter aadhar number");
                    edAadharNo.requestFocus();
                    //MyHelper.showToast(getActivity(), "enter aadhar number");
                    return;
                }
                if (edAadharNo.getCardNumber().length() < 12) {
                    //MyHelper.showToast(getActivity(), "enter valid aadhar number");
                    edAadharNo.setError("enter valid aadhar number");
                    edAadharNo.requestFocus();
                    return;
                }

                mAadhaarNextListener.onAadhaarNextClick(1, mFilePath);
            }

        } else if (view.getId() == R.id.linearFront) {
            if (edAadharNo.getCardNumber().isEmpty()) {
                edAadharNo.setError("enter aadhar number");
                edAadharNo.requestFocus();
                //MyHelper.showToast(getActivity(), "enter aadhar number");
                return;
            }
            if (edAadharNo.getCardNumber().length() < 12) {
                //MyHelper.showToast(getActivity(), "enter valid aadhar number");
                edAadharNo.setError("enter valid aadhar number");
                edAadharNo.requestFocus();
                return;
            }
            mCameraCaptureListener.onCaptureRequest(0);
        }
    }

    public void updateImageFilePath(String absolutePath) {
        mFilePath = absolutePath;
        ivAadharFront.setPadding(0, 0, 0, 0);
        Glide.with(getActivity()).load(absolutePath).centerCrop().into(ivAadharFront);

    }

    public String getAadharNumber(){
        return edAadharNo.getCardNumber();
    }
}