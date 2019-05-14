package com.shreeyesh.ui.fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.HomeImagesModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.HomeActivity;
import com.shreeyesh.ui.activity.HolidayActivity;
import com.shreeyesh.ui.activity.UploadNotesActivity;
import com.shreeyesh.utils.NetworkUtils;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {

    Context mContext;
    LinearLayout ll_only_std, ll_ts, ll_attendance_hide, ll_notes_hide, ll_result_hide, ll_library_hide, ll_ts_admin;
    RelativeLayout rel_notification_hide;
    LinearLayout ll_attendance, ll_tutorials, ll_events, ll_leave, ll_video, ll_gk_quiz, ll_holiday, ll_result, ll_library,
            ll_home_work, ll_time_table, ll_gallery, ll_bus_track, ll_notification, ll_fees, ll_teacher;
    CarouselView carousel_view;

    RelativeLayout rel_gallery_count, rel_notification_count;
    TextView tv_gallery_count, tv_notification_count;
    String gallery_count, notification_count;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, isSelectUser;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Fast Connect";

    ArrayList<HomeImagesModule> homeImagesModuleArrayList = new ArrayList<>();
    ArrayList<String> imageArrayList = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getActivity();

        find_All_IDs(view);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        editor = sharedpreferences.edit();
        uId = SharedPreference.getUserID(mContext);

        if (isSelectUser != null && isSelectUser.equalsIgnoreCase("1")) {
            ll_only_std.setVisibility(View.GONE);
            ll_ts.setVisibility(View.GONE);
            ll_attendance_hide.setVisibility(View.GONE);
            ll_notes_hide.setVisibility(View.GONE);

        } else if (isSelectUser != null && isSelectUser.equalsIgnoreCase("2")) {
            ll_only_std.setVisibility(View.GONE);

        } else if (isSelectUser != null && isSelectUser.equalsIgnoreCase("3")) {
            ll_result_hide.setVisibility(View.VISIBLE);
            ll_teacher.setVisibility(View.GONE);

        } else if (isSelectUser != null && isSelectUser.equalsIgnoreCase("4")) {
            ll_library_hide.setVisibility(View.VISIBLE);
            rel_notification_hide.setVisibility(View.GONE);
            ll_only_std.setVisibility(View.GONE);
            ll_ts.setVisibility(View.GONE);
            ll_ts_admin.setVisibility(View.GONE);
        }

        if (homeImagesModuleArrayList != null) {
            //============ Api Call ================
            if (NetworkUtils.isNetworkAvailable(mContext))
                imagesCall();
            else
                NetworkUtils.isNetworkNotAvailable(mContext);

        }

        carouselViewMethod();

        event();

        return view;

    }//========== End onCreate () ==============

    private void carouselViewMethod() {
        carousel_view.setImageListener(imageListener);
    }

    private void find_All_IDs(View view) {
        ll_only_std = view.findViewById(R.id.ll_only_std);
        ll_ts = view.findViewById(R.id.ll_ts);
        ll_attendance_hide = view.findViewById(R.id.ll_attendance_hide);
        ll_notes_hide = view.findViewById(R.id.ll_notes_hide);
        ll_result_hide = view.findViewById(R.id.ll_result_hide);
        ll_library_hide = view.findViewById(R.id.ll_library_hide);
        rel_notification_hide = view.findViewById(R.id.rel_notification_hide);
        ll_ts_admin = view.findViewById(R.id.ll_ts_admin);

        ll_gk_quiz = view.findViewById(R.id.ll_gk_quiz);
        ll_library = view.findViewById(R.id.ll_library);
        ll_attendance = view.findViewById(R.id.ll_attendance);
        ll_tutorials = view.findViewById(R.id.ll_tutorials);
        ll_result = view.findViewById(R.id.ll_result);
        ll_events = view.findViewById(R.id.ll_events);
        ll_leave = view.findViewById(R.id.ll_leave);
        ll_holiday = view.findViewById(R.id.ll_holiday);
        ll_video = view.findViewById(R.id.ll_video);
        ll_home_work = view.findViewById(R.id.ll_home_work);
        ll_time_table = view.findViewById(R.id.ll_time_table);
        ll_gallery = view.findViewById(R.id.ll_gallery);
        ll_bus_track = view.findViewById(R.id.ll_bus_track);
        ll_notification = view.findViewById(R.id.ll_notification);
        ll_fees = view.findViewById(R.id.ll_fees);
        ll_teacher = view.findViewById(R.id.ll_teacher);

        carousel_view = view.findViewById(R.id.carousel_view);

        tv_gallery_count = view.findViewById(R.id.tv_gallery_count);
        tv_notification_count = view.findViewById(R.id.tv_notification_count);

        rel_gallery_count = view.findViewById(R.id.rel_gallery_count);
        rel_notification_count = view.findViewById(R.id.rel_notification_count);
    }

    @Override
    public void onResume() {
        super.onResume();
//        carouselViewMethod();

        notification_count = sharedpreferences.getString(SharedPreference.NOTIFICATION_COUNT, "");
        tv_notification_count.setText(notification_count);
        if (notification_count != null && notification_count.equals("")) {
            rel_notification_count.setVisibility(View.GONE);
        }

        gallery_count = sharedpreferences.getString(SharedPreference.GALLERY_COUNT, "");
        tv_gallery_count.setText(gallery_count);
        if (gallery_count != null && gallery_count.equals("")) {
            rel_gallery_count.setVisibility(View.GONE);
        }
    }

    private void event() {

        ll_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectUser.equalsIgnoreCase("2")) {
                    ((HomeActivity) mContext).replaceFragment(new AttendanceFragment());
                } else {
                    ((HomeActivity) mContext).replaceFragment(new AttendanceStdFragment());
                }
            }
        });

        ll_tutorials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectUser.equalsIgnoreCase("2")) {
                    ((HomeActivity) mContext).replaceFragment(new UploadNotesFragment());
                    editor.putString(SharedPreference.TIME_TABLE_NOTES, "teacher");
                    editor.apply();
                } else {
                    ((HomeActivity) mContext).replaceFragment(new DownloadNotesFragment());
                }
            }
        });

        ll_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).replaceFragment(new EventFragment());
            }
        });

        ll_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).replaceFragment(new LeaveFragment());
            }
        });

        ll_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).replaceFragment(new VideoTutorialsFragment());
            }
        });

        ll_home_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectUser.equalsIgnoreCase("2")) {
                    ((HomeActivity) mContext).replaceFragment(new HomeWorkTeacherFragment());
                } else {
                    ((HomeActivity) mContext).replaceFragment(new HomeWorkStudentListFragment());
                }
            }
        });

        ll_library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectUser.equalsIgnoreCase("2")) {
                    ((HomeActivity) mContext).replaceFragment(new LibraryListStdTeacherFragment());
                }
                if (isSelectUser.equalsIgnoreCase("3")) {
                    ((HomeActivity) mContext).replaceFragment(new LibraryListStdTeacherFragment());

                } else if (isSelectUser.equalsIgnoreCase("4")) {
                    ((HomeActivity) mContext).replaceFragment(new LibraryFragment());
                }
            }
        });

        ll_library_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((HomeActivity) mContext).replaceFragment(new LibraryFragment());
            }
        });

        ll_holiday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HolidayActivity.class);
                startActivity(intent);
            }
        });


        ll_time_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectUser.equalsIgnoreCase("1")) {
                    editor.putString(SharedPreference.TIME_TABLE_NOTES, "admin");
                    editor.apply();
                    Intent intent = new Intent(mContext, UploadNotesActivity.class);
                    startActivity(intent);
                } else {
                    ((HomeActivity) mContext).replaceFragment(new TimeTableFragment());
                }
            }
        });

        ll_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(SharedPreference.GALLERY_COUNT, "");
                editor.apply();
                ((HomeActivity) mContext).replaceFragment(new GalleryFragment());
            }
        });

        ll_gk_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).replaceFragment(new QuizFragment());
            }
        });

        ll_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).replaceFragment(new ResultFragment());
            }
        });

        ll_bus_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).replaceFragment(new BusTrackFragment());
            }
        });

        ll_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(SharedPreference.NOTIFICATION_COUNT, "");
                editor.apply();
                ((HomeActivity) mContext).replaceFragment(new NotificationFragment());
            }
        });

        ll_fees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).replaceFragment(new FeesFragment());
            }
        });

        ll_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).replaceFragment(new TeacherFragment());
            }
        });
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Picasso.get()
                    .load(imageArrayList.get(position))
                    .into(imageView);
        }
    };


    private void imagesCall() {
        Call<SuccessModule> call = apiInterface.homeSplashCall(uId);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
//                String str_response = new Gson().toJson(response.body());
//                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModule loginModule = response.body();

                        String message = null, errorCode = null;
                        if (loginModule != null) {
                            message = loginModule.getMsg();
                            errorCode = loginModule.getError_code();

                            if (errorCode.equalsIgnoreCase("1")) {

                                homeImagesModuleArrayList = loginModule.getHomeImagesModuleArrayList();

                                for (HomeImagesModule homeImagesModule : homeImagesModuleArrayList) {
//                                    arrayList.add(Integer.valueOf(homeImagesModule.getImage_url()));
                                    imageArrayList.add(homeImagesModule.getImage_url());
                                }
                                carousel_view.setPageCount(imageArrayList.size());
                            }
                        }
                    } else {
                        Toasty.info(mContext, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

}
