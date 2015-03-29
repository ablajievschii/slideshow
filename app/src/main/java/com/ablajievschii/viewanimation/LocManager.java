package com.ablajievschii.viewanimation;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocManager {

	private static final String TAG = "LocManager";

    private static final long MIN_PERIOD = 7000;//24*60*60*1000; //in milliseconds
    private static final float MIN_DISTANCE = 1;
    private Location currentLocation = null;
    private MyLocationListener locListener = null;

//    private Context mContext = null;

    // testing location dependency
    boolean dontCheck = true;

    private LocationManager locManager = null;
    private static LocManager location = null;
    private double currentLat = 0;
    private double currentLon = 0;

    static boolean showLocalLogs = true;

    protected static LocManager getInstance(Context context){
        if (location == null){
            location = new LocManager(context);
        }
        return location;
    }

    private LocManager(final Context context){

        locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locListener = new MyLocationListener(context);
        setUpLocationListener(context);
        locManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locListener, null);
        currentLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (showLocalLogs){
            Log.d(TAG, "current location: " + currentLocation);
        }

        TimerTask changeLocationTask = new TimerTask() {

            @Override
            public void run() {
                Log.d(TAG, "changeLocationTask");
                dontCheck = false;
                Intent updateImgInf = new Intent();
                updateImgInf.setAction(Constants.ACTION_UPDATE_IMAGES_INFO);
                context.sendBroadcast(updateImgInf);
            }
        };
        Timer timer= new Timer();
        Log.d(TAG, "schedule timer");
        timer.schedule(changeLocationTask, 10000);
    }

    // testing mock locations
    public Location createLocation(double lat, double lng, float accuracy) {
        // Create a new Location
        Location newLocation = new Location("flp");
        newLocation.setLatitude(lat);
        newLocation.setLongitude(lng);
        newLocation.setAccuracy(accuracy);
        return newLocation;
    }

    private void setUpLocationListener(Context context){
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_PERIOD, MIN_DISTANCE, locListener);
        if (showLocalLogs){
            Log.d(TAG, "setUpLocationListener: " + locListener);
        }
    }

    protected boolean islocationRight(double lat, double lon, double rad){
        boolean result = false;
        if (dontCheck){
            return true;
        }
        if ((lat == 0 && lon == 0)){
            return true;
        } else {
            return false;
        }
        /*
        if ((lat <= currentLat + rad && lat >= currentLat - rad)
             &&(lon <= currentLon + rad && lat >= currentLon - rad)){
            result = true;
        }
        Log.d(TAG, "islocationRight: " + result);
        return result;
        */
    }

    class MyLocationListener implements LocationListener{

        Context mContext = null;

        public MyLocationListener(Context context) {
            mContext = context;
        }

        @Override
        public void onLocationChanged(Location location) {
           currentLocation = location;
           currentLat = currentLocation.getLatitude();
           currentLon = currentLocation.getLongitude();
//           dontCheck = false;
           Intent updateImgInf = new Intent();
           updateImgInf.setAction(Constants.ACTION_UPDATE_IMAGES_INFO);
           mContext.sendBroadcast(updateImgInf);
           if (showLocalLogs){
               Log.d(TAG, "onLocationChanged: " + location);
           }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }

    }
}
 