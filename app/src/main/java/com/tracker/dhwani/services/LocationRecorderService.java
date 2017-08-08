package com.tracker.dhwani.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.tracker.dhwani.database.SQLController;
import com.tracker.dhwani.vehicletracker.Utils;

/**
 * Created by dhwani.sanghvi on 8/4/2017.
 */
public class LocationRecorderService extends Service {

    private static final String TAG = "LocationTrackService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 0;
    private static final float LOCATION_DISTANCE = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SQLController dbcon;
    Float speed;

    private class LocationListener implements android.location.LocationListener {

        Location mLastLocation;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            speed = location.getSpeed() * (float) 3.6;
            int nextTaskInterval = addSpeedinDB(location, speed);
            Log.e("Tracker", "" + speed + "next: " + nextTaskInterval);
            if (ActivityCompat.checkSelfPermission(LocationRecorderService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationRecorderService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Consider calling
                return;
            }
            mLocationManager.removeUpdates(mLocationListeners[0]);
            startLocationRelatedTask((long) nextTaskInterval);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }

        /**
         * method to calculate the next interval based on the current speed and the previous speed.
         * @param location
         * @param speed
         * @return
         */
        int addSpeedinDB(Location location, float speed) {
            int next;
            if (speed >= 80) {
                next = 30;
            }
            // this if for the condition that checks the current speed and also the previous speed. so that the change is only gradual and not drastic.
            else if (speed >= 60 || sharedPreferences.getInt(Utils.PREV_INTERVAL,0)==30) {
                next = 60;
            } else if (speed >= 30 || sharedPreferences.getInt(Utils.PREV_INTERVAL,0)==60) {
                next = 120;
            } else {
                next = 300;
            }
            try {
                Utils.insertIntoDB(getApplicationContext(),location,sharedPreferences.getInt(Utils.PREV_INTERVAL,0),next);
                editor.putInt(Utils.PREV_INTERVAL, next).commit();
                Utils.insertIntoFile(location, sharedPreferences.getInt(Utils.PREV_INTERVAL, 0), next);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return next*1000;
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        Log.e(TAG, "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        sharedPreferences = getSharedPreferences(Utils.SHARED_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        initializeLocationManager();
        startLocationRelatedTask(0);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
    }

    /**
     * location update request handled here. The location updates are sent based on the interval.
     * @param nextTaskInterval
     */
    public void startLocationRelatedTask(final long nextTaskInterval) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Log.e("Tracker","inside handle message");
                        try {
                            mLocationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    LOCATION_INTERVAL,
                                    LOCATION_DISTANCE,
                                    mLocationListeners[0]
                            );
                        } catch (java.lang.SecurityException ex) {
                            Log.e(TAG, "fail to request location update, ignore", ex);
                        } catch (IllegalArgumentException ex) {
                            Log.e(TAG, "gps provider does not exist " + ex.getMessage());
                        }

                        break;
                    default:
                        break;
                }
            }
        };
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, nextTaskInterval);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex);
                }
            }
        }
    }

    /**
     * initialize location manager.
     */
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: " + LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
