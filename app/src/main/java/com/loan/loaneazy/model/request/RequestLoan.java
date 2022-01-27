package com.loan.loaneazy.model.request;

import java.util.ArrayList;

public class RequestLoan {

        String disbursement_amount;
        String gst_interest;
        String loan_amount;
        String processing_fees;
        String total_interest;

        ArrayList<RequestLoan.RepaymentDetails> mList;

    public ArrayList<RepaymentDetails> getmList() {
        return mList;
    }

    public void setmList(ArrayList<RepaymentDetails> mList) {
        this.mList = mList;
    }

    public RequestLoan(String disbursement_amount, String gst_interest, String loan_amount, String processing_fees, String total_interest) {
        this.disbursement_amount = disbursement_amount;
        this.gst_interest = gst_interest;
        this.loan_amount = loan_amount;
        this.processing_fees = processing_fees;
        this.total_interest = total_interest;

    }

    public String getDisbursement_amount() {
        return disbursement_amount;
    }

    public String getGst_interest() {
        return gst_interest;
    }

    public String getLoan_amount() {
        return loan_amount;
    }

    public String getProcessing_fees() {
        return processing_fees;
    }


    public String getTotal_interest() {
        return total_interest;
    }



    public static class RepaymentDetails{
        int amount;
        String repayment_date;
        String teanure;

        public RepaymentDetails(int amount, String repayment_date, String teanure) {
            this.amount = amount;
            this.repayment_date = repayment_date;
            this.teanure = teanure;
        }

        public int getAmount() {
            return amount;
        }

        public String getRepayment_date() {
            return repayment_date;
        }

        public String getTeanure() {
            return teanure;
        }
    }
}
