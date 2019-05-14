package com.shreeyesh.ui.module;

public class FeesPaymentDetailsModule {
    String payment_details;
    String paid_amount;


    public FeesPaymentDetailsModule(String payment_details, String paid_amount) {
        this.payment_details = payment_details;
        this.paid_amount = paid_amount;
    }

    public String getPayment_details() {
        return payment_details;
    }

    public void setPayment_details(String payment_details) {
        this.payment_details = payment_details;
    }

    public String getPaid_amount() {
        return paid_amount;
    }

    public void setPaid_amount(String paid_amount) {
        this.paid_amount = paid_amount;
    }





}
