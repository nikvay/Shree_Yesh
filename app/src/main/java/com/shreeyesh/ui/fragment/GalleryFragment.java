package com.shreeyesh.ui.fragment;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.GalleryModule;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.domain.network.BaseApi;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.HomeActivity;
import com.shreeyesh.ui.activity.GalleryActivity;
import com.shreeyesh.ui.adapter.GalleryAdapter;
import com.shreeyesh.utils.NetworkUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class GalleryFragment extends Fragment {

    Context mContext;
    ImageView iv_empty_list;
    String url = null, currentDateTime;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String token, isSelectUser;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Fast Connect";

    ArrayList<GalleryModule> galleryModuleArrayList = new ArrayList<>();
    RecyclerView recycler_view_gallery_image;
    GalleryAdapter galleryAdapter;
    FloatingActionButton fab_add_gallery;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        mContext = getActivity();

        find_All_IDs(view);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        currentDateTime = sdf.format(new Date());

        if (isSelectUser != null && isSelectUser.equalsIgnoreCase("3") || isSelectUser != null && isSelectUser.equalsIgnoreCase("5")) {
            fab_add_gallery.setVisibility(View.GONE);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        recycler_view_gallery_image.setLayoutManager(gridLayoutManager);
        recycler_view_gallery_image.setHasFixedSize(true);

        event();

        if (NetworkUtils.isNetworkAvailable(mContext)) {
            doGallery();
        } else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }

        return view;
    }//========== End onCreate () ==============


    private void find_All_IDs(View view) {
        recycler_view_gallery_image = view.findViewById(R.id.recycler_view_gallery_image);
        iv_empty_list = view.findViewById(R.id.iv_empty_list);
        fab_add_gallery = view.findViewById(R.id.fab_add_gallery);
    }

    private void event() {

        fab_add_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GalleryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void doGallery() {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();
        Call<SuccessModule> call = apiInterface.galleryListCall();

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModule loginModule = response.body();

                        String message = null, errorCode = null;
                        if (loginModule != null) {
                            message = loginModule.getMsg();
                            errorCode = loginModule.getError_code();
                            url = loginModule.getImg_base_url();

                            if (errorCode.equalsIgnoreCase("1")) {

                                galleryModuleArrayList = loginModule.getGalleryModuleArrayList();
                                Collections.reverse(galleryModuleArrayList);

                                galleryAdapter = new GalleryAdapter(mContext, url, galleryModuleArrayList);
                                recycler_view_gallery_image.setAdapter(galleryAdapter);
                                galleryAdapter.notifyDataSetChanged();

                                if (galleryModuleArrayList.size() != 0) {
                                    recycler_view_gallery_image.setVisibility(View.VISIBLE);
                                    iv_empty_list.setVisibility(View.GONE);
                                } else {
                                    recycler_view_gallery_image.setVisibility(View.GONE);
                                    iv_empty_list.setVisibility(View.VISIBLE);
                                }

//                                Toasty.success(mContext, "Gallery List Successfully !!", Toast.LENGTH_SHORT,true).show();

                                //========= adapter onClick ===============
                                adapterOnClick();
                            }
                        }
                    } else {
                        Toasty.warning(mContext, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void adapterOnClick() {
        galleryAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onAdapterClick(GalleryModule galleryModule, int position) {

                String imageName = galleryModule.getImage_name();
                String imageUrl = url + imageName;
                final String finalImageUrl = BaseApi.BASE_URL + imageUrl;


                final String id = galleryModule.getId();

                String fileTitleName = galleryModule.getTitle();

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_dialog_gallery, null);
                builder.setView(dialogView);
                builder.setCancelable(true);

                final AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();

                final ImageView iv_gallery_image_dialog = dialogView.findViewById(R.id.iv_gallery_image_dialog);
                final TextView tv_file_name = dialogView.findViewById(R.id.tv_file_name);
                final ImageView iv_gallery_image_download = dialogView.findViewById(R.id.iv_gallery_image_download);
                final ImageView iv_gallery_image_delete = dialogView.findViewById(R.id.iv_gallery_image_delete);

                Picasso.get()
                        .load(finalImageUrl)
                        .into(iv_gallery_image_dialog);

                tv_file_name.setText(fileTitleName);

                if (isSelectUser.equalsIgnoreCase("1")) {
                    iv_gallery_image_delete.setVisibility(View.VISIBLE);
                    iv_gallery_image_download.setVisibility(View.GONE);
                }

                // two way the name of image
                @SuppressLint("DefaultLocale") final String fileName = String.format("%d", System.currentTimeMillis());
//                final String fileName = DateFormat.getDateTimeInstance().format(new Date());

                iv_gallery_image_download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      /*  iv_gallery_image_dialog.buildDrawingCache();

                        Bitmap bitmap = iv_gallery_image_dialog.getDrawingCache();
                        File storageLoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //context.getExternalFilesDir(null);

                        File file = new File(storageLoc, fileName + ".jpg");

                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                            fos.close();

                            scanFile(mContext, Uri.fromFile(file));
                            Toast.makeText(mContext, "Image Saved Successfully", Toast.LENGTH_LONG).show();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(finalImageUrl);
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, currentDateTime + ".png");
                        Toast.makeText(mContext, "Start downloading...", Toast.LENGTH_SHORT).show();
                        Long reference = downloadManager.enqueue(request);
                        alertDialog.dismiss();
                    }
                });

                iv_gallery_image_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //======= Api Call ====
                        if (NetworkUtils.isNetworkAvailable(mContext)) {
                            deletePhoto(id);
                        } else {
                            NetworkUtils.isNetworkNotAvailable(mContext);
                        }
                        alertDialog.dismiss();
                    }
                });
            }
        });

    }

    private void deletePhoto(String id) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.deleteGalleryCall(id);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModule successModule = response.body();

                        String message = null, errorCode = null;
                        if (successModule != null) {
                            message = successModule.getMsg();
                            errorCode = successModule.getError_code();

                            if (errorCode.equalsIgnoreCase("1")) {

                                Toasty.success(mContext, "Delete Successfully !!", Toast.LENGTH_SHORT, true).show();
                                ((HomeActivity) mContext).replaceFragment(new GalleryFragment());

                            } else {
                                Toasty.info(mContext, "Not Response !!", Toast.LENGTH_SHORT, true).show();
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
                pd.dismiss();
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

}
