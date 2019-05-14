package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.TeacherListModule;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LibraryTeacherAdapter extends RecyclerView.Adapter<LibraryTeacherAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<TeacherListModule> teacherListModuleArrayArrayList;
    private OnItemClickListener listener;

    public LibraryTeacherAdapter(Context mContext, ArrayList<TeacherListModule> teacherListModuleArrayArrayList) {
        this.mContext = mContext;
        this.teacherListModuleArrayArrayList = teacherListModuleArrayArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_lib_std_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final TeacherListModule teacherListModule = teacherListModuleArrayArrayList.get(position);

        String id = teacherListModule.getUser_id();
        String name = teacherListModule.getFull_name();

        holder.tv_lib_std_id.setText(id);
        holder.tv_lib_std_name.setText(name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.iv_check_true.getVisibility() == View.GONE) {
                    holder.iv_check_true.setVisibility(View.VISIBLE);
                } else if (holder.iv_check_true.getVisibility() == View.VISIBLE) {
                    holder.iv_check_true.setVisibility(View.GONE);
                }

                listener.onAdapterClick(teacherListModule, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return teacherListModuleArrayArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_lib_std_id, tv_lib_std_name;
        CircleImageView cv_iv_lib_std_profile;
        ImageView iv_check_true;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_lib_std_id = itemView.findViewById(R.id.tv_lib_std_id);
            tv_lib_std_name = itemView.findViewById(R.id.tv_lib_std_name);
            cv_iv_lib_std_profile = itemView.findViewById(R.id.cv_iv_lib_std_profile);
            iv_check_true = itemView.findViewById(R.id.iv_check_true);
        }
    }

    //=====================================================
    public interface OnItemClickListener {
        void onAdapterClick(TeacherListModule teacherListModule, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
