package com.shreeyesh.ui.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.ClassDivisionModule;
import com.shreeyesh.domain.module.ClassListModule;
import com.shreeyesh.domain.module.HomeWorkModule;
import com.shreeyesh.domain.module.StudentAttendanceListModule;
import com.shreeyesh.domain.module.StudentListModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.HomeActivity;
import com.shreeyesh.ui.adapter.StudentAttendanceAdapter;
import com.shreeyesh.ui.adapter.StudentAttendanceListAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.shreeyesh.utils.SuccessDialog;
import com.shreeyesh.utils.SuccessDialogClosed;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AttendanceFragment extends Fragment implements SuccessDialogClosed {

    private SuccessDialog successDialog;
    Context mContext;
    Spinner spinner_class_list, spinner_division_list;
    TextView tv_select_student, tv_stud_count_attendance;
    TextView txt_present_count, txt_absent_count;
    EditText edt_attendance_date;
    Button btn_attendance_save;
    boolean buttonClick = false;
    boolean isSelectedAll = false;
    int selectCount = 0, unSelectCount = 0;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, uType;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";

    //========= spinner class list module ===========
    ArrayList<ClassListModule> classListModuleArrayArrayList = new ArrayList<>();
    ArrayList<String> selectClassList = new ArrayList<>();

    //========= spinner division list module ===========
    ArrayList<ClassDivisionModule> classDivisionModuleArrayList = new ArrayList<>();
    ArrayList<String> selectDivisionList = new ArrayList<>();

    String classId = "", divId = "", currentDate = "", date = "", is_attendance = "", sltClass = "", sltDiv = "", sCount = "0";
    CharSequence strDate = null;
    String sltCount;

    //=========== Student list dialog =======
    ArrayList<StudentListModule> studentListModuleArrayArrayList = new ArrayList<>();
    StudentAttendanceAdapter studentAttendanceAdapter;
    RecyclerView recycler_view_attendance_student_dialog;
//    ImageView iv_empty_list_std_attend;

    JSONArray selectJSONArray, unSelectJSONArray, updateSelectJSONArray;

    //===========attendance list
    ArrayList<StudentAttendanceListModule> studentAttendanceListModuleArrayList = new ArrayList<>();
    StudentAttendanceListAdapter studentAttendanceListAdapter;

    public AttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        mContext = getActivity();

        find_All_IDs(view);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        uId = SharedPreference.getUserID(mContext);
        uType = SharedPreference.getUserType(mContext);
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        //============ Api Call ================
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            doClassList();
        }else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }

        event();

        return view;

    }//========== End onCreate () ==============

    private void getDate(String currentDate) {
        edt_attendance_date.setText(currentDate);
        date = edt_attendance_date.getText().toString().trim();
        //==== Api Call =========
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            doAttendanceSecondListCall(date);
        }else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }
    }

    private void find_All_IDs(View view) {
        spinner_class_list = view.findViewById(R.id.spinner_class_list);
        spinner_division_list = view.findViewById(R.id.spinner_division_list);
        tv_select_student = view.findViewById(R.id.tv_select_student);
        tv_stud_count_attendance = view.findViewById(R.id.tv_stud_count_attendance);
        edt_attendance_date = view.findViewById(R.id.edt_attendance_date);
        btn_attendance_save = view.findViewById(R.id.btn_attendance_save);
    }

    private void event() {
        edt_attendance_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        Time chosenDate = new Time();
                        chosenDate.set(day, month, year);

                        long dateAttendance = chosenDate.toMillis(true);
                        currentDate = String.valueOf(DateFormat.format("yyyy-MM-dd", dateAttendance));
                        getDate(currentDate);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        tv_select_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doValidation();
            }
        });

        btn_attendance_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClick) {
                    btn_attendance_save.setClickable(false);
                } else {
                    saveValidation();
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

                            //===== Api Call =======
                            classListSpinner();

                        } else {
                            Toasty.info(mContext, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.info(mContext, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void classListSpinner() {
//        selectClassList.add(0, "Select Class");

        ArrayAdapter aa = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, selectClassList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_class_list.setAdapter(aa);

        spinner_class_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                sltClass = parent.getItemAtPosition(position).toString();
                String s = spinner_class_list.getSelectedItem().toString();
//                Toast.makeText(mContext, "" + sltClass, Toast.LENGTH_SHORT).show();

                try {
                    int clsId = Integer.parseInt(classListModuleArrayArrayList.get(position).getClass_id());
                    classId = String.valueOf(clsId);

                    //============ Api Call ================
                    if (NetworkUtils.isNetworkAvailable(mContext)) {
                        doDivisionList(classId);
                    }else {
                        NetworkUtils.isNetworkNotAvailable(mContext);
                    }

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
                            Toasty.info(mContext, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.info(mContext, "Server Response Err !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });

    }

    private void divisionListSpinner() {
//        selectDivisionList.add(0, "Select Division");

        ArrayAdapter bb = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, selectDivisionList);
        bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_division_list.setAdapter(bb);
        bb.notifyDataSetChanged();

        spinner_division_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                sltDiv = parent.getItemAtPosition(position).toString();
                String s = spinner_division_list.getSelectedItem().toString();
//                Toast.makeText(mContext, "" + sltDiv, Toast.LENGTH_SHORT).show();

                try {
                    int clsId = Integer.parseInt(classDivisionModuleArrayList.get(position).getDivision_id());
                    divId = String.valueOf(clsId);

                    //============ Api Call ================
                    if (NetworkUtils.isNetworkAvailable(mContext)) {
                        getDate(currentDate);
                    }else {
                        NetworkUtils.isNetworkNotAvailable(mContext);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void doValidation() {
        if (sltClass.equalsIgnoreCase("")) {
            Toasty.warning(mContext, "Please Select Class", Toast.LENGTH_SHORT, true).show();
        } /*else if (sltDiv.equalsIgnoreCase("")) {
            Toasty.warning(mContext, "Please Select Division", Toast.LENGTH_SHORT, true).show();
        }*/ else if (date.equalsIgnoreCase("")) {
            Toasty.warning(mContext, "Please Select Date", Toast.LENGTH_SHORT, true).show();
        } else {
            popUpStudent();

        }
    }

    private void saveValidation() {
        if (sltClass.equalsIgnoreCase("")) {
            Toasty.warning(mContext, "Please Select Class", Toast.LENGTH_SHORT, true).show();
        } else if (sltDiv.equalsIgnoreCase("")) {
            Toasty.warning(mContext, "Please Select Division", Toast.LENGTH_SHORT, true).show();
        }  else if (date.equalsIgnoreCase("")) {
            Toasty.warning(mContext, "Please Select Date", Toast.LENGTH_SHORT, true).show();
        } else if (sCount.equalsIgnoreCase("")) {
            Toasty.warning(mContext, "Please Select Student", Toast.LENGTH_SHORT, true).show();
        } else if (sCount.equalsIgnoreCase("0")) {
            Toasty.warning(mContext, "Please Select Student", Toast.LENGTH_SHORT, true).show();
        } else {
            //============ Api Call ================
            if (NetworkUtils.isNetworkAvailable(mContext)) {
                buttonClick = true;
                attendanceFirstCall();
            }else {
                NetworkUtils.isNetworkNotAvailable(mContext);
            }
        }
    }

    private void popUpStudent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_attendance_student_list, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog alertDialog = builder.create();

        alertDialog.show();

        final EditText edt_search_attendance_std = alertDialog.findViewById(R.id.edt_search_attendance_std);
        recycler_view_attendance_student_dialog = alertDialog.findViewById(R.id.recycler_view_attendance_student_dialog);
        Button btn_attendance = alertDialog.findViewById(R.id.btn_attendance);
        txt_present_count = alertDialog.findViewById(R.id.txt_present_count);
        txt_absent_count = alertDialog.findViewById(R.id.txt_absent_count);

        String selectCount1 = String.valueOf(selectCount);
        String unSelectCount1 = String.valueOf(unSelectCount);

        txt_present_count.setText(selectCount1);
        txt_absent_count.setText(unSelectCount1);


        if (btn_attendance != null) {
            btn_attendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (updateSelectJSONArray != null) {
                        try {
                            selectJSONArray = concatArray(selectJSONArray, updateSelectJSONArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        Toast.makeText(mContext, "Slt" + selectJSONArray + "  Un  " + unSelectJSONArray, Toast.LENGTH_SHORT).show();
                    }
                    alertDialog.dismiss();
                }
            });
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
        recycler_view_attendance_student_dialog.setLayoutManager(gridLayoutManager);
        recycler_view_attendance_student_dialog.setHasFixedSize(true);

        //============ Api Call ================
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            if (is_attendance.equalsIgnoreCase("true"))
                doAttendanceSecondListCall(date);
            else /*if (is_attendance.equalsIgnoreCase("false")||is_attendance.equalsIgnoreCase(""))*/
                studentList();

        } else
            NetworkUtils.isNetworkNotAvailable(mContext);

        //=============== Search view code =============
        if (edt_search_attendance_std != null) {
            edt_search_attendance_std.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (is_attendance.equalsIgnoreCase("true")) {
                        studentAttendanceListAdapter.getFilter().filter(edt_search_attendance_std.getText().toString());
                    } else {
                        studentAttendanceAdapter.getFilter().filter(edt_search_attendance_std.getText().toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    private void studentList() {
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

                            studentAttendanceAdapter = new StudentAttendanceAdapter(mContext, studentListModuleArrayArrayList);
                            recycler_view_attendance_student_dialog.setAdapter(studentAttendanceAdapter);
                            studentAttendanceAdapter.notifyDataSetChanged();

                            ArrayList<String> SelectArrayList = new ArrayList<>();
                            String selectAllId = "";

                            for (final StudentListModule model : studentListModuleArrayArrayList) {
                                selectAllId = model.getStudent_id();
                                SelectArrayList.add(selectAllId);

                                selectJSONArray = new JSONArray(SelectArrayList);
                            }


                            if (selectJSONArray != null) {
                                sCount = String.valueOf(selectJSONArray.length());
                                tv_stud_count_attendance.setText(sCount);
                            } else {
                                tv_stud_count_attendance.setText(sCount);
                            }

                            studentAttendanceAdapter.setOnItemClickListener(new StudentAttendanceAdapter.OnItemClickListener() {
                                @Override
                                public void onAdapterClick(StudentListModule studentListModule, int position) {
                                    ArrayList<String> SelectArrayList = new ArrayList<>();
                                    ArrayList<String> unSelectArrayList = new ArrayList<>();
                                    String selectId = "", unSelectId = "";

                                    studentListModule.setSelected(!studentListModule.isSelected());

                                    for (final StudentListModule model : studentListModuleArrayArrayList) {
                                        if (model.isSelected()) {
                                            selectId = model.getStudent_id();
                                            SelectArrayList.add(selectId);

                                        } else if (!model.isSelected()) {
                                            unSelectId = model.getStudent_id();
                                            unSelectArrayList.add(unSelectId);
                                        }

                                        unSelectJSONArray = new JSONArray(SelectArrayList);
                                        selectJSONArray = new JSONArray(unSelectArrayList);

                                        sltCount = String.valueOf(selectJSONArray.length());
                                        txt_present_count.setText(sltCount);
                                        String uSltCount = String.valueOf(unSelectJSONArray.length());
                                        txt_absent_count.setText(uSltCount);
                                    }

                                    if (selectJSONArray != null) {
                                        sCount = String.valueOf(selectJSONArray.length());
                                        tv_stud_count_attendance.setText(sCount);
                                    } else {
                                        tv_stud_count_attendance.setText(sCount);
                                    }

                                    Toast.makeText(mContext, "" + selectJSONArray + "     " + unSelectJSONArray, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toasty.info(mContext, "Response Null !!", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void doAttendanceSecondListCall(String date) {
        selectCount = 0;
        unSelectCount = 0;
        Call<HomeWorkModule> call = apiInterface.attendanceListCall(classId, divId, uId, date);

        call.enqueue(new Callback<HomeWorkModule>() {
            @Override
            public void onResponse(Call<HomeWorkModule> call, Response<HomeWorkModule> response) {
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        HomeWorkModule studentAttendanceListModule = response.body();

                        if (studentAttendanceListModule != null) {
                            is_attendance = studentAttendanceListModule.getIs_atten();

                            studentAttendanceListModuleArrayList = studentAttendanceListModule.getStudentAttendanceListModule();

                            ArrayList<String> selectArrayList11 = new ArrayList<>();
                            String selectId = "";

                            for (final StudentAttendanceListModule model : studentAttendanceListModuleArrayList) {
                                String statusCheck = model.getAttendance_status();

                                if (statusCheck.equalsIgnoreCase("0")) {
                                    unSelectCount = unSelectCount + 1;
                                } else if (statusCheck.equalsIgnoreCase("1")) {
                                    selectCount = selectCount + 1;

                                    selectId = model.getStudent_id();
                                    selectArrayList11.add(selectId);
                                }
                                selectJSONArray = new JSONArray(selectArrayList11);
                            }


                            studentAttendanceListAdapter = new StudentAttendanceListAdapter(mContext, studentAttendanceListModuleArrayList);
                            recycler_view_attendance_student_dialog.setAdapter(studentAttendanceListAdapter);
                            studentAttendanceListAdapter.notifyDataSetChanged();


                            studentAttendanceListAdapter.setOnItemClickListener(new StudentAttendanceListAdapter.OnItemClickListener() {
                                @Override
                                public void onAdapterClick(StudentAttendanceListModule studentAttendanceListModule, int position) {
                                    ArrayList<String> selectArrayList = new ArrayList<>();
                                    ArrayList<String> unSelectArrayList = new ArrayList<>();
                                    String selectId = "", unSelectId = "";
                                    studentAttendanceListModule.setSelected(!studentAttendanceListModule.isSelected());

                                    for (final StudentAttendanceListModule model : studentAttendanceListModuleArrayList) {
                                        if (model.isPresent()) {
                                            if (model.getAttendance_status().equalsIgnoreCase("0")) {
                                                if (model.isSelected()) {
                                                    selectId = model.getStudent_id();
                                                    selectArrayList.add(selectId);

                                                } else if (!model.isSelected()) {
                                                    unSelectId = model.getStudent_id();
                                                    unSelectArrayList.add(unSelectId);
                                                }
                                            }
                                        }

                                        updateSelectJSONArray = new JSONArray(selectArrayList);
                                        unSelectJSONArray = new JSONArray(unSelectArrayList);

                                        /*String a = String.valueOf(updateSelectJSONArray.length());
                                        int bb = Integer.parseInt(sltCount + a);
                                        String finalUpdateCount= String.valueOf(bb);
                                        txt_present_count.setText(finalUpdateCount);

                                        String aa = String.valueOf(unSelectJSONArray.length());
                                        txt_absent_count.setText(aa);*/
                                    }

                                    if (updateSelectJSONArray != null) {
                                        sCount = String.valueOf(updateSelectJSONArray.length());
                                        tv_stud_count_attendance.setText(sCount);
                                    } else {
                                        tv_stud_count_attendance.setText(sCount);
                                    }

                                }
                            });


                        } else {
                            Toasty.info(mContext, "Response Null !!", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<HomeWorkModule> call, Throwable t) {
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }


    private JSONArray concatArray(JSONArray arr1, JSONArray arr2) throws JSONException {
        JSONArray result = new JSONArray();
        for (int i = 0; i < arr1.length(); i++) {
            result.put(arr1.get(i));
        }
        for (int i = 0; i < arr2.length(); i++) {
            result.put(arr2.get(i));
        }
        return result;
    }


    private void attendanceFirstCall() {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.attendanceAddCall(classId, divId, uId, selectJSONArray, unSelectJSONArray, date);

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

                                successDialog = new SuccessDialog(mContext);
                                successDialog.showDialog("Attendance Added Successfully !!", true);

                                ((HomeActivity) mContext).replaceFragment(new HomeFragment());

                            } else {
                                Toasty.info(mContext, "Not Response !!", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    } else {
                        Toasty.info(mContext, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    @Override
    public void dialogClosed(boolean mClosed) {
    }
}
