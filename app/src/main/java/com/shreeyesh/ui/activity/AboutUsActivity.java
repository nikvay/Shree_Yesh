package com.shreeyesh.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.shreeyesh.R;

public class AboutUsActivity extends AppCompatActivity {

    /*
   Toasty.error(yourContext, "This is an error toast.", Toast.LENGTH_SHORT, true).show();
   Toasty.success(yourContext, "Success!", Toast.LENGTH_SHORT, true).show();
   Toasty.info(yourContext, "Here is some info for you.", Toast.LENGTH_SHORT, true).show();
   Toasty.warning(yourContext, "Beware of the dog.", Toast.LENGTH_SHORT, true).show();
    * */
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        find_All_IDs();

        event();


    }// ============== End onCreate () =========

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
    }

    private void event() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }
}
