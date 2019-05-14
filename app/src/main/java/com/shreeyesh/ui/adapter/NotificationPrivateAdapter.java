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
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.NotificationPrivateModule;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.activity.NotificationDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class NotificationPrivateAdapter extends RecyclerView.Adapter<NotificationPrivateAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<NotificationPrivateModule> notificationPrivateModuleArrayList;

    int lastPosition = -1;

    public NotificationPrivateAdapter(Context mContext, ArrayList<NotificationPrivateModule> notificationPrivateModuleArrayList) {
        this.mContext = mContext;
        this.notificationPrivateModuleArrayList = notificationPrivateModuleArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_item_noti_private_adapter, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Here you apply the animation when the view is bound
//        setAnimation(holder.itemView, position);
        setScaleAnimation(holder.itemView);

        final NotificationPrivateModule notificationPrivateModule = notificationPrivateModuleArrayList.get(position);

        final String name = notificationPrivateModule.getTitle();
        final String desc = notificationPrivateModule.getMessage();
        final String notificationSendBy = notificationPrivateModule.getNotification_send_by();
        final String date = notificationPrivateModule.getDate_time();
        final String time = notificationPrivateModule.getTime();
        final String senderName = notificationPrivateModule.getSender_name();
        final String mediaUrl = notificationPrivateModule.getMedia_url();

        holder.tv_notification_name.setText(senderName);
        holder.tv_notification_desc.setText(desc);
        holder.tv_notification_date_p.setText("Updated Date : " + date + " " + time);

        SharedPreferences sharedpreferences = mContext.getSharedPreferences("Fast Connect", MODE_PRIVATE);
        String isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");

        if (isSelectUser.equalsIgnoreCase("3")) {
            // empty
        } else {
            String uId = SharedPreference.getUserID(mContext);
            if (notificationSendBy.equalsIgnoreCase(uId)) {
                Picasso.get()
                        .load(R.drawable.ic_vector_send_message_n)
                        .placeholder(R.drawable.ic_vector_send_message_n)
                        .into(holder.iv_sms_image);
            }
        }

        //========== Adapter onClick() ===========
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NotificationDetailsActivity.class);
                intent.putExtra("NOTI_TITLE", name);
                intent.putExtra("NOTI_DESC", desc);
                intent.putExtra("MEDIA_URL", mediaUrl);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationPrivateModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_product_list;
        TextView tv_notification_name, tv_notification_desc, tv_notification_date_p;
        ImageView iv_sms_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_notification_name = itemView.findViewById(R.id.tv_notification_name);
            tv_notification_desc = itemView.findViewById(R.id.tv_notification_desc);
            tv_notification_date_p = itemView.findViewById(R.id.tv_notification_date_p);
            iv_sms_image = itemView.findViewById(R.id.iv_sms_image);
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

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(300);
        view.startAnimation(anim);
    }

}
