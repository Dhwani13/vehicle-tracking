package com.tracker.dhwani.vehicletracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Environment;

import com.tracker.dhwani.database.DBHelper;
import com.tracker.dhwani.database.SQLController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dhwani.sanghvi on 8/5/2017.
 */
public class Utils {
    public static final String SHARED_PREF = "LOCATION_PREF";
    public static final String PREV_INTERVAL = "LOCATION_NEXT_INTERVAL";
    public static final String FILE_DIR = "TRACKER";
    public static final String FILE_NAME = "Tracker.txt";

    /**
     * method to insert the location into txt file
     * @param location
     * @param current
     * @param next
     * @throws IOException
     */
    public static void insertIntoFile(Location location, int current, int next) throws IOException {
        File file = new File(Environment.getExternalStorageDirectory()+File.separator+FILE_DIR+File.separator+FILE_NAME);
        String data = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        data = data.concat(dateFormat.format(new Date()));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        data = data.concat("-"+timeFormat.format(new Date()));
        data = data.concat(" "+String.format("%.02f",location.getLatitude()));
        data = data.concat(" "+String.format("%.02f",location.getLongitude()));
        data = data.concat(" "+current);
        data = data.concat(" "+next);
        FileOutputStream fileOutputStream = new FileOutputStream(file,true);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        outputStreamWriter.append(data);
        outputStreamWriter.append("\n");
        outputStreamWriter.flush();
        outputStreamWriter.close();
    }

    /**
     * method to insert into the db file
     * @param context
     * @param location
     * @param prev
     * @param next
     */
    public static void insertIntoDB(Context context, Location location, int prev, int next) {
        SQLController dbcon = new SQLController(context);
        dbcon.open();
        dbcon.insert(location.getLatitude(), location.getLongitude(), prev, next);
    }
}
