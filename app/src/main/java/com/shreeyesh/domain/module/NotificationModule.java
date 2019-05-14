package com.shreeyesh.domain.module;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NotificationModule {

    private String msg;
    private String error_code;

    @SerializedName("public_notification_list")
    private ArrayList<NotificationPublicModule> notificationPublicArrayList;

    @SerializedName("private_notification_list")
    private ArrayList<NotificationPrivateModule> notificationPrivateModuleArrayList;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public ArrayList<NotificationPublicModule> getNotificationPublicArrayList() {
        return notificationPublicArrayList;
    }

    public void setNotificationPublicArrayList(ArrayList<NotificationPublicModule> notificationPublicArrayList) {
        this.notificationPublicArrayList = notificationPublicArrayList;
    }

    public ArrayList<NotificationPrivateModule> getNotificationPrivateModuleArrayList() {
        return notificationPrivateModuleArrayList;
    }

    public void setNotificationPrivateModuleArrayList(ArrayList<NotificationPrivateModule> notificationPrivateModuleArrayList) {
        this.notificationPrivateModuleArrayList = notificationPrivateModuleArrayList;
    }
}
