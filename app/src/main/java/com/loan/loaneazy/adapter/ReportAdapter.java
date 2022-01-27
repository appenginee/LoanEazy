package com.loan.loaneazy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loan.loaneazy.R;
import com.loan.loaneazy.model.ReportStatus;
import com.loan.loaneazy.model.request.RequestLoan;
import com.loan.loaneazy.views.MyTextView;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportHolder> {
    ArrayList<ReportStatus.LoanHistory> mListLoan;
    Context mCtx;

    public ReportAdapter(ArrayList<ReportStatus.LoanHistory> mListLoan, Context mCtx) {
        this.mListLoan = mListLoan;
        this.mCtx = mCtx;
    }


    @NonNull
    @Override
    public ReportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mCtx).inflate(R.layout.row_loan_report_item, parent, false);
        return new ReportHolder(inflate);

    }

    @Override
    public void onBindViewHolder(@NonNull ReportHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return mListLoan.size();
    }

    public class ReportHolder extends RecyclerView.ViewHolder {
        MyTextView tvLoanAmt, tvStatusLoan, tvApplyOnLoan, tvRepaymentAmtLoan, tvRepaymentAmtDate;

        public ReportHolder(@NonNull View itemView) {
            super(itemView);
            tvLoanAmt = itemView.findViewById(R.id.tvLoanAmt);
            tvStatusLoan = itemView.findViewById(R.id.tvStatusLoan);
            tvApplyOnLoan = itemView.findViewById(R.id.tvApplyOnLoan);
            tvRepaymentAmtLoan = itemView.findViewById(R.id.tvRepaymentAmtLoan);
            tvRepaymentAmtDate = itemView.findViewById(R.id.tvRepaymentAmtDate);
        }

        public void bind() {
            tvLoanAmt.setText("\u20B9" + mListLoan.get(getAdapterPosition()).getLoan_amount());
            if (mListLoan.get(getAdapterPosition()).getMsg().equalsIgnoreCase("paid")) {
                tvStatusLoan.setText(mListLoan.get(getAdapterPosition()).getMsg());
                tvStatusLoan.setTextColor(Color.parseColor("#85C53B"));
            } else {
                tvStatusLoan.setText(mListLoan.get(getAdapterPosition()).getMsg());
                tvStatusLoan.setTextColor(Color.parseColor("#FF0000"));
            }
            tvApplyOnLoan.setText(mListLoan.get(getAdapterPosition()).getLoan_applied_date_and_time());
            tvRepaymentAmtLoan.setText("\u20B9" + mListLoan.get(getAdapterPosition()).getLoan_repayment_amount());
            tvRepaymentAmtDate.setText(mListLoan.get(getAdapterPosition()).getLoan_repayment_date());


        }
    }
}
