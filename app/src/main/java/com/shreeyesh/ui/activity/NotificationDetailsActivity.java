package com.shreeyesh.ui.activity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shreeyesh.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationDetailsActivity extends AppCompatActivity {

    TextView tv_notification_name, tv_notification_desc;
    ImageView iv_back, iv_notification_file;
    RelativeLayout rel_notification_file;


    String mediaUrl, currentDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_details);

        find_All_IDs();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        currentDateTime = sdf.format(new Date());


        Intent intent = getIntent();
        String title = intent.getStringExtra("NOTI_TITLE");
        String desc = intent.getStringExtra("NOTI_DESC");
        mediaUrl = intent.getStringExtra("MEDIA_URL");

        if (mediaUrl!=null) {

        }else{
            rel_notification_file.setVisibility(View.GONE);
        }

        tv_notification_name.setText(title);
        tv_notification_desc.setText(desc);

        events();


    }

    private void events() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        iv_notification_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(mediaUrl);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, currentDateTime /*+ "." + dotEx*/);
                Toast.makeText(NotificationDetailsActivity.this, "Start downloading...", Toast.LENGTH_SHORT).show();
                Long reference = downloadManager.enqueue(request);
            }
        });
    }


    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
        iv_notification_file = findViewById(R.id.iv_notification_file);
        tv_notification_name = findViewById(R.id.tv_notification_name);
        tv_notification_desc = findViewById(R.id.tv_notification_desc);
        rel_notification_file = findViewById(R.id.rel_notification_file);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }
}
