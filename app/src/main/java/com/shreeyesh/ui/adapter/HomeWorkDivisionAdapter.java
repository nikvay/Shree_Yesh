package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.ClassDivisionModule;

import java.util.ArrayList;

public class HomeWorkDivisionAdapter extends RecyclerView.Adapter<HomeWorkDivisionAdapter.MyViewHolder> {
    Context mContext;
    private ArrayList<ClassDivisionModule> classDivisionModuleArrayList;
    private OnItemClickListener listener;

    public HomeWorkDivisionAdapter(Context mContext, ArrayList<ClassDivisionModule> classDivisionModuleArrayList) {
        this.mContext = mContext;
        this.classDivisionModuleArrayList = classDivisionModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_home_work_division_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final ClassDivisionModule classDivisionModule = classDivisionModuleArrayList.get(position);

        String divName= classDivisionModule.getDivision_name();
        holder.checkbox_className.setText(divName);

        holder.checkbox_className.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onAdapterClick(classDivisionModule, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classDivisionModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkbox_className;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            checkbox_className = itemView.findViewById(R.id.checkbox_className);
        }
    }

    //=====================================================
    public interface OnItemClickListener {
        void onAdapterClick(ClassDivisionModule divisionModuleArray, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
