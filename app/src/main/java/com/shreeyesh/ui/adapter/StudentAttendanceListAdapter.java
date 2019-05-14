package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.StudentAttendanceListModule;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentAttendanceListAdapter extends RecyclerView.Adapter<StudentAttendanceListAdapter.MyViewHolder>implements Filterable {
    Context mContext;
    ArrayList<StudentAttendanceListModule> studentAttendanceListModuleArrayList;
    private OnItemClickListener listener;
    ArrayList<StudentAttendanceListModule> filterArrayList;

    public StudentAttendanceListAdapter(Context mContext, ArrayList<StudentAttendanceListModule> studentAttendanceListModuleArrayList) {
        this.mContext = mContext;
        this.studentAttendanceListModuleArrayList = studentAttendanceListModuleArrayList;
        this.filterArrayList = studentAttendanceListModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_attendance_list_student_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final StudentAttendanceListModule studentAttendanceListModule = studentAttendanceListModuleArrayList.get(position);

        String rollNo = studentAttendanceListModule.getRoll_num();
        String name = studentAttendanceListModule.getFull_name();
        final String status = studentAttendanceListModule.getAttendance_status();
        final String gender = studentAttendanceListModule.getGender();

        holder.student_rollNo_txt.setText(rollNo);
        holder.student_name_txt.setText(name);

        if(gender!=null) {
            if (gender.equalsIgnoreCase("1")) {
                holder.cv_iv_std_atte_list_profile.setImageResource(R.drawable.ic_vector_student_boy);
            } else if (gender.equalsIgnoreCase("2")) {
                holder.cv_iv_std_atte_list_profile.setImageResource(R.drawable.ic_vector_student_girl);
            }
        }

        if (status.equalsIgnoreCase("0")) {
            holder.iv_check_false.setVisibility(View.VISIBLE);
            holder.iv_check_true.setVisibility(View.GONE);
        } else if (status.equalsIgnoreCase("1")) {
            holder.iv_check_true.setVisibility(View.VISIBLE);
            holder.iv_check_false.setVisibility(View.GONE);
        }
        //========== Adapter onClick() ===========
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status.equalsIgnoreCase("0")) {
                    if (holder.iv_check_true.getVisibility() == View.GONE) {
                        holder.iv_check_true.setVisibility(View.VISIBLE);
                        holder.iv_check_false.setVisibility(View.GONE);
                    } else if (holder.iv_check_true.getVisibility() == View.VISIBLE) {
                        holder.iv_check_true.setVisibility(View.GONE);
                        holder.iv_check_false.setVisibility(View.VISIBLE);
                    }
                } else if (status.equalsIgnoreCase("1")) {
                }

                listener.onAdapterClick(studentAttendanceListModule, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentAttendanceListModuleArrayList == null ? 0 : studentAttendanceListModuleArrayList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().replaceAll("\\s", "").toLowerCase().trim();
                if (charString.isEmpty() || charSequence.equals("")) {
                    studentAttendanceListModuleArrayList = filterArrayList;
                } else {
                    ArrayList<StudentAttendanceListModule> filteredList = new ArrayList<>();

                    for (int i = 0; i < studentAttendanceListModuleArrayList.size(); i++) {
                        String studentName = studentAttendanceListModuleArrayList.get(i).getFull_name().replaceAll("\\s", "").toLowerCase().trim();
                        String studentId = studentAttendanceListModuleArrayList.get(i).getRoll_num().replaceAll("\\s", "").toLowerCase().trim();
                        if (studentName.contains(charString)||studentId.contains(charString)) {
                            filteredList.add(studentAttendanceListModuleArrayList.get(i));
                        }
                    }
                    if (filteredList.size() > 0) {
                        studentAttendanceListModuleArrayList = filteredList;
                    } else {
                        studentAttendanceListModuleArrayList = filterArrayList;
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = studentAttendanceListModuleArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                studentAttendanceListModuleArrayList = (ArrayList<StudentAttendanceListModule>) filterResults.values;
                notifyDataSetChanged();
            }

        };

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView student_rollNo_txt, student_name_txt;
        CircleImageView cv_iv_std_atte_list_profile;
        ImageView iv_check_true, iv_check_false;
        RelativeLayout rel_attendance_list_checkable;

        public MyViewHolder(View itemView) {
            super(itemView);

            rel_attendance_list_checkable = itemView.findViewById(R.id.rel_attendance_list_checkable);
            cv_iv_std_atte_list_profile = itemView.findViewById(R.id.cv_iv_std_atte_list_profile);
            student_rollNo_txt = itemView.findViewById(R.id.student_rollNo_txt);
            student_name_txt = itemView.findViewById(R.id.student_name_txt);
            iv_check_true = itemView.findViewById(R.id.iv_check_true);
            iv_check_false = itemView.findViewById(R.id.iv_check_false);

        }
    }


    //=====================================================
    public interface OnItemClickListener {
        void onAdapterClick(StudentAttendanceListModule studentAttendanceListModule, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
