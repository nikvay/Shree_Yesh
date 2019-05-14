package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.EnquiryModule;

import java.util.ArrayList;

public class EnquiryAdapter extends RecyclerView.Adapter<EnquiryAdapter.MyViewHolder> implements Filterable {

    Context mContext;
    private ArrayList<EnquiryModule> enquiryModuleArrayList;
    private ArrayList<EnquiryModule> filterArrayList;

    public EnquiryAdapter(Context mContext, ArrayList<EnquiryModule> enquiryModuleArrayList) {
        this.mContext = mContext;
        this.enquiryModuleArrayList = enquiryModuleArrayList;
        this.filterArrayList = enquiryModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_enquiry_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final EnquiryModule enquiryModule = enquiryModuleArrayList.get(position);
        String name = enquiryModule.getFull_name();
        String className = enquiryModule.getClass_name();
        String status = enquiryModule.getStatus();
        String remark = enquiryModule.getRemark();
        String date = enquiryModule.getDate();
        String number = enquiryModule.getPhone_number();

        holder.tv_enq_std_name.setText(name);
        holder.tv_enquiry_class.setText(className);
        holder.tv_enquiry_status.setText(status);
        holder.tv_enq_for.setText(remark);
        holder.tv_enq_number.setText(number);
        holder.tv_enq_date_current_s.setText("Updated Date : "+date);
    }

    @Override
    public int getItemCount() {
        return enquiryModuleArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().replaceAll("\\s", "").toLowerCase().trim();
                if (charString.isEmpty() || charSequence.equals("")) {
                    enquiryModuleArrayList = filterArrayList;
                } else {
                    ArrayList<EnquiryModule> filteredList = new ArrayList<>();

                    for (int i = 0; i < enquiryModuleArrayList.size(); i++) {
                        String status = enquiryModuleArrayList.get(i).getStatus().replaceAll("\\s", "").toLowerCase().trim();
                        String date = enquiryModuleArrayList.get(i).getDate().replaceAll("\\s", "").toLowerCase().trim();
                        if (status.contains(charString) || date.contains(charString)) {
                            filteredList.add(enquiryModuleArrayList.get(i));
                        }
                    }
                    if (filteredList.size() > 0) {
                        enquiryModuleArrayList = filteredList;
                    } else {
                        enquiryModuleArrayList = filterArrayList;
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = enquiryModuleArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                enquiryModuleArrayList = (ArrayList<EnquiryModule>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_enq_std_name, tv_enquiry_class, tv_enquiry_status, tv_enq_number, tv_enq_for, tv_enq_date_current_s;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_enq_std_name = itemView.findViewById(R.id.tv_enq_std_name);
            tv_enquiry_class = itemView.findViewById(R.id.tv_enquiry_class);
            tv_enquiry_status = itemView.findViewById(R.id.tv_enquiry_status);
            tv_enq_number = itemView.findViewById(R.id.tv_enq_number);
            tv_enq_for = itemView.findViewById(R.id.tv_enq_for);
            tv_enq_date_current_s = itemView.findViewById(R.id.tv_enq_date_current_s);
        }
    }
}
