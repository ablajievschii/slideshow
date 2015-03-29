package com.ablajievschii.viewanimation;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

public class SlideShowService extends Service {

    private static final String TAG = "RunSlideShow";
    private Timer timer = new Timer();
    private ChangePictureTimerTask timerTask;
    private ResultReceiver resultReceiver;

    static boolean showLocalLogs = false;

    HashMap<String, Integer> mCountImageShow = new HashMap<String, Integer>();

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.i(TAG, "intent: " + intent);

        String imgId = intent.getStringExtra(Constants.KEY_SERVICE_MSG);
        if (showLocalLogs){
            Log.d(TAG, "received image id: " + imgId);
        }

        if (imgId != null){
            if (!imgId.equals("")){
                addCountImageShown(imgId);
            }
        }

        //intent = null;
        /*
        if (intent != null){
            resultReceiver = intent.getParcelableExtra("receiver");
            Log.i(TAG, "resultreceiver: " + resultReceiver);
            timerTask = new ChangePictureTimerTask();
            timer.scheduleAtFixedRate(timerTask, Constants.DELAY, Constants.PERIOD);
        }
        */
        return START_NOT_STICKY;
    }

    private void addCountImageShown(String imgId){
        if (mCountImageShow.get(imgId) == null){
            mCountImageShow.put(imgId,1);
        } else {
            mCountImageShow.put(imgId,(mCountImageShow.get(imgId) + 1));
        }
        if (showLocalLogs){
            Log.d(TAG, "image with id " + imgId + " has been shown #" + mCountImageShow.get(imgId));
        }
    }

    class ChangePictureTimerTask extends TimerTask {
        public ChangePictureTimerTask() {
        }

        @Override
        public void run() {
            Bundle b = new Bundle();
            b.putString("command", "nextImage");
            resultReceiver.send(1, b);
        }
    }

    private void saveToFile(String data){
        
    }

    @Override
    public void onDestroy() {
       super.onDestroy();
       timer.cancel();
    }
}
