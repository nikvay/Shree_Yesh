package com.shreeyesh.domain.module;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class HomeWorkModule {

    private String msg;
    private String is_atten;
    private String error_code;
    private String img_base_url;

    @SerializedName("student_homework_list")
    private ArrayList<HomeWorkListModule> homeWorkListStudentModuleArrayList;

    @SerializedName("teacher_homework_list")
    private ArrayList<HomeWorkListModule> homeWorkListTeacherModuleArrayList;

    @SerializedName("student_list")
    private ArrayList<StudentAttendanceListModule> studentAttendanceListModule;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getIs_atten() {
        return is_atten;
    }

    public void setIs_atten(String is_atten) {
        this.is_atten = is_atten;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getImg_base_url() {
        return img_base_url;
    }

    public void setImg_base_url(String img_base_url) {
        this.img_base_url = img_base_url;
    }

    public ArrayList<HomeWorkListModule> getHomeWorkListStudentModuleArrayList() {
        return homeWorkListStudentModuleArrayList;
    }

    public void setHomeWorkListStudentModuleArrayList(ArrayList<HomeWorkListModule> homeWorkListStudentModuleArrayList) {
        this.homeWorkListStudentModuleArrayList = homeWorkListStudentModuleArrayList;
    }

    public ArrayList<HomeWorkListModule> getHomeWorkListTeacherModuleArrayList() {
        return homeWorkListTeacherModuleArrayList;
    }

    public void setHomeWorkListTeacherModuleArrayList(ArrayList<HomeWorkListModule> homeWorkListTeacherModuleArrayList) {
        this.homeWorkListTeacherModuleArrayList = homeWorkListTeacherModuleArrayList;
    }

    public ArrayList<StudentAttendanceListModule> getStudentAttendanceListModule() {
        return studentAttendanceListModule;
    }

    public void setStudentAttendanceListModule(ArrayList<StudentAttendanceListModule> studentAttendanceListModule) {
        this.studentAttendanceListModule = studentAttendanceListModule;
    }
}
