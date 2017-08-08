package com.tracker.dhwani.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.tracker.dhwani.vehicletracker.Utils;

import java.io.File;

/**
 * Created by dhwani.sanghvi on 8/6/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "Location";

    // Table columns
    public static final String _ID = "_id";
    public static final String Date = "Date";
    public static final String Time = "Time";
    public static final String Latitude = "latitude";
    public static final String Longitude = "longitude";
    public static final String Current_Interval = "current";
    public static final String Next_Interval = "next";
    // Database Information
    static final String DB_NAME = "LOCATION_COORDS.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Date + " TEXT NOT NULL, " + Time + " TEXT NOT NULL, "  + Latitude + " DOUBLE, " + Longitude + " DOUBLE, " + Current_Interval + " INTEGER NOT NULL, " + Next_Interval + " INTEGER NOT NULL);";

    public DBHelper(final Context context) {
        super(context, Environment.getExternalStorageDirectory() + File.separator + Utils.FILE_DIR + File.separator + DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}