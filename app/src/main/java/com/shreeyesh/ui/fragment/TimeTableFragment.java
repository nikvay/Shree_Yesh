package com.shreeyesh.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.ClassDivisionModule;
import com.shreeyesh.domain.module.ClassListModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.module.TimeTableModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.domain.network.BaseApi;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class TimeTableFragment extends Fragment {

    Context mContext;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, uType, isSelectUser, timeTable_notes;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";

    Spinner  spinner_class_list_time_table, spinner_division_list_time_table;
    LinearLayout ll_select_class, ll_select_div,  ll_exam_timetable, ll_lecture_timetable, ll_time_table_hide_class;
    WebView web_view_time_table;
    String  sltClass = "", sltDiv = "", sltPosition = "", documentUrl = "";

    //========= class list module ===========
    ArrayList<ClassListModule> classListModuleArrayArrayList = new ArrayList<>();
    ArrayList<String> selectClassList = new ArrayList<>();
    String classId, divId;
    //========= spinner division list module ===========
    ArrayList<ClassDivisionModule> classDivisionModuleArrayList = new ArrayList<>();
    ArrayList<String> selectDivisionList = new ArrayList<>();

    ArrayList<TimeTableModule> timeTableModuleArrayList = new ArrayList<>();

    public TimeTableFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_table, container, false);
        mContext = getActivity();

        find_All_IDs(view);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        timeTable_notes = sharedpreferences.getString(SharedPreference.TIME_TABLE_NOTES, "");
        uId = SharedPreference.getUserID(mContext);
        uType = SharedPreference.getUserType(mContext);

        webViewDisplay();

        if (isSelectUser.equalsIgnoreCase("2")) {
            if (NetworkUtils.isNetworkAvailable(mContext)) {
                doClassList();
            } else {
                NetworkUtils.isNetworkNotAvailable(mContext);
            }

        } else if (isSelectUser.equalsIgnoreCase("3")) {
            ll_time_table_hide_class.setVisibility(View.GONE);
        }

        events();

        return view;
    }//========== End onCreate () ==============

    private void find_All_IDs(View view) {
        spinner_class_list_time_table = view.findViewById(R.id.spinner_class_list_time_table);
        spinner_division_list_time_table = view.findViewById(R.id.spinner_division_list_time_table);

        ll_select_class = view.findViewById(R.id.ll_select_class);
        ll_select_div = view.findViewById(R.id.ll_select_div);

        ll_exam_timetable = view.findViewById(R.id.ll_exam_timetable);
        ll_lecture_timetable = view.findViewById(R.id.ll_lecture_timetable);
        ll_time_table_hide_class = view.findViewById(R.id.ll_time_table_hide_class);

        web_view_time_table = view.findViewById(R.id.web_view_time_table);
    }

    private void events() {
        ll_lecture_timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sltPosition = "1";
                if (isSelectUser.equalsIgnoreCase("2")) {
                    validation();
                } else if (isSelectUser.equalsIgnoreCase("3")) {
                    if (NetworkUtils.isNetworkAvailable(mContext)) {
                        displayTimeTableStudent();
                    } else {
                        NetworkUtils.isNetworkNotAvailable(mContext);
                    }
                }
            }
        });

        ll_exam_timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sltPosition = "2";
                if (isSelectUser.equalsIgnoreCase("2")) {
                    validation();
                } else if (isSelectUser.equalsIgnoreCase("3")) {
                    if (NetworkUtils.isNetworkAvailable(mContext)) {
                        displayTimeTableStudent();
                    } else {
                        NetworkUtils.isNetworkNotAvailable(mContext);
                    }
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

                        classListModuleArrayArrayList.clear();
                        if (code.equalsIgnoreCase("1")) {
                            classListModuleArrayArrayList = classListModule.getClassListModuleArrayArrayList();

                            for (ClassListModule classListModuleArray : classListModuleArrayArrayList) {
                                selectClassList.add(classListModuleArray.getClass_name());
                            }

                            spinnerClass();

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

    private void spinnerClass() {
        ArrayAdapter aa = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, selectClassList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_class_list_time_table.setAdapter(aa);

        spinner_class_list_time_table.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String sltClass1 = parent.getItemAtPosition(position).toString();
                sltClass = String.valueOf(parent.getItemIdAtPosition(position));
                String s = spinner_class_list_time_table.getSelectedItem().toString();
//                Toast.makeText(mContext, "" + sltClass1, Toast.LENGTH_SHORT).show();

                try {
                    int clsId = Integer.parseInt(classListModuleArrayArrayList.get(position).getClass_id());
                    classId = String.valueOf(clsId);
//                    Toast.makeText(mContext, "" + classId, Toast.LENGTH_SHORT).show();
                    if (NetworkUtils.isNetworkAvailable(mContext)) {
                        doDivisionList(classId);
                    } else {
                        NetworkUtils.isNetworkNotAvailable(mContext);
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

    private void divisionListSpinner() {
        ArrayAdapter bb = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, selectDivisionList);
        bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_division_list_time_table.setAdapter(bb);
        bb.notifyDataSetChanged();

        spinner_division_list_time_table.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String sltClass = parent.getItemAtPosition(position).toString();
                sltDiv = String.valueOf(parent.getItemIdAtPosition(position));
                String s = spinner_division_list_time_table.getSelectedItem().toString();
//                Toast.makeText(mContext, "" + sltClass, Toast.LENGTH_SHORT).show();

                try {
                    int clsId = Integer.parseInt(classDivisionModuleArrayList.get(position).getDivision_id());
//                    divId = String.valueOf(clsId - 1);
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

    private void validation() {
        if (sltClass.equalsIgnoreCase("")) {
            Toasty.warning(mContext, "Please Select Class", Toast.LENGTH_SHORT, true).show();
        } else if (sltDiv.equalsIgnoreCase("")) {
            Toasty.warning(mContext, "Please Select Division", Toast.LENGTH_SHORT, true).show();
        } else {
            if (NetworkUtils.isNetworkAvailable(mContext)) {
                displayTimeTableTeacher();
            } else {
                NetworkUtils.isNetworkNotAvailable(mContext);
            }
        }
    }

    private void displayTimeTableTeacher() {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();
        Call<SuccessModule> call = apiInterface.timeTableListCall(classId, divId, sltPosition, uId, "");

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModule loginModule = response.body();

                        String message = null, errorCode = null, url = null;
                        if (loginModule != null) {
                            message = loginModule.getMsg();
                            errorCode = loginModule.getError_code();
                            url = loginModule.getImg_base_url();

                            if (errorCode.equalsIgnoreCase("1")) {

                                timeTableModuleArrayList = loginModule.getTimeTableModuleArrayList();
                                String documentName = "";
                                for (TimeTableModule timeTableModule : timeTableModuleArrayList) {
                                    documentName = timeTableModule.getNote_document();
                                }

                                documentUrl = BaseApi.BASE_URL + url + documentName;
                                loadPdfUrlStd(documentUrl);

                                Toasty.success(mContext, "Successful !!", Toast.LENGTH_SHORT, true).show();
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

    private void displayTimeTableStudent() {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();
        Call<SuccessModule> call = apiInterface.timeTableListCall("", "", sltPosition, "", uId);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModule loginModule = response.body();

                        String message = null, errorCode = null, url = null;
                        if (loginModule != null) {
                            message = loginModule.getMsg();
                            errorCode = loginModule.getError_code();
                            url = loginModule.getImg_base_url();

                            if (errorCode.equalsIgnoreCase("1")) {

                                timeTableModuleArrayList = loginModule.getTimeTableModuleArrayList();
                                String documentName = "";
                                for (TimeTableModule timeTableModule : timeTableModuleArrayList) {
                                    documentName = timeTableModule.getNote_document();
                                }

                                documentUrl = BaseApi.BASE_URL + url + documentName;
                                loadPdfUrlStd(documentUrl);

                                Toasty.success(mContext, "Successful !!", Toast.LENGTH_SHORT, true).show();
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

    private void webViewDisplay() {
        web_view_time_table.getSettings().setJavaScriptEnabled(true);
        web_view_time_table.getSettings().setBuiltInZoomControls(true);
        web_view_time_table.setVerticalScrollBarEnabled(true);
        web_view_time_table.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        web_view_time_table.setInitialScale(100);
    }

    private void loadPdfUrlStd(String url) {
        web_view_time_table.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String pdfUrl) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        web_view_time_table.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
    }
}



