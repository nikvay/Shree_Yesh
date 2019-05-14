package com.shreeyesh.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.HomeActivity;
import com.shreeyesh.utils.Const;
import com.shreeyesh.utils.NetworkUtils;
import com.shreeyesh.utils.SuccessDialog;
import com.shreeyesh.utils.SuccessDialogClosed;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnquiryActivity extends AppCompatActivity implements SuccessDialogClosed {

    private SuccessDialog successDialog;

    ImageView iv_back;
    Spinner spinner_enq_status;
    EditText edt_enq_name, edt_enq_class, edt_enq_number, edt_enq_email, edt_enq_address, edt_enq_remark;
    LinearLayout ll_enq_address, ll_enq_remark;

    String sltStatusName, currentDate;
    Button btn_enq_save;

    ArrayList<String> selectEnquiryStatus = new ArrayList<>();

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, isSelectUser;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Fast Connect";

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry);

        find_All_IDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        uId = SharedPreference.getUserID(EnquiryActivity.this);

        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        spinnerSelect();

        event();

    }//============ End onCreate () =====================

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
        edt_enq_name = findViewById(R.id.edt_enq_name);
        edt_enq_class = findViewById(R.id.edt_enq_class);
        edt_enq_number = findViewById(R.id.edt_enq_number);
        edt_enq_email = findViewById(R.id.edt_enq_email);
        edt_enq_address = findViewById(R.id.edt_enq_address);
        edt_enq_remark = findViewById(R.id.edt_enq_remark);
        spinner_enq_status = findViewById(R.id.spinner_enq_status);
        btn_enq_save = findViewById(R.id.btn_enq_save);

        ll_enq_address = findViewById(R.id.ll_enq_address);
        ll_enq_remark = findViewById(R.id.ll_enq_remark);
    }

    private void event() {

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ll_enq_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_enq_address.requestFocus();
                Const.openKeyBord(EnquiryActivity.this);
            }
        });

        ll_enq_remark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_enq_remark.requestFocus();
                Const.openKeyBord(EnquiryActivity.this);
            }
        });

        btn_enq_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doValidation();
            }
        });
    }


    private void spinnerSelect() {
        selectEnquiryStatus.add(0, "Lead Success");
        selectEnquiryStatus.add(1, "Lead Hold");
        selectEnquiryStatus.add(2, "Lead Cancel");

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, selectEnquiryStatus);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_enq_status.setAdapter(aa);

        spinner_enq_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sltStatusName = parent.getItemAtPosition(position).toString();
                String sltStatusId = String.valueOf(parent.getItemIdAtPosition(position));
                String s = spinner_enq_status.getSelectedItem().toString();

//                Toast.makeText(LeaveDetailsActivity.this, "" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void doValidation() {

        String name = edt_enq_name.getText().toString().trim();
        String className = edt_enq_class.getText().toString().trim();
        String number = edt_enq_number.getText().toString().trim();
        String email = edt_enq_email.getText().toString().trim();
        String address = edt_enq_address.getText().toString().trim();
        String remark = edt_enq_remark.getText().toString().trim();

        if (name.equalsIgnoreCase("")) {
            edt_enq_name.setError("Please Enter Name");
            edt_enq_name.requestFocus();
        } else if (className.equalsIgnoreCase("")) {
            edt_enq_class.setError("Please Enter Class Name");
            edt_enq_class.requestFocus();
        } else if (number.equalsIgnoreCase("")) {
            edt_enq_number.setError("Please Enter 10 Digit Number");
            edt_enq_number.requestFocus();
        } else if (number.length() < 10) {
            edt_enq_number.setError("Number Is Too Short !! ");
            edt_enq_number.requestFocus();
        }/* else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edt_enq_email.setError("Invalid Email !!");
            edt_enq_email.requestFocus();
        }*/ else if (address.equalsIgnoreCase("")) {
            edt_enq_address.setError("Please Enter Address");
            edt_enq_address.requestFocus();
        } else if (remark.equalsIgnoreCase("")) {
            edt_enq_remark.setError("Please Enter Remark");
            edt_enq_remark.requestFocus();
        } else {
            if (NetworkUtils.isNetworkAvailable(this)) {
                doEnquiry(name, className, number, email, address, remark);
            } else
                NetworkUtils.isNetworkNotAvailable(this);
        }
    }

    private void doEnquiry(String name, String className, String number, String email, String address, String remark) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.enquiryAddCall(name, className, number, email, address, sltStatusName, remark, currentDate, uId);

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

                                successDialog = new SuccessDialog(EnquiryActivity.this, true);
                                successDialog.showDialog("Enquiry Added Successful !!", true);
                            }
                        } else {
                            Toasty.warning(EnquiryActivity.this, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(EnquiryActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }

    @Override
    public void dialogClosed(boolean mClosed) {
        editor.putString(SharedPreference.REFRESH, "9");
        editor.apply();
        Intent intent = new Intent(EnquiryActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }

}
