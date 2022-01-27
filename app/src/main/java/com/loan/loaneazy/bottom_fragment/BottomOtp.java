package com.loan.loaneazy.bottom_fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.loan.loaneazy.R;
import com.loan.loaneazy.my_interface.OtpConfirmListener;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.views.MyTextView;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BottomOtp extends BottomSheetDialogFragment implements View.OnClickListener {
    private MyButton btnKey1, btnKey2, btnKey3, btnKey4, btnKey5, btnKey6,
            btnKey7, btnKey8, btnKey9, btnKey10, btnClear, btnConfirm;
    ArrayList<MyButton> mListBtn;

    private String codeString = "";
    List<MyTextView> dots;
    private BottomSheetBehavior mBehaviour;
    private MyButton btnCloseDialog;
    private OtpConfirmListener mPinListenr;
    MyTextView tvOtp1, tvOtp2, tvOtp3, tvOtp4,tvOtp5;
    private String mOtp;
    private static final String TAG = "BottomOtp";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: first " );
        Bundle arguments = this.getArguments();
        mOtp = arguments.getString("otp");
        Log.e(TAG, "onCreate: "+mOtp );
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateDialog: second " );
        BottomSheetDialog fBtmShtDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View mView = View.inflate(getActivity(), R.layout.otp_bottom_fragment, null);
        fBtmShtDialog.setContentView(mView);
        mBehaviour = BottomSheetBehavior.from((View) mView.getParent());
        //Log.e(TAG, "onCreate: first " );
        //Bundle arguments = this.getArguments();
        //mOtp = arguments.getString("opt");
        //Log.e(TAG, "onCreate: "+mOtp );
        /*mOtp = Prefs.getString("opt");
        Log.e(TAG, "onCreate: "+mOtp );*/
        mListBtn = new ArrayList<>();
        dots = new ArrayList<>();
        findIds(mView);
        //getRandomKeyFromRange();
        return fBtmShtDialog;
    }

    private void findIds(View mView) {
        tvOtp1 = (MyTextView) mView.findViewById(R.id.tvOtp1);
        tvOtp2 = (MyTextView) mView.findViewById(R.id.tvOtp2);
        tvOtp3 = (MyTextView) mView.findViewById(R.id.tvOtp3);
        tvOtp4 = (MyTextView) mView.findViewById(R.id.tvOtp4);
        tvOtp5 = (MyTextView) mView.findViewById(R.id.tvOtp5);

        btnKey1 = (MyButton) mView.findViewById(R.id.btnKey1);
        btnKey2 = (MyButton) mView.findViewById(R.id.btnKey2);
        btnKey3 = (MyButton) mView.findViewById(R.id.btnKey3);
        btnKey4 = (MyButton) mView.findViewById(R.id.btnKey4);
        btnKey5 = (MyButton) mView.findViewById(R.id.btnKey5);
        btnKey6 = (MyButton) mView.findViewById(R.id.btnKey6);
        btnKey7 = (MyButton) mView.findViewById(R.id.btnKey7);
        btnKey8 = (MyButton) mView.findViewById(R.id.btnKey8);
        btnKey9 = (MyButton) mView.findViewById(R.id.btnKey9);
        btnKey10 = (MyButton) mView.findViewById(R.id.btnKey10);
        btnClear = (MyButton) mView.findViewById(R.id.btnClear);
        btnConfirm = (MyButton) mView.findViewById(R.id.btnConfirm);
        btnCloseDialog = (MyButton) mView.findViewById(R.id.btnCloseDialog);

        btnKey1.setOnClickListener(this);
        btnKey2.setOnClickListener(this);
        btnKey3.setOnClickListener(this);
        btnKey4.setOnClickListener(this);
        btnKey5.setOnClickListener(this);
        btnKey6.setOnClickListener(this);
        btnKey7.setOnClickListener(this);
        btnKey8.setOnClickListener(this);
        btnKey9.setOnClickListener(this);
        btnKey10.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnCloseDialog.setOnClickListener(this);

        mListBtn.add(btnKey1);
        mListBtn.add(btnKey2);
        mListBtn.add(btnKey3);
        mListBtn.add(btnKey4);
        mListBtn.add(btnKey5);
        mListBtn.add(btnKey6);
        mListBtn.add(btnKey7);
        mListBtn.add(btnKey8);
        mListBtn.add(btnKey9);
        mListBtn.add(btnKey10);

        dots.add(tvOtp1);
        dots.add(tvOtp2);
        dots.add(tvOtp3);
        dots.add(tvOtp4);
        dots.add(tvOtp5);


    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
        View touchOutsideView = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow())
                .getDecorView()
                .findViewById(com.google.android.material.R.id.touch_outside);
        touchOutsideView.setClickable(false);
        touchOutsideView.setFocusable(false);

        mBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
        mBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        mBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnKey1:
                if (codeString.length() == 5)
                    return;
                codeString += btnKey1.getText().toString().trim();
                //tvInputPin.setText(codeString);
                setDotEnable();
                break;
            case R.id.btnKey2:
                if (codeString.length() == 5)
                    return;
                codeString += btnKey2.getText().toString().trim();
                //tvInputPin.setText(codeString);
                setDotEnable();


                break;
            case R.id.btnKey3:
                if (codeString.length() == 5)
                    return;
                codeString += btnKey3.getText().toString().trim();
                //tvInputPin.setText(codeString);
                setDotEnable();

                break;
            case R.id.btnKey4:
                if (codeString.length() == 5)
                    return;
                codeString += btnKey4.getText().toString().trim();
                //tvInputPin.setText(codeString);
                setDotEnable();

                break;

            case R.id.btnKey5:
                if (codeString.length() == 5)
                    return;
                codeString += btnKey5.getText().toString().trim();
                //tvInputPin.setText(codeString);
                setDotEnable();

                break;
            case R.id.btnKey6:
                if (codeString.length() == 5)
                    return;
                codeString += btnKey6.getText().toString().trim();
                //tvInputPin.setText(codeString);
                setDotEnable();

                break;

            case R.id.btnKey7:
                if (codeString.length() == 5)
                    return;
                codeString += btnKey7.getText().toString().trim();
                //tvInputPin.setText(codeString);
                setDotEnable();

                break;

            case R.id.btnKey8:
                if (codeString.length() == 5)
                    return;
                codeString += btnKey8.getText().toString().trim();
                //tvInputPin.setText(codeString);
                setDotEnable();
                break;

            case R.id.btnKey9:
                if (codeString.length() == 5)
                    return;
                codeString += btnKey9.getText().toString().trim();
                //tvInputPin.setText(codeString);
                setDotEnable();

                break;

            case R.id.btnKey10:
                if (codeString.length() == 5)
                    return;
                codeString += btnKey10.getText().toString().trim();
                //tvInputPin.setText(codeString);
                setDotEnable();

                break;
            case R.id.btnConfirm:
                //getRandomKeyFromRange();
                if (codeString.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter your OTP", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (codeString.length() < 4) {
                    Toast.makeText(getActivity(), "ivalid OTP", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mOtp.equalsIgnoreCase(codeString)) {
                    mPinListenr.onOtpConfirm();
                } else {
                    Toast.makeText(getActivity(), "InValid OTP", Toast.LENGTH_SHORT).show();
                    dismiss();
                }


                //Log.e(TAG, "onClick: " + codeString);
                break;
            case R.id.btnClear:
                setDotDisable();


                break;
            case R.id.btnCloseDialog:
                dismiss();
                break;


        }
    }

    private void setDotEnable() {
        char[] chars = codeString.toCharArray();

        for (int i = 0; i < codeString.length(); i++) {
            dots.get(i).setText(""+chars[i]);

        }
    }

    private void setDotDisable() {
        for (int i = 0; i < 5; i++) {
            dots.get(i).setText("");
        }
        codeString = "";
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mPinListenr = (OtpConfirmListener) context;
    }
}
