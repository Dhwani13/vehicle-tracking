package com.tracker.dhwani.vehicletracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tracker.dhwani.services.LocationRecorderService;

import java.io.File;

/**
 * Main Activity which contains the start stop buttons to start and stop the location tracking.
 */
public class StartActivity extends AppCompatActivity {

    Button startButton, stopButton;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initialise();
    }

    /**
     * method to initialise the view and elements used in the activity
     */
    void initialise(){
        startButton = (Button) findViewById(R.id.start_button);
        stopButton = (Button) findViewById(R.id.stop_button);
        //allow permissions that are must for the app
        addPermissions();
        sharedPreferences = getSharedPreferences(Utils.SHARED_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        final Intent intent = new Intent(StartActivity.this,LocationRecorderService.class);
        // handles the on click of the start button. starts the service.
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    Toast.makeText(getApplicationContext(),"GPS should be enabled to use the service. Kindly enable GPS first.",Toast.LENGTH_SHORT).show();
                } else {
                    startService(intent);
                    updateButton(true);
                }
            }
        });
        // handles the on click of the stop button. stops the service.
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(intent);
                updateButton(false);
            }
        });
    }

    /**
     * method to update the start and stop buttons based on wether the service is running or not.
     * @param serviceRunning
     */
    private void updateButton(boolean serviceRunning) {
        startButton.setEnabled(!serviceRunning);
        stopButton.setEnabled(serviceRunning);
        if(serviceRunning){
            stopButton.setAlpha(1.0f);
            startButton.setAlpha(0.4f);
        } else {
            startButton.setAlpha(1.0f);
            stopButton.setAlpha(0.4f);
        }
    }

    /**
     * method to allow the run time permissions needed for the application
     */
    void addPermissions(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            } else {
                //continue with the app, do nothing
            }
        } else {
            //continue with the app, do nothing
        }
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(getApplicationContext(),"GPS should be enabled to use the service. Kindly enable GPS first.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    // Validate the permissions result
                    if (grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                        // continue with the app, do nothing
                    } else {
                        Toast.makeText(getApplicationContext(),"Allow Permission for the app to function",Toast.LENGTH_SHORT).show();
                        // close app as permission not granted for the app.
                        finish();
                    }
                }
                break;
        }
    }
}
