package com.shreeyesh.domain.module;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResultExamNameModule {
    private String test_date;
    private String test_name;
    private String test_id;

    @SerializedName("marks_list")
    private ArrayList<ResultMarkModule> resultMarkModuleArrayList;

    public String getTest_date() {
        return test_date;
    }

    public void setTest_date(String test_date) {
        this.test_date = test_date;
    }

    public String getTest_name() {
        return test_name;
    }

    public void setTest_name(String test_name) {
        this.test_name = test_name;
    }

    public String getTest_id() {
        return test_id;
    }

    public void setTest_id(String test_id) {
        this.test_id = test_id;
    }

    public ArrayList<ResultMarkModule> getResultMarkModuleArrayList() {
        return resultMarkModuleArrayList;
    }

    public void setResultMarkModuleArrayList(ArrayList<ResultMarkModule> resultMarkModuleArrayList) {
        this.resultMarkModuleArrayList = resultMarkModuleArrayList;
    }
}
