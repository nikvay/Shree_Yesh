package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.HomeWorkListModule;
import com.shreeyesh.domain.network.BaseApi;
import com.shreeyesh.ui.activity.HomeWorkDetailsActivity;

import java.util.ArrayList;

public class HomeWorkTeacherAdapter extends RecyclerView.Adapter<HomeWorkTeacherAdapter.MyViewHolder> implements Filterable {

    Context mContext;
    String url;
    ArrayList<HomeWorkListModule> homeWorkListStudentModuleArrayList;
    ArrayList<HomeWorkListModule> filterArrayList;

    public HomeWorkTeacherAdapter(Context mContext, String url, ArrayList<HomeWorkListModule> homeWorkListStudentModuleArrayList) {
        this.url = url;
        this.mContext = mContext;
        this.homeWorkListStudentModuleArrayList = homeWorkListStudentModuleArrayList;
        this.filterArrayList = homeWorkListStudentModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_homework_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final HomeWorkListModule homeWorkListModule = homeWorkListStudentModuleArrayList.get(position);

        final String id = homeWorkListModule.getId();
        final String subject = homeWorkListModule.getTitle();
        final String desc = homeWorkListModule.getDescription();
        final String gDate = homeWorkListModule.getGiven_date();
        final String sDate = homeWorkListModule.getSubmission_date();
        final String className = homeWorkListModule.getClass_name();
        final String divisionName = homeWorkListModule.getDivision_name();
        final String time = homeWorkListModule.getTime();

        String imageName = homeWorkListModule.getImg_name();
        String imageUrl=url+imageName;
        final String finalImageUrl = BaseApi.BASE_URL + imageUrl;

        holder.txt_subject_name.setText(subject);
       /* Picasso.get()
                .load(finalImageUrl)
                .error(R.drawable.app_image_not_found)
                .into(holder.iv_homework_image);
*/
        holder.txt_description.setText(desc);
        holder.txt_submit_date.setText(sDate);
        holder.txt_class_div_name.setText(className+" "+divisionName);
        holder.txt_home_work_time_t.setText("Updated Time : "+time);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HomeWorkDetailsActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("TITLE", subject);
                intent.putExtra("IMAGE", finalImageUrl);
                intent.putExtra("DESC", desc);
                intent.putExtra("G_DATE", gDate);
                intent.putExtra("S_DATE", sDate);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeWorkListStudentModuleArrayList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString().replaceAll("\\s", "").toLowerCase().trim();
                if (charString.isEmpty() || charSequence.equals("")) {
                    homeWorkListStudentModuleArrayList = filterArrayList;

                } else {
                    ArrayList<HomeWorkListModule> filteredList = new ArrayList<>();
                    for (int i = 0; i < homeWorkListStudentModuleArrayList.size(); i++) {
                        String subjectName = homeWorkListStudentModuleArrayList.get(i).getTitle().replaceAll("\\s", "").toLowerCase().trim();
                        String subDate = homeWorkListStudentModuleArrayList.get(i).getSubmission_date().replaceAll("\\s", "").toLowerCase().trim();
                        if (subjectName.contains(charString) || subDate.contains(charString)) {
                            filteredList.add(homeWorkListStudentModuleArrayList.get(i));
                        }
                    }
                    if (filteredList.size() > 0) {
                        homeWorkListStudentModuleArrayList = filteredList;
                    } else {
                        homeWorkListStudentModuleArrayList = filterArrayList;
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = homeWorkListStudentModuleArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                homeWorkListStudentModuleArrayList = (ArrayList<HomeWorkListModule>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_subject_name, txt_description, txt_submit_date, txt_class_div_name,txt_home_work_time_t;
        ImageView iv_homework_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_subject_name = itemView.findViewById(R.id.txt_subject_name);
            txt_description = itemView.findViewById(R.id.txt_description);
            txt_submit_date = itemView.findViewById(R.id.txt_submit_date);
            txt_class_div_name = itemView.findViewById(R.id.txt_class_div_name);
            txt_home_work_time_t = itemView.findViewById(R.id.txt_home_work_time_t);

            iv_homework_image = itemView.findViewById(R.id.iv_homework_image);
        }
    }
}