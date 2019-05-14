package com.shreeyesh.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.shreeyesh.domain.module.LibraryStudentModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.adapter.LibraryRecordStdAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class LibraryRecordStdFragment extends Fragment {

    Context mContext;
    ImageView iv_empty_list;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String isSelectUser, uId, teacherUId, studentUId, uType;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";

    RecyclerView recycler_view_library_list_std;
    ArrayList<LibraryStudentModule> libraryRecordStdModuleArrayList = new ArrayList<>();
    LibraryRecordStdAdapter libraryRecordStdAdapter;

    public LibraryRecordStdFragment() {
        // empty
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library_record_std, container, false);
        mContext = getActivity();

        find_All_Ids(view);

        if (mContext != null) {
            sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        }
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        uType = SharedPreference.getUserType(mContext);

        uId = SharedPreference.getUserID(mContext);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycler_view_library_list_std.setLayoutManager(linearLayoutManager);
        recycler_view_library_list_std.hasFixedSize();

        if (NetworkUtils.isNetworkAvailable(mContext)) {
//            libraryListStd();
        } else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }


        return view;

    }//============= End onCreate () =============

    private void find_All_Ids(View view) {
        recycler_view_library_list_std = view.findViewById(R.id.recycler_view_library_list_std);
        iv_empty_list = view.findViewById(R.id.iv_empty_list);
    }

    private void libraryListStd() {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.libraryListLibrarianCall(uId);

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
                                    libraryRecordStdModuleArrayList = successModule.getLibraryStudentModuleArrayList();
                                    Collections.reverse(libraryRecordStdModuleArrayList);

                                    libraryRecordStdAdapter = new LibraryRecordStdAdapter(mContext, libraryRecordStdModuleArrayList);
                                    recycler_view_library_list_std.setAdapter(libraryRecordStdAdapter);
                                    libraryRecordStdAdapter.notifyDataSetChanged();

                                    if (libraryRecordStdModuleArrayList.size() != 0) {
                                        recycler_view_library_list_std.setVisibility(View.VISIBLE);
                                        iv_empty_list.setVisibility(View.GONE);
                                    } else {
                                        recycler_view_library_list_std.setVisibility(View.GONE);
                                        iv_empty_list.setVisibility(View.VISIBLE);
                                    }

//                                    Toasty.success(mContext, "Event List Display Successfully !!", Toast.LENGTH_SHORT, true).show();

                                } else {
                                    Toasty.warning(mContext, "Not Response !!", Toast.LENGTH_SHORT, true).show();
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
