package com.shreeyesh.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.activity.AboutUsActivity;
import com.shreeyesh.ui.activity.DriverActivity;
import com.shreeyesh.ui.activity.EditProfileActivity;
import com.shreeyesh.ui.activity.HolidayActivity;
import com.shreeyesh.ui.activity.SelectUserActivity;
import com.shreeyesh.ui.activity.SettingsActivity;
import com.shreeyesh.ui.activity.UploadNotesActivity;
import com.shreeyesh.ui.fragment.AttendanceFragment;
import com.shreeyesh.ui.fragment.AttendanceStdFragment;
import com.shreeyesh.ui.fragment.ChatFragment;
import com.shreeyesh.ui.fragment.DownloadNotesFragment;
import com.shreeyesh.ui.fragment.EnquiryFragment;
import com.shreeyesh.ui.fragment.EventFragment;
import com.shreeyesh.ui.fragment.FeesFragment;
import com.shreeyesh.ui.fragment.GalleryFragment;
import com.shreeyesh.ui.fragment.HomeFragment;
import com.shreeyesh.ui.fragment.HomeWorkStudentListFragment;
import com.shreeyesh.ui.fragment.HomeWorkTeacherFragment;
import com.shreeyesh.ui.fragment.LeaveFragment;
import com.shreeyesh.ui.fragment.LibraryFragment;
import com.shreeyesh.ui.fragment.LibraryListStdTeacherFragment;
import com.shreeyesh.ui.fragment.NotificationFragment;
import com.shreeyesh.ui.fragment.QuizFragment;
import com.shreeyesh.ui.fragment.ResultFragment;
import com.shreeyesh.ui.fragment.TimeTableFragment;
import com.shreeyesh.ui.fragment.UploadNotesFragment;
import com.shreeyesh.ui.fragment.VideoTutorialsFragment;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawer;
    private ImageView iv_menu_toolbar, iv_nav_edt_profile;
    private LinearLayout ll_nav_click_driver_hide, ll_bottom_nav_click, ll_nav_notification, ll_nav_attendance, ll_nav_time_table, ll_nav_result, ll_nav_home_work,
            ll_nav_gallery, ll_nav_fees, ll_nav_Library, ll_nav_video_tutorials, ll_nav_upload_notes, ll_nav_download_notes,
            ll_nav_events, ll_nav_holiday, ll_nav_gk_quiz, ll_nav_enquiry, ll_nav_leave, ll_nav_leave_request, ll_nav_about_us,
            ll_nav_setting, ll_nav_share, ll_nav_log_out, ll_comp_name;

    View ll_view_nav_click_driver_visible;
    private LinearLayout ll_bottom_home, ll_bottom_notification, ll_bottom_home_work, ll_bottom_group_chat, ll_bottom_event;
    String fName = null, schoolName, userFullName, userName, classDiv;
    TextView tv_school_name, tv_title_name, tv_user_name, tv_user_email, tv_user_class;
    CircleImageView cir_iv_nav_user_profile;

    View view_notification_count;

    Fragment fragment;
    private boolean doubleBackToExitPressedOnce = false;

    String isSelectUser;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Fast Connect";

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        find_All_IDs();

        tv_title_name.setText("Dashboard");
        replaceFragment(new HomeFragment());

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        schoolName = sharedpreferences.getString(SharedPreference.SCHOOL_NAME, "");
        userFullName = sharedpreferences.getString(SharedPreference.USER_FULL_NAME, "");
        userName = sharedpreferences.getString(SharedPreference.USER_NAME, "");
        classDiv = sharedpreferences.getString(SharedPreference.CLASS_DIV, "");
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");

        Intent intent = getIntent();

        if (intent != null) {
            String title = intent.getStringExtra("TITLE");
            String description = intent.getStringExtra("DESCRIPTION");
            String redirectId = intent.getStringExtra("REDIRECT_ID");

            notificationRedirect(redirectId);
        }


        if (isSelectUser.equalsIgnoreCase("1")) {
            ll_bottom_event.setVisibility(View.VISIBLE);

            ll_nav_attendance.setVisibility(View.GONE);
            ll_nav_home_work.setVisibility(View.GONE);
            ll_nav_leave.setVisibility(View.GONE);
            ll_bottom_home_work.setVisibility(View.GONE);
            ll_nav_leave_request.setVisibility(View.GONE);
            ll_nav_upload_notes.setVisibility(View.GONE);
            ll_nav_download_notes.setVisibility(View.GONE);
            ll_nav_video_tutorials.setVisibility(View.GONE);
            ll_nav_result.setVisibility(View.GONE);
            ll_nav_Library.setVisibility(View.GONE);
            ll_nav_fees.setVisibility(View.GONE);

        } else if (isSelectUser.equalsIgnoreCase("2")) {
            ll_nav_leave.setVisibility(View.GONE);
            ll_nav_download_notes.setVisibility(View.GONE);
            ll_nav_result.setVisibility(View.GONE);
            ll_nav_fees.setVisibility(View.GONE);

        } else if (isSelectUser.equalsIgnoreCase("3")) {
            ll_nav_leave_request.setVisibility(View.GONE);
            ll_nav_enquiry.setVisibility(View.GONE);
            ll_nav_upload_notes.setVisibility(View.GONE);
            tv_user_class.setVisibility(View.VISIBLE);
            ll_nav_gk_quiz.setVisibility(View.VISIBLE);

        } else if (isSelectUser.equalsIgnoreCase("4")) {
            ll_bottom_nav_click.setVisibility(View.GONE);
            ll_view_nav_click_driver_visible.setVisibility(View.VISIBLE);

            ll_nav_attendance.setVisibility(View.GONE);
            ll_nav_home_work.setVisibility(View.GONE);
            ll_nav_leave.setVisibility(View.GONE);
            ll_bottom_home_work.setVisibility(View.GONE);
            ll_nav_leave_request.setVisibility(View.GONE);
            ll_nav_upload_notes.setVisibility(View.GONE);
            ll_nav_download_notes.setVisibility(View.GONE);
            ll_nav_video_tutorials.setVisibility(View.GONE);
            ll_nav_result.setVisibility(View.GONE);
            ll_nav_notification.setVisibility(View.GONE);
            ll_nav_time_table.setVisibility(View.GONE);
            ll_nav_fees.setVisibility(View.GONE);
            ll_nav_enquiry.setVisibility(View.GONE);

        } else if (isSelectUser.equalsIgnoreCase("5")) {
            ll_view_nav_click_driver_visible.setVisibility(View.VISIBLE);
            ll_nav_click_driver_hide.setVisibility(View.GONE);
            ll_bottom_nav_click.setVisibility(View.GONE);
            Intent intent1 = new Intent(HomeActivity.this, DriverActivity.class);
            startActivity(intent1);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            finish();
        }

        String refresh = sharedpreferences.getString(SharedPreference.REFRESH, "");
        refreshActivity(refresh);

        iv_menu_toolbar.setOnClickListener(this);
        iv_nav_edt_profile.setOnClickListener(this);
        ll_nav_notification.setOnClickListener(this);
        ll_nav_attendance.setOnClickListener(this);
        ll_nav_time_table.setOnClickListener(this);
        ll_nav_result.setOnClickListener(this);
        ll_nav_home_work.setOnClickListener(this);
        ll_nav_gallery.setOnClickListener(this);
        ll_nav_fees.setOnClickListener(this);
        ll_nav_Library.setOnClickListener(this);
        ll_nav_video_tutorials.setOnClickListener(this);
        ll_nav_download_notes.setOnClickListener(this);
        ll_nav_upload_notes.setOnClickListener(this);
        ll_nav_events.setOnClickListener(this);
        ll_nav_holiday.setOnClickListener(this);
        ll_nav_gk_quiz.setOnClickListener(this);
        ll_nav_leave.setOnClickListener(this);
        ll_nav_leave_request.setOnClickListener(this);
        ll_nav_enquiry.setOnClickListener(this);
        ll_nav_about_us.setOnClickListener(this);
        ll_nav_setting.setOnClickListener(this);
        ll_nav_share.setOnClickListener(this);
        ll_nav_log_out.setOnClickListener(this);
        ll_comp_name.setOnClickListener(this);

        //======= bottom =======
        ll_bottom_home.setOnClickListener(this);
        ll_bottom_notification.setOnClickListener(this);
        ll_bottom_home_work.setOnClickListener(this);
        ll_bottom_group_chat.setOnClickListener(this);
        ll_bottom_event.setOnClickListener(this);

    } //==========End onCreate() ==================

    private void refreshActivity(String refresh) {
        if (refresh != null && refresh.equals("1")) {
            replaceFragment(new GalleryFragment());
            editor.putString(SharedPreference.REFRESH, "0");
            editor.apply();
        } else if (refresh != null && refresh.equals("2")) {
            replaceFragment(new EventFragment());
            editor.putString(SharedPreference.REFRESH, "0");
            editor.apply();
        } else if (refresh != null && refresh.equals("3")) {
            replaceFragment(new UploadNotesFragment());
            editor.putString(SharedPreference.REFRESH, "0");
            editor.apply();
        } else if (refresh != null && refresh.equals("4")) {
            replaceFragment(new HomeWorkTeacherFragment());
            editor.putString(SharedPreference.REFRESH, "0");
            editor.apply();
        } else if (refresh != null && refresh.equals("5")) {
            replaceFragment(new LeaveFragment());
            editor.putString(SharedPreference.REFRESH, "0");
            editor.apply();
        } else if (refresh != null && refresh.equals("6")) {
            replaceFragment(new LeaveFragment());
            editor.putString(SharedPreference.REFRESH, "0");
            editor.apply();
        } else if (refresh != null && refresh.equals("7")) {
            replaceFragment(new NotificationFragment());
            editor.putString(SharedPreference.REFRESH, "0");
            editor.apply();


        } else if (refresh != null && refresh.equals("9")) {
            replaceFragment(new EnquiryFragment());
            editor.putString(SharedPreference.REFRESH, "0");
            editor.apply();
        } else if (refresh != null && refresh.equals("10")) {
            replaceFragment(new LibraryFragment());
            editor.putString(SharedPreference.REFRESH, "0");
            editor.apply();
        }
    }

    private void notificationRedirect(String redirectId) {
        if (redirectId != null) {
            switch (redirectId) {
                case "1":
                    editor.putString(SharedPreference.GALLERY_COUNT, "");
                    editor.apply();
                    replaceFragment(new GalleryFragment());
                    break;
                case "2":
                    replaceFragment(new HomeWorkStudentListFragment());
                    break;
                case "3":
                    replaceFragment(new EventFragment());
                    break;
                case "4":
                    replaceFragment(new AttendanceStdFragment());
                    break;
                case "5":
                    replaceFragment(new TimeTableFragment());
                    break;
                case "6":
                    replaceFragment(new FeesFragment());
                    break;
                case "7":
                    replaceFragment(new DownloadNotesFragment());
                    break;
                case "8":
                    replaceFragment(new LeaveFragment());
                    break;
                case "9":
                    replaceFragment(new VideoTutorialsFragment());
                    break;
                case "10":
//                replaceFragment(new VideoTutorialsFragment());
                    break;
                case "11":
                    editor.putString(SharedPreference.NOTIFICATION_COUNT, "");
                    editor.apply();
                    replaceFragment(new NotificationFragment());
                    break;
                case "12":
                    replaceFragment(new NotificationFragment());
                    break;
            }
        }
    }

    private void find_All_IDs() {
        iv_menu_toolbar = findViewById(R.id.iv_menu_toolbar);
        tv_title_name = findViewById(R.id.tv_title_name);
        drawer = findViewById(R.id.drawer_layout);
        //====== header =======
        tv_school_name = findViewById(R.id.tv_school_name);
        cir_iv_nav_user_profile = findViewById(R.id.cir_iv_nav_user_profile);
        iv_nav_edt_profile = findViewById(R.id.iv_nav_edt_profile);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_user_email = findViewById(R.id.tv_user_email);
        tv_user_class = findViewById(R.id.tv_user_class);

        //======== navigation ==============
        ll_nav_click_driver_hide = findViewById(R.id.ll_nav_click_driver_hide);
        ll_view_nav_click_driver_visible = findViewById(R.id.ll_view_nav_click_driver_visible);
        ll_bottom_nav_click = findViewById(R.id.ll_bottom_nav_click);

        ll_nav_notification = findViewById(R.id.ll_nav_notification);
        ll_nav_attendance = findViewById(R.id.ll_nav_attendance);
        ll_nav_time_table = findViewById(R.id.ll_nav_time_table);
        ll_nav_result = findViewById(R.id.ll_nav_result);
        ll_nav_home_work = findViewById(R.id.ll_nav_home_work);
        ll_nav_gallery = findViewById(R.id.ll_nav_gallery);
        ll_nav_fees = findViewById(R.id.ll_nav_fees);
        ll_nav_Library = findViewById(R.id.ll_nav_Library);
        ll_nav_video_tutorials = findViewById(R.id.ll_nav_video_tutorials);
        ll_nav_download_notes = findViewById(R.id.ll_nav_download_notes);
        ll_nav_upload_notes = findViewById(R.id.ll_nav_upload_notes);
        ll_nav_events = findViewById(R.id.ll_nav_events);
        ll_nav_holiday = findViewById(R.id.ll_nav_holiday);
        ll_nav_gk_quiz = findViewById(R.id.ll_nav_gk_quiz);
        ll_nav_leave = findViewById(R.id.ll_nav_leave);
        ll_nav_leave_request = findViewById(R.id.ll_nav_leave_request);
        ll_nav_enquiry = findViewById(R.id.ll_nav_enquiry);
        ll_nav_about_us = findViewById(R.id.ll_nav_about_us);
        ll_nav_setting = findViewById(R.id.ll_nav_setting);
        ll_nav_share = findViewById(R.id.ll_nav_share);
        ll_nav_log_out = findViewById(R.id.ll_nav_log_out);
        ll_comp_name = findViewById(R.id.ll_comp_name);

        //====== bottom ============
        ll_bottom_home = findViewById(R.id.ll_bottom_home);
        ll_bottom_notification = findViewById(R.id.ll_bottom_notification);
        ll_bottom_home_work = findViewById(R.id.ll_bottom_home_work);
        ll_bottom_group_chat = findViewById(R.id.ll_bottom_group_chat);
        ll_bottom_event = findViewById(R.id.ll_bottom_event);

        //========== notification count ======
        view_notification_count = findViewById(R.id.view_notification_count);
    }

    public void replaceFragment(Fragment fragment) {
        fName = fragment.getClass().getSimpleName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
//        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.trans_left_in, R.anim.trans_left_out);
        fragmentTransaction.commit();

        switch (fName) {
            case "HomeFragment":
                tv_title_name.setText("Dashboard");
                break;
            case "AttendanceFragment":
                tv_title_name.setText("Attendance");
                break;
            case "AttendanceStdFragment":
                tv_title_name.setText("Attendance");
                break;
            case "ChatFragment":
                tv_title_name.setText("Chat");
                break;
            case "EventFragment":
                tv_title_name.setText("Event");
                break;
            case "ExamsFragment":
                tv_title_name.setText("Exams");
                break;
            case "FeesFragment":
                tv_title_name.setText("Fees");
                break;
            case "GalleryFragment":
                tv_title_name.setText("Gallery");
                break;
            case "HomeWorkTeacherFragment":
                tv_title_name.setText("Home Work");
                break;
            case "HomeWorkStudentListFragment":
                tv_title_name.setText("Home Work");
                break;
            case "LibraryFragment":
                tv_title_name.setText("Library");
                break;
            case "LibraryListStdTeacherFragment":
                tv_title_name.setText("Library");
                break;
            case "NotificationFragment":
                tv_title_name.setText("Notification");
                break;
            case "TimeTableFragment":
                tv_title_name.setText("Time Table");
                break;
            case "ResultFragment":
                tv_title_name.setText("Result");
                break;
            case "QuizFragment":
                tv_title_name.setText("Quiz");
                break;
            case "TeacherFragment":
                tv_title_name.setText("Teacher");
                break;
            case "LeaveFragment":
                tv_title_name.setText("Leave");
                break;
            case "UploadNotesFragment":
                tv_title_name.setText("Upload Notes");
                break;
            case "DownloadNotesFragment":
                tv_title_name.setText("Download Notes");
                break;
            case "TutorialsFragment":
                tv_title_name.setText("Tutorials");
                break;
            case "VideoTutorialsFragment":
                tv_title_name.setText("Video Tutorials");
                break;
            case "BusTrackFragment":
                tv_title_name.setText("Bus Track");
                break;
            case "EnquiryFragment":
                tv_title_name.setText("Enquiry List");
                break;
        }
    }


    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.iv_menu_toolbar) {
            drawer.openDrawer(GravityCompat.START);

        } else if (id == R.id.iv_nav_edt_profile) {
            Intent intent = new Intent(HomeActivity.this, EditProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_bottom_home) {
            replaceFragment(new HomeFragment());

        } else if (id == R.id.ll_bottom_notification) {
            editor.putString(SharedPreference.NOTIFICATION_COUNT, "");
            editor.apply();
            replaceFragment(new NotificationFragment());

        } else if (id == R.id.ll_bottom_home_work) {
            if (isSelectUser.equalsIgnoreCase("2")) {
                replaceFragment(new HomeWorkTeacherFragment());
            } else {
                replaceFragment(new HomeWorkStudentListFragment());
            }

        } else if (id == R.id.ll_bottom_group_chat) {
            replaceFragment(new ChatFragment());

        } else if (id == R.id.ll_bottom_event) {
            replaceFragment(new EventFragment());

        } else if (id == R.id.ll_nav_notification) {
            editor.putString(SharedPreference.NOTIFICATION_COUNT, "");
            editor.apply();
            replaceFragment(new NotificationFragment());
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_attendance) {
            if (isSelectUser.equalsIgnoreCase("2")) {
                replaceFragment(new AttendanceFragment());
            } else {
                replaceFragment(new AttendanceStdFragment());
            }
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_time_table) {
            if (isSelectUser.equalsIgnoreCase("1")) {
                editor.putString(SharedPreference.TIME_TABLE_NOTES, "admin");
                editor.apply();
                Intent intent = new Intent(HomeActivity.this, UploadNotesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            } else {
                replaceFragment(new TimeTableFragment());
            }

            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_result) {
//            if (isSelectUser.equalsIgnoreCase("2")) {
            replaceFragment(new ResultFragment());
//            } else {
//                Intent intent = new Intent(HomeActivity.this, ResultStudentActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//            }
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_home_work) {
            if (isSelectUser.equalsIgnoreCase("2")) {
                replaceFragment(new HomeWorkTeacherFragment());
            } else {
                replaceFragment(new HomeWorkStudentListFragment());
            }
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_video_tutorials) {
            replaceFragment(new VideoTutorialsFragment());
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_upload_notes) {
            replaceFragment(new UploadNotesFragment());
            editor.putString(SharedPreference.TIME_TABLE_NOTES, "teacher");
            editor.apply();
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_download_notes) {
            replaceFragment(new DownloadNotesFragment());
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_events) {
            replaceFragment(new EventFragment());
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_gallery) {
            replaceFragment(new GalleryFragment());
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_fees) {
            replaceFragment(new FeesFragment());
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_Library) {
            if (isSelectUser.equalsIgnoreCase("4")) {
                replaceFragment(new LibraryFragment());
            } else if (isSelectUser.equalsIgnoreCase("2") || isSelectUser.equalsIgnoreCase("3")) {
                replaceFragment(new LibraryListStdTeacherFragment());
            }
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_holiday) {
            Intent intent = new Intent(HomeActivity.this, HolidayActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_gk_quiz) {
            replaceFragment(new QuizFragment());
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_leave) {
            replaceFragment(new LeaveFragment());
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_leave_request) {
            replaceFragment(new LeaveFragment());
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_enquiry) {
            replaceFragment(new EnquiryFragment());
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_about_us) {
            Intent intent = new Intent(HomeActivity.this, AboutUsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_setting) {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_share) {
            shareApp();
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.ll_nav_log_out) {
            logOut();
        } else if (id == R.id.ll_comp_name) {
           /* Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.nikvay.com"));
            startActivity(browserIntent);*/
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void shareApp() {
        String shareBody = "https://g.co/payinvite/s37hG";

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Google Pay (Open it in Google Play Store to Download the Application)");

        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_logout, null);
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
                alertDialog.dismiss();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(SharedPreference.IS_LOGIN, "false");
                editor.apply();
                Intent intent = new Intent(HomeActivity.this, SelectUserActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String notification_count = sharedpreferences.getString(SharedPreference.NOTIFICATION_COUNT, "");
        if (notification_count != null && notification_count.equals("")) {
            view_notification_count.setVisibility(View.GONE);
        }

        tv_school_name.setText(schoolName);
        tv_user_name.setText(userFullName);
        tv_user_email.setText(userName);
        tv_user_class.setText(classDiv);


    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fName.equals("AttendanceFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("AttendanceStdFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("ChatFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("EventFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("ExamsFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("FeesFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("GalleryFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("HomeWorkTeacherFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("HomeWorkStudentListFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("LibraryFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("LibraryListStdTeacherFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("NotificationFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("TimeTableFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("ResultFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("QuizFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("TeacherFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("LeaveFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("UploadNotesFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("DownloadNotesFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("TutorialsFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("VideoTutorialsFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("BusTrackFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("EnquiryFragment")) {
            replaceFragment(new HomeFragment());
        } else if (fName.equals("HomeFragment")) {
            doubleBackPressLogic();
        }
    }

    // ============ End Double tab back press logic =================
    private void doubleBackPressLogic() {
        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toasty.info(this, "Please click back again to exit !!", Toast.LENGTH_SHORT, true).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1000);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}


