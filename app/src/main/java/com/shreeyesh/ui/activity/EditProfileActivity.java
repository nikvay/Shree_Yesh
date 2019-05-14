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
import android.widget.Toast;

import com.shreeyesh.R;
import com.shreeyesh.domain.module.SuccessModule;
import com.shreeyesh.domain.network.ApiClient;
import com.shreeyesh.domain.network.ApiInterface;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.utils.NetworkUtils;
import com.shreeyesh.utils.SuccessDialog;
import com.shreeyesh.utils.SuccessDialogClosed;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.net.SocketTimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_LONG;

public class EditProfileActivity extends AppCompatActivity implements SuccessDialogClosed {

    private SuccessDialog successDialog;

    ImageView iv_back, iv_edit_profile;
    CircleImageView cir_iv_edit_user_profile;
    Button btn_update_profile;
    EditText edit_profile_name, edit_user_name, edit_user_mobile, edit_email_user, edit_user_password, edit_address_user;

    // =========== Upload image ================
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_GALLERY_REQUEST_CODE = 2;
    Bitmap bitmap;
    Uri fileUri;
    Uri imageUrl;
    String photo;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId, uType, isSelectUser;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Fast Connect";

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        find_All_IDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        isSelectUser = sharedpreferences.getString(SharedPreference.IS_SELECT_USER, "");
        uId = SharedPreference.getUserID(EditProfileActivity.this);
        uType = SharedPreference.getUserType(EditProfileActivity.this);

        String userFullName = sharedpreferences.getString(SharedPreference.USER_FULL_NAME, "");
        String userName = sharedpreferences.getString(SharedPreference.USER_NAME, "");
        String userPassword = sharedpreferences.getString(SharedPreference.USER_PASSWORD, "");
        String userEmail = sharedpreferences.getString(SharedPreference.USER_EMAIL, "");
        String userContact = sharedpreferences.getString(SharedPreference.USER_CONTACT, "");

        edit_profile_name.setText(userFullName);
        edit_user_name.setText(userName);
        edit_user_password.setText(userPassword);
        edit_email_user.setText(userEmail);
        edit_user_mobile.setText(userContact);

        event();

    }// ============== End onCreate () =========

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
        iv_edit_profile = findViewById(R.id.iv_edit_profile);
        cir_iv_edit_user_profile = findViewById(R.id.cir_iv_edit_user_profile);

        edit_profile_name = findViewById(R.id.edit_profile_name);
        edit_user_name = findViewById(R.id.edit_user_name);
        edit_user_mobile = findViewById(R.id.edit_user_mobile);
        edit_email_user = findViewById(R.id.edit_email_user);
        edit_user_password = findViewById(R.id.edit_user_password);
        edit_address_user = findViewById(R.id.edit_address_user);

        btn_update_profile = findViewById(R.id.btn_update_profile);
    }

    private void event() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doValidation();
            }
        });
    }

    // ===================================*** Image Upload Data ***=================================
    private void showPictureDialog() {
        final AlertDialog.Builder pictureDialog = new AlertDialog.Builder(EditProfileActivity.this);
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
            Toast.makeText(EditProfileActivity.this, "Camera not supported", LENGTH_LONG).show();
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
                bitmap = MediaStore.Images.Media.getBitmap(EditProfileActivity.this.getContentResolver(), imageUrl);
            }
            // ==== User Defined Method ======
            convertToBase64(bitmap); //converting image to base64 string

            cir_iv_edit_user_profile.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void convertToBase64(final Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();
        photo = Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }// ===========*** End Image Upload Data ***=============

    private void doValidation() {
        String fullName = edit_profile_name.getText().toString().trim();
        String userName = edit_user_name.getText().toString().trim();
        String userNumber = edit_user_mobile.getText().toString().trim();
        String userEmail = edit_email_user.getText().toString().trim();
        String userPassword = edit_user_password.getText().toString().trim();
        String userAddress = edit_address_user.getText().toString().trim();

        if (fullName.equalsIgnoreCase("")) {
            edit_profile_name.setError("Please Enter User Name");
            edit_profile_name.requestFocus();
        } else if (userName.length() < 4) {
            edit_user_name.setError("User Name Is Too Short !! ");
            edit_user_name.requestFocus();
        } else if (userName.length() > 15) {
            edit_user_name.setError("User Name Is Too Long !! ");
            edit_user_name.requestFocus();
        } else if (userNumber.equalsIgnoreCase("")) {
            edit_user_mobile.setError("Please Enter User Number !! ");
            edit_user_mobile.requestFocus();
        } else if (userNumber.length() < 10) {
            edit_user_mobile.setError("Please Enter 10 Digit Number !! ");
            edit_user_mobile.requestFocus();
        } else if (userEmail.equalsIgnoreCase("")) {
            edit_email_user.setError("Please Enter Email ID");
            edit_email_user.requestFocus();
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(edit_email_user.getText().toString()).matches()) {
            edit_email_user.setError("Invalid Email !!");
            edit_email_user.requestFocus();
        } else if (userPassword.equalsIgnoreCase("")) {
            edit_user_password.setError("Please Enter Password");
            edit_user_password.requestFocus();
        } else if (userPassword.length() < 4) {
            edit_user_password.setError("Password Is Too Short !! ");
            edit_user_password.requestFocus();
        } else if (userPassword.length() > 15) {
            edit_user_password.setError("Password Is Too Long !! ");
            edit_user_password.requestFocus();
        }/* else if (userAddress.equalsIgnoreCase("")) {
            edit_address_user.setError("Please Enter Address !! ");
            edit_address_user.requestFocus();
        }*/ else {
            if (NetworkUtils.isNetworkAvailable(this))
                doUpdateProfile(userEmail, userNumber, userPassword);
            else
                NetworkUtils.isNetworkNotAvailable(this);
        }
    }

    private void doUpdateProfile(final String userEmail, final String userNumber, final String userPassword) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.editProfileCall(uId, uType, userEmail, userNumber, userPassword);

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

                                editor.putString(SharedPreference.USER_PASSWORD, userPassword);
                                editor.putString(SharedPreference.USER_EMAIL, userEmail);
                                editor.putString(SharedPreference.USER_CONTACT, userNumber);
                                editor.apply();

                                successDialog = new SuccessDialog(EditProfileActivity.this, true);
                                successDialog.showDialog("Update Profile Successful !!", true);

                            } else
                                Toasty.info(EditProfileActivity.this, "Err Response !!", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.info(EditProfileActivity.this, "Service Unavailable !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                if (t instanceof SocketTimeoutException) {
                    Toasty.error(EditProfileActivity.this, "Socket Time out. Please try again.", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(EditProfileActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
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
        onBackPressed();
    }
}
