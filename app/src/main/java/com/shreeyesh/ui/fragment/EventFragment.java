package com.shreeyesh.ui.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import com.shreeyesh.domain.module.EventModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.activity.EventAddActivity;
import com.shreeyesh.ui.adapter.EventAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class EventFragment extends Fragment {

    Context mContext;
    ImageView iv_empty_list;

    ArrayList<EventModule> eventModuleArrayList = new ArrayList<>();
    RecyclerView recycler_view_event;
    EventAdapter eventAdapter;
    FloatingActionButton fab_add_event;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String isSelectUser, uId;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";

    public EventFragment() {
        // Required empty public constructor
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        mContext = getActivity();

        if (mContext != null) {
            sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        }
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        uId = SharedPreference.getUserID(mContext);

        find_All_IDs(view);

        if (isSelectUser.equalsIgnoreCase("3")||isSelectUser.equalsIgnoreCase("5")) {
            fab_add_event.setVisibility(View.GONE);
        }
        //Set layout to the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycler_view_event.setLayoutManager(linearLayoutManager);
        recycler_view_event.setHasFixedSize(true);

        if (NetworkUtils.isNetworkAvailable(mContext)) {
            eventList();
        } else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }

        event();

        return view;
    }//========== End onCreate () ==============

    private void find_All_IDs(View view) {
        recycler_view_event = view.findViewById(R.id.recycler_view_event);
        iv_empty_list = view.findViewById(R.id.iv_empty_list);
        fab_add_event = view.findViewById(R.id.fab_add_event);
    }

    private void event() {
        fab_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EventAddActivity.class);
                startActivity(intent);
            }
        });
    }

    private void eventList() {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.evenListCall(uId);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModule successModule = response.body();

                        String message = null, errorCode = null, url = null;
                        if (successModule != null) {
                            message = successModule.getMsg();
                            errorCode = successModule.getError_code();
                            url = successModule.getImg_base_url();

                            if (errorCode != null) {
                                if (errorCode.equalsIgnoreCase("1")) {
                                    eventModuleArrayList = successModule.getEventModuleArrayList();
                                    Collections.reverse(eventModuleArrayList);

                                    eventAdapter = new EventAdapter(mContext, url, eventModuleArrayList);
                                    recycler_view_event.setAdapter(eventAdapter);
                                    eventAdapter.notifyDataSetChanged();

                                    if (eventModuleArrayList.size() != 0) {
                                        recycler_view_event.setVisibility(View.VISIBLE);
                                        iv_empty_list.setVisibility(View.GONE);
                                    } else {
                                        recycler_view_event.setVisibility(View.GONE);
                                        iv_empty_list.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    Toasty.info(mContext, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                                }
                            }
                        }
                    } else {
                        Toasty.info(mContext, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }
}
