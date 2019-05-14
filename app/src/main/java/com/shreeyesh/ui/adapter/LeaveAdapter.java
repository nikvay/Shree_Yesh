package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.LeaveListModule;
import com.shreeyesh.ui.activity.LeaveDetailsActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaveAdapter extends RecyclerView.Adapter<LeaveAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<LeaveListModule> studentLeaveModuleArrayList;

    public LeaveAdapter(Context mContext, ArrayList<LeaveListModule> studentLeaveModuleArrayList) {
        this.mContext = mContext;
        this.studentLeaveModuleArrayList = studentLeaveModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_leave_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final LeaveListModule studentLeaveModule = studentLeaveModuleArrayList.get(position);

        final String id = studentLeaveModule.getLeave_id();
        final String name = studentLeaveModule.getFull_name();
        final String subject = studentLeaveModule.getLeave_subject();
        final String sDate = studentLeaveModule.getStart_date();
        final String eDate = studentLeaveModule.getEnd_date();
        final String className = studentLeaveModule.getClass_name();
        final String divName = studentLeaveModule.getDivision_name();
        final String status = studentLeaveModule.getLeave_status();
        final String cDate = studentLeaveModule.getStart_date();
        final String desc = studentLeaveModule.getLeave_description();
        final String comment = studentLeaveModule.getTeacher_comment();
        final String gender = studentLeaveModule.getGender();

        if(status!=null) {
            if (status.equalsIgnoreCase("0")) {
                holder.tv_leave_status_s.setTextColor(Color.RED);
                holder.tv_leave_status_s.setText("Cancel");

            } else if (status.equalsIgnoreCase("1")) {
//            holder.tv_leave_status_s.setTextColor(Color.YELLOW);
                holder.tv_leave_status_s.setTextColor(Color.parseColor("#FFA500"));
                holder.tv_leave_status_s.setText("Pending");

            } else if (status.equalsIgnoreCase("2")) {
                holder.tv_leave_status_s.setTextColor(Color.GREEN);
                holder.tv_leave_status_s.setText("Approve");
            }
        }
        if(gender!=null) {
            if (gender.equalsIgnoreCase("1")) {
                holder.cir_iv_std_leave.setImageResource(R.drawable.ic_vector_student_boy);
            } else if (gender.equalsIgnoreCase("2")) {
                holder.cir_iv_std_leave.setImageResource(R.drawable.ic_vector_student_girl);
            }
        }

        holder.tv_std_leave_s.setText(name);
        holder.tv_std_leave_subject_s.setText(subject);
        holder.tv_leave_date_s.setText(sDate + " To " + eDate);
        holder.tv_leave_class_std_s.setText(className);
        holder.tv_leave_div_std_s.setText(divName);
        holder.tv_std_leave_date_current_s.setText("Updated Date : "+cDate);

        //========== Adapter onClick() ===========
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LeaveDetailsActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("STATUS", status);
                intent.putExtra("NAME", name);
                intent.putExtra("SUBJECT", subject);
                intent.putExtra("S_DATE", sDate);
                intent.putExtra("E_DATE", eDate);
                intent.putExtra("CLASS_NAME", className);
                intent.putExtra("DIV", divName);
                intent.putExtra("C_DATE", cDate);
                intent.putExtra("DESC", desc);
                intent.putExtra("COMMENT", comment);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentLeaveModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_std_leave_s, tv_std_leave_subject_s, tv_leave_date_s, tv_leave_class_std_s, tv_leave_div_std_s,
                tv_leave_status_s, tv_std_leave_date_current_s;
        CircleImageView cir_iv_std_leave;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_std_leave_s = itemView.findViewById(R.id.tv_std_leave_s);
            tv_std_leave_subject_s = itemView.findViewById(R.id.tv_std_leave_subject_s);
            tv_leave_date_s = itemView.findViewById(R.id.tv_leave_date_s);
            tv_leave_class_std_s = itemView.findViewById(R.id.tv_leave_class_std_s);
            tv_leave_div_std_s = itemView.findViewById(R.id.tv_leave_div_std_s);
            tv_leave_status_s = itemView.findViewById(R.id.tv_leave_status_s);
            cir_iv_std_leave = itemView.findViewById(R.id.cir_iv_std_leave);
            tv_std_leave_date_current_s = itemView.findViewById(R.id.tv_std_leave_date_current_s);
        }
    }
}
