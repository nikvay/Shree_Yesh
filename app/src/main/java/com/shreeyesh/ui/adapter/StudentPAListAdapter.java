package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.StudentPAListModule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class StudentPAListAdapter extends BaseAdapter {
    private Context mContext;

    private java.util.Calendar month;
    public GregorianCalendar pmonth;

    public GregorianCalendar pmonthmaxset;
    private GregorianCalendar selectedDate;
    int firstDay, maxWeeknumber, maxP, calMaxP, mnthlength;
    String itemvalue, curentDateString;
    DateFormat df;

    private ArrayList<String> items;
    public static List<String> day_string;
    ArrayList<StudentPAListModule> studentPAListModuleArrayList;
    private String gridvalue;

    public StudentPAListAdapter(Context mContext, GregorianCalendar monthCalendar, ArrayList<StudentPAListModule> studentPAListModuleArrayList) {
        this.mContext = mContext;
        this.studentPAListModuleArrayList = studentPAListModuleArrayList;

        StudentPAListAdapter.day_string = new ArrayList<String>();
        Locale.setDefault(Locale.US);
        month = monthCalendar;
        selectedDate = (GregorianCalendar) monthCalendar.clone();

        month.set(GregorianCalendar.DAY_OF_MONTH, 1);

        this.items = new ArrayList<String>();
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        curentDateString = df.format(selectedDate.getTime());
        refreshDays();

    }

    public int getCount() {
        return day_string.size();
    }

    public Object getItem(int position) {
        return day_string.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        TextView tv_std_att_date;
        if (convertView == null) { // if it's not recycled, initialize some
            // attributes
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.list_row_item_student_pa_list_adapter, null);

        }


        tv_std_att_date = view.findViewById(R.id.tv_std_att_date);

        String[] separatedTime = day_string.get(position).split("-");


        gridvalue = separatedTime[2].replaceFirst("^0*", "");
        if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
            tv_std_att_date.setTextColor(Color.parseColor("#A9A9A9"));
            tv_std_att_date.setClickable(false);
            tv_std_att_date.setFocusable(false);
        } else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
            tv_std_att_date.setTextColor(Color.parseColor("#A9A9A9"));
            tv_std_att_date.setClickable(false);
            tv_std_att_date.setFocusable(false);
        } else {
            // setting curent month's days in blue color.
            tv_std_att_date.setTextColor(Color.parseColor("#696969"));
        }


        if (day_string.get(position).equals(curentDateString)) {
            view.setBackgroundColor(Color.parseColor("#ffffff"));

        } else {
            view.setBackgroundColor(Color.parseColor("#ffffff"));
        }


        tv_std_att_date.setText(gridvalue);

        // create date string for comparison
        String date = day_string.get(position);

        if (date.length() == 1) {
            date = "0" + date;
        }
        String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }

        setEventView(view, position, tv_std_att_date);

        return view;
    }

    public void refreshDays() {
        // clear items
        items.clear();
        day_string.clear();
        Locale.setDefault(Locale.US);
        pmonth = (GregorianCalendar) month.clone();
        // month start day. ie; sun, mon, etc
        firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
        // finding number of weeks in current month.
        maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
        // allocating maximum row number for the gridview.
        mnthlength = maxWeeknumber * 7;
        maxP = getMaxP(); // previous month maximum day 31,30....
        calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
        pmonthmaxset = (GregorianCalendar) pmonth.clone();

        pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

        for (int n = 0; n < mnthlength; n++) {
            itemvalue = df.format(pmonthmaxset.getTime());
            pmonthmaxset.add(GregorianCalendar.DATE, 1);
            day_string.add(itemvalue);
        }
    }

    private int getMaxP() {
        int maxP;
        if (month.get(GregorianCalendar.MONTH) == month.getActualMinimum(GregorianCalendar.MONTH)) {
            pmonth.set((month.get(GregorianCalendar.YEAR) - 1), month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            pmonth.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        return maxP;
    }


    public void setEventView(View v, int pos, TextView tv_std_att_date) {

        int len = studentPAListModuleArrayList.size();
        for (int i = 0; i < len; i++) {
            StudentPAListModule cal_obj = studentPAListModuleArrayList.get(i);
            String date = cal_obj.getDate();
            int len1 = day_string.size();
            if (len1 > pos) {

                if (day_string.get(pos).equals(date)) {
                    if ((Integer.parseInt(gridvalue) > 1) && (pos < firstDay)) {

                    } else if ((Integer.parseInt(gridvalue) < 7) && (pos > 28)) {

                    } else {

                        String status = cal_obj.getAttendance_status();
                        if (status.equalsIgnoreCase("0")) {
                            tv_std_att_date.setBackgroundResource(R.drawable.shape_absent);
//                            tv_std_att_date.setTextColor(Color.parseColor("#FFF"));
                        } else if (status.equalsIgnoreCase("1")) {
                            tv_std_att_date.setBackgroundResource(R.drawable.shape_present);
//                            tv_std_att_date.setTextColor(Color.parseColor("#FFF"));
                        }

                    }

                }
            }
        }
    }
}

