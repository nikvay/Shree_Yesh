package com.shreeyesh.ui.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.HomeActivity;
import com.shreeyesh.utils.NetworkUtils;
import com.shreeyesh.utils.SuccessDialog;
import com.shreeyesh.utils.SuccessDialogClosed;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_LONG;

public class EventAddActivity extends AppCompatActivity implements SuccessDialogClosed {

    private SuccessDialog successDialog;

    ImageView iv_back, iv_add_event_hide, iv_add_event;
    RelativeLayout rel_add_event;
    Button btn_event_save;

    // dateTime
    EditText edt_event_name, edt_event_date_time, edt_event_location, edt_admin_noti_desc;
    private int mYear, mMonth, mDay; // datePicker
    int hour, minute, hourFinal, minuteFinal;
    String currentDate;
    boolean isSelect = false, buttonClick = false;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String token, isSelectUser;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Fast Connect";

    // =========== Upload image ================
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_GALLERY_REQUEST_CODE = 2;
    Bitmap bitmap;
    Uri fileUri;
    Uri imageUrl;
    String photo;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);

        findAllIDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");

        event();

        String currDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        edt_event_date_time.setText(currDate);

    }//========== End onCreate() =============

    private void findAllIDs() {
        iv_back = findViewById(R.id.iv_back);
        rel_add_event = findViewById(R.id.rel_add_event);
        iv_add_event_hide = findViewById(R.id.iv_add_event_hide);
        iv_add_event = findViewById(R.id.iv_add_event);

        edt_event_name = findViewById(R.id.edt_event_name);
        edt_event_date_time = findViewById(R.id.edt_event_date_time);
        edt_event_location = findViewById(R.id.edt_event_location);
        edt_admin_noti_desc = findViewById(R.id.edt_admin_noti_desc);

        btn_event_save = findViewById(R.id.btn_event_save);
    }

    private void event() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rel_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        edt_event_date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });

        btn_event_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClick) {
                    btn_event_save.setClickable(false);
                } else {
                    validation();
                }
            }
        });
    }

    // ===================================*** Image Upload Data ***=================================
    private void showPictureDialog() {
        final AlertDialog.Builder pictureDialog = new AlertDialog.Builder(EventAddActivity.this);
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
            Toast.makeText(EventAddActivity.this, "Camera not supported", LENGTH_LONG).show();
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
                bitmap = MediaStore.Images.Media.getBitmap(EventAddActivity.this.getContentResolver(), imageUrl);
            }
            iv_add_event_hide.setVisibility(View.GONE);
            iv_add_event.setVisibility(View.VISIBLE);
            iv_add_event.setImageBitmap(bitmap);
            // ==== User Defined Method ======
            convertToBase64(bitmap); //converting image to base64 string

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void convertToBase64(final Bitmap bitmap) {
        ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bAOS);
        byte[] imageBytes = bAOS.toByteArray();
        photo = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        isSelect=true;

    }// ===========*** End Image Upload Data ***=============


    private void datePicker() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(EventAddActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {


//                        currentDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
//                        edt_event_date_time.setText(currentDate);
                        CharSequence strDate = null;
                        Time chosenDate = new Time();
                        chosenDate.set(dayOfMonth, monthOfYear, year);

                        long dateAttendance = chosenDate.toMillis(true);
                        strDate = DateFormat.format("dd-MM-yyyy", dateAttendance);

                        edt_event_date_time.setText(strDate);
                        currentDate = String.valueOf(strDate);

                        timePicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void timePicker() {
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                String format;
                hourFinal = hour;
                minuteFinal = minute;

                if (hourFinal == 0) {
                    hourFinal += 12;
                    format = "AM";

                } else if (hourFinal == 12) {
                    format = "PM";

                } else if (hourFinal > 12) {
                    hourFinal -= 12;
                    format = "PM";

                } else {
                    format = "PM";
                }

                edt_event_date_time.setText(currentDate + "  " + hourFinal + ":" + minuteFinal + ":" + format);

            }
        }, hourFinal, minuteFinal, false);
        timePickerDialog.show();

    }

    private void validation() {
        String titleName = edt_event_name.getText().toString().trim();
        String date = edt_event_date_time.getText().toString().trim();
        String location = edt_event_location.getText().toString().trim();
        String desc = edt_admin_noti_desc.getText().toString().trim();

        if (!isSelect) {
            Toasty.warning(this, "Please Select Image", Toast.LENGTH_SHORT,true).show();
        }else if (titleName.equalsIgnoreCase("")) {
            edt_event_name.setError("Please Enter Name");
            edt_event_name.requestFocus();
        } else if (date.equalsIgnoreCase("")) {
            edt_event_date_time.setError("Please Select Date");
            edt_event_date_time.requestFocus();
        } else if (location.equalsIgnoreCase("")) {
            edt_event_location.setError("Please Enter Location");
            edt_event_location.requestFocus();
        } else if (desc.equalsIgnoreCase("")) {
            edt_admin_noti_desc.setError("Please Enter Description");
            edt_admin_noti_desc.requestFocus();
        }else {
            if (NetworkUtils.isNetworkAvailable(this)) {
                buttonClick = true;
                doEvent(titleName, date, desc, location, photo);
            } else
                NetworkUtils.isNetworkNotAvailable(this);
        }
    }

    private void doEvent(String titleName, String date, String desc, String location, String photo) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.evenAddCall(titleName, date, desc, location,photo);

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

                                successDialog = new SuccessDialog(EventAddActivity.this, true);
                                successDialog.showDialog("Event Added Successfully !!", true);
                            }
                        } else {
                            Toasty.warning(EventAddActivity.this, "Service Unavailable !!", Toast.LENGTH_SHORT,true).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(EventAddActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT,true).show();
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
        editor.putString(SharedPreference.REFRESH, "2");
        editor.apply();
        Intent intent = new Intent(EventAddActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();

    }
}
