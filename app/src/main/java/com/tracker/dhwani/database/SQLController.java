package com.tracker.dhwani.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dhwani.sanghvi on 8/6/2017.
 */
public class SQLController {

    private DBHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public SQLController(Context context){
        this.context = context;
    }

    public SQLController open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * method to insert the values into the database.
     * @param latitude
     * @param longitude
     * @param current
     * @param next
     */
    public void insert(Double latitude, Double longitude, int current, int next) {
        ContentValues contentValue = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        contentValue.put(DBHelper.Date, dateFormat.format(new Date()));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        contentValue.put(DBHelper.Time, timeFormat.format(new Date()));
        contentValue.put(DBHelper.Latitude,latitude);
        contentValue.put(DBHelper.Longitude,longitude);
        contentValue.put(DBHelper.Current_Interval,current);
        contentValue.put(DBHelper.Next_Interval,next);
        database.insert(DBHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor getAllData(){
        String buildSQL = "SELECT * FROM " + DBHelper.TABLE_NAME;
        return database.rawQuery(buildSQL,null);
    }

    public Cursor getAllSessionData(){
        String buildSql = "SELECT DISTINCT " + DBHelper._ID + " , " + DBHelper.Date + " , " + DBHelper.Time + " , " + DBHelper.Latitude + " , " + DBHelper.Longitude + " FROM "+ DBHelper.TABLE_NAME + " WHERE " +DBHelper._ID + " IN (SELECT MIN(" +DBHelper._ID+") FROM " +DBHelper.TABLE_NAME+ ");";
        return database.rawQuery(buildSql,null);
    }
}
