package com.example.ainurbayanova.attendancesystem.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.modules.News;
import com.example.ainurbayanova.attendancesystem.modules.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Add_News extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbar;
    CardView cardView;
    ImageView putPhoto;
    File file;
    Uri fileUri;
    Button addUser;
    TextView changeIt;
    EditText title,text;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    private static final int PERMISSION_REQUEST_CODE = 200;
    boolean photoSelected = false;
    StorageReference storageReference;
    String downloadUri = "no photo", imgStorageName = "no photo";
    String infoStr, phoneNumberStr, cardNumberStr, passwordStr;
    String today,time;
    DateFormat timeF,firebaseDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__news);
        initViews();
    }
    public void initViews() {
        changeIt = findViewById(R.id.changeIt);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        manageDate();
        addUser = findViewById(R.id.addNews);
        title = findViewById(R.id.title);
        text = findViewById(R.id.text);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressBar = findViewById(R.id.ProgressBar);

        progressBar.setVisibility(View.GONE);
        addUser.setOnClickListener(this);

        file = null;
        fileUri = null;
        cardView = findViewById(R.id.takePhoto);
        putPhoto = findViewById(R.id.putPhoto);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Add_News.this);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addNews:

                boolean uOk = true;

//                EditText infoEdt, phoneNumberEdt, loginEdt, passwordEdt;

                infoStr = title.getText().toString();
                phoneNumberStr = text.getText().toString();

                if (infoStr.trim().equals("")) {
                    title.setError(getString(R.string.error_fill_name));
                    uOk = false;
                }

                if (phoneNumberStr.trim().equals("")) {
                    text.setError(getString(R.string.error_fill_phone));
                    uOk = false;
                }


                /*
                if(!photoSelected){
                    changeIt.setError("Please select book image");
                    uOk = false;
                }
                */

                if (uOk) {


                    if(photoSelected) {
                        imgStorageName = UUID.randomUUID().toString();
                        uploadImage();
                    }else{
                        uploadUser();
                    }
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        putPhoto.setVisibility(View.VISIBLE);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                fileUri = result.getUri();
                putPhoto.setImageURI(fileUri);
                putPhoto.setVisibility(View.VISIBLE);
                changeIt.setText("Change Image");

                photoSelected = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private String uploadImage() {
        if (fileUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.photoLoading));
            progressDialog.show();

            final String photoPath = "users/" + imgStorageName;
            final StorageReference ref = storageReference.child(photoPath);
            ref.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            downloadUri = taskSnapshot.getDownloadUrl().toString();

                            if (downloadUri != null) {
                                uploadUser();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Add_News.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.i("failed","e: "+e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
        return downloadUri;
    }

    public void uploadUser(){

        addUser.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        String fKey = getIdNumber();

        News user = new News(infoStr, downloadUri,phoneNumberStr,today,fKey);
        databaseReference.child("news").child(fKey).setValue(user);
        Toast.makeText(Add_News.this, getString(R.string.userAdded), Toast.LENGTH_SHORT).show();

        finish();
    }

    public void manageDate() {
        firebaseDateFormat = new SimpleDateFormat("dd/MM/yy");//2001.07.04
        today = firebaseDateFormat.format(Calendar.getInstance().getTime());
    }

    public String getCurrentTime() {
        timeF = new SimpleDateFormat("HH:mm");
        time = timeF.format(Calendar.getInstance().getTime());

//        firebaseDateFormat = new SimpleDateFormat("dd_MM_yyyy");//2001.07.04
//        firebaseDate = firebaseDateFormat.format(Calendar.getInstance().getTime());

//        tvDate.setText(date);
        return time;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Add_News.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    public String getIdNumber() {
        Date date = new Date();
        String idN = "i" + date.getTime();
        return idN;
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void requestPermission() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "You have been given permission.Now you can use CAMERA.", Toast.LENGTH_SHORT).show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermission();
                        }
                    }
                }
                break;
        }
    }
}
