package com.shreeyesh.ui.fragment;

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
import com.shreeyesh.domain.module.EnquiryModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.activity.EnquiryActivity;
import com.shreeyesh.ui.adapter.EnquiryAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class EnquiryFragment extends Fragment {

    Context mContext;
    FloatingActionButton fab_add_enquiry;
    ImageView iv_empty_list;
    EditText edt_search_enq;

    EnquiryAdapter enquiryAdapter;
    ArrayList<EnquiryModule> enquiryModuleArrayList = new ArrayList<>();
    RecyclerView recycler_view_enquiry;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String isSelectUser, uId, teacherUId, studentUId, uType;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";


    public EnquiryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enquiry, container, false);
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
        recycler_view_enquiry.setLayoutManager(linearLayoutManager);
        recycler_view_enquiry.setHasFixedSize(true);

        if (NetworkUtils.isNetworkAvailable(mContext)) {
            doEnquiryList();
        } else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }

        events();

        return view;
    }//========== End onCreate () ==============

    private void find_All_IDs(View view) {
        edt_search_enq = view.findViewById(R.id.edt_search_enq);
        recycler_view_enquiry = view.findViewById(R.id.recycler_view_enquiry);
        fab_add_enquiry = view.findViewById(R.id.fab_add_enquiry);
        iv_empty_list = view.findViewById(R.id.iv_empty_list);
    }

    private void events() {
        edt_search_enq.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enquiryAdapter.getFilter().filter(edt_search_enq.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fab_add_enquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EnquiryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void doEnquiryList() {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();
        Call<SuccessModule> call = apiInterface.enquiryListCall(uId);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModule loginModule = response.body();

                        String message = null, errorCode = null;
                        if (loginModule != null) {
                            message = loginModule.getMsg();
                            errorCode = loginModule.getError_code();

                            if (errorCode.equalsIgnoreCase("1")) {

                                enquiryModuleArrayList = loginModule.getEnquiryModuleArrayList();
                                Collections.reverse(enquiryModuleArrayList);

                                enquiryAdapter = new EnquiryAdapter(mContext, enquiryModuleArrayList);
                                recycler_view_enquiry.setAdapter(enquiryAdapter);
                                enquiryAdapter.notifyDataSetChanged();

                                Toasty.success(mContext, "List Display Successful !!", Toast.LENGTH_SHORT, true).show();

                                if (enquiryModuleArrayList.size() != 0) {
                                    recycler_view_enquiry.setVisibility(View.VISIBLE);
                                    iv_empty_list.setVisibility(View.GONE);
                                } else {
                                    recycler_view_enquiry.setVisibility(View.GONE);
                                    iv_empty_list.setVisibility(View.VISIBLE);
                                }

                            }
                        }
                    } else {
                        Toasty.warning(mContext, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
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
