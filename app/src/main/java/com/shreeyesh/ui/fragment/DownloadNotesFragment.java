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
import com.shreeyesh.domain.module.NotesModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.adapter.NotesStdAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class DownloadNotesFragment extends Fragment {

    Context mContext;
    ImageView iv_empty_list;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, isSelectUser;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";

    NotesStdAdapter notesStdAdapter;
    ArrayList<NotesModule> notesModuleArrayList = new ArrayList<>();
    RecyclerView recycler_view_download_notes;

    public DownloadNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_notes, container, false);
        mContext = getActivity();

        find_All_IDs(view);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        uId = SharedPreference.getUserID(mContext);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycler_view_download_notes.setLayoutManager(linearLayoutManager);
        recycler_view_download_notes.setHasFixedSize(true);

        if (NetworkUtils.isNetworkAvailable(mContext)) {
            downloadNotes();
        } else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }

        return view;

    }//========== End onCreate () ==============


    private void find_All_IDs(View view) {
        recycler_view_download_notes = view.findViewById(R.id.recycler_view_download_notes);
        iv_empty_list = view.findViewById(R.id.iv_empty_list);
    }

    private void downloadNotes() {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.notesListStdCall(uId);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModule notesStdListModule = response.body();

                        String message = null, errorCode = null, url = null;
                        if (notesStdListModule != null) {
                            message = notesStdListModule.getMsg();
                            errorCode = notesStdListModule.getError_code();
                            url = notesStdListModule.getImg_base_url();

                            if (errorCode.equalsIgnoreCase("1")) {

                                notesModuleArrayList = notesStdListModule.getNotesModuleArrayList();
//                                Collections.reverse(notesModuleArrayList);

                                notesStdAdapter = new NotesStdAdapter(mContext,url, notesModuleArrayList);
                                recycler_view_download_notes.setAdapter(notesStdAdapter);
                                notesStdAdapter.notifyDataSetChanged();

                                if (notesModuleArrayList.size() != 0) {
                                    recycler_view_download_notes.setVisibility(View.VISIBLE);
                                    iv_empty_list.setVisibility(View.GONE);
                                } else {
                                    recycler_view_download_notes.setVisibility(View.GONE);
                                    iv_empty_list.setVisibility(View.VISIBLE);
                                }

                                Toasty.success(mContext, "Notes List Display Successfully !!", Toast.LENGTH_SHORT,true).show();
                            }
                        }
                    } else {
                        Toasty.info(mContext, "Service Unavailable !!", Toast.LENGTH_SHORT,true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT,true).show();
            }
        });
    }
}
