package com.shreeyesh.ui.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.NotesModule;
import com.shreeyesh.domain.network.BaseApi;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class NotesStdAdapter extends RecyclerView.Adapter<NotesStdAdapter.MyViewHolder> {
    Context mContext;
    private ArrayList<NotesModule> notesModuleArrayList;
    String url;

    public NotesStdAdapter(Context mContext, String url, ArrayList<NotesModule> notesModuleArrayList) {
        this.mContext = mContext;
        this.url = url;
        this.notesModuleArrayList = notesModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_notes_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final NotesModule notesModule = notesModuleArrayList.get(position);

        String name = notesModule.getNote_title();
        String urlName = notesModule.getNote_document();
        String uDate = notesModule.getUploaded_date();
        String time = notesModule.getTime();

        String downloadUrl = url + urlName;
        final String finalImageUrl = BaseApi.BASE_URL + downloadUrl;

        holder.txt_notes_title_name.setText(name);
        holder.txt_notes_doc_name.setText(urlName);
        holder.txt_notes_update_date.setText("Updated Date : " + uDate+" "+time);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        final String currentDateTime = sdf.format(new Date());
        StringTokenizer st = new StringTokenizer(urlName, ".");
        final String aa = st.nextToken();
        final String dotEx = st.nextToken();

        try {
            if (dotEx.contains("png")) {
/*            Picasso.get()
                    .load(finalImageUrl)
                    .placeholder(R.drawable.app_logo)
                    .into(holder.iv_note_logo);*/

            } else if (dotEx.contains("pdf")) {
                Picasso.get()
                        .load(R.drawable.ic_vector_pdf)
                        .placeholder(R.drawable.ic_vector_pdf)
                        .into(holder.iv_note_logo);

            } else if (dotEx.contains("doc")) {
                Picasso.get()
                        .load(R.drawable.ic_vector_word)
                        .placeholder(R.drawable.ic_vector_word)
                        .into(holder.iv_note_logo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.iv_note_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(finalImageUrl);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, currentDateTime + "." + dotEx);
                Toast.makeText(mContext, "Start downloading...", Toast.LENGTH_SHORT).show();
                Long reference = downloadManager.enqueue(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_note_logo, iv_note_download;
        TextView txt_notes_title_name, txt_notes_doc_name, txt_notes_update_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_note_logo = itemView.findViewById(R.id.iv_note_logo);
            iv_note_download = itemView.findViewById(R.id.iv_note_download);
            txt_notes_title_name = itemView.findViewById(R.id.txt_notes_title_name);
            txt_notes_doc_name = itemView.findViewById(R.id.txt_notes_doc_name);
            txt_notes_update_date = itemView.findViewById(R.id.txt_notes_update_date);
        }
    }
}
