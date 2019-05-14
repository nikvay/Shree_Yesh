package com.shreeyesh.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.NotificationTypeModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.activity.NotificationTypeActivity;
import com.shreeyesh.ui.adapter.NotificationMultiTypeAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class NotificationTypeFragment extends Fragment {

    Context mContext;
    ImageView iv_empty_list;

    NotificationMultiTypeAdapter notificationMultiTypeAdapter;
    ArrayList<NotificationTypeModule> notificationTypeModuleArrayList = new ArrayList<>();
    RecyclerView recycler_view_notification_m_type;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, uType, isSelectUser;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Fast Connect";

    public NotificationTypeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_type, container, false);
        mContext = getActivity();

        find_All_IDs(view);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
//        editor = sharedpreferences.edit();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");

        uId = SharedPreference.getUserID(mContext);
        uType = SharedPreference.getUserType(mContext);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycler_view_notification_m_type.setLayoutManager(linearLayoutManager);
        recycler_view_notification_m_type.setHasFixedSize(true);

        //============ Api Call ================
        if (NetworkUtils.isNetworkAvailable(mContext))
            notificationType();
        else
            NetworkUtils.isNetworkNotAvailable(mContext);

        events();

        return view;

    }//========== End onCreate () ==============

    private void find_All_IDs(View view) {
        recycler_view_notification_m_type = view.findViewById(R.id.recycler_view_notification_m_type);
        iv_empty_list = view.findViewById(R.id.iv_empty_list);
    }

    private void events() {
    }

    private void notificationType() {
        Call<SuccessModule> call = apiInterface.notificationTypeCall(uId);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {

                        SuccessModule classListModule = response.body();

                        String message = classListModule.getMsg();
                        String code = classListModule.getError_code();

                        if (code.equalsIgnoreCase("1")) {
                            notificationTypeModuleArrayList = classListModule.getNotificationTypeModuleArrayList();

                            notificationMultiTypeAdapter = new NotificationMultiTypeAdapter(mContext, notificationTypeModuleArrayList);
                            recycler_view_notification_m_type.setAdapter(notificationMultiTypeAdapter);
                            notificationMultiTypeAdapter.notifyDataSetChanged();

                            if (notificationTypeModuleArrayList.size() != 0) {
                                recycler_view_notification_m_type.setVisibility(View.VISIBLE);
                                iv_empty_list.setVisibility(View.GONE);
                            } else {
                                recycler_view_notification_m_type.setVisibility(View.GONE);
                                iv_empty_list.setVisibility(View.VISIBLE);
                            }

                            adapterOnClick();

                        } else {
                            Toasty.info(mContext, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.info(mContext, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void adapterOnClick() {
        notificationMultiTypeAdapter.setOnItemClickListener(new NotificationMultiTypeAdapter.OnItemClickListener() {
            @Override
            public void onAdapterClick(NotificationTypeModule notificationTypeModule, int position) {
                String typeId = notificationTypeModule.getId();
                String typeName = notificationTypeModule.getName();
                Intent intent = new Intent(mContext, NotificationTypeActivity.class);
                intent.putExtra("TYPE_ID",typeId);
                intent.putExtra("NAME",typeName);
                startActivity(intent);
            }
        });
    }
}
