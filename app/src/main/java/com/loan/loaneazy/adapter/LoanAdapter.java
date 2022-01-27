package com.loan.loaneazy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loan.loaneazy.R;
import com.loan.loaneazy.model.loan.LoanRecord;
import com.loan.loaneazy.my_interface.ItemClickListener;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.views.MyTextView;

import java.util.ArrayList;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.LoanHolder> {
    private ArrayList<LoanRecord> mList;
    private Context mCtx;
    private ItemClickListener mItemClickListener;

    public LoanAdapter(ArrayList<LoanRecord> mList, Context mCtx) {
        this.mList = mList;
        this.mCtx = mCtx;
        this.mItemClickListener = (ItemClickListener) mCtx;

    }

    @NonNull
    @Override
    public LoanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mCtx).inflate(R.layout.row_loan_item, parent, false);
        return new LoanHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull LoanHolder holder, int position) {
        holder.bind();
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class LoanHolder extends RecyclerView.ViewHolder {
        MyTextView tvNameLoan, tvStatusLoan, tvApplyOn, tvPaymentOn, tvServiceCharge, tvLoanAmt, tvRepaymentAmt, tvTenure, tvRemainder;
        MyButton btnPayNow;

        public LoanHolder(@NonNull View itemView) {
            super(itemView);
            tvNameLoan = itemView.findViewById(R.id.tvNameLoan);
            btnPayNow = itemView.findViewById(R.id.btnPayNow);
            tvStatusLoan = itemView.findViewById(R.id.tvStatusLoan);
            tvApplyOn = itemView.findViewById(R.id.tvApplyOn);
            tvPaymentOn = itemView.findViewById(R.id.tvPaymentOn);
            tvServiceCharge = itemView.findViewById(R.id.tvServiceCharge);
            tvLoanAmt = itemView.findViewById(R.id.tvLoanAmt);
            tvRepaymentAmt = itemView.findViewById(R.id.tvRepaymentAmt);
            tvTenure = itemView.findViewById(R.id.tvTenure);
            tvRemainder = itemView.findViewById(R.id.tvRemainder);
            btnPayNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(getAdapterPosition());
                }
            });


        }

        public void bind() {
            if (mList.get(getAdapterPosition()).getLoanStatus().equalsIgnoreCase("APPROVED")) {
                btnPayNow.setEnabled(true);
                btnPayNow.setVisibility(View.VISIBLE);
            } else {
                //btnPayNow.setBackgroundColor(Color.parseColor("#FF0000"));
                btnPayNow.setVisibility(View.GONE);
            }
            tvNameLoan.setText(mList.get(getAdapterPosition()).getLoanNumber());
            tvStatusLoan.setText("Status : " + mList.get(getAdapterPosition()).getLoanStatus());
            tvApplyOn.setText(mList.get(getAdapterPosition()).getLoanApplicationDate());
            tvPaymentOn.setText(mList.get(getAdapterPosition()).getLoanRepaymentDate());
            tvLoanAmt.setText("\u20B9" + mList.get(getAdapterPosition()).getLoanAmount());
            tvServiceCharge.setText("\u20B9" + mList.get(getAdapterPosition()).getServiceCharges());
            tvLoanAmt.setTextColor(Color.parseColor("#4CAF50"));
            tvRepaymentAmt.setText("\u20B9" + mList.get(getAdapterPosition()).getRepaymentAmount());
            tvRepaymentAmt.setTextColor(Color.parseColor("#4CAF50"));
            tvRemainder.setText("" + mList.get(getAdapterPosition()).getReminded());

        }
    }
}
