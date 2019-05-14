package com.shreeyesh.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsActivity extends AppCompatActivity {

    TextView txt_event_name_details, txt_event_date_time_details, txt_event_location_details, txt_event_desc_details;
    ImageView iv_back, iv_delete_event, iv_event_image;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String isSelectUser, uId;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Fast Connect";

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        find_All_IDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");

        if (isSelectUser.equalsIgnoreCase("1")) {
            iv_delete_event.setVisibility(View.VISIBLE);
        }

        Intent intent = getIntent();
        final String eventId = intent.getStringExtra("EVENT_ID");
        String eventName = intent.getStringExtra("EVENT_NAME");
        String eventDate = intent.getStringExtra("EVENT_DATE");
        String eventLocation = intent.getStringExtra("EVENT_LOCATION");
        String eventImage = intent.getStringExtra("EVENT_IMAGE");
        String eventDesc = intent.getStringExtra("EVENT_DESC");

        txt_event_name_details.setText(eventName);
        txt_event_date_time_details.setText(eventDate);
        txt_event_location_details.setText(eventLocation);
        txt_event_desc_details.setText(eventDesc);

        Picasso.get()
                .load(eventImage)
                .into(iv_event_image);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_delete_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkAvailable(EventDetailsActivity.this))
                    deleteEvent(eventId);
                else
                    NetworkUtils.isNetworkNotAvailable(EventDetailsActivity.this);
            }
        });
    }

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
        iv_delete_event = findViewById(R.id.iv_delete_event);
        txt_event_name_details = findViewById(R.id.txt_event_name_details);
        txt_event_date_time_details = findViewById(R.id.txt_event_date_time_details);
        txt_event_location_details = findViewById(R.id.txt_event_location_details);
        txt_event_desc_details = findViewById(R.id.txt_event_desc_details);
        iv_event_image = findViewById(R.id.iv_event_image);
    }

    private void deleteEvent(String eventId) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.evenDeleteCall(eventId);

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

                                Toasty.success(EventDetailsActivity.this, "Event Delete Successfully !!", Toast.LENGTH_SHORT,true).show();

                                editor.putString(SharedPreference.REFRESH, "2");
                                editor.apply();
                                Intent intent = new Intent(EventDetailsActivity.this, HomeActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                finish();

                            } else {
                                Toasty.info(EventDetailsActivity.this, "Not Response !!", Toast.LENGTH_SHORT,true).show();
                            }
                        }

                    } else {
                        Toasty.info(EventDetailsActivity.this, "Service Unavailable !!", Toast.LENGTH_SHORT,true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(EventDetailsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT,true).show();
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
