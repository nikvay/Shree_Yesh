package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.ui.module.FeesPaymentDetailsModule;

import java.util.ArrayList;

public class FeesPaymentAdapter  extends RecyclerView.Adapter<FeesPaymentAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<FeesPaymentDetailsModule> arrayList;

    public FeesPaymentAdapter(Context mContext, ArrayList<FeesPaymentDetailsModule> arrayList) {
        this.mContext=mContext;
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_fees_payment_details,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final FeesPaymentDetailsModule feesPaymentDetailsModule =arrayList.get(position);


        holder.txt_payment_details.setText(feesPaymentDetailsModule.getPayment_details());
        holder.txt_payment_amount.setText(feesPaymentDetailsModule.getPaid_amount());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txt_payment_details,txt_payment_amount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_payment_details=itemView.findViewById(R.id.txt_payment_details);
            txt_payment_amount=itemView.findViewById(R.id.txt_payment_amount);
        }
    }
}
