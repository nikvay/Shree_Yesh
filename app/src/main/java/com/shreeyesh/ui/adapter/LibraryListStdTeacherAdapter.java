package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.LibraryRecordModule;

import java.util.ArrayList;

public class LibraryListStdTeacherAdapter extends RecyclerView.Adapter<LibraryListStdTeacherAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<LibraryRecordModule> libraryRecordModuleArrayList;

    public LibraryListStdTeacherAdapter(Context mContext, ArrayList<LibraryRecordModule> libraryRecordModuleArrayList) {
        this.mContext = mContext;
        this.libraryRecordModuleArrayList = libraryRecordModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_library_std_teacher, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final LibraryRecordModule libraryRecordModule = libraryRecordModuleArrayList.get(position);

        String bookId = libraryRecordModule.getBook_id();
        String bookName = libraryRecordModule.getBook_name();
        String iDate = libraryRecordModule.getIssue_date();
        String rDate = libraryRecordModule.getReturn_date();

        holder.tv_lib_id_st.setText(bookId);
        holder.tv_lib_name_st.setText(bookName);
        holder.tv_lib_issue_date_st.setText(iDate);
        holder.tv_lib_return_date_st.setText(rDate);
    }

    @Override
    public int getItemCount() {
        return libraryRecordModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_lib_id_st, tv_lib_name_st, tv_lib_issue_date_st, tv_lib_return_date_st;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_lib_id_st = itemView.findViewById(R.id.tv_lib_id_st);
            tv_lib_name_st = itemView.findViewById(R.id.tv_lib_name_st);
            tv_lib_issue_date_st = itemView.findViewById(R.id.tv_lib_issue_date_st);
            tv_lib_return_date_st = itemView.findViewById(R.id.tv_lib_return_date_st);
        }
    }
}
