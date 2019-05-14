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
import com.shreeyesh.domain.module.NotesModule;
import com.shreeyesh.domain.network.BaseApi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {
    Context mContext;
    private ArrayList<NotesModule> notesModuleArrayList;
    private String url;
    private OnItemClickListener listener;

    public NotesAdapter(Context mContext, String url, ArrayList<NotesModule> notesModuleArrayList) {
        this.mContext = mContext;
        this.url = url;
        this.notesModuleArrayList = notesModuleArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_notes_teacher_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final NotesModule notesModule = notesModuleArrayList.get(position);

        String name = notesModule.getNote_title();
        String urlName = notesModule.getNote_document();
        String uDate = notesModule.getUploaded_date();
        String time = notesModule.getTime();

        String downloadUrl = url + urlName;
        final String finalImageUrl = BaseApi.BASE_URL + downloadUrl;

        try {
            StringTokenizer st = new StringTokenizer(urlName, ".");
            final String aa = st.nextToken();
            final String dotEx = st.nextToken();

            if (dotEx.contains("png")) {
/*
            Picasso.get()
                    .load(finalImageUrl)
                    .placeholder(R.drawable.app_logo)
                    .into(holder.iv_note_logo_t);
*/

            } else if (dotEx.contains("pdf")) {
                Picasso.get()
                        .load(R.drawable.ic_vector_pdf)
                        .placeholder(R.drawable.ic_vector_pdf)
                        .into(holder.iv_note_logo_t);

            } else if (dotEx.contains("doc")) {
                Picasso.get()
                        .load(R.drawable.ic_vector_word)
                        .placeholder(R.drawable.ic_vector_word)
                        .into(holder.iv_note_logo_t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.txt_notes_title_name_t.setText(name);
        holder.txt_notes_doc_name_t.setText(urlName);
        holder.txt_notes_update_date_t.setText("Updated Date : " + uDate+" "+time);

        //========== Adapter onClick() ===========
        holder.iv_note_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAdapterClick(notesModule, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notesModuleArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_note_logo_t, iv_note_delete;
        TextView txt_notes_title_name_t, txt_notes_doc_name_t, txt_notes_update_date_t;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_note_logo_t = itemView.findViewById(R.id.iv_note_logo_t);
            iv_note_delete = itemView.findViewById(R.id.iv_note_delete);
            txt_notes_title_name_t = itemView.findViewById(R.id.txt_notes_title_name_t);
            txt_notes_doc_name_t = itemView.findViewById(R.id.txt_notes_doc_name_t);
            txt_notes_update_date_t = itemView.findViewById(R.id.txt_notes_update_date_t);
        }
    }

    //=====================================================
    public interface OnItemClickListener {
        void onAdapterClick(NotesModule notesModule, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
