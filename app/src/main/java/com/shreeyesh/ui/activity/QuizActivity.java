package com.shreeyesh.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.ui.adapter.GKQuizAdapter;
import com.shreeyesh.ui.module.GKQuizModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class QuizActivity extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 600000;

    TextView txt_current_date_display, txt_timer;
    Button btn_submit_quiz, btn_exit_quiz;
    ImageView iv_back;

    RecyclerView recyclerView_testQuestion;
    GKQuizAdapter gkQuickAdapter;
    ArrayList<GKQuizModule> arrayList = new ArrayList<>();

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        find_All_IDs();

        //Timer
        CountDownTimer mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
                int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

                txt_timer.setText(timeLeftFormatted);

            }

            @Override
            public void onFinish() {

            }
        }.start();
        boolean mTimerRunning = true;


        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        txt_current_date_display.setText(date);

        //RecyclerView Adapter and layout assignment
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_testQuestion.setLayoutManager(linearLayoutManager);

        arrayList.add(new GKQuizModule(1, "SDK Stand For ?", "software development ", "Software ", "Development", "Software Development Knowlege"));
        arrayList.add(new GKQuizModule(2, "SDK Stand For ?", "software ", "Software Development kit", "Software Development", "Software Development Knowlege"));
        arrayList.add(new GKQuizModule(3, "SDK Stand For ?", "software development Tool", "Software Development kit", "Software Development", "Software Development Knowlege"));

        gkQuickAdapter = new GKQuizAdapter(QuizActivity.this, arrayList);
        recyclerView_testQuestion.setAdapter(gkQuickAdapter);
        recyclerView_testQuestion.setHasFixedSize(true);

        event();
    }//=============== End onCreate () ===================

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
        txt_current_date_display = findViewById(R.id.txt_current_date_display);
        txt_timer = findViewById(R.id.txt_timer);
        btn_submit_quiz = findViewById(R.id.btn_submit_quiz);
        btn_exit_quiz = findViewById(R.id.btn_exit_quiz);
        recyclerView_testQuestion = findViewById(R.id.recyclerView_testQuestion);

    }
    private void event() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_exit_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_submit_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toasty.success(QuizActivity.this, "Scc", Toast.LENGTH_SHORT,true).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        exitQuiz();
    }

    private void exitQuiz() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_exit_quiz, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        TextView btn_no = dialogView.findViewById(R.id.btn_no);
        TextView btn_yes = dialogView.findViewById(R.id.btn_yes);

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                finish();
                alertDialog.dismiss();

            }
        });
    }
}