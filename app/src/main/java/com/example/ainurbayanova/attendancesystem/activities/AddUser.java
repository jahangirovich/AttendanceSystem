package com.example.ainurbayanova.attendancesystem.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ainurbayanova.attendancesystem.MainActivity;
import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.fragments.User_fragment;
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
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddUser extends AppCompatActivity implements View.OnClickListener{
    Toolbar toolbar;
    CardView cardView;
    CircleImageView putPhoto;
    File file;
    Uri fileUri;
    Button addUser;
    TextView changeIt;
    EditText infoEdt, phoneNumberEdt, cardNumber;
    DatabaseReference databaseReference;
    String version;
    ProgressBar progressBar;
    TextView changeText;
    private static final int PERMISSION_REQUEST_CODE = 200;
    boolean photoSelected = false;
    StorageReference storageReference;
    Spinner spinner;
    String downloadUri = "no photo", imgStorageName = "no photo";
    String infoStr, phoneNumberStr, cardNumberStr, passwordStr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user);
        initViews();
    }

    public void initViews() {
        changeIt = findViewById(R.id.changeIt);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        addUser = findViewById(R.id.addUSer);
        spinner = findViewById(R.id.spinner);
        infoEdt = findViewById(R.id.infoEdt);
        phoneNumberEdt = findViewById(R.id.phoneNumberEdt);
        cardNumber = findViewById(R.id.cardNumber);

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
                        .start(AddUser.this);

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addUSer:

                boolean uOk = true;

//                EditText infoEdt, phoneNumberEdt, loginEdt, passwordEdt;

                infoStr = infoEdt.getText().toString();
                phoneNumberStr = phoneNumberEdt.getText().toString();
                cardNumberStr = cardNumber.getText().toString();

                if (infoStr.trim().equals("")) {
                    infoEdt.setError(getString(R.string.error_fill_name));
                    uOk = false;
                }

                if (phoneNumberStr.trim().equals("")) {
                    phoneNumberEdt.setError(getString(R.string.error_fill_phone));
                    uOk = false;
                }

                if (cardNumberStr.trim().equals("")) {
                    cardNumber.setError(getString(R.string.error_fill_card));
                    uOk = false;
                }
                if(spinner.getSelectedItem().equals("")){
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
                            Toast.makeText(AddUser.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        String spiner = spinner.getSelectedItem().toString();

        String fKey = getIdNumber();

        User user = new User(fKey, infoStr, phoneNumberStr, downloadUri, ""+imgStorageName, ""+cardNumberStr,spiner);

        databaseReference.child("user_list").child(fKey).setValue(user);

        Intent intent = new Intent();
        intent.putExtra("keyName", "yes");
        setResult(RESULT_OK, intent);

        Toast.makeText(AddUser.this, getString(R.string.userAdded), Toast.LENGTH_SHORT).show();

        finish();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(AddUser.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void increaseVersion() {
        databaseReference.child("user_ver").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                version = dataSnapshot.getValue().toString();
                long ver = Long.parseLong(version);
                ver++;

                databaseReference.child("user_ver").setValue(ver);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
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
                            showMessageOKCancel("You need to allow access permissions to take user image",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }
}
