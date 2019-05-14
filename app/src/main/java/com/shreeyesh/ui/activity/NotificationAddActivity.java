package com.shreeyesh.ui.activity;

import android.annotation.SuppressLint;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.ClassDivisionModule;
import com.shreeyesh.domain.module.ClassListModule;
import com.shreeyesh.domain.module.NotificationTypeModule;
import com.shreeyesh.domain.module.StudentListModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.module.TeacherListModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.HomeActivity;
import com.shreeyesh.ui.adapter.StudentAdapter;
import com.shreeyesh.ui.adapter.TeacherAdapter;
import com.shreeyesh.utils.Const;
import com.shreeyesh.utils.FilePath;
import com.shreeyesh.utils.NetworkUtils;
import com.shreeyesh.utils.SuccessDialog;
import com.shreeyesh.utils.SuccessDialogClosed;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_LONG;

public class NotificationAddActivity extends AppCompatActivity implements SuccessDialogClosed {

    private SuccessDialog successDialog;

    ImageView iv_back;
    Button btn_admin_notification, btn_browse_notification;
    EditText edt_admin_noti_title, edt_admin_noti_desc;
    Spinner spinner_admin_slt_type, spinner_admin_class_list, spinner_division_list_noti, spinner_noti_type;
    LinearLayout ll_admin_noti_desc, ll_admin_noti_div;
    LinearLayout ll_admin_slt_std, ll_admin_slt_teacher, ll_admin_slt_std_class, ll_notification_type_dya;
    TextView tv_action_bar_title, tv_admin_slt_teacher, tv_admin_slt_std, txt_upload_noti_path,
            tv_slt_count_teacher, tv_slt_count_student;

    ArrayList<String> typeSelect = new ArrayList<>();

    String isSelectUser;
    String selectType, stdId = "", teacherId = "", notificationType = "", classId = "", divId = "", notiType = "", notificationTypeDya = "",
            sltClass = "", sltDiv = "";
    String tCount = "0", sCount = "0";
    boolean buttonClick = false;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, uType;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Fast Connect";

    //===== class list ==========
    JSONArray jsonArray = null, jsonArrayTeacher = null;

    // =========== Upload image ================
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_GALLERY_REQUEST_CODE = 2;
    Bitmap bitmap;
    Uri fileUri;
    Uri imageUrl;
    String photo;

    //====== file upload =========
    private int PICK_PDF_REQUEST = 1, SELECT_DOC = 3;
    String imageDocName, selectExtension;
    long fileSizeInMB;

    //======== student list ========
    CheckBox cbx_std_all_slt;
    ImageView iv_empty_list_std_noti;
    ArrayList<StudentListModule> studentListModuleArrayArrayList;
    ArrayList<StudentListModule> checkList;
    StudentAdapter studentAdapter;
    RecyclerView recycler_view_student_dialog;

    //========== teacher list ===========
    CheckBox cbx_std_all_teacher;
    ImageView iv_empty_list_teacher;
    ArrayList<TeacherListModule> teacherListModuleArrayArrayList;
    ArrayList<TeacherListModule> checkListTeacher;
    TeacherAdapter teacherAdapter;
    RecyclerView recycler_view_teacher_dialog;

    //============ class =============
    ArrayList<ClassListModule> classListModuleArrayArrayList = new ArrayList<>();
    ArrayList<String> selectClassList = new ArrayList<>();

    //========= spinner division list module ===========
    ArrayList<ClassDivisionModule> classDivisionModuleArrayList = new ArrayList<>();
    ArrayList<String> selectDivisionList = new ArrayList<>();

    //======== notification type ==============
    ArrayList<NotificationTypeModule> notificationTypeModuleArrayList = new ArrayList<>();
    ArrayList<String> selectNotificationTypeList = new ArrayList<>();

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_add);

        find_All_IDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");

        uId = SharedPreference.getUserID(NotificationAddActivity.this);
        uType = SharedPreference.getUserType(NotificationAddActivity.this);

        //====== Spinner =========
        selectTypeSpinner();

        //======== Api Call ==============
        if (NetworkUtils.isNetworkAvailable(this)) {
            doNotificationType();
            doClassList();
        } else {
            NetworkUtils.isNetworkNotAvailable(this);
        }

        event();

    } //================== End onCreate() ========================

    private void event() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ll_admin_noti_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_admin_noti_desc.requestFocus();
                Const.openKeyBord(NotificationAddActivity.this);
            }
        });

        tv_admin_slt_std.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sltClass.equals("")) {
                    Toasty.warning(NotificationAddActivity.this, "Please Select Class", Toast.LENGTH_SHORT, true).show();
                }/* else if (sltDiv.equals("")) {
                    Toasty.warning(NotificationAddActivity.this, "Please Select Division", Toast.LENGTH_SHORT,true).show();
                } */ else {
                    popUpStudent();
                }
            }
        });

        tv_admin_slt_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpTeacher();
            }
        });

        btn_browse_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument();
            }
        });

        btn_admin_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClick) {
                    btn_admin_notification.setClickable(false);
                } else {
                    doValidation();
                }
            }
        });


    }

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
        tv_action_bar_title = findViewById(R.id.tv_action_bar_title);
        edt_admin_noti_title = findViewById(R.id.edt_admin_noti_title);
        edt_admin_noti_desc = findViewById(R.id.edt_admin_noti_desc);

        spinner_admin_slt_type = findViewById(R.id.spinner_admin_slt_type);
        spinner_noti_type = findViewById(R.id.spinner_noti_type);

        ll_notification_type_dya = findViewById(R.id.ll_notification_type_dya);

        tv_admin_slt_std = findViewById(R.id.tv_admin_slt_std);
        tv_admin_slt_teacher = findViewById(R.id.tv_admin_slt_teacher);

        ll_admin_slt_std = findViewById(R.id.ll_admin_slt_std);
        ll_admin_slt_teacher = findViewById(R.id.ll_admin_slt_teacher);
        tv_slt_count_teacher = findViewById(R.id.tv_slt_count_teacher);
        tv_slt_count_student = findViewById(R.id.tv_slt_count_student);

        ll_admin_slt_std_class = findViewById(R.id.ll_admin_slt_std_class);
        ll_admin_noti_div = findViewById(R.id.ll_admin_noti_div);
        spinner_admin_class_list = findViewById(R.id.spinner_admin_class_list);
        spinner_division_list_noti = findViewById(R.id.spinner_division_list_noti);

        txt_upload_noti_path = findViewById(R.id.txt_upload_noti_path);
        btn_browse_notification = findViewById(R.id.btn_browse_notification);

        btn_admin_notification = findViewById(R.id.btn_admin_notification);
        ll_admin_noti_desc = findViewById(R.id.ll_admin_noti_desc);
    }

    private void selectTypeSpinner() {
        typeSelect.add("Public");

        if (isSelectUser.equalsIgnoreCase("1")) {
            tv_action_bar_title.setText("Admin Notification");
            typeSelect.add("Teacher");

        } else if (isSelectUser.equalsIgnoreCase("2")) {
            tv_action_bar_title.setText("Teacher Notification");
        }

        typeSelect.add("Student");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeSelect);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_admin_slt_type.setAdapter(dataAdapter);

        spinner_admin_slt_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectType = parent.getItemAtPosition(position).toString();
//                notificationType = String.valueOf(spinner_admin_slt_type.getSelectedItemPosition());

                if (selectType.equalsIgnoreCase("Public")) {
                    ll_admin_slt_std.setVisibility(View.GONE);
                    ll_admin_noti_div.setVisibility(View.GONE);
                    ll_admin_slt_teacher.setVisibility(View.GONE);
                    ll_admin_slt_std_class.setVisibility(View.GONE);
                    ll_notification_type_dya.setVisibility(View.VISIBLE);
                    notificationType = "0";

                } else if (selectType.equalsIgnoreCase("Teacher")) {
                    ll_admin_slt_std.setVisibility(View.GONE);
                    ll_admin_noti_div.setVisibility(View.GONE);
                    ll_admin_slt_teacher.setVisibility(View.VISIBLE);
                    ll_admin_slt_std_class.setVisibility(View.GONE);
                    ll_notification_type_dya.setVisibility(View.GONE);
                    notificationType = "1";

                } else if (selectType.equalsIgnoreCase("Student")) {
                    ll_admin_slt_std_class.setVisibility(View.VISIBLE);
                    ll_admin_slt_std.setVisibility(View.VISIBLE);
                    ll_admin_noti_div.setVisibility(View.VISIBLE);
                    ll_admin_slt_teacher.setVisibility(View.GONE);
                    ll_notification_type_dya.setVisibility(View.GONE);
                    notificationType = "2";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void doNotificationType() {
        Call<SuccessModule> call = apiInterface.notificationTypeCall(uId);

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
                            notificationTypeModuleArrayList = classListModule.getNotificationTypeModuleArrayList();

                            for (NotificationTypeModule notificationTypeModule : notificationTypeModuleArrayList) {
                                selectNotificationTypeList.add(notificationTypeModule.getName());
                            }

                            notificationTypeSpinner();

                        } else {
                            Toasty.info(NotificationAddActivity.this, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.info(NotificationAddActivity.this, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(NotificationAddActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void notificationTypeSpinner() {
//        selectNotificationTypeList.add(0, "Public");
        // Creating adapter for spinner
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, selectNotificationTypeList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_noti_type.setAdapter(aa);

        spinner_noti_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                notiType = parent.getItemAtPosition(position).toString();
                String s = spinner_noti_type.getSelectedItem().toString();

                try {
                    int clsId = Integer.parseInt(notificationTypeModuleArrayList.get(position).getId());
                    notificationTypeDya = String.valueOf(clsId);

                } catch (Exception e) {
                    e.printStackTrace();
                }

//                Toast.makeText(NotificationAddActivity.this, "" + sltClass, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
                            Toasty.info(NotificationAddActivity.this, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.info(NotificationAddActivity.this, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(NotificationAddActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void classListSpinner() {
//        selectClassList.add(0, "Select Class");
        // Creating adapter for spinner
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, selectClassList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_admin_class_list.setAdapter(aa);

        spinner_admin_class_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                sltClass = parent.getItemAtPosition(position).toString();
                String s = spinner_admin_class_list.getSelectedItem().toString();

                try {
                    int clsId = Integer.parseInt(classListModuleArrayArrayList.get(position).getClass_id());
//                    classId = String.valueOf(clsId - 1);
                    classId = String.valueOf(clsId);
                    //======== Api Call ===========
                    doDivisionList(classId);

                } catch (Exception e) {
                    e.printStackTrace();
                }

//                Toast.makeText(NotificationAddActivity.this, "" + sltClass, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
                            Toasty.info(NotificationAddActivity.this, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.info(NotificationAddActivity.this, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(NotificationAddActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void divisionListSpinner() {
//        selectDivisionList.add(0, "Select Division");

        ArrayAdapter bb = new ArrayAdapter(NotificationAddActivity.this, android.R.layout.simple_spinner_item, selectDivisionList);
        bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_division_list_noti.setAdapter(bb);
        bb.notifyDataSetChanged();

        spinner_division_list_noti.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                sltDiv = parent.getItemAtPosition(position).toString();
                String s = spinner_division_list_noti.getSelectedItem().toString();

                try {
                    int clsId = Integer.parseInt(classDivisionModuleArrayList.get(position).getDivision_id());
//                    divId = String.valueOf(clsId - 1);
                    divId = String.valueOf(clsId);
//                    Toast.makeText(NotificationAddActivity.this, "" + divId, Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    e.printStackTrace();
                }

//                Toast.makeText(NotificationAddActivity.this, "" + sltDiv, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void popUpStudent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NotificationAddActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_student_list, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog alertDialog = builder.create();

        alertDialog.show();

        final EditText edt_search_student = alertDialog.findViewById(R.id.edt_search_student);
        recycler_view_student_dialog = alertDialog.findViewById(R.id.recycler_view_student_dialog);
        Button btn_admin_std_select = alertDialog.findViewById(R.id.btn_admin_std_select);
        iv_empty_list_std_noti = alertDialog.findViewById(R.id.iv_empty_list_std_noti);
        cbx_std_all_slt = alertDialog.findViewById(R.id.cbx_std_all_slt);

        if (btn_admin_std_select != null) {
            btn_admin_std_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    doActionStd();
                    alertDialog.dismiss();
                }
            });
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(NotificationAddActivity.this, 3, GridLayoutManager.VERTICAL, false);
        recycler_view_student_dialog.setLayoutManager(gridLayoutManager);
        recycler_view_student_dialog.setHasFixedSize(true);


        //============ Api Call ================
        if (NetworkUtils.isNetworkAvailable(this))
            doStudentList();
        else
            NetworkUtils.isNetworkNotAvailable(this);


        if (edt_search_student != null) {
            edt_search_student.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    studentAdapter.getFilter().filter(edt_search_student.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    private void doActionStd() {
        final ArrayList<String> arrayList = new ArrayList<>();
        checkList = studentAdapter.getSelectedArrayList();

        if (checkList.containsAll(studentListModuleArrayArrayList))
            cbx_std_all_slt.setChecked(true);
        else
            cbx_std_all_slt.setChecked(false);

        for (int i = 0; i < checkList.size(); i++) {
            StudentListModule studentListModule = checkList.get(i);
            String stdId = studentListModule.getStudent_id();
            arrayList.add(stdId);
        }

        jsonArray = new JSONArray(arrayList);
        if (jsonArray != null) {
            sCount = String.valueOf(jsonArray.length());
            tv_slt_count_student.setText(sCount);
        } else {
            tv_slt_count_student.setText(sCount);
        }
//        Toast.makeText(NotificationAddActivity.this, "" + jsonArray, Toast.LENGTH_SHORT).show();
    }

    private void doStudentList() {
        Call<SuccessModule> call = apiInterface.classStudentListModuleCall(classId, divId);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {

                        SuccessModule classStudentListModule = response.body();

                        if (classStudentListModule != null) {

                            studentListModuleArrayArrayList = classStudentListModule.getStudentListModuleArrayArrayList();

                            studentAdapter = new StudentAdapter(NotificationAddActivity.this, studentListModuleArrayArrayList);
                            recycler_view_student_dialog.setAdapter(studentAdapter);
                            studentAdapter.notifyDataSetChanged();

                            if (studentListModuleArrayArrayList.size() != 0) {
                                recycler_view_student_dialog.setVisibility(View.VISIBLE);
                                iv_empty_list_std_noti.setVisibility(View.GONE);
                            } else {
                                recycler_view_student_dialog.setVisibility(View.GONE);
                                iv_empty_list_std_noti.setVisibility(View.VISIBLE);
                            }

                            studentAdapter.setOnItemClickListener(new StudentAdapter.OnItemClickListener() {
                                @Override
                                public void onAdapterClick() {
                                    cbx_std_all_slt.setChecked(false);
                                    doActionStd();
                                }
                            });

                        } else {
                            Toasty.info(NotificationAddActivity.this, "Response Null !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(NotificationAddActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void popUpTeacher() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NotificationAddActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_teacher_list, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog alertDialog = builder.create();

        alertDialog.show();

        final EditText edt_search_teacher = alertDialog.findViewById(R.id.edt_search_teacher);
        recycler_view_teacher_dialog = alertDialog.findViewById(R.id.recycler_view_teacher_dialog);
        final Button btn_teacher_noti = alertDialog.findViewById(R.id.btn_teacher_noti);
        iv_empty_list_teacher = alertDialog.findViewById(R.id.iv_empty_list_teacher);
        cbx_std_all_teacher = alertDialog.findViewById(R.id.cbx_std_all_teacher);

        btn_teacher_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doActionTeacher();
                alertDialog.dismiss();
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(NotificationAddActivity.this, 3, GridLayoutManager.VERTICAL, false);
        recycler_view_teacher_dialog.setLayoutManager(gridLayoutManager);
        recycler_view_teacher_dialog.setHasFixedSize(true);

        //============ Api Call ================
        if (NetworkUtils.isNetworkAvailable(this))
            doTeacherList();
        else
            NetworkUtils.isNetworkNotAvailable(this);

        if (edt_search_teacher != null) {
            edt_search_teacher.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    teacherAdapter.getFilter().filter(edt_search_teacher.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    private void doActionTeacher() {
        final ArrayList<String> arrayList = new ArrayList<>();
        checkListTeacher = teacherAdapter.getSelectedArrayList();

        if (checkListTeacher.containsAll(teacherListModuleArrayArrayList))
            cbx_std_all_teacher.setChecked(true);
        else
            cbx_std_all_teacher.setChecked(false);

        for (int i = 0; i < checkListTeacher.size(); i++) {
            TeacherListModule teacherListModule = checkListTeacher.get(i);
            String stdId = teacherListModule.getUser_id();
            arrayList.add(stdId);
        }

        jsonArrayTeacher = new JSONArray(arrayList);
        if (jsonArrayTeacher != null) {
            tCount = String.valueOf(jsonArrayTeacher.length());
            tv_slt_count_teacher.setText(tCount);
        } else {
            tv_slt_count_teacher.setText(tCount);
        }
//        Toast.makeText(NotificationAddActivity.this, "" + jsonArrayTeacher, Toast.LENGTH_SHORT).show();
    }

    private void doTeacherList() {
        Call<SuccessModule> call = apiInterface.teacherListModuleCall();

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {

                        SuccessModule teacherListModule = response.body();

                        if (teacherListModule != null) {

                            teacherListModuleArrayArrayList = teacherListModule.getTeacherListModuleArrayArrayList();

                            teacherAdapter = new TeacherAdapter(NotificationAddActivity.this, teacherListModuleArrayArrayList);
                            recycler_view_teacher_dialog.setAdapter(teacherAdapter);
                            teacherAdapter.notifyDataSetChanged();

                            teacherAdapter.setOnItemClickListener(new TeacherAdapter.OnItemClickListener() {
                                @Override
                                public void onAdapterClick() {
                                    cbx_std_all_teacher.setChecked(false);
                                    doActionTeacher();
                                }
                            });

                            if (studentListModuleArrayArrayList.size() != 0) {
                                recycler_view_teacher_dialog.setVisibility(View.VISIBLE);
                                iv_empty_list_teacher.setVisibility(View.GONE);
                            } else {
                                recycler_view_teacher_dialog.setVisibility(View.GONE);
                                iv_empty_list_teacher.setVisibility(View.VISIBLE);
                            }

                        } else {
                            Toasty.info(NotificationAddActivity.this, "Response Null !!", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(NotificationAddActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    //=================== Upload document Image ========================================
    private void selectDocument() {

        final AlertDialog.Builder documentDialog = new AlertDialog.Builder(NotificationAddActivity.this);
        documentDialog.setTitle("Select Action");
        documentDialog.setIcon(R.drawable.ic_vector_camera_logo);

        String[] uploadDoc = {"Select Camera", "Select Gallery", "Select PDF", "Select Word File", "Cancel"};
        documentDialog.setCancelable(false);
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
            Toasty.warning(NotificationAddActivity.this, "Camera not supported !!", LENGTH_LONG, true).show();
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
        txt_upload_noti_path.setText("file.doc");
    }

    private void pdfIntent() {
        selectExtension = ".pdf";
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Doc"), PICK_PDF_REQUEST);
        txt_upload_noti_path.setText("file.pdf");
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
                bitmap = MediaStore.Images.Media.getBitmap(NotificationAddActivity.this.getContentResolver(), imageUrl);
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
        txt_upload_noti_path.setText("image.png");
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

    private void doValidation() {
        String desc = edt_admin_noti_desc.getText().toString().trim();
        String title = edt_admin_noti_title.getText().toString().trim();

        if (title.equalsIgnoreCase("")) {
            edt_admin_noti_title.setError("Please Enter Title");
            edt_admin_noti_title.requestFocus();
        } else if (fileSizeInMB > 1) {
            Toasty.warning(this, "Please Select Bellow 1 MB", Toast.LENGTH_SHORT, true).show();
        } else if (notiType.equals("")) {
            Toasty.warning(this, "Please Select Notification Type", Toast.LENGTH_SHORT, true).show();
        } else if (desc.equalsIgnoreCase("")) {
            edt_admin_noti_desc.setError("Please Enter Description");
            edt_admin_noti_desc.requestFocus();
        } else {
            //======== Api Call ==============
            if (selectType.equals("Public")) {
                // all validation
                jsonArray = null;
                jsonArrayTeacher = null;
                if (NetworkUtils.isNetworkAvailable(NotificationAddActivity.this))
                    doNotification(title, desc);
                else
                    NetworkUtils.isNetworkNotAvailable(NotificationAddActivity.this);

            } else if (selectType.equals("Teacher")) {
                // all validation
                jsonArray = null;
                if (tCount.equalsIgnoreCase("")) {
                    Toasty.warning(NotificationAddActivity.this, "Please Select Teacher !!", Toast.LENGTH_SHORT, true).show();
                } else if (tCount.equalsIgnoreCase("0")) {
                    Toasty.warning(NotificationAddActivity.this, "Please Select Teacher !!", Toast.LENGTH_SHORT, true).show();
                } else {
                    if (NetworkUtils.isNetworkAvailable(NotificationAddActivity.this))
                        doNotification(title, desc);
                    else
                        NetworkUtils.isNetworkNotAvailable(NotificationAddActivity.this);
                }

            } else if (selectType.equals("Student")) {
                // all validation
                jsonArrayTeacher = null;
                if (sltClass.equals("")) {
                    Toasty.warning(NotificationAddActivity.this, "Please Select Class !!", Toast.LENGTH_SHORT, true).show();
                } else if (sltDiv.equals("")) {
                    Toasty.warning(NotificationAddActivity.this, "Please Select Division !!", Toast.LENGTH_SHORT,true).show();
                } else if (sCount.equalsIgnoreCase("")) {
                    Toasty.warning(NotificationAddActivity.this, "Please Select Student !!", Toast.LENGTH_SHORT, true).show();
                } else if (sCount.equalsIgnoreCase("0")) {
                    Toasty.warning(NotificationAddActivity.this, "Please Select Student !!", Toast.LENGTH_SHORT, true).show();
                } else {
                    if (NetworkUtils.isNetworkAvailable(NotificationAddActivity.this))
                        doNotification(title, desc);
                    else
                        NetworkUtils.isNetworkNotAvailable(NotificationAddActivity.this);
                }
            }
        }
    }

    private void doNotification(String title, String desc) {
        buttonClick = true;
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.notificationModuleCall(uId, title, desc, notificationType, jsonArrayTeacher, classId, jsonArray, imageDocName, selectExtension, notificationTypeDya, uType);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModule notificationModule = response.body();

                        String message = null, errorCode = null;
                        if (notificationModule != null) {
                            message = notificationModule.getMsg();
                            errorCode = notificationModule.getError_code();
                        }

                        if (errorCode.equalsIgnoreCase("0")) {
                            Toasty.error(NotificationAddActivity.this, "Response Error !!", Toast.LENGTH_SHORT, true).show();

                        } else if (errorCode.equalsIgnoreCase("1")) {

                            successDialog = new SuccessDialog(NotificationAddActivity.this, true);
                            successDialog.showDialog("Notification Send Successfully !!", true);

                        }
                    } else {
                        Toasty.info(NotificationAddActivity.this, "Response Error !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                if (t instanceof SocketTimeoutException) {
                    Toasty.error(NotificationAddActivity.this, "Socket Time out. Please try again.", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(NotificationAddActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
                }
            }
        });
    }

    //============ student all select ==========
    public void selectAllStd(View v) {
        if (cbx_std_all_slt.isChecked()) {
            studentAdapter.selectAll();
        } else {
            cbx_std_all_slt.setChecked(false);
            studentAdapter.clearSelected();
        }
        doActionStd();
    }

    //============ teacher all select ==========
    public void selectAllTeacher(View v) {
        if (cbx_std_all_teacher.isChecked()) {
            teacherAdapter.selectAll();
        } else {
            cbx_std_all_teacher.setChecked(false);
            teacherAdapter.clearSelected();
        }
        doActionTeacher();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }

    @Override
    public void dialogClosed(boolean mClosed) {
        editor.putString(SharedPreference.REFRESH, "7");
        editor.apply();
        Intent intent = new Intent(NotificationAddActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }
}
