package com.shreeyesh.ui.activity;

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
import android.util.Base64;
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
import com.shreeyesh.domain.module.ClassDivisionModule;
import com.shreeyesh.domain.module.ClassListModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.HomeActivity;
import com.shreeyesh.utils.FilePath;
import com.shreeyesh.utils.NetworkUtils;
import com.shreeyesh.utils.SuccessDialog;
import com.shreeyesh.utils.SuccessDialogClosed;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_LONG;

public class UploadNotesActivity extends AppCompatActivity implements SuccessDialogClosed {

    private SuccessDialog successDialog;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, uType, isSelectUser, timeTable_notes = "";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Fast Connect";

    Spinner spinner_class_list_upload, spinner_division_list_upload, spinner_time_table_select_type;
    EditText edt_subject_upload;
    Button btn_browse_upload, btn_upload_file;
    TextView tv_action_bar_title, txt_upload_path;
    ImageView iv_back;
    LinearLayout ll_subject_select, ll_time_table_select, ll_teacher_notes, ll_time_table_subject_hide;

    String typePosition = "", sltClass = "", sltDiv = "", selectType = "";
    boolean buttonClick = false;

    // =========== Upload image ================
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_GALLERY_REQUEST_CODE = 2;
    Bitmap bitmap;
    Uri fileUri;
    Uri imageUrl;
    String photo;

    //====== file upload =========
//    public static final String UPLOAD_URL = "http://sevawebsolutions.com/fast-connect/upload_pdf.php";
    private int PICK_PDF_REQUEST = 1, SELECT_DOC = 3;
    String imageDocName;
    String selectExtension;
    long fileSizeInMB;

    //========= class list module ===========
    ArrayList<ClassListModule> classListModuleArrayArrayList = new ArrayList<>();
    ArrayList<String> selectClassList = new ArrayList<>();
    String classId, divId;
    //========= spinner division list module ===========
    ArrayList<ClassDivisionModule> classDivisionModuleArrayList = new ArrayList<>();
    ArrayList<String> selectDivisionList = new ArrayList<>();

    //=== select time table type ==========
    ArrayList<String> selectTimeTable = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notes);

        find_All_IDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        timeTable_notes = sharedpreferences.getString(SharedPreference.TIME_TABLE_NOTES, "");
        uId = SharedPreference.getUserID(this);
        uType = SharedPreference.getUserType(UploadNotesActivity.this);

        if (timeTable_notes.equalsIgnoreCase("admin")) {
            ll_time_table_select.setVisibility(View.VISIBLE);
            ll_teacher_notes.setVisibility(View.GONE);
            ll_time_table_subject_hide.setVisibility(View.GONE);
//            ll_subject_select.setVisibility(View.GONE);
            tv_action_bar_title.setText("Time Table");
            selectTypeSpinner();

        } else if (timeTable_notes.equalsIgnoreCase("teacher")) {
            ll_time_table_select.setVisibility(View.GONE);
            ll_teacher_notes.setVisibility(View.VISIBLE);
            tv_action_bar_title.setText("Upload Notes");
//            ll_subject_select.setVisibility(View.VISIBLE);
        }

        if (NetworkUtils.isNetworkAvailable(this)) {
            doClassList();
        } else {
            NetworkUtils.isNetworkNotAvailable(this);
        }

        event();

    }//========= End onCreate () ===========

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
        tv_action_bar_title = findViewById(R.id.tv_action_bar_title);
        edt_subject_upload = findViewById(R.id.edt_subject_upload);
        spinner_class_list_upload = findViewById(R.id.spinner_class_list_upload);
        spinner_division_list_upload = findViewById(R.id.spinner_division_list_upload);
        txt_upload_path = findViewById(R.id.txt_upload_path);
        btn_browse_upload = findViewById(R.id.btn_browse_upload);
        btn_upload_file = findViewById(R.id.btn_upload_file);

        ll_subject_select = findViewById(R.id.ll_subject_select);
        ll_time_table_select = findViewById(R.id.ll_time_table_select);
        ll_teacher_notes = findViewById(R.id.ll_teacher_notes);
        ll_time_table_subject_hide = findViewById(R.id.ll_time_table_subject_hide);
        spinner_time_table_select_type = findViewById(R.id.spinner_time_table_select_type);
    }

    private void event() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_browse_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument();
            }
        });

        btn_upload_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClick) {
                    btn_upload_file.setClickable(false);
                } else {
                    validation();
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

                            spinnerClass();

                        } else {
                            Toasty.info(UploadNotesActivity.this, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                        }

                    } else {
                        Toasty.info(UploadNotesActivity.this, "Response Null !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(UploadNotesActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void spinnerClass() {
//        selectClassList.add(0, "Select Class");

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, selectClassList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_class_list_upload.setAdapter(aa);

        spinner_class_list_upload.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                sltClass = parent.getItemAtPosition(position).toString();
                String s = spinner_class_list_upload.getSelectedItem().toString();
//                Toast.makeText(UploadNotesActivity.this, "" + sltClass, Toast.LENGTH_SHORT).show();

                try {
                    int clsId = Integer.parseInt(classListModuleArrayArrayList.get(position).getClass_id());
//                    classId = String.valueOf(clsId - 1);
                    classId = String.valueOf(clsId);
//                    Toast.makeText(UploadNotesActivity.this, "" + classId, Toast.LENGTH_SHORT).show();

                    if (NetworkUtils.isNetworkAvailable(UploadNotesActivity.this)) {
                        doDivisionList(classId);
                    } else {
                        NetworkUtils.isNetworkNotAvailable(UploadNotesActivity.this);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        }); //============ End Spinner ================
    }

    //Class Division List Call
    private void doDivisionList(String classId) {

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

                            selectDivisionList.clear(); // clear spinner

                            for (ClassDivisionModule classDivisionModule : classDivisionModuleArrayList) {
                                selectDivisionList.add(classDivisionModule.getDivision_name());
                            }

                            //==== Api Call =======
                            divisionListSpinner();

                        } else {
                            Toasty.info(UploadNotesActivity.this, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                        }

                    } else {
                        Toasty.info(UploadNotesActivity.this, "Response Null !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(UploadNotesActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });

    }

    private void divisionListSpinner() {
//        selectDivisionList.add(0, "Select Division");

        ArrayAdapter bb = new ArrayAdapter(UploadNotesActivity.this, android.R.layout.simple_spinner_item, selectDivisionList);
        bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_division_list_upload.setAdapter(bb);
        bb.notifyDataSetChanged();

        spinner_division_list_upload.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                sltDiv = parent.getItemAtPosition(position).toString();
                String s = spinner_division_list_upload.getSelectedItem().toString();
//                Toast.makeText(UploadNotesActivity.this, "" + sltDiv, Toast.LENGTH_SHORT).show();

                try {
                    int clsId = Integer.parseInt(classDivisionModuleArrayList.get(position).getDivision_id());
//                    divId = String.valueOf(clsId - 1);
                    divId = String.valueOf(clsId);
//                    Toast.makeText(UploadNotesActivity.this, "" + divId, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //=================== Upload document Image ========================================
    private void selectDocument() {

        final AlertDialog.Builder documentDialog = new AlertDialog.Builder(UploadNotesActivity.this);
        documentDialog.setTitle("Select Action");
        documentDialog.setIcon(R.drawable.ic_vector_camera_logo);

        String[] timeTable = {"Select PDF", "Select Word File", "Cancel"};
        String[] uploadDoc = {"Select Camera", "Select Gallery", "Select PDF", "Select Word File", "Cancel"};
        documentDialog.setCancelable(false);

        if (timeTable_notes.equalsIgnoreCase("admin")) {
            documentDialog.setItems(timeTable, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {
                        case 0:
                            pdfIntent();
                            break;
                        case 1:
                            wordIntent();
                            break;
                        case 2:
                            dialog.dismiss();
                            break;
                    }
                }
            });
        } else if (timeTable_notes.equalsIgnoreCase("teacher"))
            documentDialog.setItems(uploadDoc, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {

                        case 0:
                            takeFromCamera();
                            break;
                        case 1:
                            takeFromGallery();
                            break;
                        case 2:
                            pdfIntent();
                            break;
                        case 3:
                            wordIntent();
                            break;
                        case 4:
                            dialog.dismiss();
                            break;
                    }
                }
            });

        documentDialog.show();
    }

    private void takeFromCamera() {
        // Check if this device has a camera
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            selectExtension = ".png";
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    // Open default camera
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);             // start the image capture Intent
            startActivityForResult(intent, MY_CAMERA_REQUEST_CODE);        //100
        } else {
            // no camera on this device
            Toast.makeText(UploadNotesActivity.this, "Camera not supported", LENGTH_LONG).show();
        }
    }

    private void takeFromGallery() {
        selectExtension = ".png";
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), MY_GALLERY_REQUEST_CODE);
    }

    private void wordIntent() {
        selectExtension = ".doc";
        Intent intent = new Intent();
        intent.setType("application/msword");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Doc"), SELECT_DOC);
        txt_upload_path.setText("file.doc");
    }

    private void pdfIntent() {
        selectExtension = ".pdf";
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Doc"), PICK_PDF_REQUEST);
        txt_upload_path.setText("file.pdf");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            imageUrl = data.getData();
            if (requestCode == MY_CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
                // ==== User Defined Method ======
                convertToBase64(bitmap); //converting image to base64 string

            } else if (requestCode == MY_GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
                bitmap = MediaStore.Images.Media.getBitmap(UploadNotesActivity.this.getContentResolver(), imageUrl);
                // ==== User Defined Method ======
                convertToBase64(bitmap); //converting image to base64 string
            } else {
                /*if (requestCode == SELECT_DOC && resultCode == RESULT_OK && data != null && data.getData() != null) */
                Uri uri = data.getData();
                docConvertToBase64(uri);//converting document to base64 string
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void convertToBase64(final Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();
        imageDocName = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        txt_upload_path.setText("image.png");
    }

    private void docConvertToBase64(final Uri uri) {
        String path = FilePath.getPath(this, uri);

        File originalFile = new File(path);

        // Get length of file in bytes
        long fileSizeInBytes = originalFile.length();
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        fileSizeInMB = fileSizeInKB / 1024;

        try {
            FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
            byte[] bytes = new byte[(int) originalFile.length()];
            fileInputStreamReader.read(bytes);
            imageDocName = Base64.encodeToString(bytes, Base64.NO_WRAP);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void validation() {

        String title = edt_subject_upload.getText().toString().trim();
        String fileName = txt_upload_path.getText().toString().trim();

        if (sltClass.equalsIgnoreCase("")) {
            Toasty.warning(this, "Please Select Class", Toast.LENGTH_SHORT, true).show();
        } else if (sltDiv.equalsIgnoreCase("")) {
            Toasty.warning(this, "Please Select Division", Toast.LENGTH_SHORT, true).show();
        } else if (fileName.equalsIgnoreCase("select file")) {
            Toasty.warning(this, "Please Select File", Toast.LENGTH_SHORT, true).show();
        } else if (fileSizeInMB > 1) {
            Toasty.warning(this, "Please Select Bellow 1 MB", Toast.LENGTH_SHORT, true).show();
        } else if (isSelectUser.equalsIgnoreCase("1")) {
            if (selectType.equalsIgnoreCase("Select Type")) {
                Toasty.warning(this, "Please Select Type", Toast.LENGTH_SHORT, true).show();
            } else {
                if (NetworkUtils.isNetworkAvailable(UploadNotesActivity.this)) {
                    buttonClick = true;
                    timeTableCall(title);
                } else {
                    NetworkUtils.isNetworkNotAvailable(UploadNotesActivity.this);
                }
            }
        } else {
            if (NetworkUtils.isNetworkAvailable(UploadNotesActivity.this)) {
                if (title.equalsIgnoreCase("")) {
                    edt_subject_upload.setError("Please Enter Title");
                    edt_subject_upload.requestFocus();
                } else {
                    buttonClick = true;
                    uploadNotesCall(title);
                }
            } else {
                NetworkUtils.isNetworkNotAvailable(UploadNotesActivity.this);
            }
        }

    }

    private void uploadNotesCall(String title) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.notesUploadCall(imageDocName, title, uId, divId, classId, selectExtension);

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
                                successDialog = new SuccessDialog(UploadNotesActivity.this, true);
                                successDialog.showDialog("Upload Notes Successful !!", true);

//                                Toasty.success(UploadNotesActivity.this, "Upload Notes Successful !!", Toast.LENGTH_SHORT,true).show();

                            } else
                                Toasty.info(UploadNotesActivity.this, "Err Response !!", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.info(UploadNotesActivity.this, "Service Unavailable", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(UploadNotesActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void selectTypeSpinner() {
        selectTimeTable.add(0, "Select Type");
        selectTimeTable.add(1, "Lecture");
        selectTimeTable.add(2, "Examination");

        ArrayAdapter bb = new ArrayAdapter(UploadNotesActivity.this, android.R.layout.simple_spinner_item, selectTimeTable);
        bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_time_table_select_type.setAdapter(bb);
        bb.notifyDataSetChanged();

        spinner_time_table_select_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectType = parent.getItemAtPosition(position).toString();
                typePosition = String.valueOf(parent.getItemIdAtPosition(position));
                String s = spinner_time_table_select_type.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void timeTableCall(String title) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.uploadTimeTableCall(uId, typePosition, divId, classId, title, imageDocName, selectExtension);

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
                                successDialog = new SuccessDialog(UploadNotesActivity.this, true);
                                successDialog.showDialog("Upload TimeTable Successful !!", true);

//                                Toasty.success(UploadNotesActivity.this, "Upload TimeTable Successful !!", Toast.LENGTH_SHORT,true).show();

                            } else
                                Toasty.info(UploadNotesActivity.this, "Err Response !!", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.info(UploadNotesActivity.this, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(UploadNotesActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
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
        if (isSelectUser.equalsIgnoreCase("2")) {
            editor.putString(SharedPreference.REFRESH, "3");
            editor.apply();
            Intent intent = new Intent(UploadNotesActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }
}
