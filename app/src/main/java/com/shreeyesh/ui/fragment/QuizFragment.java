package com.shreeyesh.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shreeyesh.R;
import com.shreeyesh.ui.activity.QuizActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class QuizFragment extends Fragment {

    Context mContext;
    TextView txt_instruction, txt_currentDate;
    Button btn_startTest;

    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        mContext = getActivity();

        find_All_IDs(view);

        //set instruction to textView
        txt_instruction.setText("Read the following instructions carefully:\n" + "\n" +
                "Total No of Question: 36\n" +
                "Time alloted : 15 Minutes.\n" +
                "Marks for each correct answer: 1 mark\n" +
                "Penalty for each incorrect answer: -0.25 mark\n" +
                "Click on radio button to select/deselect your answer\n" +
                "To complete the test, click on Submit Test button given on the bottom of the test page\n" +
                "Test will be submitted automatically if time got expired and it will display report page\n" +
                "DO NOT refresh the page");

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        txt_currentDate.setText(date);

        //start Test Method call
        btn_startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, QuizActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }//========== End onCreate () ==============

    private void find_All_IDs(View view) {
        txt_instruction = view.findViewById(R.id.txt_instruction);
        txt_currentDate = view.findViewById(R.id.txt_current_date);
        btn_startTest = view.findViewById(R.id.btn_startTest);
    }
}
