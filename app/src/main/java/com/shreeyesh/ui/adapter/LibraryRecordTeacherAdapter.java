package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.LibraryTeacherModule;

import java.util.ArrayList;

public class LibraryRecordTeacherAdapter extends RecyclerView.Adapter<LibraryRecordTeacherAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<LibraryTeacherModule> libraryTeacherModuleArrayList;

    public LibraryRecordTeacherAdapter(Context mContext, ArrayList<LibraryTeacherModule> libraryTeacherModuleArrayList) {
        this.mContext = mContext;
        this.libraryTeacherModuleArrayList = libraryTeacherModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_library_record_teacher_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final LibraryTeacherModule libraryRecordModule = libraryTeacherModuleArrayList.get(position);
        String bookId = libraryRecordModule.getBook_id();
        String bookName = libraryRecordModule.getBook_name();
        String iDate = libraryRecordModule.getIssue_date();
        String rDate = libraryRecordModule.getReturn_date();
        String id = libraryRecordModule.getId();
        String sName = libraryRecordModule.getFull_name();

        holder.tv_lib_id_t.setText(bookId);
        holder.tv_lib_name_t.setText(bookName);
        holder.tv_lib_issue_date_t.setText(iDate);
        holder.tv_lib_return_date_t.setText(rDate);
        holder.tv_lib_teacher_id.setText(id);
        holder.tv_lib_teacher_name.setText(sName);
    }


    @Override
    public int getItemCount() {
        return libraryTeacherModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_lib_id_t, tv_lib_name_t, tv_lib_issue_date_t, tv_lib_return_date_t, tv_lib_teacher_id, tv_lib_teacher_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_lib_id_t = itemView.findViewById(R.id.tv_lib_id_t);
            tv_lib_name_t = itemView.findViewById(R.id.tv_lib_name_t);
            tv_lib_issue_date_t = itemView.findViewById(R.id.tv_lib_issue_date_t);
            tv_lib_return_date_t = itemView.findViewById(R.id.tv_lib_return_date_t);
            tv_lib_teacher_id = itemView.findViewById(R.id.tv_lib_teacher_id);
            tv_lib_teacher_name = itemView.findViewById(R.id.tv_lib_teacher_name);
        }
    }
}
