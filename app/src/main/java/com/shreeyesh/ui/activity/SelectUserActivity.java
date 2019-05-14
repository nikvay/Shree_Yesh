package com.shreeyesh.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.shreeyesh.R;
import com.shreeyesh.shared_pref.SharedPreference;

public class SelectUserActivity extends AppCompatActivity {

    RelativeLayout rel_principle, rel_librarian,rel_teacher, rel_student,rel_driver;
    boolean select = false;

    private SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();

        find_All_IDs();

        rel_principle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select = true;
                editor.putString(SharedPreference.IS_SELECT_USER, "1");
                editor.apply();

                loginPage();
            }
        });

        rel_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select = true;
                editor.putString(SharedPreference.IS_SELECT_USER, "2");
                editor.apply();

                loginPage();
            }
        });

        rel_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select = true;
                editor.putString(SharedPreference.IS_SELECT_USER, "3");
                editor.apply();

                loginPage();
            }
        });

        rel_librarian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select = true;
                editor.putString(SharedPreference.IS_SELECT_USER, "4");
                editor.apply();

                loginPage();
            }
        });

        rel_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select = true;
                editor.putString(SharedPreference.IS_SELECT_USER, "5");
                editor.apply();

                loginPage();
            }
        });

    } //============ End onCreate () ============


    private void find_All_IDs() {
        rel_principle = findViewById(R.id.rel_principle);
        rel_librarian = findViewById(R.id.rel_librarian);
        rel_teacher = findViewById(R.id.rel_teacher);
        rel_student = findViewById(R.id.rel_student);
        rel_driver = findViewById(R.id.rel_driver);
    }

    private void loginPage() {
        Intent intent = new Intent(SelectUserActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
