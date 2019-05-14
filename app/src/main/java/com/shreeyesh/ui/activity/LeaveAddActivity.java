package com.shreeyesh.ui.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveAddActivity extends AppCompatActivity implements SuccessDialogClosed {

    private SuccessDialog successDialog;

    ImageView iv_back;
    Button btn_std_leave;
    LinearLayout ll_std_leave;
    TextView edt_std_leave_subject, edt_std_leave_desc, edt_std_leave_sDate, edt_std_leave_eDate;

    private int mYear, mMonth, mDay; // datePicker
    String currentDate;
    boolean buttonClick = false;

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
        setContentView(R.layout.activity_leave_add);

        findAllIDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        uId = SharedPreference.getUserID(LeaveAddActivity.this);

        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        edt_std_leave_sDate.setText(currentDate);
        edt_std_leave_eDate.setText(currentDate);

        event();

    }// ============= End onCreate () ==================

    private void event() {

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edt_std_leave_sDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(LeaveAddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
//                                currentDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
//                                edt_std_leave_sDate.setText(currentDate);

                                CharSequence strDate = null;
                                Time chosenDate = new Time();
                                chosenDate.set(dayOfMonth, monthOfYear, year);

                                long dateAttendance = chosenDate.toMillis(true);
                                strDate = DateFormat.format("dd-MM-yyyy", dateAttendance);

                                edt_std_leave_sDate.setText(strDate);
                                currentDate = String.valueOf(strDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        edt_std_leave_eDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(LeaveAddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
//                                currentDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
//                                edt_std_leave_eDate.setText(currentDate);
                                CharSequence strDate = null;
                                Time chosenDate = new Time();
                                chosenDate.set(dayOfMonth, monthOfYear, year);

                                long dateAttendance = chosenDate.toMillis(true);
                                strDate = DateFormat.format("dd-MM-yyyy", dateAttendance);

                                edt_std_leave_eDate.setText(strDate);
                                currentDate = String.valueOf(strDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        ll_std_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_std_leave.requestFocus();
                Const.openKeyBord(LeaveAddActivity.this);
            }
        });

        btn_std_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClick) {
                    btn_std_leave.setClickable(false);
                } else {
                    validation();
                }
            }
        });
    }

    private void findAllIDs() {
        iv_back = findViewById(R.id.iv_back);
        edt_std_leave_subject = findViewById(R.id.edt_std_leave_subject);
        edt_std_leave_desc = findViewById(R.id.edt_std_leave_desc);
        ll_std_leave = findViewById(R.id.ll_std_leave);
        edt_std_leave_sDate = findViewById(R.id.edt_std_leave_sDate);
        edt_std_leave_eDate = findViewById(R.id.edt_std_leave_eDate);
        btn_std_leave = findViewById(R.id.btn_std_leave);
    }

    private void validation() {
        String subject = edt_std_leave_subject.getText().toString().trim();
        String desc = edt_std_leave_desc.getText().toString().trim();
        String sDate = edt_std_leave_sDate.getText().toString().trim();
        String eDate = edt_std_leave_eDate.getText().toString().trim();

        if (subject.equalsIgnoreCase("")) {
            edt_std_leave_subject.setError("Please Enter Subject");
            edt_std_leave_subject.requestFocus();
        } else if (desc.equalsIgnoreCase("")) {
            edt_std_leave_desc.setError("Please Enter Description");
            edt_std_leave_desc.requestFocus();
        } else if (sDate.equalsIgnoreCase("")) {
            Toasty.warning(this, "Please Fill Leave Date", Toast.LENGTH_SHORT, true).show();
        } else if (eDate.equalsIgnoreCase("")) {
            Toasty.warning(this, "Please Fill Leave Date", Toast.LENGTH_SHORT, true).show();
        } else {
            //============ Api Call ================
            if (NetworkUtils.isNetworkAvailable(this)) {
                leaveCall(subject, desc, sDate, eDate);
                buttonClick = true;
            }else
                NetworkUtils.isNetworkNotAvailable(this);
        }
    }

    private void leaveCall(String subject, String desc, String sDate, String eDate) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.studentApplyLeaveCall(uId, subject, desc, sDate, eDate);

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

                                successDialog = new SuccessDialog(LeaveAddActivity.this, true);
                                successDialog.showDialog("Leave Apply Successful !!", true);

//                                Toasty.success(LeaveAddActivity.this, "Leave Apply Successful !!", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    } else {
                        Toasty.info(LeaveAddActivity.this, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(LeaveAddActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
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
        editor.putString(SharedPreference.REFRESH, "5");
        editor.apply();
        Intent intent = new Intent(LeaveAddActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }
}
