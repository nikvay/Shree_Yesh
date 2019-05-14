package com.shreeyesh.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.HomeWorkListModule;
import com.shreeyesh.domain.module.HomeWorkModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.adapter.HomeWorkStdAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class HomeWorkStudentListFragment extends Fragment {

    Context mContext;
    ImageView iv_empty_list;
    EditText edt_search_std_home_work;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";

    RecyclerView recycler_view_homework_std;
    ArrayList<HomeWorkListModule> homeWorkListStudentModuleArrayList = new ArrayList<>();
    HomeWorkStdAdapter homeWorkStdAdapter;

    public HomeWorkStudentListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_work_std_list, container, false);
        mContext = getActivity();

        find_All_IDs(view);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        uId = SharedPreference.getUserID(mContext);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycler_view_homework_std.setLayoutManager(linearLayoutManager);
        recycler_view_homework_std.setHasFixedSize(true);

        if (NetworkUtils.isNetworkAvailable(mContext)) {
            doHomeWorkList();
        } else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }

        event();

        return view;
    }//========== End onCreate () ==============

    private void find_All_IDs(View view) {
        edt_search_std_home_work = view.findViewById(R.id.edt_search_std_home_work);
        recycler_view_homework_std = view.findViewById(R.id.recycler_view_homework_std);
        iv_empty_list = view.findViewById(R.id.iv_empty_list);
    }

    private void event() {
        edt_search_std_home_work.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                homeWorkStdAdapter.getFilter().filter(edt_search_std_home_work.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void doHomeWorkList() {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<HomeWorkModule> call = apiInterface.homeWorkStdModuleCall(uId);

        call.enqueue(new Callback<HomeWorkModule>() {
            @Override
            public void onResponse(Call<HomeWorkModule> call, Response<HomeWorkModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        HomeWorkModule homeWorkModule = response.body();

                        String message = null, errorCode = null, url = null;
                        if (homeWorkModule != null) {
                            message = homeWorkModule.getMsg();
                            errorCode = homeWorkModule.getError_code();
                            url = homeWorkModule.getImg_base_url();

                            if (errorCode.equalsIgnoreCase("1")) {

                                homeWorkListStudentModuleArrayList = homeWorkModule.getHomeWorkListStudentModuleArrayList();
                                Collections.reverse(homeWorkListStudentModuleArrayList);

                                homeWorkStdAdapter = new HomeWorkStdAdapter(mContext, url, homeWorkListStudentModuleArrayList);
                                recycler_view_homework_std.setAdapter(homeWorkStdAdapter);
                                homeWorkStdAdapter.notifyDataSetChanged();

                                if (homeWorkListStudentModuleArrayList.size() != 0) {
                                    recycler_view_homework_std.setVisibility(View.VISIBLE);
                                    iv_empty_list.setVisibility(View.GONE);
                                } else {
                                    recycler_view_homework_std.setVisibility(View.GONE);
                                    iv_empty_list.setVisibility(View.VISIBLE);
                                }

                                Toasty.success(mContext, "Home Work List Display Successfully !!", Toast.LENGTH_SHORT, true).show();
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
            public void onFailure(Call<HomeWorkModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }
}
