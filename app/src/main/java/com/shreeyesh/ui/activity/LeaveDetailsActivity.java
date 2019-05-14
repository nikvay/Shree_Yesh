package com.shreeyesh.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveDetailsActivity extends AppCompatActivity implements SuccessDialogClosed {

    private SuccessDialog successDialog;

    ImageView iv_back;
    Button btn_std_leave_approval;
    TextView tv_leave_name, tv_leave_subject, tv_leave_class, tv_leave_div, tv_leave_sDate, tv_leave_eDate,
            tv_leave_desc, tv_leave_remark, tv_leave_status;
    EditText edt_std_leave_remark_a;

    Spinner spinner_std_leave_a;
    LinearLayout ll_std_leave_remark_a, ll_leave_hide_t, ll_leave_remark;
    String sltStatus, sltStatusId, uId, id, status, comment, stdStatus = "1";
    boolean buttonClick=false;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String token, isSelectUser;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Fast Connect";

    ArrayList<String> selectLeaveStatus = new ArrayList<>();

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_details);

        find_All_IDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        uId = SharedPreference.getUserID(LeaveDetailsActivity.this);

        if (isSelectUser != null && isSelectUser.equalsIgnoreCase("2")) {
            ll_leave_hide_t.setVisibility(View.VISIBLE);
        } else if (isSelectUser != null && isSelectUser.equalsIgnoreCase("3")) {
            ll_leave_hide_t.setVisibility(View.GONE);
        }

        details();

        if (status.equalsIgnoreCase("0")) {
            tv_leave_status.setTextColor(Color.RED);
            //tv_leave_status.setTextColor(Color.parseColor("#FFA500"));
            tv_leave_status.setText("Cancel");
            ll_leave_remark.setVisibility(View.VISIBLE);
            ll_leave_hide_t.setVisibility(View.GONE);

        } else if (status.equalsIgnoreCase("1")) {
//            tv_leave_status.setTextColor(Color.YELLOW);
            tv_leave_status.setTextColor(Color.parseColor("#FFA500"));
            tv_leave_status.setText("Pending");
            ll_leave_remark.setVisibility(View.GONE);

        } else if (status.equalsIgnoreCase("2")) {
            tv_leave_status.setTextColor(Color.GREEN);
            tv_leave_status.setText("Approve");
            ll_leave_remark.setVisibility(View.VISIBLE);
            ll_leave_hide_t.setVisibility(View.GONE);
        }

        spinnerSelect();

        event();

    }//=========== End onCreate () ================

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
        tv_leave_status = findViewById(R.id.tv_leave_status);
        tv_leave_name = findViewById(R.id.tv_leave_name);
        tv_leave_subject = findViewById(R.id.tv_leave_subject);
        tv_leave_class = findViewById(R.id.tv_leave_class);
        tv_leave_div = findViewById(R.id.tv_leave_div);
        tv_leave_sDate = findViewById(R.id.tv_leave_sDate);
        tv_leave_eDate = findViewById(R.id.tv_leave_eDate);
        tv_leave_desc = findViewById(R.id.tv_leave_desc);
        tv_leave_remark = findViewById(R.id.tv_leave_remark);
        ll_leave_remark = findViewById(R.id.ll_leave_remark);
        spinner_std_leave_a = findViewById(R.id.spinner_std_leave_a);
        ll_std_leave_remark_a = findViewById(R.id.ll_std_leave_remark_a);
        edt_std_leave_remark_a = findViewById(R.id.edt_std_leave_remark_a);
        btn_std_leave_approval = findViewById(R.id.btn_std_leave_approval);

        ll_leave_hide_t = findViewById(R.id.ll_leave_hide_t);
    }

    private void details() {
        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        String name = intent.getStringExtra("NAME");
        status = intent.getStringExtra("STATUS");
        String subject = intent.getStringExtra("SUBJECT");
        String sDate = intent.getStringExtra("S_DATE");
        String eDate = intent.getStringExtra("E_DATE");
        String className = intent.getStringExtra("CLASS_NAME");
        String div = intent.getStringExtra("DIV");
        String cDate = intent.getStringExtra("C_DATE");
        String desc = intent.getStringExtra("DESC");
        comment = intent.getStringExtra("COMMENT");

        tv_leave_status.setText(name);
        tv_leave_name.setText(name);
        tv_leave_subject.setText(subject);
        tv_leave_class.setText(className);
        tv_leave_div.setText(div);
        tv_leave_sDate.setText(sDate);
        tv_leave_eDate.setText(eDate);
        tv_leave_desc.setText(desc);
        tv_leave_remark.setText(comment);
    }

    private void event() {

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ll_std_leave_remark_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_std_leave_remark_a.requestFocus();
                Const.openKeyBord(LeaveDetailsActivity.this);
            }
        });

        btn_std_leave_approval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (buttonClick) {
                    btn_std_leave_approval.setClickable(false);
                } else {
                    doValidation();
                }
            }
        });
    }

    private void spinnerSelect() {
        selectLeaveStatus.add(0, "Pending");
        selectLeaveStatus.add(1, "Approval");
        selectLeaveStatus.add(2, "Cancel");

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, selectLeaveStatus);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_std_leave_a.setAdapter(aa);

        spinner_std_leave_a.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sltStatus = parent.getItemAtPosition(position).toString();
                sltStatusId = String.valueOf(parent.getItemIdAtPosition(position));
                String s = spinner_std_leave_a.getSelectedItem().toString();

                if (sltStatus.equalsIgnoreCase("Cancel")) {
                    stdStatus = "0";
                } else if (sltStatus.equalsIgnoreCase("Pending")) {
                    stdStatus = "1";
                } else if (sltStatus.equalsIgnoreCase("Approval")) {
                    stdStatus = "2";
                }
//                Toast.makeText(LeaveDetailsActivity.this, "" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void doValidation() {
        String reMark = edt_std_leave_remark_a.getText().toString();
        if (reMark.equalsIgnoreCase("")) {
            Toasty.warning(this, "Please Fill Up Remark", Toast.LENGTH_SHORT, true).show();
        } else {
            if (NetworkUtils.isNetworkAvailable(this)) {
                buttonClick = true;
                leaveApproval(reMark);
            }else
                NetworkUtils.isNetworkNotAvailable(this);
        }
    }

    private void leaveApproval(String reMark) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.leaveApproveCall(id, uId, stdStatus, reMark);

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

                                successDialog = new SuccessDialog(LeaveDetailsActivity.this, true);
                                successDialog.showDialog("Leave FeedBack !!", true);

//                                onBackPressed();
//                                Toasty.success(LeaveDetailsActivity.this, "Leave Approval Successfully !!", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    } else {
                        Toasty.info(LeaveDetailsActivity.this, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(LeaveDetailsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
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
        editor.putString(SharedPreference.REFRESH, "6");
        editor.apply();
        Intent intent = new Intent(LeaveDetailsActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }
}
