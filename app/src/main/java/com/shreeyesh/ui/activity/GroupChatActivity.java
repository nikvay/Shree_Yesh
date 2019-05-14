package com.shreeyesh.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.ChattingModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.adapter.ChattingAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupChatActivity extends AppCompatActivity {

    ImageView iv_back, iv_empty_list, iv_btn_hold_message_chat, iv_btn_send_message_chat;
    EditText edt_chatting;
    TextView tv_action_bar_title;
    FloatingActionButton fab_chatting;

    ChattingAdapter chattingAdapter;
    ArrayList<ChattingModule> chattingModuleArrayList = new ArrayList<>();
    RecyclerView recycler_view_chat_details;

    String grpId, type, class_id, division_id;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String isSelectUser, uId, teacherUId, studentUId, uType;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        find_All_IDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        class_id = sharedpreferences.getString(SharedPreference.CLASS_ID, "");
        division_id = sharedpreferences.getString(SharedPreference.DIVISION_ID, "");

        uType = SharedPreference.getUserType(this);

        uId = SharedPreference.getUserID(this);

        Intent intent = getIntent();
        grpId = intent.getStringExtra("GRP_ID");
        String grpName = intent.getStringExtra("GRP_NAME");
        tv_action_bar_title.setText(grpName);

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view_chat_details.setLayoutManager(linearLayoutManager);

        if (NetworkUtils.isNetworkAvailable(GroupChatActivity.this))
            chattingList();
        else
            NetworkUtils.isNetworkNotAvailable(GroupChatActivity.this);

        events();

    }//=============== End onCreate () =============

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
        iv_empty_list = findViewById(R.id.iv_empty_list);
        recycler_view_chat_details = findViewById(R.id.recycler_view_chat_details);
        fab_chatting = findViewById(R.id.fab_chatting);

        tv_action_bar_title = findViewById(R.id.tv_action_bar_title);
        edt_chatting = findViewById(R.id.edt_chatting);
        iv_btn_hold_message_chat = findViewById(R.id.iv_btn_hold_message_chat);
        iv_btn_send_message_chat = findViewById(R.id.iv_btn_send_message_chat);
    }

    private void events() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        iv_btn_send_message_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doValidation();
            }
        });

        // ==================== Timer for Repeat Call ==========================
       /* new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // =========== Api Call ===========
                if (NetworkUtils.isNetworkAvailable(GroupChatActivity.this))
                    chattingList();
                else
                    NetworkUtils.isNetworkNotAvailable(GroupChatActivity.this);

            }
        }, 0, 3000);*/

        recycler_view_chat_details.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab_chatting.getVisibility() == View.VISIBLE) {
                    fab_chatting.hide();
                } else if (dy < 0 && fab_chatting.getVisibility() != View.VISIBLE) {
                    fab_chatting.show();
                }
            }
        });

    }


    private void chattingList() {
        Call<SuccessModule> call = apiInterface.chattingListCall(grpId);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
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

                                chattingModuleArrayList = loginModule.getChattingModuleArrayList();
                                Collections.reverse(chattingModuleArrayList);

                                chattingAdapter = new ChattingAdapter(GroupChatActivity.this, chattingModuleArrayList);
                                recycler_view_chat_details.setAdapter(chattingAdapter);
                                chattingAdapter.notifyDataSetChanged();
                                recycler_view_chat_details.setHasFixedSize(true);
                                recycler_view_chat_details.scrollToPosition(recycler_view_chat_details.getAdapter().getItemCount() - 1);

                                if (chattingModuleArrayList.size() != 0) {
                                    recycler_view_chat_details.setVisibility(View.VISIBLE);
                                    iv_empty_list.setVisibility(View.GONE);
                                } else {
                                    recycler_view_chat_details.setVisibility(View.GONE);
                                    iv_empty_list.setVisibility(View.VISIBLE);
                                }

//                                Toasty.success(mContext, "Gallery List Successfully !!", Toast.LENGTH_SHORT,true).show();

                            }
                        }
                    } else {
                        Toasty.warning(GroupChatActivity.this, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(GroupChatActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }


    private void doValidation() {
        String chatMessage = edt_chatting.getText().toString().trim();

        if (chatMessage.equals("")) {
            Toasty.warning(this, "You can't send empty message", Toast.LENGTH_SHORT, true).show();
        } else {
            if (NetworkUtils.isNetworkAvailable(GroupChatActivity.this))
                sendMessage(chatMessage);
            else
                NetworkUtils.isNetworkNotAvailable(GroupChatActivity.this);
        }
    }

    private void sendMessage(String chatMessage) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.sendChatMessageCall(uId, type, chatMessage, grpId);

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

                            if (errorCode.equalsIgnoreCase("1")) {

                                Toasty.success(GroupChatActivity.this, "Send Message Successful !!", Toast.LENGTH_SHORT, true).show();
                                edt_chatting.setText("");
                                // =========== Api Call ===========
                                if (NetworkUtils.isNetworkAvailable(GroupChatActivity.this))
                                    chattingList();
                                else
                                    NetworkUtils.isNetworkNotAvailable(GroupChatActivity.this);
                            } else {
                                Toasty.warning(GroupChatActivity.this, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
                            }
                        } else {
                            Toasty.warning(GroupChatActivity.this, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(GroupChatActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fab_chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                recycler_view_chat_details.smoothScrollToPosition(0);
                try {
                    recycler_view_chat_details.smoothScrollToPosition(chattingModuleArrayList.size() - 1);
                } catch (Exception e) {
                    fab_chatting.hide();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }

}
