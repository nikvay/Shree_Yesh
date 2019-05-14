package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.ResultExamNameModule;
import com.shreeyesh.domain.module.ResultMarkModule;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHolder> {
    Context mContext;
    private ArrayList<ResultExamNameModule> resultExamNameModuleArrayList;
    private ResultChildAdapter resultChildAdapter;
    int marksObtain, outOfMarks;
    double finalPer, m, o;

    public ResultAdapter(Context mContext, ArrayList<ResultExamNameModule> resultExamNameModuleArrayList) {
        this.mContext = mContext;
        this.resultExamNameModuleArrayList = resultExamNameModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_result_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ResultExamNameModule resultExamNameModule = resultExamNameModuleArrayList.get(position);

        String testName = resultExamNameModule.getTest_name();
        String testDate = resultExamNameModule.getTest_date();

        holder.tv_result_test_name.setText(testName);
        holder.tv_result_test_date.setText(testDate);

        try {
            marksObtain = 0;
            outOfMarks = 0;
            for (ResultMarkModule resultMarkModule : resultExamNameModule.getResultMarkModuleArrayList()) {

                int marks = Integer.parseInt(resultMarkModule.getMarks_obtain());
                int outOf = Integer.parseInt(resultMarkModule.getOut_of_marks());
                marksObtain = marksObtain + marks;
                outOfMarks = outOfMarks + outOf;

                m = marksObtain;
                o = outOfMarks;
            }

            String per = String.valueOf(m / o * 100);
            String finalPer = per.substring(0, 5);//returns va

            holder.tv_result_total.setText("" + marksObtain);
            holder.tv_result_out_of_mark.setText("" + outOfMarks);
            holder.tv_result_percentage.setText("" + finalPer + " %");
        }catch (Exception e){
            e.printStackTrace();
        }
        resultChildAdapter = new ResultChildAdapter(resultExamNameModule.getResultMarkModuleArrayList());
        holder.recycler_view_result_child.setAdapter(resultChildAdapter);
        resultChildAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return resultExamNameModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_result_test_name, tv_result_test_date, tv_result_total, tv_result_out_of_mark, tv_result_percentage;
        RecyclerView recycler_view_result_child;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_result_test_name = itemView.findViewById(R.id.tv_result_test_name);
            tv_result_test_date = itemView.findViewById(R.id.tv_result_test_date);
            tv_result_total = itemView.findViewById(R.id.tv_result_total);
            tv_result_out_of_mark = itemView.findViewById(R.id.tv_result_out_of_mark);
            tv_result_percentage = itemView.findViewById(R.id.tv_result_percentage);
//            tv_sub_name_one = itemView.findViewById(R.id.tv_sub_name_one);

            recycler_view_result_child = itemView.findViewById(R.id.recycler_view_result_child);
//            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL,false);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            recycler_view_result_child.setLayoutManager(layoutManager);
            recycler_view_result_child.setHasFixedSize(true);
        }
    }
}
