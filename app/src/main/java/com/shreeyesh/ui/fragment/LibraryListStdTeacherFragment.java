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
import com.shreeyesh.domain.module.LibraryRecordModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.adapter.LibraryListStdTeacherAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class LibraryListStdTeacherFragment extends Fragment {

    Context mContext;
    ImageView iv_empty_list;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String isSelectUser, uId, uType;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";

    LibraryListStdTeacherAdapter libraryListStdTeacherAdapter;
    ArrayList<LibraryRecordModule> libraryRecordModuleArrayList = new ArrayList<>();
    RecyclerView recycler_view_library_list_std_teacher;


    public LibraryListStdTeacherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library_list_std_teacher, container, false);
        mContext = getActivity();

        find_All_IDs(view);

        if (mContext != null) {
            sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        }
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        uId = SharedPreference.getUserID(mContext);
//        uType = SharedPreference.getUserType(mContext);

        if (isSelectUser.equals("2")) {
            uType = "1";
        } else if (isSelectUser.equals("3")) {
            uType = "2";
        }

        //Set layout to the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycler_view_library_list_std_teacher.setLayoutManager(linearLayoutManager);
        recycler_view_library_list_std_teacher.setHasFixedSize(true);

        if (NetworkUtils.isNetworkAvailable(mContext)) {
//            doLibraryList();
        } else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }

        return view;
    }//========== End onCreate () ==============


    private void find_All_IDs(View view) {
        recycler_view_library_list_std_teacher = view.findViewById(R.id.recycler_view_library_list_std_teacher);
        iv_empty_list = view.findViewById(R.id.iv_empty_list);
    }

    private void doLibraryList() {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.libraryListCall(uId, uType);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModule successModule = response.body();

                        String message = null, errorCode = null;
                        if (successModule != null) {
                            message = successModule.getMsg();
                            errorCode = successModule.getError_code();

                            if (errorCode != null) {
                                if (errorCode.equalsIgnoreCase("1")) {
                                    libraryRecordModuleArrayList = successModule.getLibraryRecordModuleArrayList();
                                    Collections.reverse(libraryRecordModuleArrayList);

                                    libraryListStdTeacherAdapter = new LibraryListStdTeacherAdapter(mContext, libraryRecordModuleArrayList);
                                    recycler_view_library_list_std_teacher.setAdapter(libraryListStdTeacherAdapter);
                                    libraryListStdTeacherAdapter.notifyDataSetChanged();

                                    if (libraryRecordModuleArrayList.size() != 0) {
                                        recycler_view_library_list_std_teacher.setVisibility(View.VISIBLE);
                                        iv_empty_list.setVisibility(View.GONE);
                                    } else {
                                        recycler_view_library_list_std_teacher.setVisibility(View.GONE);
                                        iv_empty_list.setVisibility(View.VISIBLE);
                                    }

//                                    Toasty.success(mContext, "Event List Display Successfully !!", Toast.LENGTH_SHORT, true).show();

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