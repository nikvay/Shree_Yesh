package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.LibraryStudentModule;

import java.util.ArrayList;

public class LibraryRecordStdAdapter extends RecyclerView.Adapter<LibraryRecordStdAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<LibraryStudentModule> libraryStudentModuleArrayList;

    public LibraryRecordStdAdapter(Context mContext, ArrayList<LibraryStudentModule> libraryStudentModuleArrayList) {
        this.mContext = mContext;
        this.libraryStudentModuleArrayList = libraryStudentModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_library_record_std_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final LibraryStudentModule libraryRecordStdModule = libraryStudentModuleArrayList.get(position);
        String bookId = libraryRecordStdModule.getBook_id();
        String bookName = libraryRecordStdModule.getBook_name();
        String iDate = libraryRecordStdModule.getIssue_date();
        String rDate = libraryRecordStdModule.getReturn_date();
        String rollNo = libraryRecordStdModule.getRoll_num();
        String sName = libraryRecordStdModule.getFull_name();

        holder.tv_lib_id.setText(bookId);
        holder.tv_lib_name.setText(bookName);
        holder.tv_lib_issue_date.setText(iDate);
        holder.tv_lib_return_date.setText(rDate);
        holder.tv_lib_std_roll.setText(rollNo);
        holder.tv_lib_std_name.setText(sName);
    }


    @Override
    public int getItemCount() {
        return libraryStudentModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_lib_id, tv_lib_name, tv_lib_issue_date, tv_lib_return_date,tv_lib_std_roll,tv_lib_std_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_lib_id = itemView.findViewById(R.id.tv_lib_id);
            tv_lib_name = itemView.findViewById(R.id.tv_lib_name);
            tv_lib_issue_date = itemView.findViewById(R.id.tv_lib_issue_date);
            tv_lib_return_date = itemView.findViewById(R.id.tv_lib_return_date);
            tv_lib_std_roll = itemView.findViewById(R.id.tv_lib_std_roll);
            tv_lib_std_name = itemView.findViewById(R.id.tv_lib_std_name);
        }
    }
}

