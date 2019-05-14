package com.shreeyesh.ui.adapter;

import android.content.Context;
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
import com.shreeyesh.domain.module.TeacherListModule;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.MyViewHolder> implements Filterable {

    private Context mContext;
    public ArrayList<TeacherListModule> teacherListModuleArrayArrayList;
    ArrayList<TeacherListModule> filterArrayList;
    ArrayList<TeacherListModule> selectedArrayList;
    private OnItemClickListener listener;

    public TeacherAdapter(Context mContext, ArrayList<TeacherListModule> teacherListModuleArrayArrayList) {
        this.mContext = mContext;
        this.teacherListModuleArrayArrayList = teacherListModuleArrayArrayList;
        this.filterArrayList = teacherListModuleArrayArrayList;
        this.selectedArrayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_teacher_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final TeacherListModule teacherListModule = teacherListModuleArrayArrayList.get(position);

        String rollNo = teacherListModule.getUser_id();
        String name = teacherListModule.getFull_name();

        holder.txt_teacher_id.setText(rollNo);
        holder.txt_teacher_name.setText(name);

        //========== Adapter onClick() ===========
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedArrayList.contains(teacherListModule)) {
                    selectedArrayList.remove(teacherListModule);
                    unSelectAll(holder);
                } else {
                    selectedArrayList.add(teacherListModule);
                    selectAll(holder);
                }
                listener.onAdapterClick();
            }
        });

        if (selectedArrayList.contains(teacherListModule))
            selectAll(holder);
        else
            unSelectAll(holder);

    }

    private void unSelectAll(MyViewHolder holder) {
        holder.iv_check_true.setVisibility(View.GONE);
    }

    private void selectAll(MyViewHolder holder) {
        holder.iv_check_true.setVisibility(View.VISIBLE);
    }

    public void clearSelected() {
        selectedArrayList.clear();
        notifyDataSetChanged();
    }

    public void selectAll() {
        selectedArrayList.clear();
        selectedArrayList.addAll(teacherListModuleArrayArrayList);
        notifyDataSetChanged();
    }

    public ArrayList<TeacherListModule> getSelectedArrayList() {
        return selectedArrayList;
    }


    @Override
    public int getItemCount() {
        return teacherListModuleArrayArrayList == null ? 0 : teacherListModuleArrayArrayList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().replaceAll("\\s", "").toLowerCase().trim();
                if (charString.isEmpty() || charSequence.equals("")) {
                    teacherListModuleArrayArrayList = filterArrayList;
                } else {
                    ArrayList<TeacherListModule> filteredList = new ArrayList<>();

                    for (int i = 0; i < teacherListModuleArrayArrayList.size(); i++) {
                        String userName = teacherListModuleArrayArrayList.get(i).getFull_name().replaceAll("\\s", "").toLowerCase().trim();
                        String userId = teacherListModuleArrayArrayList.get(i).getUser_id().replaceAll("\\s", "").toLowerCase().trim();
                        if (userName.contains(charString) || userId.contains(charString)) {
                            filteredList.add(teacherListModuleArrayArrayList.get(i));
                        }
                    }
                    if (filteredList.size() > 0) {
                        teacherListModuleArrayArrayList = filteredList;
                    } else {
                        teacherListModuleArrayArrayList = filterArrayList;
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = teacherListModuleArrayArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                teacherListModuleArrayArrayList = (ArrayList<TeacherListModule>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_teacher_id, txt_teacher_name;
        CircleImageView cir_iv_teacher;
        ImageView iv_check_true;

        public MyViewHolder(View itemView) {
            super(itemView);

            cir_iv_teacher = itemView.findViewById(R.id.cir_iv_teacher);
            txt_teacher_id = itemView.findViewById(R.id.txt_teacher_id);
            txt_teacher_name = itemView.findViewById(R.id.txt_teacher_name);
            iv_check_true = itemView.findViewById(R.id.iv_check_true);

        }
    }


    public interface OnItemClickListener {
        void onAdapterClick();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
