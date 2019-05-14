package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.GalleryModule;
import com.shreeyesh.domain.network.BaseApi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder>
{
    Context context;
    ArrayList<GalleryModule> galleryModuleArrayList;
    String url;
    private OnItemClickListener listener;

    public GalleryAdapter(Context context, String url,ArrayList<GalleryModule> galleryModuleArrayList) {
        this.context=context;
        this.url=url;
        this.galleryModuleArrayList=galleryModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_gallery_adpater,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
      final GalleryModule galleryModule=galleryModuleArrayList.get(position);

        String imageTitle=galleryModule.getTitle();
        String imageName=galleryModule.getImage_name();

        String imageUrl=url+imageName;
        final String finalImageUrl = BaseApi.BASE_URL + imageUrl;
        Picasso.get()
                .load(finalImageUrl)
                .into(holder.imageView);

        holder.textView.setText(imageTitle);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAdapterClick(galleryModule, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return galleryModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.iv_gallery_image);
            textView=itemView.findViewById(R.id.txt_gallery_image_title);

        }
    }

    // ============================================
    public interface OnItemClickListener {
        void onAdapterClick(GalleryModule galleryModule, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
