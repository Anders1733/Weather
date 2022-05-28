package com.example.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "weatherDb";
    public static final String TABLE_WEATHER = "weather";

    public static final String KEY_ID = "_id";
    public static final String KEY_TIME = "time";
    public static final String KEY_CITY = "city";
    public static final String KEY_TEMPERATURE = "temperature";
    public static final String KEY_SPEED = "speed";
    public static final String KEY_WEATHER = "weather";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_WEATHER + "("
                + KEY_ID + " integer primary key,"
                + KEY_TIME + " text,"
                + KEY_CITY + " text,"
                + KEY_TEMPERATURE + " text,"
                + KEY_SPEED + " text,"
                + KEY_WEATHER + " text" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_WEATHER);
        onCreate(db);

    }
}
