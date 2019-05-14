package com.shreeyesh.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.ui.module.GKQuizModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GKQuizAdapter extends RecyclerView.Adapter<GKQuizAdapter.MyViewHolder> {

    Context context;
    ArrayList<GKQuizModule> arrayList;
    Map<String,Object> map = new HashMap<>();


    HashMap<String,String> hashMap;
    public GKQuizAdapter(Context context, ArrayList<GKQuizModule> arrayList) {
        this.context=context;
        this.arrayList=arrayList;
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item_gkquiz_adpater,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
       final GKQuizModule gkQuizModule=arrayList.get(position);

        String question= gkQuizModule.getQuestion();
        final int qid = gkQuizModule.getQuestion_number();


        final ArrayList<String> options = new ArrayList<>();
        options.add(gkQuizModule.getOption1());
        options.add(gkQuizModule.getOption2());
        options.add(gkQuizModule.getOption3());
        options.add(gkQuizModule.getOption4());

        Collections.shuffle(options);
        final String option1 = options.get(0);
        String option2 = options.get(1);
        String option3 = options.get(2);
        String option4 =  options.get(3);

        final String[] answer = new String[1];

        holder.txt_question_number.setText("Question "+qid);
        holder.txt_question.setText(question);
        holder.rb_option1.setText(option1);
        holder.rb_option2.setText(option2);
        holder.rb_option3.setText(option3);
        holder.rb_option4.setText(option4);

        holder.rg_option.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                switch (checkedId){
                    case R.id.rb_option1:
                        Toast.makeText(context, holder.rb_option1.getText().toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rb_option2:
                        Toast.makeText(context, holder.rb_option2.getText().toString(), Toast.LENGTH_SHORT).show();

                        break;
                    case R.id.rb_option3:
                        Toast.makeText(context, holder.rb_option3.getText().toString(), Toast.LENGTH_SHORT).show();

                        break;
                    case R.id.rb_option4:
                        Toast.makeText(context, holder.rb_option4.getText().toString(), Toast.LENGTH_SHORT).show();

                        break;
                }
                RadioButton checked =radioGroup.findViewById(checkedId);
                answer[0] = checked.getText().toString();
                Toast.makeText(context, checked.getText().toString(), Toast.LENGTH_SHORT).show();
                map.put(String.valueOf(qid),answer[0]);
            }
        });

    }

    @Override
    public int getItemCount(){
    if (arrayList!=null){
        return arrayList.size();

    }else {
        return 0;
    }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_question_number,txt_question;
        RadioButton rb_option1,rb_option2,rb_option3,rb_option4;
        RadioGroup rg_option;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_question_number=itemView.findViewById(R.id.txt_question_number);
            txt_question=itemView.findViewById(R.id.txt_question);

            rg_option=itemView.findViewById(R.id.rg_option);
            rb_option1=itemView.findViewById(R.id.rb_option1);
            rb_option2=itemView.findViewById(R.id.rb_option2);
            rb_option3=itemView.findViewById(R.id.rb_option3);
            rb_option4=itemView.findViewById(R.id.rb_option4);

        }
    }
}
