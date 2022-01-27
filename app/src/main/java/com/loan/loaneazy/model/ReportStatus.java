package com.loan.loaneazy.model;



public class ReportStatus {
    boolean aadhar_kyc_is_done;
    boolean image_verification_is_done;
    boolean kycStatus;
    boolean pan_kyc_is_done;
    boolean scoreAccepted;
    String kyc_message;
    String name;
    Long total_amount_of_loan_applied;
    Long total_amount_of_loan_disbursed;
    Long total_amount_of_loan_repayed;
    Long total_number_loan_taken;

    public class LoanHistory{
        String loan_amount;
        String loan_applied_date_and_time;
        String loan_repayment_amount;
        String loan_repayment_date;
        String msg;

        public LoanHistory(String loan_amount, String loan_applied_date_and_time, String loan_repayment_amount, String loan_repayment_date, String msg) {
            this.loan_amount = loan_amount;
            this.loan_applied_date_and_time = loan_applied_date_and_time;
            this.loan_repayment_amount = loan_repayment_amount;
            this.loan_repayment_date = loan_repayment_date;
            this.msg = msg;
        }

        public String getLoan_amount() {
            return loan_amount;
        }

        public String getLoan_applied_date_and_time() {
            return loan_applied_date_and_time;
        }

        public String getLoan_repayment_amount() {
            return loan_repayment_amount;
        }

        public String getLoan_repayment_date() {
            return loan_repayment_date;
        }

        public String getMsg() {
            return msg;
        }
    }

    public boolean isAadhar_kyc_is_done() {
        return aadhar_kyc_is_done;
    }

    public void setAadhar_kyc_is_done(boolean aadhar_kyc_is_done) {
        this.aadhar_kyc_is_done = aadhar_kyc_is_done;
    }

    public boolean isImage_verification_is_done() {
        return image_verification_is_done;
    }

    public void setImage_verification_is_done(boolean image_verification_is_done) {
        this.image_verification_is_done = image_verification_is_done;
    }

    public boolean isKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(boolean kycStatus) {
        this.kycStatus = kycStatus;
    }

    public boolean isPan_kyc_is_done() {
        return pan_kyc_is_done;
    }

    public void setPan_kyc_is_done(boolean pan_kyc_is_done) {
        this.pan_kyc_is_done = pan_kyc_is_done;
    }

    public boolean isScoreAccepted() {
        return scoreAccepted;
    }

    public void setScoreAccepted(boolean scoreAccepted) {
        this.scoreAccepted = scoreAccepted;
    }

    public String getKyc_message() {
        return kyc_message;
    }

    public void setKyc_message(String kyc_message) {
        this.kyc_message = kyc_message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTotal_amount_of_loan_applied() {
        return total_amount_of_loan_applied;
    }

    public void setTotal_amount_of_loan_applied(Long total_amount_of_loan_applied) {
        this.total_amount_of_loan_applied = total_amount_of_loan_applied;
    }

    public Long getTotal_amount_of_loan_disbursed() {
        return total_amount_of_loan_disbursed;
    }

    public void setTotal_amount_of_loan_disbursed(Long total_amount_of_loan_disbursed) {
        this.total_amount_of_loan_disbursed = total_amount_of_loan_disbursed;
    }

    public Long getTotal_amount_of_loan_repayed() {
        return total_amount_of_loan_repayed;
    }

    public void setTotal_amount_of_loan_repayed(Long total_amount_of_loan_repayed) {
        this.total_amount_of_loan_repayed = total_amount_of_loan_repayed;
    }

    public Long getTotal_number_loan_taken() {
        return total_number_loan_taken;
    }

    public void setTotal_number_loan_taken(Long total_number_loan_taken) {
        this.total_number_loan_taken = total_number_loan_taken;
    }
}
