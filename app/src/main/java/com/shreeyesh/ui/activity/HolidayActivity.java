package com.shreeyesh.ui.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.HolidayListModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.domain.network.BaseApi;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HolidayActivity extends AppCompatActivity {

    ImageView iv_back;
    WebView web_view_holiday;
    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, isSelectUser, timeTable_notes;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";

    ArrayList<HolidayListModule> holidayListModuleArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday);

        find_All_IDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        uId = SharedPreference.getUserID(this);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        webViewDisplay();

        //============ Api Call ================
        if (NetworkUtils.isNetworkAvailable(this)) {
            holidayCall();
        }else
            NetworkUtils.isNetworkNotAvailable(this);

    }//============ End onCreate() ===========

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
        web_view_holiday = findViewById(R.id.web_view_holiday);
    }

    private void webViewDisplay() {
        web_view_holiday.getSettings().setJavaScriptEnabled(true);
        web_view_holiday.getSettings().setBuiltInZoomControls(true);
        web_view_holiday.setVerticalScrollBarEnabled(true);
        web_view_holiday.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        web_view_holiday.setInitialScale(100);
    }

    private void loadPdfUrlStd(String url) {

        final String pdfUrl = url;

        web_view_holiday.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String pdfUrl) {

                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        web_view_holiday.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + pdfUrl);
//        web_view_time_table.loadUrl(pdfUrl);

    }

    private void holidayCall() {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();
        Call<SuccessModule> call = apiInterface.holidayListCall(uId);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModule loginModule = response.body();

                        String message = null, errorCode = null, url = null;
                        if (loginModule != null) {
                            message = loginModule.getMsg();
                            errorCode = loginModule.getError_code();
                            url = loginModule.getImg_base_url();

                            if (errorCode.equalsIgnoreCase("1")) {

                                holidayListModuleArrayList = loginModule.getHolidayListModuleArrayList();
                                String documentName = "";
                                for (HolidayListModule timeTableModule : holidayListModuleArrayList) {
                                    documentName = timeTableModule.getPdf_name();
                                }
                                /*myPdfUrl = "http://www.bits-dubai.ac.ae/Admission/Downloads/Fee_structure.pdf";
                                loadPdfUrl(myPdfUrl);*/

                                String documentUrl = BaseApi.BASE_URL + url + documentName;
                                loadPdfUrlStd(documentUrl);

//                                Toast.makeText(HolidayActivity.this, "Successful !!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toasty.info(HolidayActivity.this, "Service Unavailable", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(HolidayActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
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
