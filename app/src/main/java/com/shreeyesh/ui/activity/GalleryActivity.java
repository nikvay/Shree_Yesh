package com.shreeyesh.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.HomeActivity;
import com.shreeyesh.utils.NetworkUtils;
import com.shreeyesh.utils.SuccessDialog;
import com.shreeyesh.utils.SuccessDialogClosed;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.net.SocketTimeoutException;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_LONG;

public class GalleryActivity extends AppCompatActivity implements SuccessDialogClosed {

    private SuccessDialog successDialog;

    ImageView iv_back, iv_add_gallery_hide, iv_add_gallery;
    RelativeLayout rel_add_gallery;
    EditText edt_gallery_name;
    Button btn_gallery_save;
    boolean isSelect = false, buttonClick = false;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, isSelectUser;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Fast Connect";
    // =========== Upload image ================
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_GALLERY_REQUEST_CODE = 2;
    Bitmap bitmap;
    Uri fileUri;
    Uri imageUrl;
    String photo;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        find_All_IDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        uId = SharedPreference.getUserID(GalleryActivity.this);

        event();

    }//================== End onCreate() =================================

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
        rel_add_gallery = findViewById(R.id.rel_add_gallery);
        iv_add_gallery_hide = findViewById(R.id.iv_add_gallery_hide);
        iv_add_gallery = findViewById(R.id.iv_add_gallery);
        edt_gallery_name = findViewById(R.id.edt_gallery_name);
        btn_gallery_save = findViewById(R.id.btn_gallery_save);
    }

    private void event() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rel_add_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        btn_gallery_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClick) {
                    btn_gallery_save.setClickable(false);
                } else {
                    validation();
                }
            }
        });
    }

    // ===================================*** Image Upload Data ***=================================
    private void showPictureDialog() {
        final AlertDialog.Builder pictureDialog = new AlertDialog.Builder(GalleryActivity.this);
        pictureDialog.setTitle("Select Action");
        pictureDialog.setIcon(R.drawable.ic_vector_camera_logo);
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera", "Cancel"};
        pictureDialog.setCancelable(false);

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        takeFromGallery();
                        break;
                    case 1:
                        takeFromCamera();
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private void takeFromCamera() {
        // Check if this device has a camera
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    // Open default camera
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);             // start the image capture Intent
            startActivityForResult(intent, MY_CAMERA_REQUEST_CODE);        //100
        } else {
            // no camera on this device
            Toast.makeText(GalleryActivity.this, "Camera not supported", LENGTH_LONG).show();
        }
    }

    private void takeFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), MY_GALLERY_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            imageUrl = data.getData();
            if (requestCode == MY_CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");

            } else {
                bitmap = MediaStore.Images.Media.getBitmap(GalleryActivity.this.getContentResolver(), imageUrl);
            }
            iv_add_gallery_hide.setVisibility(View.GONE);
            iv_add_gallery.setVisibility(View.VISIBLE);
            iv_add_gallery.setImageBitmap(bitmap);
            // ==== User Defined Method ======
            convertToBase64(bitmap); //converting image to base64 string

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void convertToBase64(final Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();
        photo = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        isSelect = true;

    }// ===========*** End Image Upload Data ***=============

    private void validation() {
        String name = edt_gallery_name.getText().toString().trim();
        if (!isSelect) {
            Toasty.warning(this, "Please Select Image", Toast.LENGTH_SHORT, true).show();
        } else if (name.equalsIgnoreCase("")) {
            edt_gallery_name.setError("Please Enter Title");
            edt_gallery_name.requestFocus();
        } else {
            //===== Api Call =======
            if (NetworkUtils.isNetworkAvailable(GalleryActivity.this)) {
                buttonClick = true;
                uploadImage(name);
            } else
                NetworkUtils.isNetworkNotAvailable(GalleryActivity.this);
        }

    }

    private void uploadImage(String name) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.galleryAddCall(name, photo, uId);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModule responseModule = response.body();

                        String message = null, errorCode = null;
                        if (responseModule != null) {
                            message = responseModule.getMsg();
                            errorCode = responseModule.getError_code();

                            if (errorCode.equalsIgnoreCase("1")) {
                                successDialog = new SuccessDialog(GalleryActivity.this, true);
                                successDialog.showDialog("Gallery Photo Added Successful !!", true);

//                                Toasty.success(GalleryActivity.this, "Gallery Photo Added Successful !!", Toast.LENGTH_SHORT, true).show();


                            } else
                                Toasty.info(GalleryActivity.this, "Err Response !!", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.info(GalleryActivity.this, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                if (t instanceof SocketTimeoutException) {
                    Toasty.error(GalleryActivity.this, "Socket Time out. Please try again.", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(GalleryActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }

    @Override
    public void dialogClosed(boolean mClosed) {
        editor.putString(SharedPreference.REFRESH, "1");
        editor.apply();
        Intent intent = new Intent(GalleryActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }
}