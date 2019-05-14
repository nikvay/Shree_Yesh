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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.ClassListModule;
import com.shreeyesh.domain.module.LeaveListModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.activity.LeaveAddActivity;
import com.shreeyesh.ui.adapter.LeaveAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class LeaveFragment extends Fragment {

    Context mContext;
    ImageView iv_empty_list;
    FloatingActionButton fab_add_leave;
    LinearLayout ll_leave_list;
    Spinner spinner_class_list_leave;

    LeaveAdapter leaveAdapter;
    ArrayList<LeaveListModule> studentLeaveModuleArrayList = new ArrayList<>();
    RecyclerView recycler_view_std_leave;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String isSelectUser, uId, teacherUId, studentUId, uType;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";

    ArrayList<ClassListModule> classListModuleArrayArrayList = new ArrayList<>();
    ArrayList<String> selectClassList = new ArrayList<>();
    String sltClass = "", classId = "";

    public LeaveFragment() {
        // Required empty public constructor
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leave, container, false);
        mContext = getActivity();

        find_All_IDs(view);

        if (mContext != null) {
            sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        }
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        uType = SharedPreference.getUserType(mContext);

        uId = SharedPreference.getUserID(mContext);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycler_view_std_leave.setLayoutManager(linearLayoutManager);
        recycler_view_std_leave.setHasFixedSize(true);

        if (isSelectUser.equalsIgnoreCase("2")) {
            teacherUId = uId;
            studentUId = "";
            ll_leave_list.setVisibility(View.VISIBLE);
            fab_add_leave.setVisibility(View.GONE);

            if (NetworkUtils.isNetworkAvailable(mContext)) {
                doClassList();
            } else {
                NetworkUtils.isNetworkNotAvailable(mContext);
            }
        } else if (isSelectUser.equalsIgnoreCase("3")) {
            studentUId = uId;
            teacherUId = "";
            ll_leave_list.setVisibility(View.GONE);
            fab_add_leave.setVisibility(View.VISIBLE);

            if (NetworkUtils.isNetworkAvailable(mContext)) {
                doLeaveList();
            } else {
                NetworkUtils.isNetworkNotAvailable(mContext);
            }
        }

        event();

        return view;
    }//========== End onCreate () ==============

    private void find_All_IDs(View view) {
        ll_leave_list = view.findViewById(R.id.ll_leave_list);
        recycler_view_std_leave = view.findViewById(R.id.recycler_view_std_leave);
        iv_empty_list = view.findViewById(R.id.iv_empty_list);

        spinner_class_list_leave = view.findViewById(R.id.spinner_class_list_leave);
        fab_add_leave = view.findViewById(R.id.fab_add_leave);
    }

    private void event() {
        fab_add_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LeaveAddActivity.class);
                startActivity(intent);
            }
        });

    }

    //Class List Call
    private void doClassList() {
        Call<SuccessModule> call = apiInterface.classListCall(uId, uType);

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
                            classListModuleArrayArrayList = classListModule.getClassListModuleArrayArrayList();

                            for (ClassListModule classListModuleArray : classListModuleArrayArrayList) {
                                selectClassList.add(classListModuleArray.getClass_name());
                            }

                            classListSpinner();

                        } else {
                            Toasty.info(mContext, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                        }

                    } else {
                        Toasty.info(mContext, "Response null !!", Toast.LENGTH_SHORT, true).show();
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

    private void classListSpinner() {
        ArrayAdapter<String> aa = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, selectClassList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_class_list_leave.setAdapter(aa);

        spinner_class_list_leave.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                sltClass = parent.getItemAtPosition(position).toString();
                String s = spinner_class_list_leave.getSelectedItem().toString();

                try {
                    int clsId = Integer.parseInt(classListModuleArrayArrayList.get(position).getClass_id());
//                    classId = String.valueOf(clsId - 1);
                    classId = String.valueOf(clsId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // ====== Api Call ========
                if (NetworkUtils.isNetworkAvailable(mContext)) {
                    doLeaveList();
                } else {
                    NetworkUtils.isNetworkNotAvailable(mContext);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void doLeaveList() {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.leaveListCall(classId, teacherUId, studentUId);

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
                                studentLeaveModuleArrayList.clear();
                                if (errorCode.equalsIgnoreCase("1")) {
                                    studentLeaveModuleArrayList = successModule.getLeaveListModuleArrayList();
                                    Collections.reverse(studentLeaveModuleArrayList);

                                    leaveAdapter = new LeaveAdapter(mContext, studentLeaveModuleArrayList);
                                    recycler_view_std_leave.setAdapter(leaveAdapter);
                                    leaveAdapter.notifyDataSetChanged();

                                    if (studentLeaveModuleArrayList.size() != 0) {
                                        recycler_view_std_leave.setVisibility(View.VISIBLE);
                                        iv_empty_list.setVisibility(View.GONE);
                                    } else {
                                        recycler_view_std_leave.setVisibility(View.GONE);
                                        iv_empty_list.setVisibility(View.VISIBLE);
                                    }
//                                    Toasty.success(mContext, "Leave List Display Successfully !!", Toast.LENGTH_SHORT, true).show();
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
