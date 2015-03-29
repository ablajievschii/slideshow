package com.ablajievschii.viewanimation;

import java.util.HashMap;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.viewanimation.R;

@SuppressLint("NewApi")
public class AnimationActivity extends Activity {

    static boolean runShow = true;

    private static final String TAG = "AnimActvt";
    HashMap<String, ImageInfo> mImages = null;
    Context mContext = this;
    Bitmap mImageToShow = null;

    Handler mImageHandler = null;
    UpdateImagesInfoReceiver mReceiver = null;

//    private Bitmap myBitmap = null;
    private ImageView mImageView = null;
    private ProgressBar mPbUpdate = null;

    Intent mIntentService = null;

    static boolean showLocalLogs = true;

    ObjectAnimator mCurrentAnimation = new ObjectAnimator();
//    Animation mCurrentAnimation = null;
//    int currentListPos = 0;
    long mDuration = Constants.DEFAULT_DELAY;
    String mNextImageID = "";

    ResultReceiver resultReceiver = null;

    protected boolean mImagesInfoLoaded = false;

//    private boolean continiousShow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);        

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void loadImagesInfo() {
        final LocManager locManager = LocManager.getInstance(mContext);
        Thread tLoad = new Thread(new Runnable() {

            @Override
            public void run() {
                if (!mImagesInfoLoaded){
                    mImages = JSONUtils.loadImagesFromJson(JSONUtils.loadJsonFromAsset(mContext), locManager);
                    mImagesInfoLoaded  = true;
                }
                runShow = true;
                if (showLocalLogs){
                    Log.d(TAG, "Images info loaded, sending message to handler: " + mImages);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mImageHandler.sendEmptyMessage(Constants.KEY_IMG_LOADED);
            }
        });
        if (runShow){
            tLoad.start();
        }
    }

    Runnable startServiceRunnable = new Runnable() {

        @Override
        public void run() {
            if (showLocalLogs){
                Log.d(TAG, "startServiceRunnable");
            }
            startSlideShow();
            startAnimationService();
        }
    };

    Runnable showNextImgRunnable = new Runnable() {

        @Override
        public void run() {
            if (showLocalLogs){
                Log.d(TAG, "showNextImgRunnable");
            }
            // send image id to service
            sendMessageToService(mNextImageID);

            Bitmap showCurrentImg = mImageToShow;
            showImageWithAnimation(showCurrentImg, mDuration);
            showCurrentImg = null;

            // preparing next image while current is showing
            mNextImageID = ImageManager.getNextImageID(mImages);
            ImageInfo im = mImages.get(mNextImageID);
            if (im == null){
                mImageHandler.sendEmptyMessage(-1); // exit
            } else {
                mImageToShow = ImageManager.loadImage(im.getFilePath(), AnimationActivity.this);	
                if (showLocalLogs){
                    Log.d(TAG, "next image id = " + mNextImageID + " loaded");
                }
                mDuration = im.getDuration();
            }
        }
    };

    Runnable updateImgInfRunnable = new Runnable() {

        @Override
        public void run() {
            if (showLocalLogs){
                Log.d(TAG, "updateImgInfRunnable");
            }
            mPbUpdate.setVisibility(View.VISIBLE);
            mPbUpdate.animate();
            loadImagesInfo();
        }
    };

    private void startSlideShow(){
        Thread t = new Thread(new Runnable() {
            
            @Override
            public void run() {
                if (showLocalLogs){
                    Log.d(TAG, "startSlideShow()::run()");
                }
                mNextImageID = ImageManager.getNextImageID(mImages);
                ImageInfo im = mImages.get(mNextImageID);

                if (im == null){
                    mImageHandler.sendEmptyMessage(-1); // exit
                } else {
                    // loading first image
                    mImageToShow = ImageManager.loadImage(im.getFilePath(), AnimationActivity.this);
                    mDuration = im.getDuration();

                    mImageHandler.sendEmptyMessage(Constants.SHOW_NEXT_IMAGE);
                }
            }
        });
        if (runShow){
            t.start();
        }
    }

    void init(){
        if (showLocalLogs){
            Log.d(TAG, "init()");
        }
        mImageView = (ImageView) findViewById(R.id.imageView);
        mPbUpdate = (ProgressBar) findViewById(R.id.pbUpdate);

//        resultReceiver = new SlideShowResultReceiver(null);
        runShow = true;

        float dest = 1;
        mCurrentAnimation = ObjectAnimator.ofFloat(mImageView, "alpha", dest);
        mCurrentAnimation.addListener(animatorListener);

        // default image, for testing
        mImageToShow = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        // loading images info from json
        mPbUpdate.setVisibility(View.VISIBLE);
        mPbUpdate.animate();
        loadImagesInfo();

        mReceiver = new UpdateImagesInfoReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_UPDATE_IMAGES_INFO);
        registerReceiver(mReceiver, filter);

        mImageHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case Constants.KEY_IMG_LOADED:
                    mPbUpdate.setVisibility(View.INVISIBLE);
                    post(startServiceRunnable);
                    break;
                case Constants.SHOW_NEXT_IMAGE:
                    post(showNextImgRunnable);
                    break;
                case Constants.KEY_EXIT: 
                    if (showLocalLogs){
                        Log.d(TAG, "no images to show, exit");
                    }
                    stopSlideShow();
                    finish();
                    break;
                case Constants.KEY_UPDATE_IMAGE_INFO: 
                    post(updateImgInfRunnable);
                default:
                    break;
                }
            }
        };
    }


    private void showImageWithAnimation(Bitmap img, long duration){
        if (showLocalLogs){
            Log.d(TAG, "showing image with id = " + mNextImageID);
        }

        mImageView.setAlpha(0f);
        mCurrentAnimation.setDuration(duration);
        mCurrentAnimation.start();

        mImageView.setImageBitmap(img);
    }

    

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopSlideShow();
    }

    private void stopSlideShow() {
    // stop animation
        mCurrentAnimation.cancel();
        mCurrentAnimation = null;

        // set key to false, to exclude any chance to start new threads
        runShow = false;

        // stop service
        stopAnimationService();

        mImages = null;
        if (mImages == null){
            mImagesInfoLoaded = false;
        }

        // remove callbacks
        mImageHandler.removeCallbacks(showNextImgRunnable);
        mImageHandler.removeCallbacks(startServiceRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        startAnimationService();
//        if (mImagesInfoLoaded){
//            startSlideShow();
//        } else {
//        }
    }

    private void startAnimationService(){
         mIntentService = new Intent(this, SlideShowService.class);
//         mIntentService.putExtra("receiver", resultReceiver);
         startService(mIntentService);
    }

    private void stopAnimationService(){
        if (mIntentService != null){
            stopService(mIntentService);
            mIntentService = null;
        }
   }

    AnimatorListener animatorListener = new AnimatorListener() {

        long start = 0;
        long end = 0;
        long currentDuration = 0;
        
        @Override
        public void onAnimationStart(Animator animation) {
//            Log.d(TAG + "_anmLstnr", "onAnimationStart " + animation);
            start = System.currentTimeMillis();
            currentDuration = mDuration;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
//            Log.d(TAG + "_anmLstnr", "onAnimationRepeat");
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            end = System.currentTimeMillis();
            mImageHandler.removeCallbacks(showNextImgRunnable);
            if (!runShow){
                return;
            }
            if (mImagesInfoLoaded){
                mImageHandler.post(showNextImgRunnable);
            } else {
                mImageHandler.postDelayed(updateImgInfRunnable, 50);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
//            Log.d(TAG + "_anmLstnr", "onAnimationCancel");
        }
    };

    class UpdateImagesInfoReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (showLocalLogs){
                Log.d(TAG, "onReceive() action = " + intent.getAction());
            }
            if (Constants.ACTION_UPDATE_IMAGES_INFO.equals(intent.getAction())){
                mImagesInfoLoaded = false;
                mImageHandler.removeCallbacks(showNextImgRunnable);
                mImageHandler.sendEmptyMessage(10);
            };
        }
        
    }

    private void sendMessageToService(String msg){
        if (showLocalLogs){
            Log.d(TAG, "sendMessageToService() msg: " + msg);
        }
        if (msg == null){
            return;
        }
        Intent serviceIntent = new Intent(mContext, SlideShowService.class);
        serviceIntent.putExtra(Constants.KEY_SERVICE_MSG, msg);
        startService(serviceIntent);
    }

    /*
    class SlideShowResultReceiver extends ResultReceiver{

        public SlideShowResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            runOnUiThread(new UpdateImageView(resultData.getString("command")));
        }
    }

    class UpdateImageView implements Runnable{
        String command;
        public UpdateImageView(String command) {
            this.command = command;
        }

        public void run() {
            if (command.equals("nextImage")){
                loadNextImage(mImageView);
            }
        }
    }
  */
}
