package com.shreeyesh.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.shreeyesh.R;

public class WebViewActivity extends AppCompatActivity {

    WebView web_view_time_table_teacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        web_view_time_table_teacher=findViewById(R.id.web_view_time_table_teacher);

        Intent intent=getIntent();

        String url=intent.getStringExtra("URL");
        webViewDisplay();
        loadPdfUrl(url);

    }

    private void webViewDisplay() {

        web_view_time_table_teacher.getSettings().setJavaScriptEnabled(true);
        web_view_time_table_teacher.getSettings().setBuiltInZoomControls(true);
        web_view_time_table_teacher.setVerticalScrollBarEnabled(true);
        web_view_time_table_teacher.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        web_view_time_table_teacher.setInitialScale(100);
    }

    private void loadPdfUrl(String url) {
        final String pdfUrl = url;

        web_view_time_table_teacher.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String pdfUrl) {

               /* if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }*/
            }
        });
        web_view_time_table_teacher.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + pdfUrl);
//        web_view_time_table.loadUrl(pdfUrl);

    }
}
