package com.shreeyesh.ui.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.ClassDivisionModule;
import com.shreeyesh.domain.module.ClassListModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.HomeActivity;
import com.shreeyesh.ui.adapter.HomeWorkDivisionAdapter;
import com.shreeyesh.utils.Const;
import com.shreeyesh.utils.NetworkUtils;
import com.shreeyesh.utils.SuccessDialog;
import com.shreeyesh.utils.SuccessDialogClosed;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_LONG;

public class HomeWorkActivity extends AppCompatActivity implements SuccessDialogClosed {

    // ==== dialog ======
    private SuccessDialog successDialog;

    ImageView iv_back, iv_add_homeWork, iv_add_homeWork_hide;
    Button btn_add_class_homeWork;
    Spinner spinner_standard;
    EditText edt_subject_title, edt_homework_desc, edt_given_date, edt_submit_date;
    RelativeLayout rel_add_image;
    LinearLayout ll_homework_desc;
    JSONArray jsonArray;

    // =========== Upload image ================
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_GALLERY_REQUEST_CODE = 2;
    Bitmap bitmap;
    Uri fileUri;
    Uri imageUrl;
    String photo;
    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, uType;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Fast Connect";
    //========= class list module ===========
    ArrayList<ClassListModule> classListModuleArrayArrayList = new ArrayList<>();
    ArrayList<String> selectClassList = new ArrayList<>();
    String isSelectUser;

    String classId, divId, sltClass = "", sCount = "";
    boolean buttonClick = false;

    //============= class division list ===============
    HomeWorkDivisionAdapter homeWorkDivisionAdapter;
    ArrayList<ClassDivisionModule> classDivisionModuleArrayList = new ArrayList<>();
    RecyclerView recycler_view_homework_class;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_work);

        find_All_Ids();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        uId = SharedPreference.getUserID(HomeWorkActivity.this);
        uType = SharedPreference.getUserType(HomeWorkActivity.this);


        //===== Api Call =======
        if (NetworkUtils.isNetworkAvailable(this))
            doClassList();
        else
            NetworkUtils.isNetworkNotAvailable(this);

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        edt_given_date.setText(date);
        edt_submit_date.setText(date);

        event();

    }//============ End onCreate() ============

    private void find_All_Ids() {
        iv_back = findViewById(R.id.iv_back);
        edt_subject_title = findViewById(R.id.edt_subject_title);
        rel_add_image = findViewById(R.id.rel_add_image);
        iv_add_homeWork_hide = findViewById(R.id.iv_add_homeWork_hide);
        iv_add_homeWork = findViewById(R.id.iv_add_homeWork);
        ll_homework_desc = findViewById(R.id.ll_homework_desc);
        edt_homework_desc = findViewById(R.id.edt_homework_desc);

        spinner_standard = findViewById(R.id.spinner_standard);
        recycler_view_homework_class = findViewById(R.id.recycler_view_homework_class);
        edt_given_date = findViewById(R.id.edt_given_date);
        edt_submit_date = findViewById(R.id.edt_submit_date);
        btn_add_class_homeWork = findViewById(R.id.btn_add_class_homeWork);
    }

    private void event() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rel_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        ll_homework_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_homework_desc.requestFocus();
                Const.openKeyBord(HomeWorkActivity.this);
            }
        });

        edt_submit_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(HomeWorkActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        CharSequence strDate = null;
                        Time chosenDate = new Time();
                        chosenDate.set(day, month, year);

                        long dateAttendance = chosenDate.toMillis(true);
                        strDate = DateFormat.format("dd-MM-yyyy", dateAttendance);

                        edt_submit_date.setText(strDate);

//                        edt_submit_date.setText(day + "-" + (month + 1) + "-" + year);

                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        btn_add_class_homeWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClick) {
                    btn_add_class_homeWork.setClickable(false);
                } else {
                    doValidation();
                }
            }
        });
    }

    //Class List Call
    private void doClassList() {
        Call<SuccessModule> call = apiInterface.classListCall(uId, uType);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {

                        SuccessModule classListModule = response.body();

                        String message = classListModule.getMsg();
                        String code = classListModule.getError_code();

                        if (code.equalsIgnoreCase("1")) {
                            classListModuleArrayArrayList = classListModule.getClassListModuleArrayArrayList();

                            for (ClassListModule classListModuleArray : classListModuleArrayArrayList) {
                                selectClassList.add(classListModuleArray.getClass_name());
                            }

                            classListSpinner();

                        } else {
                            Toasty.info(HomeWorkActivity.this, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                        }

                    } else {
                        Toasty.info(HomeWorkActivity.this, "Server Response !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toast.makeText(HomeWorkActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void classListSpinner() {
        //        selectClassList.add(0, "Select Class");

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, selectClassList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_standard.setAdapter(aa);

        spinner_standard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                sltClass = parent.getItemAtPosition(position).toString();
                String s = spinner_standard.getSelectedItem().toString();
//                Toast.makeText(HomeWorkActivity.this, "" + sltClass, Toast.LENGTH_SHORT).show();

                try {
                    int clsId = Integer.parseInt(classListModuleArrayArrayList.get(position).getClass_id());
//                    classId = String.valueOf(clsId - 1);
                    classId = String.valueOf(clsId);
//                    Toast.makeText(HomeWorkActivity.this, "" + classId, Toast.LENGTH_SHORT).show();

                    //===== Api Call =======
                    if (NetworkUtils.isNetworkAvailable(HomeWorkActivity.this))
                        doDivision(classId);
                    else
                        NetworkUtils.isNetworkNotAvailable(HomeWorkActivity.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void doDivision(String classId) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(HomeWorkActivity.this, 2, GridLayoutManager.VERTICAL, false);
        recycler_view_homework_class.setLayoutManager(gridLayoutManager);
        recycler_view_homework_class.setHasFixedSize(true);

        Call<SuccessModule> call = apiInterface.classDivisionModuleCall(classId);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);
                try {
                    if (response.isSuccessful()) {

                        SuccessModule notificationModule = response.body();

                        String message = notificationModule.getMsg();
                        String code = notificationModule.getError_code();

                        if (code.equalsIgnoreCase("1")) {
                            classDivisionModuleArrayList = notificationModule.getClassDivisionModuleArrayList();

                            homeWorkDivisionAdapter = new HomeWorkDivisionAdapter(HomeWorkActivity.this, classDivisionModuleArrayList);
                            recycler_view_homework_class.setAdapter(homeWorkDivisionAdapter);
                            homeWorkDivisionAdapter.notifyDataSetChanged();

                            // adapter onClick
                            divisionAdapter();

                        } else {
                            Toasty.info(HomeWorkActivity.this, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                        }

                    } else {
                        Toasty.info(HomeWorkActivity.this, "Server Response !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(HomeWorkActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });


    }

    private void divisionAdapter() {
        homeWorkDivisionAdapter.setOnItemClickListener(new HomeWorkDivisionAdapter.OnItemClickListener() {
            @Override
            public void onAdapterClick(ClassDivisionModule divisionModuleArray, int position) {
                ArrayList<String> arrayList = new ArrayList<>();
                divisionModuleArray.setSelectDiv(!divisionModuleArray.isSelectDiv());
                divId = "";
                for (final ClassDivisionModule model : classDivisionModuleArrayList) {
                    if (model.isSelectDiv()) {
                        divId = model.getDivision_id();
                        arrayList.add(divId);
                    }
                    jsonArray = new JSONArray(arrayList);
                    if (jsonArray != null) {
                        sCount = String.valueOf(jsonArray.length());
                    }

                }
//                Toast.makeText(HomeWorkActivity.this, "" + jsonArray, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===================================*** Image Upload Data ***=================================
    private void showPictureDialog() {
        final AlertDialog.Builder pictureDialog = new AlertDialog.Builder(HomeWorkActivity.this);
        pictureDialog.setTitle("Select Action");
        pictureDialog.setIcon(R.drawable.ic_vector_camera_logo);
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera", "Cancel"};
        pictureDialog.setCancelable(false);

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        takeFromGallery();
                        break;
                    case 1:
                        takeFromCamera();
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private void takeFromCamera() {
        // Check if this device has a camera
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    // Open default camera
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);             // start the image capture Intent
            startActivityForResult(intent, MY_CAMERA_REQUEST_CODE);        //100
        } else {
            // no camera on this device
            Toast.makeText(HomeWorkActivity.this, "Camera not supported", LENGTH_LONG).show();
        }
    }

    private void takeFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), MY_GALLERY_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            imageUrl = data.getData();
            if (requestCode == MY_CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(HomeWorkActivity.this.getContentResolver(), imageUrl);
            }
            // ==== User Defined Method ======
            convertToBase64(bitmap); //converting image to base64 string

            iv_add_homeWork_hide.setVisibility(View.GONE);
            iv_add_homeWork.setVisibility(View.VISIBLE);
            iv_add_homeWork.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void convertToBase64(final Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();
        photo = Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }// ===========*** End Image Upload Data ***=============

    private void doValidation() {
        String title = edt_subject_title.getText().toString().trim();
        String desc = edt_homework_desc.getText().toString().trim();
        String gDate = edt_given_date.getText().toString().trim();
        String sDate = edt_submit_date.getText().toString().trim();

        if (title.equalsIgnoreCase("")) {
            edt_subject_title.setError("Please Enter Title !!");
            edt_subject_title.requestFocus();
        } else if (desc.equalsIgnoreCase("")) {
            edt_homework_desc.setError("Please Fill Up Description !! ");
            edt_homework_desc.requestFocus();
        } else if (sDate.equalsIgnoreCase("")) {
            edt_submit_date.setError("Please Select Date !! ");
            edt_submit_date.requestFocus();
        } else if (sltClass.equalsIgnoreCase("")) {
            Toasty.warning(this, "Please Select Class", Toast.LENGTH_SHORT, true).show();
        } else if (sCount.equalsIgnoreCase("")) {
            Toasty.warning(this, "Please Select Division", Toast.LENGTH_SHORT, true).show();
        } else {
            //===== Api Call =======
            if (NetworkUtils.isNetworkAvailable(this)) {
                buttonClick = true;
                classHomeWork(title, desc, gDate, sDate, uId, classId);
            } else
                NetworkUtils.isNetworkNotAvailable(this);
        }
    }

    private void classHomeWork(String title, String desc, String gDate, String sDate, String uId, String classId) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.responseModuleCall(title, photo, desc, gDate, sDate, jsonArray, uId, classId);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModule responseModule = response.body();

                        String message = null, errorCode = null;
                        if (responseModule != null) {
                            message = responseModule.getMsg();
                            errorCode = responseModule.getError_code();

                            if (errorCode.equalsIgnoreCase("1")) {
                                successDialog = new SuccessDialog(HomeWorkActivity.this, true);
                                successDialog.showDialog("Home Work Added Successful !!", true);
//                                Toasty.success(HomeWorkActivity.this, "Home Work Added Successful !!", Toast.LENGTH_SHORT,true).show();

                            } else
                                Toasty.info(HomeWorkActivity.this, "Err Response !!", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.info(HomeWorkActivity.this, "Service Unavailable", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                if (t instanceof SocketTimeoutException) {
                    Toasty.error(HomeWorkActivity.this, "Socket Time out. Please try again.", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(HomeWorkActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
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

    @Override
    public void dialogClosed(boolean mClosed) {
        editor.putString(SharedPreference.REFRESH, "4");
        editor.apply();
        Intent intent = new Intent(HomeWorkActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();

    }
}
