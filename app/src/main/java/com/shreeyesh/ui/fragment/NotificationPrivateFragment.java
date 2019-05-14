package com.shreeyesh.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.NotificationModule;
import com.shreeyesh.domain.module.NotificationPrivateModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.adapter.NotificationPrivateAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class NotificationPrivateFragment extends Fragment {

    Context mContext;
    ImageView iv_empty_list;

    String TAG = getClass().getSimpleName();
    //======Interface Declaration=========
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, isSelectUser, type;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";

    RecyclerView recyclerView_notification_private;
    SwipeRefreshLayout swipeToRefresh;
    NotificationPrivateAdapter notificationPrivateAdapter;
    ArrayList<NotificationPrivateModule> notificationPrivateModuleArrayList = new ArrayList<>();

    public NotificationPrivateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_private, container, false);
        mContext = getActivity();

        find_All_IDs(view);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");

        uId = SharedPreference.getUserID(mContext);

        if (isSelectUser.equalsIgnoreCase("1")) {
            type = "staff";
        } else if (isSelectUser.equalsIgnoreCase("2")) {
            type = "staff";
        } else if (isSelectUser.equalsIgnoreCase("3")) {
            type = "student";
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView_notification_private.setLayoutManager(linearLayoutManager);
        recyclerView_notification_private.setHasFixedSize(true);

        //============ Api Call ================
        if (NetworkUtils.isNetworkAvailable(mContext))
            privateNotification();
        else
            NetworkUtils.isNetworkNotAvailable(mContext);

        swipeToRefresh.setColorSchemeResources(R.color.app_color);
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //============ Api Call ================
                if (NetworkUtils.isNetworkAvailable(mContext))
                    privateNotification();
                else
                    NetworkUtils.isNetworkNotAvailable(mContext);

                swipeToRefresh.setRefreshing(false);
            }
        });
        return view;
    }//========== End onCreate () ==============

    private void find_All_IDs(View view) {
        recyclerView_notification_private = view.findViewById(R.id.recyclerView_notification_private);
        swipeToRefresh = view.findViewById(R.id.swipeToRefresh);
        iv_empty_list = view.findViewById(R.id.iv_empty_list);
    }

    private void privateNotification() {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<NotificationModule> call = apiInterface.notificationOneModuleCall(uId, type, "");

        call.enqueue(new Callback<NotificationModule>() {
            @Override
            public void onResponse(Call<NotificationModule> call, Response<NotificationModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);
                try {
                    if (response.isSuccessful()) {

                        NotificationModule notificationOneModule = response.body();

                        String message = notificationOneModule.getMsg();
                        String code = notificationOneModule.getError_code();

                        if (code.equalsIgnoreCase("1")) {
                            notificationPrivateModuleArrayList = notificationOneModule.getNotificationPrivateModuleArrayList();
                            Collections.reverse(notificationPrivateModuleArrayList);

                            notificationPrivateAdapter = new NotificationPrivateAdapter(mContext, notificationPrivateModuleArrayList);
                            recyclerView_notification_private.setAdapter(notificationPrivateAdapter);
                            notificationPrivateAdapter.notifyDataSetChanged();

                            if (notificationPrivateModuleArrayList.size() != 0) {
                                recyclerView_notification_private.setVisibility(View.VISIBLE);
                                iv_empty_list.setVisibility(View.GONE);
                            } else {
                                recyclerView_notification_private.setVisibility(View.GONE);
                                iv_empty_list.setVisibility(View.VISIBLE);
                            }

                        } else {
                            Toasty.warning(mContext, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                        }

                    } else {
                        Toasty.warning(mContext, "Server Response !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<NotificationModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }
}