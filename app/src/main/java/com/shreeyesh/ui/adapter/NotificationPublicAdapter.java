package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.NotificationPublicModule;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.activity.NotificationDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class NotificationPublicAdapter extends RecyclerView.Adapter<NotificationPublicAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<NotificationPublicModule> notificationPublicArrayList;

    int lastPosition = -1;

    public NotificationPublicAdapter(Context mContext, ArrayList<NotificationPublicModule> notificationPublicArrayList) {
        this.mContext = mContext;
        this.notificationPublicArrayList = notificationPublicArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_item_noti_public_adapter, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position);

        final NotificationPublicModule notificationPublicModule = notificationPublicArrayList.get(position);

        final String name = notificationPublicModule.getTitle();
        final String desc = notificationPublicModule.getMessage();
        final String senderName = notificationPublicModule.getSender_name();
        String notificationSendBy = notificationPublicModule.getNotification_send_by();
        String date = notificationPublicModule.getDate_time();
        String time = notificationPublicModule.getTime();
        final String mediaUrl = notificationPublicModule.getMedia_url();

        holder.tv_notification_name.setText(senderName);
        holder.tv_notification_title.setText(desc);
        holder.tv_notification_date_public.setText("Updated Date : "+date+ " " + time);

        SharedPreferences sharedpreferences = mContext.getSharedPreferences("Fast Connect", MODE_PRIVATE);
        String isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");

        if (isSelectUser.equalsIgnoreCase("3")) {
            // empty
        } else {
            String uId = SharedPreference.getUserID(mContext);
            try {
                if (notificationSendBy.equalsIgnoreCase(uId)) {
                    Picasso.get()
                            .load(R.drawable.ic_vector_send_message_n)
                            .placeholder(R.drawable.ic_vector_send_message_n)
                            .into(holder.iv_sms_image_p);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //========== Adapter onClick() ===========
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NotificationDetailsActivity.class);
                intent.putExtra("NOTI_TITLE", name);
                intent.putExtra("NOTI_DESC", desc);
                intent.putExtra("MEDIA_URL",mediaUrl);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationPublicArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_product_list;
        TextView tv_notification_name, tv_notification_title,tv_notification_date_public;
        ImageView iv_sms_image_p;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_notification_name = itemView.findViewById(R.id.tv_notification_name);
            tv_notification_title = itemView.findViewById(R.id.tv_notification_title);
            tv_notification_date_public = itemView.findViewById(R.id.tv_notification_date_public);
            iv_sms_image_p = itemView.findViewById(R.id.iv_sms_image_p);
        }
    }

    //    ========================================================================================
    private void setAnimation(View viewToAnimate, int position) {
//        if (position > lastPosition) {
//            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
//            viewToAnimate.startAnimation(animation);
//            lastPosition = position;
//        }

        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);
        lastPosition = position;
    }

}
