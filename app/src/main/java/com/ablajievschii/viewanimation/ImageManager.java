package com.ablajievschii.viewanimation;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;

public class ImageManager {

    private static final String TAG = "ImageMgr";
    private static ImageUtils mUtils = new ImageUtils();

    private static String nextImageId = "";
    private static int mPrevImage = -1;

    private static int mWidth = 0;
    private static int mHeight = 0;

    static boolean showLocalLogs = false;

    /**
     * 
     * @param images
     * @return next image path
     */
    static String getNextImageID(HashMap<String,ImageInfo> images){
//        BitmapInfoObject bio = new BitmapInfoObject();
        String id = null;
        if (images == null || images.size() == 0){
            return "";
        }
        while (true){
            if (nextImageId.equals("") || nextImageId == null ){
                id = getRandomImage(images);
                nextImageId = id;
                break;
            } else {
                if (showLocalLogs){
                     Log.d(TAG, "getNextImage(): get next image id : " + nextImageId);
                }
                ImageInfo im = images.get(nextImageId);
                nextImageId = im.getNextImageID();
                if (!nextImageId.equals(""))
                    break;
            }
        }
        if (showLocalLogs){
            Log.d(TAG, "getNextImage(): nextImageId = " + nextImageId);
        }
        return nextImageId;
    }

    private static String getRandomImage(HashMap<String, ImageInfo> images) {
        ImageInfo result = null;
        if (images.size() <= 0){
            return "";
        }
        Set<String> set = images.keySet();
        Iterator<String> iterator = set.iterator();
        iterator.next();
        Random r = new Random();
        String key = "";
        int bound = 0;
        while (true){
            bound = r.nextInt(set.size()) + 1;
            if (bound != mPrevImage){
                break;
            }
        }
        mPrevImage = bound;
        for (int i = 0; i < bound; i++) {
            if (iterator.hasNext()){
                key = iterator.next();
            }
        }
        result = images.get(key);
        if (showLocalLogs){
            Log.d(TAG, "getRandomImage(): random id : " + result);
        }
        return result.getImageID();
    }

    protected static Bitmap loadImage(String path, Context context) {
        if (mWidth == 0 || mHeight == 0){
            initDisplaySizeInfo(context);
        }
//      Log.d(TAG, "loadImage(): path = " + path);
        File testImage = new File(path);
//      Log.i(TAG, "loading image >> " + path);
        if (testImage.exists()){

        Bitmap myBitmap = mUtils.decodeBitmapFromFile(testImage, mWidth, mHeight);
        int inSampleSize = mUtils.getInSampleSize();
          // getScaledBitmap - double to check for huge images
        int width = mWidth / inSampleSize;
        int height = mHeight / inSampleSize;
        Bitmap scaledImage = mUtils.getScaledBitmap(myBitmap
                  , context.getResources().getConfiguration().orientation, width, height);
//          Log.d(TAG, "original picture size: " + testImage.length() + " bytes");
//          Log.d(TAG, "compressed picture size: " + scaledImage.getByteCount() + " bytes");
        return scaledImage;
        } else {
            Log.d(TAG, "file " + testImage + " not found");
            return null;
        }
    }

    @SuppressLint("NewApi")
    private static void initDisplaySizeInfo(Context context){
        Point point = new Point();
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        display.getSize(point);
        mWidth = point.x;
        mHeight = point.y;
        if (showLocalLogs){
            Log.d(TAG, "Display: width = " + mWidth + "; height = " + mHeight);
        }
    }
}
