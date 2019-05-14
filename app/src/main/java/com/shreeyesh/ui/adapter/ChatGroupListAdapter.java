package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.ChatGroupListModule;
import com.shreeyesh.ui.activity.GroupChatActivity;

import java.util.ArrayList;

public class ChatGroupListAdapter extends RecyclerView.Adapter<ChatGroupListAdapter.MyViewHolder> {
    Context mContext;
    private ArrayList<ChatGroupListModule> chatGroupListModuleArrayList;

    public ChatGroupListAdapter(Context mContext, ArrayList<ChatGroupListModule> chatGroupListModuleArrayList) {
        this.mContext = mContext;
        this.chatGroupListModuleArrayList = chatGroupListModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_chat_group_list_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ChatGroupListModule chatGroupListModule = chatGroupListModuleArrayList.get(position);

        final String grpName = chatGroupListModule.getGroup_name();
        final String grpId = chatGroupListModule.getGroup_id();

        holder.tv_chat_grp_name.setText(grpName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GroupChatActivity.class);
                intent.putExtra("GRP_ID",grpId);
                intent.putExtra("GRP_NAME",grpName);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatGroupListModuleArrayList == null ? 0 : chatGroupListModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_chat_grp_name, tv_chat_grp_time, tv_chat_grp_desc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_chat_grp_name = itemView.findViewById(R.id.tv_chat_grp_name);
            tv_chat_grp_time = itemView.findViewById(R.id.tv_chat_grp_time);
            tv_chat_grp_desc = itemView.findViewById(R.id.tv_chat_grp_desc);
        }
    }
}
