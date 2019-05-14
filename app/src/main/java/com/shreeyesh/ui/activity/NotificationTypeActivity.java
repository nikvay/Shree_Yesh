package com.shreeyesh.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.NotificationModule;
import com.shreeyesh.domain.module.NotificationPublicModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.adapter.NotificationPublicAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationTypeActivity extends AppCompatActivity {

    ImageView iv_empty_list, iv_back;
    TextView tv_action_bar_title;

    String TAG = getClass().getSimpleName();
    //======Interface Declaration=========
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, isSelectUser, type;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";

    RecyclerView recyclerView_notification_public_type;
    SwipeRefreshLayout swipeToRefresh;
    NotificationPublicAdapter notificationPublicAdapter;
    ArrayList<NotificationPublicModule> notificationPublicModuleArrayList = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_type);

        find_All_IDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");

        uId = SharedPreference.getUserID(NotificationTypeActivity.this);

        if (isSelectUser.equalsIgnoreCase("1")) {
            type = "staff";
        } else if (isSelectUser.equalsIgnoreCase("2")) {
            type = "staff";
        } else if (isSelectUser.equalsIgnoreCase("3")) {
            type = "student";
        }

        Intent intent = getIntent();
        String typeID = intent.getStringExtra("TYPE_ID");
        String name = intent.getStringExtra("NAME");
        tv_action_bar_title.setText(name + "Notification");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NotificationTypeActivity.this);
        recyclerView_notification_public_type.setLayoutManager(linearLayoutManager);
        recyclerView_notification_public_type.setHasFixedSize(true);

        if (NetworkUtils.isNetworkAvailable(this))
            publicNotificationType(typeID);
        else
            NetworkUtils.isNetworkNotAvailable(this);

        event();

    }//============= End onCreate () ==============

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
        tv_action_bar_title = findViewById(R.id.tv_action_bar_title);
        recyclerView_notification_public_type = findViewById(R.id.recyclerView_notification_public_type);
        iv_empty_list = findViewById(R.id.iv_empty_list);
    }

    private void event() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void publicNotificationType(String typeID) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<NotificationModule> call = apiInterface.notificationOneModuleCall(uId, type, typeID);

        call.enqueue(new Callback<NotificationModule>() {
            @Override
            public void onResponse(Call<NotificationModule> call, Response<NotificationModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);
                try {
                    if (response.isSuccessful()) {

                        NotificationModule notificationModule = response.body();

                        String message = notificationModule.getMsg();
                        String code = notificationModule.getError_code();

                        if (code.equalsIgnoreCase("1")) {
                            notificationPublicModuleArrayList = notificationModule.getNotificationPublicArrayList();
                            Collections.reverse(notificationPublicModuleArrayList);

                            notificationPublicAdapter = new NotificationPublicAdapter(NotificationTypeActivity.this, notificationPublicModuleArrayList);
                            recyclerView_notification_public_type.setAdapter(notificationPublicAdapter);
                            notificationPublicAdapter.notifyDataSetChanged();

                            if (notificationPublicModuleArrayList.size() != 0) {
                                recyclerView_notification_public_type.setVisibility(View.VISIBLE);
                                iv_empty_list.setVisibility(View.GONE);
                            } else {
                                recyclerView_notification_public_type.setVisibility(View.GONE);
                                iv_empty_list.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toasty.info(NotificationTypeActivity.this, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.info(NotificationTypeActivity.this, "Server Response !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<NotificationModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(NotificationTypeActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }
}
