package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.ui.module.FeeStructureModule;

import java.util.ArrayList;

public class FeesPaymentStructureAdapter extends RecyclerView.Adapter<FeesPaymentStructureAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<FeeStructureModule> arrayList;

    public FeesPaymentStructureAdapter(Context mContext, ArrayList<FeeStructureModule> arrayList) {

        this.mContext=mContext;
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_fees_payment_structure,parent,false);
        return new MyViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final FeeStructureModule feeStructureModule =arrayList.get(position);

        holder.txt_payment_header.setText(feeStructureModule.getFee_details());
        holder.txt_amount.setText(feeStructureModule.getFee_amount());
        holder.txt_payment_date.setText(feeStructureModule.getFee_date());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder  extends  RecyclerView.ViewHolder{

        TextView txt_payment_header,txt_amount,txt_payment_date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_payment_header=itemView.findViewById(R.id.txt_payment_header);
            txt_amount=itemView.findViewById(R.id.txt_amount);
            txt_payment_date=itemView.findViewById(R.id.txt_payment_date);
        }
    }
}
