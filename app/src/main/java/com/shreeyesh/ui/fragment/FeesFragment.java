package com.shreeyesh.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shreeyesh.R;
import com.shreeyesh.ui.adapter.FeesDueAdapter;
import com.shreeyesh.ui.adapter.FeesPaymentAdapter;
import com.shreeyesh.ui.adapter.FeesPaymentStructureAdapter;
import com.shreeyesh.ui.module.FeeDuesModule;
import com.shreeyesh.ui.module.FeeStructureModule;
import com.shreeyesh.ui.module.FeesPaymentDetailsModule;

import java.util.ArrayList;

public class FeesFragment extends Fragment {

    Context mContext;

    private RecyclerView recycler_view_payment_details,recycler_view_due,recycler_view_fess_details;

    ArrayList<FeesPaymentDetailsModule> feesPaymentDetailsModules_arrayList =new ArrayList<>();
    FeesPaymentAdapter feesPaymentAdapter;

    ArrayList<FeeDuesModule>  feeDuesModules_arrayList=new ArrayList<>();
    FeesDueAdapter feesDueAdapter;

    ArrayList<FeeStructureModule> feeStructureModules_arrayList=new ArrayList<>();
    FeesPaymentStructureAdapter feesPaymentStructureAdapter;


    public FeesFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fees, container, false);
        mContext = getActivity();

        find_All_IDs(view);


        recycler_view_fess_details.setLayoutManager(new LinearLayoutManager(mContext));
        recycler_view_payment_details.setLayoutManager(new LinearLayoutManager(mContext));
        recycler_view_due.setLayoutManager(new LinearLayoutManager(mContext));

        feesPaymentAdapter=new FeesPaymentAdapter(mContext, feesPaymentDetailsModules_arrayList);
        recycler_view_payment_details.setAdapter(feesPaymentAdapter);
        recycler_view_payment_details.hasFixedSize();


        feesDueAdapter=new FeesDueAdapter(mContext,feeDuesModules_arrayList);
        recycler_view_due.setAdapter(feesDueAdapter);
        recycler_view_due.hasFixedSize();


        feesPaymentStructureAdapter=new FeesPaymentStructureAdapter(mContext,feeStructureModules_arrayList);
        recycler_view_fess_details.setAdapter(feesPaymentStructureAdapter);
        recycler_view_fess_details.hasFixedSize();

        //set Data to Module
        for(int i=0;i<=2;i++)
        {
            feesPaymentDetailsModules_arrayList.add(new FeesPaymentDetailsModule("paid fees(Rs)","2000"));
        }


        for(int i=0;i<=3;i++)
        {
            feeDuesModules_arrayList.add(new FeeDuesModule("03-04-2019","3000"));
        }

        for(int i=0;i<=4;i++)
        {
            feeStructureModules_arrayList.add(new FeeStructureModule("Admission Fees","3000","20-01-2019"));
        }

        //Fees payment Adapter Call



        return view;

    }//========== End onCreate () ==============

    private void find_All_IDs(View view) {
        recycler_view_due=view.findViewById(R.id.recycler_view_due);
        recycler_view_fess_details=view.findViewById(R.id.recycler_view_fess_details);
        recycler_view_payment_details=view.findViewById(R.id.recycler_view_payment_details);
    }
}
