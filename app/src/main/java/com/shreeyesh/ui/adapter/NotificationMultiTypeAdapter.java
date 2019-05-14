package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.NotificationTypeModule;

import java.util.ArrayList;

public class NotificationMultiTypeAdapter extends RecyclerView.Adapter<NotificationMultiTypeAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<NotificationTypeModule> notificationMultiTypeModuleArrayList;
    private OnItemClickListener listener;

    public NotificationMultiTypeAdapter(Context mContext, ArrayList<NotificationTypeModule> notificationTypeModuleArrayList) {
        this.mContext = mContext;
        this.notificationMultiTypeModuleArrayList=notificationTypeModuleArrayList;
    }

    @NonNull
    @Override
    public NotificationMultiTypeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_notification_type, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final NotificationTypeModule notificationTypeModule = notificationMultiTypeModuleArrayList.get(position);

        final String nId = notificationTypeModule.getId();
        final String nName = notificationTypeModule.getName();
        final String nStatus = notificationTypeModule.getStatus();

        holder.tv_n_type_name.setText(nName);
//        holder.tv_n_type_count.setText(nStatus);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAdapterClick(notificationTypeModule, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationMultiTypeModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_n_type_name, tv_n_type_count;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_n_type_name = itemView.findViewById(R.id.tv_n_type_name);
//            tv_n_type_count = itemView.findViewById(R.id.tv_n_type_count);
        }
    }

    //=====================================================
    public interface OnItemClickListener {
        void onAdapterClick(NotificationTypeModule notificationTypeModule, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
