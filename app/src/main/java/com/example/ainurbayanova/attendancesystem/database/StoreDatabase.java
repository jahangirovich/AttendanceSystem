package com.example.ainurbayanova.attendancesystem.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ainurbayanova.attendancesystem.modules.User;


public class StoreDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "attendance_system.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_USER = "user_store";

    public static final String COLUMN_FKEY = "fkey";
    public static final String COLUMN_GROUP = "class";
    public static final String COLUMN_PHOTO = "photo";
    public static final String COLUMN_F_PHOTO_NAME = "f_photo_name";

    public static final String COLUMN_INFO = "info";
    public static final String COLUMN_CARD_NUMBER = "card_number";
    public static final String COLUMN_PHONE = "phone_number";

    public static final String TABLE_VER = "versions";
    public static final String COLUMN_USER_VER = "user_ver";

    Context context;

    public StoreDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_USER + "(" +
                COLUMN_FKEY + " TEXT, " +
                COLUMN_INFO + " TEXT, " +
                COLUMN_CARD_NUMBER + " TEXT, " +
                COLUMN_PHOTO + " TEXT, " +
                COLUMN_PHONE + " TEXT , " +
                COLUMN_GROUP + " TEXT , " +
                COLUMN_F_PHOTO_NAME + " TEXT )" );

        db.execSQL("CREATE TABLE " + TABLE_VER + "(" +
                COLUMN_USER_VER + " TEXT)");

        addVersions(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VER);

        onCreate(db);
    }

    public void cleanUsers(SQLiteDatabase db) {
        db.execSQL("delete from " + TABLE_USER);

    }

    public void cleanVersions(SQLiteDatabase db) {
        db.execSQL("delete from " + TABLE_VER);

    }

    public void addVersions(SQLiteDatabase db) {
        ContentValues versionValues = new ContentValues();
        versionValues.put(COLUMN_USER_VER, "0");

        db.insert(TABLE_VER, null, versionValues);
    }

    public Cursor getSinlgeEntry(String fKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " +
                COLUMN_FKEY + "=?", new String[]{fKey});
        return res;

    }

    public void updateUser(SQLiteDatabase db, User user) {
        ContentValues updateValues = new ContentValues();

        updateValues.put(COLUMN_INFO, user.getInfo());
        updateValues.put(COLUMN_PHONE, user.getPhoneNumber());
        updateValues.put(COLUMN_PHOTO, user.getPhoto());
        updateValues.put(COLUMN_F_PHOTO_NAME, user.getfPhotoName());

        db.update(TABLE_USER, updateValues, COLUMN_FKEY + "='" + user.getFkey()+"'", null);
    }

}