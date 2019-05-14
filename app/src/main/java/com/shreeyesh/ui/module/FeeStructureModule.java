package com.shreeyesh.ui.module;

public class FeeStructureModule {


    String  fee_details;
    String  fee_amount;

    public FeeStructureModule(String fee_details, String fee_amount, String fee_date) {
        this.fee_details = fee_details;
        this.fee_amount = fee_amount;
        this.fee_date = fee_date;
    }

    String fee_date;

    public String getFee_details() {
        return fee_details;
    }

    public void setFee_details(String fee_details) {
        this.fee_details = fee_details;
    }

    public String getFee_amount() {
        return fee_amount;
    }

    public void setFee_amount(String fee_amount) {
        this.fee_amount = fee_amount;
    }

    public String getFee_date() {
        return fee_date;
    }

    public void setFee_date(String fee_date) {
        this.fee_date = fee_date;
    }
}
