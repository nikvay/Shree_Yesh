package com.shreeyesh.domain.module;

public class ClassDivisionModule {
    private String class_id;
    private String division_name;
    private String division_id;
    private String status;

    private boolean isSelectDiv=false;

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getDivision_name() {
        return division_name;
    }

    public void setDivision_name(String division_name) {
        this.division_name = division_name;
    }

    public String getDivision_id() {
        return division_id;
    }

    public void setDivision_id(String division_id) {
        this.division_id = division_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSelectDiv() {
        return isSelectDiv;
    }

    public void setSelectDiv(boolean selectDiv) {
        isSelectDiv = selectDiv;
    }
}
