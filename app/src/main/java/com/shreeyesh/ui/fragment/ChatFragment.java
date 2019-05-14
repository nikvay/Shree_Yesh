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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.ChatGroupListModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.adapter.ChatGroupListAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ChatFragment extends Fragment {

    Context mContext;
    Button btn_enq_add;
    ImageView iv_empty_list;
    EditText edt_search_enq;

    String type, class_id, division_id;

    ChatGroupListAdapter chatGroupListAdapter;
    ArrayList<ChatGroupListModule> chatGroupListModuleArrayList = new ArrayList<>();
    RecyclerView recycler_view_chat_grp_list;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String isSelectUser, uId, teacherUId, studentUId, uType;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";


    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        mContext = getActivity();

        find_All_IDs(view);

        if (mContext != null) {
            sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        }
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        class_id = sharedpreferences.getString(SharedPreference.CLASS_ID, "");
        division_id = sharedpreferences.getString(SharedPreference.DIVISION_ID, "");
        uType = SharedPreference.getUserType(mContext);

        uId = SharedPreference.getUserID(mContext);

        if (isSelectUser.equals("1")) {
            type = "1";
            class_id = "";
            division_id = "";
        } else if (isSelectUser.equals("2")) {
            type = "2";
            class_id = "";
            division_id = "";
        } else if (isSelectUser.equals("3")) {
            type = "3";
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycler_view_chat_grp_list.setLayoutManager(linearLayoutManager);
        recycler_view_chat_grp_list.setHasFixedSize(true);

        if (NetworkUtils.isNetworkAvailable(mContext)) {
            doGrpList();
        } else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }

        return view;
    }//========== End onCreate () ==============


    private void find_All_IDs(View view) {
        recycler_view_chat_grp_list = view.findViewById(R.id.recycler_view_chat_grp_list);
    }

    private void doGrpList() {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();
        Call<SuccessModule> call = apiInterface.grpListCall(uId, type, class_id, division_id);

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

                                chatGroupListModuleArrayList = loginModule.getChatGroupListModuleArrayList();
//                                Collections.reverse(chatGroupListModuleArrayList);

                                chatGroupListAdapter = new ChatGroupListAdapter(mContext, chatGroupListModuleArrayList);
                                recycler_view_chat_grp_list.setAdapter(chatGroupListAdapter);
                                chatGroupListAdapter.notifyDataSetChanged();

                                if (chatGroupListModuleArrayList.size() != 0) {
                                    recycler_view_chat_grp_list.setVisibility(View.VISIBLE);
                                    iv_empty_list.setVisibility(View.GONE);
                                } else {
                                    recycler_view_chat_grp_list.setVisibility(View.GONE);
                                    iv_empty_list.setVisibility(View.VISIBLE);
                                }

//                                Toasty.success(mContext, "Gallery List Successfully !!", Toast.LENGTH_SHORT,true).show();

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
