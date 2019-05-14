package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.ChattingModule;
import com.shreeyesh.shared_pref.SharedPreference;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.MyViewHolder> {

    Context mContext;
    private ArrayList<ChattingModule> chattingModuleArrayList;
    ChattingAdapter chattingAdapter;

    public ChattingAdapter(Context mContext, ArrayList<ChattingModule> chattingModuleArrayList) {
        this.mContext = mContext;
        this.chattingModuleArrayList = chattingModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chatting, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ChattingModule chattingModule = chattingModuleArrayList.get(position);

        String senderName = chattingModule.getSender_name();
        String message = chattingModule.getMessage();
        String timestamp = chattingModule.getTimestamp();
        String userType = chattingModule.getUser_type();
        String userId = chattingModule.getUser_id();

        holder.tv_chat_sender_name.setText(senderName);
        holder.tv_chat_sender_message.setText(message);
        holder.tv_chatting_sender_time.setText(timestamp);

        SharedPreferences sharedpreferences = mContext.getSharedPreferences("Fast Connect", MODE_PRIVATE);
        String isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        String uType = SharedPreference.getUserType(mContext);
        String uId = SharedPreference.getUserID(mContext);

        if (userType.equalsIgnoreCase(uType)&&userId.equals(uId)) {
            holder.rel_chat_sender.setVisibility(View.VISIBLE);
            holder.rel_chat_receiver.setVisibility(View.GONE);

            holder.tv_chat_sender_name.setText("You");
            holder.tv_chat_sender_message.setText(message);
            holder.tv_chatting_sender_time.setText(timestamp);
        } else {

            holder.rel_chat_sender.setVisibility(View.GONE);
            holder.rel_chat_receiver.setVisibility(View.VISIBLE);

            holder.tv_chat_receiver_name.setText(senderName);
            holder.tv_chat_receiver_message.setText(message);
            holder.tv_chatting_receiver_time.setText(timestamp);
        }

    }

    @Override
    public int getItemCount() {
        return chattingModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_chat_sender_name, tv_chat_sender_message, tv_chatting_sender_time;
        TextView tv_chat_receiver_name, tv_chat_receiver_message, tv_chatting_receiver_time;
        RelativeLayout rel_chat_receiver, rel_chat_sender;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rel_chat_receiver = itemView.findViewById(R.id.rel_chat_receiver);
            rel_chat_sender = itemView.findViewById(R.id.rel_chat_sender);

            tv_chat_sender_name = itemView.findViewById(R.id.tv_chat_sender_name);
            tv_chat_sender_message = itemView.findViewById(R.id.tv_chat_sender_message);
            tv_chatting_sender_time = itemView.findViewById(R.id.tv_chatting_sender_time);

            tv_chat_receiver_name = itemView.findViewById(R.id.tv_chat_receiver_name);
            tv_chat_receiver_message = itemView.findViewById(R.id.tv_chat_receiver_message);
            tv_chatting_receiver_time = itemView.findViewById(R.id.tv_chatting_receiver_time);
        }
    }
}
