package com.shreeyesh.ui.module;

public class FeeDuesModule {

    String  due_date;


    public FeeDuesModule(String due_date, String due_amount) {
        this.due_date = due_date;
        this.due_amount = due_amount;
    }

    String due_amount;


    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getDue_amount() {
        return due_amount;
    }

    public void setDue_amount(String due_amount) {
        this.due_amount = due_amount;
    }


}
