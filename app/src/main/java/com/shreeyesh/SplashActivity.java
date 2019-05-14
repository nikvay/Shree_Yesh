package com.shreeyesh;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.shreeyesh.domain.module.SplashModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.domain.network.BaseApi;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.HomeActivity;
import com.shreeyesh.ui.activity.SelectUserActivity;
import com.shreeyesh.utils.NetworkUtils;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;

public class SplashActivity extends AppCompatActivity {

    Animation animBounce;
    String loginOrNot,name;
    ImageView iv_splash;
    TextView tv_splash_title, tv_splash_sub_title;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, isSelectUser;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Fast Connect";

    ArrayList<SplashModule> splashModuleArrayList = new ArrayList<>();

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        //=============== hide status bar ===============
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        find_All_IDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        loginOrNot = sharedpreferences.getString(SharedPreference.IS_LOGIN, "");
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

         name = sharedpreferences.getString(SharedPreference.S_NAME, "");
        String nameTwo = sharedpreferences.getString(SharedPreference.S_NAME_TWO, "");
        String image = sharedpreferences.getString(SharedPreference.S_IMG, "");

        if (name != null) {
            if (name.equals("")) {
                dynamicImage();
            } else {

                Picasso.get()
                        .load(image)
                        .error(R.drawable.app_logo)
                        .into(iv_splash);
                tv_splash_title.setText(name);
                animBounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                tv_splash_title.startAnimation(animBounce);
                tv_splash_sub_title.setText(nameTwo);
                setScaleAnimation(tv_splash_sub_title);

//                setScaleAnimationImage(iv_logo);
            }
        }

        if (NetworkUtils.isNetworkAvailable(this))
            dynamicImage();
        else
            NetworkUtils.isNetworkNotAvailable(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CheckPermissions();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent;
                    assert loginOrNot != null;
                    if (loginOrNot.equalsIgnoreCase("true")) {
                        intent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    } else {
                        intent = new Intent(SplashActivity.this, SelectUserActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                    finish();
                }
            }, 3000);
        }
    }

    private void find_All_IDs() {
        iv_splash = findViewById(R.id.iv_splash);
        tv_splash_title = findViewById(R.id.tv_splash_title);
        tv_splash_sub_title = findViewById(R.id.tv_splash_sub_title);
    }

    private void dynamicImage() {
        Call<SuccessModule> call = apiInterface.splashCall(uId);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
//                String str_response = new Gson().toJson(response.body());
//                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModule loginModule = response.body();

                        String message = null, errorCode = null, schoolName = null;
                        if (loginModule != null) {
                            message = loginModule.getMsg();
                            errorCode = loginModule.getError_code();

                            schoolName = loginModule.getSchool_name();
                            editor.putString(SharedPreference.SCHOOL_NAME, schoolName);
                            editor.apply();

                            if (errorCode.equalsIgnoreCase("1")) {

                                splashModuleArrayList = loginModule.getSplashModuleArrayList();

                                for (SplashModule splashModule : splashModuleArrayList) {
                                    String title = splashModule.getTitle();
                                    String subTitle = splashModule.getSubtitle();
                                    String imageUrl = splashModule.getImg_url();
                                    String finalUrl = BaseApi.BASE_URL + imageUrl;

                                    editor.putString(SharedPreference.S_NAME, title);
                                    editor.putString(SharedPreference.S_NAME_TWO, subTitle);
                                    editor.putString(SharedPreference.S_IMG, finalUrl);
                                    editor.apply();

                                    if (name != null && name.equals("")) {
                                        Picasso.get()
                                                .load(finalUrl)
                                                .error(R.drawable.app_logo)
                                                .into(iv_splash);
                                        tv_splash_title.setText(title);
                                        animBounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                                        tv_splash_title.startAnimation(animBounce);
                                        tv_splash_sub_title.setText(subTitle);
                                        setScaleAnimation(tv_splash_sub_title);

                                    }
                                }
                            }
                        }
                    } else {
                        Toasty.info(SplashActivity.this, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(SplashActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void CheckPermissions() {
        RxPermissions.getInstance(SplashActivity.this)
                .request(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        initialize(aBoolean);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }

    private void initialize(Boolean isAppInitialized) {
        if (isAppInitialized) {
            Thread background = new Thread() {
                public void run() {
                    try {
                        sleep(2 * 1500);//4000 (4 Sec)
                        Intent intent;
                        assert loginOrNot != null;
                        if (loginOrNot.equalsIgnoreCase("true")) {
                            intent = new Intent(SplashActivity.this, HomeActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        } else {
                            intent = new Intent(SplashActivity.this, SelectUserActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            background.start();
        } else {
            /* If one Of above permission not grant show alert (force to grant permission)*/
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
            builder.setTitle("Alert");
            builder.setMessage("All permissions necessary");

            builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CheckPermissions();
                }
            });

            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        }
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, Animation.REVERSE, 0.5f, Animation.REVERSE, 0.5f);
        anim.setDuration(300);
        view.animate();
        view.startAnimation(anim);
    }

    private void setScaleAnimationImage(View view) {
       ScaleAnimation anim = new ScaleAnimation(1.0f, 0.1f, 1.0f, 0.1f, Animation.REVERSE, 0.5f, Animation.REVERSE, 0.5f);
       anim.setDuration(300);
       view.animate();
       view.startAnimation(anim);
   }
}