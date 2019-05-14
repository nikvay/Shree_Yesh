package com.shreeyesh.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.ResultMarkModule;

import java.util.ArrayList;

public class ResultChildAdapter extends RecyclerView.Adapter<ResultChildAdapter.MyViewHolder> {
    private ArrayList<ResultMarkModule> resultMarkModuleArrayList;
    int count=0;

    public ResultChildAdapter(ArrayList<ResultMarkModule> resultMarkModuleArrayList) {
        this.resultMarkModuleArrayList = resultMarkModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_result_child_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ResultMarkModule resultMarkModule = resultMarkModuleArrayList.get(position);

        String sName = resultMarkModule.getSubject_name();
        String marksObtain = resultMarkModule.getMarks_obtain();
        String outOfMarks = resultMarkModule.getOut_of_marks();

        count=count+1;
        String totalCount= String.valueOf(count);

        holder.tv_result_id.setText(totalCount);
        holder.tv_sub_name_one.setText(sName);
        holder.tv_sub_mark_one.setText(marksObtain);
        holder.tv_sub_out_of_mark_one.setText(outOfMarks);
    }

    @Override
    public int getItemCount() {
        return resultMarkModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_result_id, tv_sub_name_one, tv_sub_mark_one, tv_sub_out_of_mark_one;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_result_id = itemView.findViewById(R.id.tv_result_id);
            tv_sub_name_one = itemView.findViewById(R.id.tv_sub_name_one);
            tv_sub_mark_one = itemView.findViewById(R.id.tv_sub_mark_one);
            tv_sub_out_of_mark_one = itemView.findViewById(R.id.tv_sub_out_of_mark_one);
        }
    }
}
