package com.example.ainurbayanova.attendancesystem.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ainurbayanova.attendancesystem.MainActivity;
import com.example.ainurbayanova.attendancesystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {
    Button btnLogin;
    EditText email;
    EditText password;
    FirebaseAuth auth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        setTitle("Login Page");

        initWidgets();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
//            attendance@sdcl.kz
//            admin@sdcl.kz
//            qwe123
            if (auth.getCurrentUser().getEmail().contains("attendance")) {
                startActivity(new Intent(LoginPage.this, AttendanceActivity.class));
            } else if (auth.getCurrentUser().getEmail().contains("admin")) {
                startActivity(new Intent(LoginPage.this, MainActivity.class));
            }
            else{
                startActivity(new Intent(LoginPage.this, MainActivity.class));
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternetConnection()) {
                    hideKeyboardFrom(LoginPage.this, v);

                    final String emails = email.getText().toString();
                    String passwords = password.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.GONE);

                    if (TextUtils.isEmpty(emails) || TextUtils.isEmpty(passwords)) {
                        Snackbar.make(btnLogin, "Please fill all info", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        btnLogin.setVisibility(View.VISIBLE);
                    } else {

                        auth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {

                                            if (email.getText().toString().contains("Attendance")) {
                                                startActivity(new Intent(LoginPage.this, AttendanceActivity.class));
                                                finish();

                                            } else if (email.getText().toString().contains("admin")) {
                                                startActivity(new Intent(LoginPage.this, MainActivity.class));
                                                finish();
                                            }
                                            else{
                                                startActivity(new Intent(LoginPage.this, MainActivity.class));
                                                finish();
                                            }

                                        } else {

                                            String sub = task.getException() + "";

                                            String subbed = "Login or password incorrect";
                                            Toast.makeText(LoginPage.this, "" + subbed, Toast.LENGTH_LONG).show();

                                            Log.i("exceptions", "" + sub);

//                                            Snackbar.make(btnLogin, subbed, Toast.LENGTH_SHORT).setActionTextColor(getResources().getColor(R.color.red)).show();
                                            progressBar.setVisibility(View.GONE);
                                            btnLogin.setVisibility(View.VISIBLE);
                                        }

                                    }
                                });
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void initWidgets() {
        btnLogin = findViewById(R.id.btnLogin);
        email = findViewById(R.id.emailToLogin);
        password = findViewById(R.id.passwordToLogin);
        progressBar = findViewById(R.id.progressBarForLogin);
    }

    private boolean checkInternetConnection() {
        if (isNetworkAvailable(this)) {
            return true;

        } else {
            Toast.makeText(LoginPage.this, getResources().getString(R.string.inetConnection), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;  //<--  --  -- Connected
                    }
                }
            }
        }
        return false;  //<--  --  -- NOT Connected
    }
}
