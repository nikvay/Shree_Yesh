package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.TeacherListModule;

import java.util.ArrayList;

public class TeacherListAdminAdapter extends RecyclerView.Adapter<TeacherListAdminAdapter.MyViewHolder> {

    Context mContext;
    private ArrayList<TeacherListModule> teacherListModuleArrayList;

    public TeacherListAdminAdapter(Context mContext, ArrayList<TeacherListModule> teacherListModuleArrayList) {
        this.mContext = mContext;
        this.teacherListModuleArrayList = teacherListModuleArrayList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_techer_list_admin_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final TeacherListModule teacherListModule = teacherListModuleArrayList.get(position);

        final String fullName = teacherListModule.getFull_name();
        final String userName = teacherListModule.getUser_name();
        final String email = teacherListModule.getEmail_id();
        final String contactNumber = teacherListModule.getContact_number1();

        holder.tv_teacher_name.setText(fullName);
        holder.tv_teacher_email.setText(email);
        holder.tv_teacher_contact_no.setText(contactNumber);

    }

    @Override
    public int getItemCount() {
        return teacherListModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_teacher_name, tv_teacher_email, tv_teacher_contact_no;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_teacher_name = itemView.findViewById(R.id.tv_teacher_name);
            tv_teacher_email = itemView.findViewById(R.id.tv_teacher_email);
            tv_teacher_contact_no = itemView.findViewById(R.id.tv_teacher_contact_no);
        }
    }
}
