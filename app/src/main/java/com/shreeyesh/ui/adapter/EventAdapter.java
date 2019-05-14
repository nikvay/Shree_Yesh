package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.EventModule;
import com.shreeyesh.domain.network.BaseApi;
import com.shreeyesh.ui.activity.EventDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<EventModule> eventModuleArrayList;
    String url;

    public EventAdapter(Context mContext, String url, ArrayList<EventModule> eventModuleArrayList) {
        this.mContext = mContext;
        this.eventModuleArrayList = eventModuleArrayList;
        this.url = url;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_event_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final EventModule eventModule = eventModuleArrayList.get(position);

        final String senderName = eventModule.getSender_name();
        final String eventName = eventModule.getEvent_title();
        final String date = eventModule.getEvent_date();
        final String location = eventModule.getLocation();
        final String desc = eventModule.getEvent_description();
        final String eventId = eventModule.getEvent_id();

        final String imageName = eventModule.getImage();
        String imageUrl = url + imageName;
        final String finalImageUrl = BaseApi.BASE_URL + imageUrl;

        Picasso.get()
                .load(finalImageUrl)
                .error(R.drawable.app_image_not_found)
                .into(holder.imageView_event);

        holder.txt_event_sender_name.setText(": " + senderName);
        holder.textView_event_name.setText(": " + eventName);
        holder.textView_event_location.setText(": " + location);
        holder.textView_event_date_time.setText("Updated Date : " + date);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EventDetailsActivity.class);
                intent.putExtra("EVENT_ID", eventId);
                intent.putExtra("EVENT_NAME", eventName);
                intent.putExtra("EVENT_DATE", date);
                intent.putExtra("EVENT_LOCATION", location);
                intent.putExtra("EVENT_IMAGE", finalImageUrl);
                intent.putExtra("EVENT_DESC", desc);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView_event;
        TextView txt_event_sender_name, textView_event_name, textView_event_date_time, textView_event_location;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView_event = itemView.findViewById(R.id.iv_event_image);
            txt_event_sender_name = itemView.findViewById(R.id.txt_event_sender_name);
            textView_event_name = itemView.findViewById(R.id.txt_event_name);
            textView_event_date_time = itemView.findViewById(R.id.txt_event_date_time);
            textView_event_location = itemView.findViewById(R.id.txt_event_location);
        }
    }
}
