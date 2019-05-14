package com.shreeyesh.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.StudentPAListModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.adapter.StudentPAListAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AttendanceStdFragment extends Fragment {

    public GregorianCalendar cal_month, cal_month_copy;
    Context mContext;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String isSelectUser, uId;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";

    private TextView tv_month, txt_present_count_std, txt_absent_count_std;
    LinearLayout ll_prevMonth, ll_nextMonth;

    String monthName;
    PieChart mChart;
    // we're going to display pie chart for school attendance
    private int[] yValues;
    private String[] xValues = {"Present Days", "Absents Days"};

    GridView gridview;
    ArrayList<StudentPAListModule> studentPAListModuleArrayList = new ArrayList<>();
    private StudentPAListAdapter studentPAListAdapter;

    public AttendanceStdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_std, container, false);
        mContext = getActivity();

        find_All_IDs(view);


        if (mContext != null) {
            sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        }
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        uId = SharedPreference.getUserID(mContext);

        cal_month = (GregorianCalendar) GregorianCalendar.getInstance();

        cal_month_copy = (GregorianCalendar) cal_month.clone();

//        hwAdapter = new StudentPAListAdapter(mContext, cal_month,HomeCollection.date_collection_arr);


        tv_month.setText(DateFormat.format("MMMM yyyy", cal_month));
        monthName = tv_month.getText().toString().trim();

        String aa = String.valueOf(DateFormat.format("MM", cal_month));
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            attendanceList(aa);
        } else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }
        events();

        return view;

    }//========== End onCreate () ==============

    private void find_All_IDs(View view) {
        gridview = view.findViewById(R.id.gv_calendar);
        tv_month = view.findViewById(R.id.tv_month);
        ll_prevMonth = view.findViewById(R.id.ll_prevMonth);
        ll_nextMonth = view.findViewById(R.id.ll_nextMonth);
        txt_present_count_std = view.findViewById(R.id.txt_present_count_std);
        txt_absent_count_std = view.findViewById(R.id.txt_absent_count_std);

        mChart = view.findViewById(R.id.chart1);
    }

    private void events() {
        ll_prevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cal_month.get(GregorianCalendar.MONTH) == 4 && cal_month.get(GregorianCalendar.YEAR) == 2018) {
                    //cal_month.set((cal_month.get(GregorianCalendar.YEAR) - 1), cal_month.getActualMaximum(GregorianCalendar.MONTH), 1);
                    Toast.makeText(mContext, "Available for ( 2018-2030 ) Year only.", Toast.LENGTH_SHORT).show();
                } else {
                    if (NetworkUtils.isNetworkAvailable(mContext)) {
                        setPreviousMonth();
                        refreshCalendar();
                    } else {
                        NetworkUtils.isNetworkNotAvailable(mContext);
                    }
                }
            }
        });

        ll_nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cal_month.get(GregorianCalendar.MONTH) == 5 && cal_month.get(GregorianCalendar.YEAR) == 2030) {
                    //cal_month.set((cal_month.get(GregorianCalendar.YEAR) + 1), cal_month.getActualMinimum(GregorianCalendar.MONTH), 1);
                    Toast.makeText(mContext, "Available for ( 2018-2030 ) Year only.", Toast.LENGTH_SHORT).show();
                } else {
                    if (NetworkUtils.isNetworkAvailable(mContext)) {
                        setNextMonth();
                        refreshCalendar();
                    } else {
                        NetworkUtils.isNetworkNotAvailable(mContext);
                    }
                }
            }
        });

    }

    private void pieChart() {
        //   mChart.setUsePercentValues(true);
        mChart.setDescription("");
        mChart.setCenterText(monthName);

        mChart.setRotationEnabled(true);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                // display msg when value selected
                if (e == null)
                    return;

//                Toast.makeText(mContext, xValues[e.getXIndex()] + " is " + e.getVal() + "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // setting sample Data for Pie Chart
        setDataForPieChart();

    }

    public void setDataForPieChart() {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < yValues.length; i++)
            yVals1.add(new Entry(yValues[i], i));

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < xValues.length; i++)
            xVals.add(xValues[i]);

        // create pieDataSet
        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        // adding colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

//        colors.add(Color.GREEN);
//        colors.add(Color.RED);
        colors.add((Color.parseColor("#DC68F857")));
        colors.add((Color.parseColor("#D0FF3D00")));


        dataSet.setColors(colors);

        //  create pie data object and set xValues and yValues and set it to the pieChart
        PieData data = new PieData(xVals, dataSet);
        //   data.setValueFormatter(new DefaultValueFormatter());
        //   data.setValueFormatter(new PercentFormatter());

        data.setValueFormatter(new MyValueFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        // refresh/update pie chart
        mChart.invalidate();

        // animate piechart
        mChart.animateXY(1400, 1400);


        // Legends to show on bottom of the graph
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
    }


    public class MyValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0"); // use one decimal if needed
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value) + ""; // e.g. append a dollar-sign
        }
    }


    protected void setNextMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month.getActualMaximum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) + 1), cal_month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH,
                    cal_month.get(GregorianCalendar.MONTH) + 1);
        }

        String aa = String.valueOf(DateFormat.format("MM", cal_month));
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            attendanceList(aa);
        } else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }
    }

    protected void setPreviousMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month.getActualMinimum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) - 1), cal_month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH, cal_month.get(GregorianCalendar.MONTH) - 1);
        }
        String aa = String.valueOf(DateFormat.format("MM", cal_month));
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            attendanceList(aa);
        } else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }
    }

    public void refreshCalendar() {
        studentPAListAdapter.refreshDays();
        studentPAListAdapter.notifyDataSetChanged();
        tv_month.setText(DateFormat.format("MMMM yyyy", cal_month));
        monthName = tv_month.getText().toString().trim();
    }

    private void attendanceList(String finalMonth) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.studentAttMonthCall(finalMonth, uId);

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
                                int present = 0, absent = 0;
                                studentPAListModuleArrayList = successModule.getStudentPAListModuleArrayList();

                                for (StudentPAListModule studentPAListModule : studentPAListModuleArrayList) {
                                    String paStatus = studentPAListModule.getAttendance_status();
                                    if ((paStatus.equalsIgnoreCase("0"))) {
                                        absent = absent + 1;
                                    } else if ((paStatus.equalsIgnoreCase("1"))) {
                                        present = present + 1;
                                    }
                                }

                                String p = String.valueOf(present);
                                String a = String.valueOf(absent);
                                String c = String.valueOf(present + absent);
                                txt_present_count_std.setText(p + " / " + c);
                                txt_absent_count_std.setText(a + " / " + c);

                                yValues = new int[]{present, absent};
                                pieChart();

                                studentPAListAdapter = new StudentPAListAdapter(mContext, cal_month, studentPAListModuleArrayList);
                                gridview.setAdapter(studentPAListAdapter);
                                refreshCalendar();

//                                Toasty.success(mContext, "Attendance List Display Successfully !!", Toast.LENGTH_SHORT, true).show();

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
}