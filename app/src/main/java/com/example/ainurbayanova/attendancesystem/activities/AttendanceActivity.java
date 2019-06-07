package com.example.ainurbayanova.attendancesystem.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.adapter.AttendanceAdapter;
import com.example.ainurbayanova.attendancesystem.database.StoreDatabase;
import com.example.ainurbayanova.attendancesystem.modules.Attendance;
import com.example.ainurbayanova.attendancesystem.modules.Entered;
import com.example.ainurbayanova.attendancesystem.modules.User;
import com.example.ainurbayanova.attendancesystem.services.ParentServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ainurbayanova.attendancesystem.database.StoreDatabase.COLUMN_CARD_NUMBER;
import static com.example.ainurbayanova.attendancesystem.database.StoreDatabase.COLUMN_FKEY;
import static com.example.ainurbayanova.attendancesystem.database.StoreDatabase.COLUMN_F_PHOTO_NAME;
import static com.example.ainurbayanova.attendancesystem.database.StoreDatabase.COLUMN_INFO;
import static com.example.ainurbayanova.attendancesystem.database.StoreDatabase.COLUMN_PHONE;
import static com.example.ainurbayanova.attendancesystem.database.StoreDatabase.COLUMN_PHOTO;
import static com.example.ainurbayanova.attendancesystem.database.StoreDatabase.TABLE_USER;

public class AttendanceActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    AttendanceAdapter adapter;
    ArrayList<Attendance> attendance_list = new ArrayList<>();
    ArrayList<Attendance> attendances = new ArrayList<>();

    HashSet<Attendance> todayList;
    HashSet<Attendance> pastPastDayList;
    HashSet<Attendance> pastDayList;

    Toolbar toolbar;

    TextView pastPastDay;
    TextView pastDay;
    TextView nowDay;
    EditText editTextAttendance;
    SQLiteDatabase sqdb;
    StoreDatabase storeDb;
    Dialog userDescDialog;
    CircleImageView userPhoto;
    ImageView imageAccess;
    TextView userName, desc;
    CountDownTimer dialogShowingTimer;
    SoundPool mSoundPool;
    AssetManager assets;
    int errorSound;
    DatabaseReference mDatabaseRef;
    ArrayList<String> dateList;
    FirebaseAuth auth;
    Calendar calendar;
    ProgressBar progressBar;
    FirebaseUser user;
    String[] dayNames;
    ArrayList<User> userArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        initWidgets();
        initAnimationForRecyclerView();
    }

    public void initWidgets() {
        recyclerView = findViewById(R.id.recyclerView);
        attendance_list = new ArrayList<>();
        todayList = new HashSet<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        pastPastDayList = new HashSet<>();
        pastDayList = new HashSet<>();
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Attendance");
        nowDay = findViewById(R.id.nowDay);
        editTextAttendance = findViewById(R.id.editTextAttendance);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        storeDb = new StoreDatabase(this);
        sqdb = storeDb.getWritableDatabase();
        dateList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        calendar = Calendar.getInstance();
        dayNames = getResources().getStringArray(R.array.dayNames);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (checkInternetConnection()) {
            initializeAttendanceFunc();
            createAttendDescDilaog();
            manageDate();
            getAttendanceUserFromFr();
            checkVersion();
            setDatesForMenuButtons();
        }
    }

    public void createAttendDescDilaog() {
        userDescDialog = new Dialog(this);
        userDescDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        userDescDialog.setContentView(R.layout.dialog_desc_attend);

        userPhoto = userDescDialog.findViewById(R.id.userPhoto);
        imageAccess = userDescDialog.findViewById(R.id.imageAccess);
        userName = userDescDialog.findViewById(R.id.userName);
        desc = userDescDialog.findViewById(R.id.desc);

        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        assets = getAssets();

        errorSound = loadSound("error_sound.wav");

        dialogShowingTimer = new CountDownTimer(2000, 1500) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                userDescDialog.dismiss();
                editTextAttendance.requestFocus();
            }
        };


    }

    //attendance@euro.kz
//    123123
    public void setDatesForMenuButtons() {

        String dateName;

        dateName = getFullDayName(today);
        nowDay.setText("Сегодня: " + dateName + ", " + today.replace("_", "."));

    }


//  attendance@euro.kz
//  123123

    public void getAttendanceUserFromFr() {
        mDatabaseRef.child("attendance").child(today).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                attendance_list.clear();
                attendances.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child("closed").exists()) {
                        Attendance attendance = new Attendance(today, data.getKey(), data.child("entered").child("time").getValue().toString(), data.child("closed").child("time").getValue().toString());
                        attendance_list.add(attendance);
                    } else {
                        Attendance attendance = new Attendance(today, data.getKey(), data.child("entered").child("time").getValue().toString(), "no");
                        attendance_list.add(attendance);
                    }
                }
                mDatabaseRef.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            for (Attendance attendance : attendance_list) {
                                if (data.getKey().equals(attendance.getfKey())) {
                                    Attendance new_attend = new Attendance(attendance.getDate(),
                                            attendance.getfKey(),
                                            data.child("info").getValue().toString(),
                                            data.child("photo").getValue().toString(),
                                            attendance.getEnterDate(),
                                            attendance.getExitDate());
                                    attendances.add(new_attend);
                                }
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        adapter = new AttendanceAdapter(AttendanceActivity.this, attendances, dayNames);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initializeAttendanceFunc() {
        editTextAttendance.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                if (checkInternetConnection() && (event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    final String card_number = editTextAttendance.getText().toString().toLowerCase();
                    editTextAttendance.setText("");
                    if (!getFullDayName(today).equals("Суббота") && !getFullDayName(today).equals("Воскресенье")) {
                        if (card_number.length() > 0) {
                            mDatabaseRef.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean isExist = false;

                                    User now_user = null;
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        User user = data.getValue(User.class);
                                        if (user != null && user.getCardNumber().equals(card_number)) {
                                            now_user = user;
                                            isExist = true;
                                        }
                                    }
                                    if (isExist) {
                                        Glide.with(AttendanceActivity.this)
                                                .load(now_user.getPhoto())
                                                .placeholder(R.drawable.man)
                                                .into(userPhoto);
                                        userDescDialog.show();
                                        dialogShowingTimer.start();
                                        userName.setText(now_user.getInfo());
                                        imageAccess.setImageResource(R.drawable.success_icon);
                                        final User curUser = now_user;

                                        mDatabaseRef.child("attendance").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                boolean exist = false;
                                                boolean isEntered = false;
                                                boolean isClosed = false;
                                                String[] splitted = getCurrentTime().split(":");
                                                int parsedHour = Integer.parseInt(splitted[0]);
                                                int parsedMinute = Integer.parseInt(splitted[1]);
                                                String[] enteredTime = null;
                                                HashMap<String, String> time = new HashMap<>();
                                                time.put("time", getCurrentTime());
                                                time.put("notified", "false");
                                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                    if (data.getKey().equals(today) && data.child(curUser.getFkey()).exists()) {
                                                        exist = true;
                                                        for (DataSnapshot dating : data.getChildren()) {
                                                            if (dating.child("entered").exists() && dating.child("closed").exists()) {
                                                                isEntered = true;
                                                                isClosed = true;
                                                            } else if (dating.child("entered").exists()) {
                                                                isEntered = true;
                                                                enteredTime = dating.child("entered").child("time").getValue().toString().split(":");
                                                            } else if (dating.child("closed").exists()) {
                                                                isClosed = true;
                                                            }
                                                        }
                                                    }
                                                }
                                                if (exist) {
                                                    int total = parsedHour * 60 + parsedMinute;
                                                    int total_end = 15 * 60 + 35;
                                                    int total_close = 19 * 60 + 35;
                                                    int total_entered = 123123;
                                                    if (enteredTime != null) {
                                                        total_entered = Integer.parseInt(enteredTime[0]) * 60 + Integer.parseInt(enteredTime[1]) + 5;
                                                    }
                                                    if (isEntered && parsedHour >= 13 && total <= total_end && (total >= total_entered)) {
                                                        time.put("action", "early");
                                                        mDatabaseRef.child("attendance").child(today).child(curUser.getFkey()).child("closed").setValue(time);
                                                    } else if (isEntered && isClosed) {
                                                        Toast.makeText(AttendanceActivity.this, "Sorry but you are already in", Toast.LENGTH_SHORT).show();
                                                    } else if (isEntered && total >= total_end && total <= total_close && (Integer.parseInt(enteredTime[0]) <= parsedHour && Integer.parseInt(enteredTime[1]) < parsedMinute + 5)) {
                                                        time.put("action", "late");
                                                        mDatabaseRef.child("attendance").child(today).child(curUser.getFkey()).child("closed").setValue(time);
                                                    }
                                                } else if (!exist) {
                                                    time.put("time", getCurrentTime());
                                                    if (parsedHour < 5 || parsedHour > 19 && parsedMinute >= 35) {
                                                        Toast.makeText(AttendanceActivity.this, "Sorry college is closed", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        if (parsedHour <= 8 && parsedMinute <= 30) {
                                                            time.put("action", "early");
                                                            mDatabaseRef.child("attendance").child(today).child(curUser.getFkey()).child("entered").setValue(time);
                                                        } else {
                                                            time.put("action", "late");
                                                            mDatabaseRef.child("attendance").child(today).child(curUser.getFkey()).child("entered").setValue(time);
                                                        }
                                                    }
                                                }
                                                progressBar.setVisibility(View.GONE);
                                                recyclerView.setVisibility(View.VISIBLE);
                                                getAttendanceUserFromFr();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        progressBar.setVisibility(View.GONE);
                                    } else if (!isExist) {
                                        userDescDialog.show();
                                        dialogShowingTimer.start();
                                        userName.setText("User does not exist");
                                        progressBar.setVisibility(View.GONE);
                                        imageAccess.setImageResource(R.drawable.error_icon);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            return true;
                        } else {
                            System.out.println("id_number length is Zero ");
                        }
                    }
                    else{
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AttendanceActivity.this,"Sorry but today is sunday or saturday",Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
    }

    public void callIt(){
        if(user.getEmail().equals("parent@sdcl.kz")){
            startService(new Intent(AttendanceActivity.this, ParentServices.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.attendance_menu, menu);
        return true;
    }

    EditText passwordEdt;
    TextView incorrectPassword;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sign_out) {

            Dialog d = new Dialog(this);
            d.setContentView(R.layout.dialog_sign_out);
            passwordEdt = d.findViewById(R.id.passwordEdt);
            incorrectPassword = d.findViewById(R.id.incorrectPassword);

            Button sign_out = d.findViewById(R.id.btnSignOut);
            sign_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (passwordEdt.getText().toString().equals("qwe123")) {
                        auth.signOut();
                        startActivity(new Intent(AttendanceActivity.this, LoginPage.class));
                    } else {
                        passwordEdt.setText("");
                        incorrectPassword.setVisibility(View.VISIBLE);
                    }
                }
            });
            d.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "To exit press sign out menu", Toast.LENGTH_SHORT).show();
    }

    Attendance curAttendance;

    public boolean checkInList(String id_number) {
        boolean contain = false;

        for (Attendance attendance : todayList) {
            if (attendance.getfKey().equals(id_number)) {
                curAttendance = attendance;
                contain = true;
                break;
            }
        }

        return contain;
    }

    protected void playSound(int sound) {
        if (sound > 0)
            mSoundPool.play(sound, 1, 1, 1, 0, 1);

    }

    private int loadSound(String fileName) {
        AssetFileDescriptor afd = null;
        try {
            afd = assets.openFd(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(AttendanceActivity.this, "Не могу загрузить файл " + fileName, Toast.LENGTH_SHORT).show();
            return -1;
        }

        return mSoundPool.load(afd, 1);
    }


    public void initAnimationForRecyclerView() {
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        recyclerView.setLayoutAnimation(animation);
    }

    public void deleteWithSwipe() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                attendance_list.remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(recyclerView);
    }

    DateFormat timeF, firebaseDateFormat;
    String time, today;

    public String getCurrentTime() {
        timeF = new SimpleDateFormat("HH:mm");
        time = timeF.format(Calendar.getInstance().getTime());

//        firebaseDateFormat = new SimpleDateFormat("dd_MM_yyyy");//2001.07.04
//        firebaseDate = firebaseDateFormat.format(Calendar.getInstance().getTime());

//        tvDate.setText(date);
        return time;
    }

    public void manageDate() {
        firebaseDateFormat = new SimpleDateFormat("dd_MM_yy");//2001.07.04
        today = firebaseDateFormat.format(Calendar.getInstance().getTime());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private boolean checkInternetConnection() {
        if (isNetworkAvailable(this)) {
            return true;

        } else {
            Toast.makeText(AttendanceActivity.this, getResources().getString(R.string.inetConnection), Toast.LENGTH_SHORT).show();
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

    public void checkVersion() {
        Query myTopPostsQuery = mDatabaseRef.child("user_ver");
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String newVersion = dataSnapshot.getValue().toString();
                    if (!getDayCurrentVersion().equals(newVersion)) {
                        updateVersion(newVersion);
                        new GetUsersFromFirebase().execute();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    String curVer;

    public String getDayCurrentVersion() {
        Cursor res = sqdb.rawQuery("SELECT user_ver FROM versions ", null);
        res.moveToNext();
        curVer = res.getString(0);
        return curVer;
    }

    public void updateVersion(String newVersion) {
        ContentValues versionValues = new ContentValues();
        versionValues.put("user_ver", newVersion);
        sqdb.update("versions", versionValues, "user_ver=" + curVer, null);
    }

    public String getFullDayName(String date) {
//        date = date.replace(".","_");
        String dateSplit[] = date.split("_"); //15_01_19

        int year = Integer.parseInt("20" + dateSplit[2]);
        int month = Integer.parseInt(dateSplit[1]) - 1;
        int day = Integer.parseInt(dateSplit[0]);

        calendar.set(year, month, day);

        String dayName = dayNames[calendar.get(Calendar.DAY_OF_WEEK)];
        return dayName;
    }

    public class GetUsersFromFirebase extends AsyncTask<Void, User, Void> {

        public GetUsersFromFirebase() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDatabaseRef.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        storeDb.cleanUsers(sqdb);

                        for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                            User user = usersSnapshot.getValue(User.class);

                            ContentValues userValue = new ContentValues();

                            userValue.put(COLUMN_FKEY, user.getFkey());
                            userValue.put(COLUMN_INFO, user.getInfo());
                            userValue.put(COLUMN_CARD_NUMBER, user.getCardNumber());
                            userValue.put(COLUMN_PHOTO, user.getPhoto());
                            userValue.put(COLUMN_PHONE, user.getPhoneNumber());
                            userValue.put(COLUMN_F_PHOTO_NAME, user.getfPhotoName());

                            sqdb.insert(TABLE_USER, null, userValue);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }

        @Override
        protected void onProgressUpdate(User... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
