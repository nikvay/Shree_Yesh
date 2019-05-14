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
import com.shreeyesh.domain.module.StudentListModule;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LibraryStudentAdapter extends RecyclerView.Adapter<LibraryStudentAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<StudentListModule> studentListModuleArrayArrayList;
    private OnItemClickListener listener;

    public LibraryStudentAdapter(Context mContext, ArrayList<StudentListModule> studentListModuleArrayArrayList) {
        this.mContext = mContext;
        this.studentListModuleArrayArrayList = studentListModuleArrayArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_lib_std_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final StudentListModule studentListModule = studentListModuleArrayArrayList.get(position);

        String id = studentListModule.getRoll_num();
        String name = studentListModule.getFull_name();
        String gender = studentListModule.getGender();

        holder.tv_lib_std_id.setText(id);
        holder.tv_lib_std_name.setText(name);
        if (gender != null) {
            if (gender.equalsIgnoreCase("1")) {
                holder.cv_iv_lib_std_profile.setImageResource(R.drawable.ic_vector_student_boy);
            } else if (gender.equalsIgnoreCase("2")) {
                holder.cv_iv_lib_std_profile.setImageResource(R.drawable.ic_vector_student_girl);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.iv_check_true.getVisibility() == View.GONE) {
                    holder.iv_check_true.setVisibility(View.VISIBLE);
                } else if (holder.iv_check_true.getVisibility() == View.VISIBLE) {
                    holder.iv_check_true.setVisibility(View.GONE);
                }

                listener.onAdapterClick(studentListModule, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return studentListModuleArrayArrayList == null ? 0 : studentListModuleArrayArrayList.size();
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
        void onAdapterClick(StudentListModule studentListModule, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
