package com.shreeyesh.ui.activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.HomeActivity;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeWorkDetailsActivity extends AppCompatActivity {

    ImageView iv_back, iv_delete_home_work, iv_homeWork, iv_image_download;
    TextView txt_subject_name_homeWork, txt_homeWork_desc, txt_homeWork_class, txt_homeWork_sdate, txt_homeWork_gdate;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, isSelectUser;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Fast Connect";

    String id = "", subject = "", imageUrl = "", desc = "", gDate = "", sDate = "";
    String currentDateTime;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_work_details);

        find_All_IDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        uId = SharedPreference.getUserID(HomeWorkDetailsActivity.this);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        currentDateTime = sdf.format(new Date());

        // name of image
        @SuppressLint("DefaultLocale") final String fileName = String.format("%d", System.currentTimeMillis());

        if (isSelectUser.equalsIgnoreCase("2")) {
            Intent intent = getIntent();

            id = intent.getStringExtra("ID");
            subject = intent.getStringExtra("TITLE");
            imageUrl = intent.getStringExtra("IMAGE");
            desc = intent.getStringExtra("DESC");
            gDate = intent.getStringExtra("G_DATE");
            sDate = intent.getStringExtra("S_DATE");
        }
        if (isSelectUser.equalsIgnoreCase("3")) {

            iv_delete_home_work.setVisibility(View.GONE);
            Intent intent = getIntent();

            subject = intent.getStringExtra("TITLE");
            imageUrl = intent.getStringExtra("IMAGE");
            desc = intent.getStringExtra("DESC");
            gDate = intent.getStringExtra("G_DATE");
            sDate = intent.getStringExtra("S_DATE");
        }


        txt_subject_name_homeWork.setText(subject);

        Picasso.get()
                .load(imageUrl)
                .error(R.drawable.app_image_not_found)
                .into(iv_homeWork);

        txt_homeWork_desc.setText(desc);
        txt_homeWork_gdate.setText(gDate);
        txt_homeWork_sdate.setText(sDate);

        event();
    }// ============== End onCreate () ================

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
        iv_delete_home_work = findViewById(R.id.iv_delete_home_work);
        iv_image_download = findViewById(R.id.iv_image_download);
        txt_subject_name_homeWork = findViewById(R.id.txt_subject_name_homeWork);
        iv_homeWork = findViewById(R.id.iv_homeWork);
        txt_homeWork_desc = findViewById(R.id.txt_homeWork_desc);
        txt_homeWork_gdate = findViewById(R.id.txt_homeWork_gdate);
        txt_homeWork_sdate = findViewById(R.id.txt_homeWork_sdate);
    }

    private void event() {

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_image_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(imageUrl);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, currentDateTime + ".png");
                Toast.makeText(HomeWorkDetailsActivity.this, "Start downloading...", Toast.LENGTH_SHORT).show();
                Long reference = downloadManager.enqueue(request);
            }
        });

        iv_delete_home_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDelete();
            }
        });
    }

    private void doDelete() {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.deleteHomeWorkCall(id, uId);

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

                                Toasty.success(HomeWorkDetailsActivity.this, "HomeWork Delete Successfully !!", Toast.LENGTH_SHORT, true).show();
                                editor.putString(SharedPreference.REFRESH, "4");
                                editor.apply();
                                Intent intent = new Intent(HomeWorkDetailsActivity.this, HomeActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                finish();

                            } else {
                                Toasty.info(HomeWorkDetailsActivity.this, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    } else {
                        Toasty.info(HomeWorkDetailsActivity.this, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(HomeWorkDetailsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
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
