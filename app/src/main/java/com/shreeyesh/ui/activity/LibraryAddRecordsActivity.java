package com.shreeyesh.ui.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.ClassDivisionModule;
import com.shreeyesh.domain.module.ClassListModule;
import com.shreeyesh.domain.module.StudentListModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.module.TeacherListModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.HomeActivity;
import com.shreeyesh.ui.adapter.LibraryStudentAdapter;
import com.shreeyesh.ui.adapter.LibraryTeacherAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.shreeyesh.utils.SuccessDialog;
import com.shreeyesh.utils.SuccessDialogClosed;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryAddRecordsActivity extends AppCompatActivity implements SuccessDialogClosed {

    // ==== dialog ======
    private SuccessDialog successDialog;

    Spinner spinner_lib_member, spinner_class, spinner_division;
    EditText edt_book_id, edt_book_name, edt_issue_date, edt_return_date;
    Button btn_submit;
    LinearLayout ll_select_issued_by, ll_student_hide, ll_teacher_hide;
    ImageView iv_back;
    TextView tv_library_slt_std, tv_slt_count_student_lib, tv_library_slt_teacher, tv_slt_count_teacher_lib;

    ArrayList<String> arrayListLibMember = new ArrayList<>();

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, uType, isSelectUser;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Fast Connect";

    //========= spinner class list module ===========
    ArrayList<ClassListModule> classListModuleArrayArrayList = new ArrayList<>();
    ArrayList<String> selectClassList = new ArrayList<>();
    //========= spinner division list module ===========
    ArrayList<ClassDivisionModule> classDivisionModuleArrayList = new ArrayList<>();
    ArrayList<String> selectDivisionList = new ArrayList<>();

    ImageView iv_empty_list, iv_empty_list_lib_t;
    AlertDialog alertDialog, teacherAlertDialog;
    //========= student list ==========
    ArrayList<StudentListModule> studentListModuleArrayArrayList = new ArrayList<>();
    LibraryStudentAdapter studentAdapter;
    RecyclerView recycler_view_lib_student;

    //====================== Teacher list =============
    ArrayList<TeacherListModule> teacherListModuleArrayArrayList = new ArrayList<>();
    RecyclerView recycler_view_lib_teacher;
    LibraryTeacherAdapter libraryTeacherAdapter;

    String type, sId, tId, sCount = "0", tCount = "0";
    String classId = "", divId = "", currentDate = "", sltClass = "", sltDiv = "";

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_add_records);

        find_All_IDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");

        uId = SharedPreference.getUserID(this);
        uType = SharedPreference.getUserType(this);

        issueBookNameSpinner();

        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        edt_issue_date.setText(currentDate);
        edt_return_date.setText(currentDate);

        tv_slt_count_student_lib.setText(sCount);
        tv_slt_count_teacher_lib.setText(tCount);

        events();

    }//=========== end onCreate () =================

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);

        spinner_lib_member = findViewById(R.id.spinner_lib_member);
        spinner_class = findViewById(R.id.spinner_class);
        spinner_division = findViewById(R.id.spinner_division);

        tv_library_slt_std = findViewById(R.id.tv_library_slt_std);
        tv_slt_count_student_lib = findViewById(R.id.tv_slt_count_student_lib);
        tv_slt_count_teacher_lib = findViewById(R.id.tv_slt_count_teacher_lib);
        tv_library_slt_teacher = findViewById(R.id.tv_library_slt_teacher);

        edt_book_id = findViewById(R.id.edt_book_id);
        edt_book_name = findViewById(R.id.edt_book_name);

        edt_issue_date = findViewById(R.id.edt_issue_date);
        edt_return_date = findViewById(R.id.edt_return_date);

        btn_submit = findViewById(R.id.btn_submit_record);

        ll_select_issued_by = findViewById(R.id.ll_select_issued_by);
        ll_student_hide = findViewById(R.id.ll_student_hide);
        ll_teacher_hide = findViewById(R.id.ll_teacher_hide);
    }

    private void events() {

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        edt_return_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(LibraryAddRecordsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        Time chosenDate = new Time();
                        chosenDate.set(day, month, year);

                        long return_date = chosenDate.toMillis(true);
                        edt_return_date.setText(String.valueOf(DateFormat.format("yyyy-MM-dd", return_date)));

                    }
                }, year, month, day);
                datePickerDialog.show();

            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("1")) {
                    teacherValidation();
                } else if (type.equals("2")) {
                    studentValidation();
                }
            }
        });

        tv_library_slt_std.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sltClass.equals("")) {
                    Toasty.warning(LibraryAddRecordsActivity.this, "Please Select Class", Toast.LENGTH_SHORT, true).show();
                }/* else if (sltDiv.equals("")) {
                    Toasty.warning(NotificationAddActivity.this, "Please Select Division", Toast.LENGTH_SHORT,true).show();
                } */ else {
                    popUpStudent();
                }
            }
        });

        tv_library_slt_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpTeacher();
            }
        });

    }

    private void issueBookNameSpinner() {
        arrayListLibMember.add(0, "Student");
        arrayListLibMember.add(1, "Staff");

        ArrayAdapter aa = new ArrayAdapter(LibraryAddRecordsActivity.this, android.R.layout.simple_spinner_item, arrayListLibMember);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_lib_member.setAdapter(aa);

        spinner_lib_member.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String libMemberNAme = parent.getItemAtPosition(position).toString();
                String libMemberP = String.valueOf(parent.getItemIdAtPosition(position));
                String s = spinner_lib_member.getSelectedItem().toString();

                if (libMemberNAme.equals("Student")) {
                    type = "2";
                    ll_teacher_hide.setVisibility(View.GONE);
                    ll_student_hide.setVisibility(View.VISIBLE);

                    //============= Api Call ==========
                    if (NetworkUtils.isNetworkAvailable(LibraryAddRecordsActivity.this)) {
                        doClassList();
                    } else {
                        NetworkUtils.isNetworkNotAvailable(LibraryAddRecordsActivity.this);
                    }
                } else if (libMemberNAme.equals("Staff")) {
                    type = "1";
                    ll_teacher_hide.setVisibility(View.VISIBLE);
                    ll_student_hide.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });

    }

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

                            selectClassList.clear();
                            for (ClassListModule classListModuleArray : classListModuleArrayArrayList) {
                                selectClassList.add(classListModuleArray.getClass_name());
                            }

                            //===== Api Call =======
                            classListSpinner();

                        } else {
                            Toasty.warning(LibraryAddRecordsActivity.this, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.warning(LibraryAddRecordsActivity.this, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(LibraryAddRecordsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void classListSpinner() {
        ArrayAdapter aa = new ArrayAdapter(LibraryAddRecordsActivity.this, android.R.layout.simple_spinner_item, selectClassList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_class.setAdapter(aa);

        spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                sltClass = parent.getItemAtPosition(position).toString();
                String s = spinner_class.getSelectedItem().toString();
//                Toast.makeText(mContext, "" + sltClass, Toast.LENGTH_SHORT).show();

                try {
                    int clsId = Integer.parseInt(classListModuleArrayArrayList.get(position).getClass_id());
                    classId = String.valueOf(clsId);

                    //============ Api Call ================
                    if (NetworkUtils.isNetworkAvailable(LibraryAddRecordsActivity.this))
                        doDivisionList(classId);
                    else
                        NetworkUtils.isNetworkNotAvailable(LibraryAddRecordsActivity.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
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
                            Toasty.warning(LibraryAddRecordsActivity.this, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.warning(LibraryAddRecordsActivity.this, "Server Response Err !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(LibraryAddRecordsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });

    }

    private void divisionListSpinner() {
//        selectDivisionList.add(0, "Select Division");

        ArrayAdapter bb = new ArrayAdapter(LibraryAddRecordsActivity.this, android.R.layout.simple_spinner_item, selectDivisionList);
        bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_division.setAdapter(bb);
        bb.notifyDataSetChanged();

        spinner_division.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                sltDiv = parent.getItemAtPosition(position).toString();
                String s = spinner_division.getSelectedItem().toString();
//                Toast.makeText(mContext, "" + sltDiv, Toast.LENGTH_SHORT).show();

                try {
                    int clsId = Integer.parseInt(classDivisionModuleArrayList.get(position).getDivision_id());
                    divId = String.valueOf(clsId);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void popUpStudent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LibraryAddRecordsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_library_std_list, null);
        builder.setView(dialogView);
        builder.setCancelable(true);

        alertDialog = builder.create();

        alertDialog.show();

        final EditText edt_search_student = alertDialog.findViewById(R.id.edt_search_student);
        recycler_view_lib_student = alertDialog.findViewById(R.id.recycler_view_lib_student);
        iv_empty_list = alertDialog.findViewById(R.id.iv_empty_list);
        Button btn_lib_std_select = alertDialog.findViewById(R.id.btn_lib_std_select);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(LibraryAddRecordsActivity.this, 3, GridLayoutManager.VERTICAL, false);
        if (recycler_view_lib_student != null) {
            recycler_view_lib_student.setLayoutManager(gridLayoutManager);
            recycler_view_lib_student.setHasFixedSize(true);
        }

        btn_lib_std_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        //============ Api Call ================
        if (NetworkUtils.isNetworkAvailable(this))
            doStudentList();
        else
            NetworkUtils.isNetworkNotAvailable(this);
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

                            studentAdapter = new LibraryStudentAdapter(LibraryAddRecordsActivity.this, studentListModuleArrayArrayList);
                            recycler_view_lib_student.setAdapter(studentAdapter);
                            studentAdapter.notifyDataSetChanged();

                            if (studentListModuleArrayArrayList.size() != 0) {
                                recycler_view_lib_student.setVisibility(View.VISIBLE);
                                iv_empty_list.setVisibility(View.GONE);
                            } else {
                                recycler_view_lib_student.setVisibility(View.GONE);
                                iv_empty_list.setVisibility(View.VISIBLE);
                            }

                            studentAdapterOnClick();

                        } else {
                            Toasty.warning(LibraryAddRecordsActivity.this, "Response Null !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(LibraryAddRecordsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void studentAdapterOnClick() {
        studentAdapter.setOnItemClickListener(new LibraryStudentAdapter.OnItemClickListener() {
            @Override
            public void onAdapterClick(StudentListModule studentListModule, int position) {
                sId = studentListModule.getStudent_id();
                Toast.makeText(LibraryAddRecordsActivity.this, "" + sId, Toast.LENGTH_SHORT).show();
                sCount = "1";
                tv_slt_count_student_lib.setText(sCount);
                alertDialog.dismiss();
            }
        });
    }


    private void popUpTeacher() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LibraryAddRecordsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_library_teacher_list, null);
        builder.setView(dialogView);
        builder.setCancelable(true);

        teacherAlertDialog = builder.create();

        teacherAlertDialog.show();

        final EditText edt_search_teacher = teacherAlertDialog.findViewById(R.id.edt_search_teacher);
        recycler_view_lib_teacher = teacherAlertDialog.findViewById(R.id.recycler_view_lib_teacher);
        iv_empty_list_lib_t = teacherAlertDialog.findViewById(R.id.iv_empty_list_lib_t);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(LibraryAddRecordsActivity.this, 3, GridLayoutManager.VERTICAL, false);
        recycler_view_lib_teacher.setLayoutManager(gridLayoutManager);
        recycler_view_lib_teacher.setHasFixedSize(true);

        //============ Api Call ================
        if (NetworkUtils.isNetworkAvailable(this))
            doTeacherList();
        else
            NetworkUtils.isNetworkNotAvailable(this);
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

                            libraryTeacherAdapter = new LibraryTeacherAdapter(LibraryAddRecordsActivity.this, teacherListModuleArrayArrayList);
                            recycler_view_lib_teacher.setAdapter(libraryTeacherAdapter);
                            libraryTeacherAdapter.notifyDataSetChanged();

                            if (teacherListModuleArrayArrayList.size() != 0) {
                                recycler_view_lib_teacher.setVisibility(View.VISIBLE);
                                iv_empty_list_lib_t.setVisibility(View.GONE);
                            } else {
                                recycler_view_lib_teacher.setVisibility(View.GONE);
                                iv_empty_list_lib_t.setVisibility(View.VISIBLE);
                            }

                            teacherAdapterOnClick();

                        } else {
                            Toasty.warning(LibraryAddRecordsActivity.this, "Response Null !!", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(LibraryAddRecordsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void teacherAdapterOnClick() {
        libraryTeacherAdapter.setOnItemClickListener(new LibraryTeacherAdapter.OnItemClickListener() {
            @Override
            public void onAdapterClick(TeacherListModule teacherListModule, int position) {
                tId = teacherListModule.getUser_id();
                Toast.makeText(LibraryAddRecordsActivity.this, "" + tId, Toast.LENGTH_SHORT).show();
                tCount = "1";
                tv_slt_count_teacher_lib.setText(tCount);
                teacherAlertDialog.dismiss();
            }
        });
    }

    private void studentValidation() {
        String tCountNo = tv_slt_count_student_lib.getText().toString().trim();
        String book_id = edt_book_id.getText().toString().trim();
        String book_name = edt_book_name.getText().toString().trim();
        String issue_date = edt_issue_date.getText().toString().trim();
        String return_date = edt_return_date.getText().toString().trim();

        if (tCountNo.equalsIgnoreCase("0")) {
            Toasty.warning(this, "Please Select Student", Toast.LENGTH_SHORT, true).show();

        } else if (book_id.equalsIgnoreCase("")) {
            edt_book_id.setError("Please Enter Book Id");
            edt_book_id.requestFocus();

        } else if (book_name.equalsIgnoreCase("")) {
            edt_book_name.setError("Please Enter Book Name");
            edt_book_name.requestFocus();

        } else if (return_date.equalsIgnoreCase("")) {
            Toasty.warning(LibraryAddRecordsActivity.this, "Please Select Return date", Toasty.LENGTH_SHORT, true).show();

        } else {
            String userId = sId;
            //===== Api Call =======
            if (NetworkUtils.isNetworkAvailable(LibraryAddRecordsActivity.this)) {
                doAddRecord(userId, classId, divId, book_id, book_name, issue_date, return_date);
            } else
                NetworkUtils.isNetworkNotAvailable(LibraryAddRecordsActivity.this);

        }
    }

    private void teacherValidation() {
        String tCountNo = tv_slt_count_teacher_lib.getText().toString().trim();
        String book_id = edt_book_id.getText().toString().trim();
        String book_name = edt_book_name.getText().toString().trim();
        String issue_date = edt_issue_date.getText().toString().trim();
        String return_date = edt_return_date.getText().toString().trim();

        if (tCountNo.equalsIgnoreCase("0")) {
            Toasty.warning(this, "Please Select Teacher", Toast.LENGTH_SHORT, true).show();

        } else if (book_id.equalsIgnoreCase("")) {
            edt_book_id.setError("Please Enter Book Id");
            edt_book_id.requestFocus();

        } else if (book_name.equalsIgnoreCase("")) {
            edt_book_name.setError("Please Enter Book Name");
            edt_book_name.requestFocus();

        } else if (return_date.equalsIgnoreCase("")) {
            Toasty.warning(LibraryAddRecordsActivity.this, "Please Select Return date", Toasty.LENGTH_SHORT, true).show();

        } else {
            classId = "";
            divId = "";
            String userId = tId;
            //===== Api Call =======
            if (NetworkUtils.isNetworkAvailable(LibraryAddRecordsActivity.this)) {
                doAddRecord(userId, classId, divId, book_id, book_name, issue_date, return_date);
            } else
                NetworkUtils.isNetworkNotAvailable(LibraryAddRecordsActivity.this);
        }
    }

    private void doAddRecord(String userId, String classId, String divId, String bookId, String book_name, String issue_date, String return_date) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.libraryAddCall(uId, userId, type, classId, divId, bookId, book_name, issue_date, return_date);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {

                        SuccessModule classListModule = response.body();

                        String message = classListModule.getMsg();
                        String code = classListModule.getError_code();

                        if (code.equalsIgnoreCase("1")) {
//                            Toasty.success(LibraryAddRecordsActivity.this, "Add Record Successfully", Toasty.LENGTH_SHORT, true).show();
                            successDialog = new SuccessDialog(LibraryAddRecordsActivity.this, true);
                            successDialog.showDialog("Add Record Successfully !!", true);
                        } else {
                            Toasty.warning(LibraryAddRecordsActivity.this, "Add Record Unsuccessfully", Toasty.LENGTH_SHORT, true).show();
                        }

                    } else {
                        Toasty.warning(LibraryAddRecordsActivity.this, "Not Response", Toasty.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(LibraryAddRecordsActivity.this, "" + t.getMessage(), Toasty.LENGTH_SHORT, true).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        editor.putString(SharedPreference.REFRESH, "10");
        editor.apply();
        Intent intent = new Intent(LibraryAddRecordsActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }

    @Override
    public void dialogClosed(boolean mClosed) {
        finish();
        onBackPressed();
    }
}
