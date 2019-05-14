package com.shreeyesh.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.LoginModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.HomeActivity;
import com.shreeyesh.utils.NetworkUtils;
import com.shreeyesh.utils.SuccessDialog;
import com.shreeyesh.utils.SuccessDialogClosed;
import com.google.gson.Gson;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements SuccessDialogClosed {

    private SuccessDialog successDialog;

    TextView tv_action_bar_title;
    ImageView iv_back;
    Button btn_login;
    EditText edt_user_id, edt_password;
    ArrayList<LoginModule> loginModuleArrayList = new ArrayList<>();

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String token, isSelectUser;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        token = SharedPreference.getDeviceToken(LoginActivity.this);

        find_All_IDs();

        if (isSelectUser.equalsIgnoreCase("1")) {
            tv_action_bar_title.setText("Admin Login");
        } else if (isSelectUser.equalsIgnoreCase("2")) {
            tv_action_bar_title.setText("Teacher Login");
        } else if (isSelectUser.equalsIgnoreCase("3")) {
            tv_action_bar_title.setText("Student Login");
        } else if (isSelectUser.equalsIgnoreCase("4")) {
            tv_action_bar_title.setText("Librarian Login");
        } else if (isSelectUser.equalsIgnoreCase("5")) {
            tv_action_bar_title.setText("Driver Login");
        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doValidation();
            }
        });

    }//================== End onCreate() =============================

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
        tv_action_bar_title = findViewById(R.id.tv_action_bar_title);
        edt_user_id = findViewById(R.id.edt_user_id);
        edt_password = findViewById(R.id.edt_password);
        btn_login = findViewById(R.id.btn_login);
    }

    private void doValidation() {
        String userName = edt_user_id.getText().toString().trim();
        String password = edt_password.getText().toString().trim();

        if (userName.equalsIgnoreCase("")) {
            edt_user_id.setError("Please Enter User Name");
            edt_user_id.requestFocus();
        } else if (userName.length() < 4) {
            edt_user_id.setError("User Name Is Too Short !! ");
            edt_user_id.requestFocus();
        } else if (userName.length() > 15) {
            edt_user_id.setError("User Name Is Too Long !! ");
            edt_user_id.requestFocus();
        } else if (password.equalsIgnoreCase("")) {
            edt_password.setError("Please Enter Password");
            edt_password.requestFocus();
        } else if (password.length() < 4) {
            edt_password.setError("Password Is Too Short !! ");
            edt_password.requestFocus();
        } else if (password.length() > 15) {
            edt_password.setError("Password Is Too Long !! ");
            edt_password.requestFocus();
        } else {
            if (NetworkUtils.isNetworkAvailable(this))
                doLogin(isSelectUser, userName, password, token);
            else
                NetworkUtils.isNetworkNotAvailable(this);
        }
    }

    private void doLogin(String isSelectUser, String userName, final String password, String token) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.loginCall(isSelectUser, userName, password, token);

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


                            if (errorCode.equalsIgnoreCase("0")) {
                                Toasty.error(LoginActivity.this, "User not Register !!", Toast.LENGTH_SHORT, true).show();

                            } else if (errorCode.equalsIgnoreCase("1")) {
                                loginModuleArrayList = loginModule.getUser_details();

                                for (LoginModule loginModule1 : loginModuleArrayList) {
                                    String userId = loginModule1.getUser_id();
                                    String fullName = loginModule1.getFull_name();
                                    String userName = loginModule1.getUser_name();
                                    String userEmail = loginModule1.getEmail_id();
                                    String userType = loginModule1.getType();
                                    String className = loginModule1.getClass_name();
                                    String divisionName = loginModule1.getDivision_name();
                                    String divisionId = loginModule1.getDivision_id();
                                    String classId = loginModule1.getClass_id();
                                    String contactNumber = loginModule1.getContact_number1();

                                    String classDiv = className + " " + divisionName;

                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(SharedPreference.IS_LOGIN, "true");
                                    editor.putString(SharedPreference.USER_FULL_NAME, fullName);
                                    editor.putString(SharedPreference.USER_NAME, userName);
                                    editor.putString(SharedPreference.USER_PASSWORD, password);
                                    editor.putString(SharedPreference.USER_EMAIL, userEmail);
                                    editor.putString(SharedPreference.USER_CONTACT, contactNumber);
                                    editor.putString(SharedPreference.CLASS_DIV, classDiv);
                                    editor.putString(SharedPreference.CLASS_ID, classId);
                                    editor.putString(SharedPreference.DIVISION_ID, divisionId);
                                    editor.apply();

                                    SharedPreference.putUserID(LoginActivity.this, userId);
                                    SharedPreference.putUserType(LoginActivity.this, userType);

                                    successDialog = new SuccessDialog(LoginActivity.this, true);
                                    successDialog.showDialog("Login Successfully !!", true);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(LoginActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
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
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        finish();
    }
}
