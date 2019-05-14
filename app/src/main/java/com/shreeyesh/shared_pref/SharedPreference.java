package com.shreeyesh.shared_pref;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {

    private static String SHARED_PREF_NAME = "Fast Connect";
    public static String IS_LOGIN = "IS_LOGIN";
    public static String USER_FULL_NAME = "USER_FULL_NAME";
    public static String USER_NAME = "USER_NAME";
    public static String USER_PASSWORD = "USER_PASSWORD";
    public static String USER_EMAIL = "USER_EMAIL";
    public static String USER_CONTACT = "USER_CONTACT";
    public static String CLASS_DIV = "CLASS_DIV";
    public static String UID = "UID";
    public static String U_TYPE = "U_TYPE";
    public static String IS_SELECT_USER = "IS_SELECT_USER";
    private static String DEVICE_TOKEN = "DEVICE_TOKEN";
    public static String TIME_TABLE_NOTES = "TIME_TABLE_NOTES";
    public static String REFRESH = "REFRESH";
    public static String SCHOOL_NAME = "SCHOOL_NAME";
    public static String CLASS_ID = "CLASS_ID";
    public static String DIVISION_ID = "DIVISION_ID";
    public static String S_NAME = "S_NAME";
    public static String S_NAME_TWO = "S_NAME_TWO";
    public static String S_IMG = "S_IMG";

    public static String NOTIFICATION_COUNT = "NOTIFICATION_COUNT";
    public static String GALLERY_COUNT = "GALLERY_COUNT";


    public static void putDeviceToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(DEVICE_TOKEN, token)
                .apply();
    }

    public static String getDeviceToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DEVICE_TOKEN, "");
    }

    public static void putUserID(Context context, String user_id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(UID, user_id)
                .apply();
    }


    public static String getUserID(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(UID, "");
    }

    public static void putUserType(Context context, String user_type) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(U_TYPE, user_type)
                .apply();
    }

    public static String getUserType(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(U_TYPE, "");
    }

}
