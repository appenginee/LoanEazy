package com.loan.loaneazy.bottom_fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.loan.loaneazy.R;
import com.loan.loaneazy.my_interface.DeleteListener;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.views.MyTextView;

import java.util.Objects;

public class BottomDeleteAccount extends BottomSheetDialogFragment implements View.OnClickListener{
    private BottomSheetBehavior mBehaviour;
    private MyButton btnConfirmDelete;
    private MyTextView tvCommonContent;
    private DeleteListener mListenerDelete;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog fBottomSheetDialog=(BottomSheetDialog)super.onCreateDialog(savedInstanceState);
        View mView= View.inflate(getActivity(), R.layout.account_delete_bottom,null);
        fBottomSheetDialog.setContentView(mView);
        mBehaviour = BottomSheetBehavior.from((View) mView.getParent());

        findIds(mView);

        return fBottomSheetDialog;



    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnConfirmDelete) {
            mListenerDelete.onDeleteConfirm();
        }
    }

    private void findIds(View mView) {
        tvCommonContent = (MyTextView) mView.findViewById(R.id.tvCommonContent);
        btnConfirmDelete = (MyButton) mView.findViewById(R.id.btnConfirmDelete);
        btnConfirmDelete.setOnClickListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListenerDelete= (DeleteListener) context;

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


}
