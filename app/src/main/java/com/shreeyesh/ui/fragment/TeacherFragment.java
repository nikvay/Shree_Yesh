package com.shreeyesh.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.module.TeacherListModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.adapter.TeacherListAdminAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class TeacherFragment extends Fragment {

    Context mContext;
    ImageView iv_empty_list;

    RecyclerView recycler_view_teacher_list;
    ArrayList<TeacherListModule> teacherListModuleArrayList = new ArrayList<>();
    TeacherListAdminAdapter teacherListAdminAdapter;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";

    public TeacherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher, container, false);
        mContext = getActivity();

        find_All_IDs(view);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

//        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");

        uId = SharedPreference.getUserID(mContext);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycler_view_teacher_list.setLayoutManager(linearLayoutManager);
        recycler_view_teacher_list.setHasFixedSize(true);

        if (NetworkUtils.isNetworkAvailable(mContext)) {
            doTeacherList();
        } else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }

        return view;
    }//========== End onCreate () ==============

    private void find_All_IDs(View view) {
        recycler_view_teacher_list = view.findViewById(R.id.recycler_view_teacher_list);
        iv_empty_list = view.findViewById(R.id.iv_empty_list);
    }

    private void doTeacherList() {
        Call<SuccessModule> call = apiInterface.teacherListModuleCall();

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {

                        SuccessModule teacherListModule = response.body();

                        if (teacherListModule != null) {

                            teacherListModuleArrayList = teacherListModule.getTeacherListModuleArrayArrayList();

                            teacherListAdminAdapter = new TeacherListAdminAdapter(mContext, teacherListModuleArrayList);
                            recycler_view_teacher_list.setAdapter(teacherListAdminAdapter);
                            teacherListAdminAdapter.notifyDataSetChanged();

                            if (teacherListModuleArrayList.size() != 0) {
                                recycler_view_teacher_list.setVisibility(View.VISIBLE);
                                iv_empty_list.setVisibility(View.GONE);
                            } else {
                                recycler_view_teacher_list.setVisibility(View.GONE);
                                iv_empty_list.setVisibility(View.VISIBLE);
                            }

                        } else {
                            Toasty.info(mContext, "Response Null !!", Toast.LENGTH_SHORT,true).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT,true).show();
            }
        });
    }
}
