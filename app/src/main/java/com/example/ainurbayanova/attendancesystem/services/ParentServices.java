package com.example.ainurbayanova.attendancesystem.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.ainurbayanova.attendancesystem.MainActivity;
import com.example.ainurbayanova.attendancesystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.ainurbayanova.attendancesystem.Application.channel1_id;

public class ParentServices extends Service {
    DatabaseReference databaseReference;
    DateFormat firebaseDateFormat;
    String today;

    NotificationManagerCompat notificationManagerCompat;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    public void manageDate() {
        firebaseDateFormat = new SimpleDateFormat("dd_MM_yy");//2001.07.04
        today = firebaseDateFormat.format(Calendar.getInstance().getTime());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("info","I am here");
        manageDate();
        uploadUserByKey();
        return START_STICKY;
    }

    public void uploadUserByKey(){
        databaseReference.child("attendance").child(today).child("i1560311579343").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String time = "";
                String late = "";
                boolean it_is_in = false;
                if(!Boolean.parseBoolean(dataSnapshot.child("notified").getValue().toString())){
                    late = dataSnapshot.child("action").getValue().toString();
                    time = dataSnapshot.child("time").getValue().toString();
                    notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                    Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                            0, notificationIntent, 0);
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), channel1_id)
                            .setContentTitle("WELCOME")
                            .setContentText("Your son(daughter)\n"+time + " entered to college.It is " + late)
                            .setSmallIcon(R.drawable.success_icon)
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                            .setAutoCancel(true)
                            .build();
                    notificationManagerCompat.notify(1, notification);
                    databaseReference.child("attendance").child(today).child("i1560311579343").child("entered").child("notified").setValue(true);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String takeAdmin(String user){
        if(user.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){
            return "You";
        }
        else{
            return user;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
