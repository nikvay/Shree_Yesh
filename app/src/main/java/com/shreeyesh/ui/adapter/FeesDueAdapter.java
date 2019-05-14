package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.ui.module.FeeDuesModule;

import java.util.ArrayList;

public class FeesDueAdapter  extends RecyclerView.Adapter<FeesDueAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<FeeDuesModule> arrayList;

    public FeesDueAdapter(Context mContext, ArrayList<FeeDuesModule> arrayList) {
        this.mContext=mContext;
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_fee_due,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final FeeDuesModule feeDuesModule =arrayList.get(position);

        holder.txt_due_date.setText(feeDuesModule.getDue_date());
        holder.txt_due_amount.setText(feeDuesModule.getDue_amount());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView  txt_due_date,txt_due_amount;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_due_date=itemView.findViewById(R.id.txt_due_date);
            txt_due_amount=itemView.findViewById(R.id.txt_due_amount);
        }
    }
}
