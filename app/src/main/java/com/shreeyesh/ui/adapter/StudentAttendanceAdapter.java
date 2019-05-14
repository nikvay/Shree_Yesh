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
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.StudentListModule;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.MyViewHolder> implements Filterable {

    private Context mContext;
    ArrayList<StudentListModule> studentListModuleArrayArrayList;
    private OnItemClickListener listener;
    ArrayList<StudentListModule> filterArrayList;

    public StudentAttendanceAdapter(Context mContext, ArrayList<StudentListModule> studentListModuleArrayArrayList) {
        this.mContext = mContext;
        this.studentListModuleArrayArrayList = studentListModuleArrayArrayList;
        this.filterArrayList = studentListModuleArrayArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_attendance_student_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final StudentListModule classStudentModule = studentListModuleArrayArrayList.get(position);

        String rollNo = classStudentModule.getRoll_num();
        String name = classStudentModule.getFull_name();
        String gender = classStudentModule.getGender();

        holder.student_rollNo_txt.setText(rollNo);
        holder.student_name_txt.setText(name);

        if(gender!=null) {
            if (gender.equalsIgnoreCase("1")) {
                holder.cv_iv_std_atte_profile.setImageResource(R.drawable.ic_vector_student_boy);
            } else if (gender.equalsIgnoreCase("2")) {
                holder.cv_iv_std_atte_profile.setImageResource(R.drawable.ic_vector_student_girl);
            }
        }

        //========== Adapter onClick() ===========
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.iv_check_true.getVisibility() == View.GONE) {
                    holder.iv_check_true.setVisibility(View.VISIBLE);
                    holder.iv_check_false.setVisibility(View.GONE);
                } else if (holder.iv_check_true.getVisibility() == View.VISIBLE) {
                    holder.iv_check_true.setVisibility(View.GONE);
                    holder.iv_check_false.setVisibility(View.VISIBLE);
                }
                listener.onAdapterClick(classStudentModule, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentListModuleArrayArrayList == null ? 0 : studentListModuleArrayArrayList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().replaceAll("\\s", "").toLowerCase().trim();
                if (charString.isEmpty() || charSequence.equals("")) {
                    studentListModuleArrayArrayList = filterArrayList;
                } else {
                    ArrayList<StudentListModule> filteredList = new ArrayList<>();

                    for (int i = 0; i < studentListModuleArrayArrayList.size(); i++) {
                        String studentName = studentListModuleArrayArrayList.get(i).getFull_name().replaceAll("\\s", "").toLowerCase().trim();
                        String studentRoll = studentListModuleArrayArrayList.get(i).getRoll_num().replaceAll("\\s", "").toLowerCase().trim();
                        if (studentName.contains(charString)||studentRoll.contains(charString)) {
                            filteredList.add(studentListModuleArrayArrayList.get(i));
                        }
                    }
                    if (filteredList.size() > 0) {
                        studentListModuleArrayArrayList = filteredList;
                    } else {
                        studentListModuleArrayArrayList = filterArrayList;
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = studentListModuleArrayArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                studentListModuleArrayArrayList = (ArrayList<StudentListModule>) filterResults.values;
                notifyDataSetChanged();
            }

        };

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView student_rollNo_txt, student_name_txt;
        CircleImageView cv_iv_std_atte_profile;
        ImageView iv_check_true, iv_check_false;

        public MyViewHolder(View itemView) {
            super(itemView);
            cv_iv_std_atte_profile = itemView.findViewById(R.id.cv_iv_std_atte_profile);
            student_rollNo_txt = itemView.findViewById(R.id.student_rollNo_txt);
            student_name_txt = itemView.findViewById(R.id.student_name_txt);
            iv_check_true = itemView.findViewById(R.id.iv_check_true);
            iv_check_false = itemView.findViewById(R.id.iv_check_false);
        }
    }

    //=====================================================
    public interface OnItemClickListener {
        void onAdapterClick(StudentListModule studentListModule, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
