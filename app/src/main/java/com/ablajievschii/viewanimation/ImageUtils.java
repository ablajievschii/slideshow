package com.ablajievschii.viewanimation;

import java.io.File;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageUtils {

    private static final String TAG = "ImageUtils";
    int imageWidth = 0;
    int imageHeight = 0;
    String imageType = null;
    int mInSampleSize = 1;

    static boolean showLocalLogs = false;

    private void loadJustImageSizeType(File image, BitmapFactory.Options options){
        BitmapFactory.decodeFile(image.getAbsolutePath(), options);
        imageWidth = options.outWidth;
        imageHeight = options.outHeight;
        imageType = options.outMimeType;
        if (showLocalLogs){
            Log.d(TAG, "imageWidth: " + imageWidth);
            Log.d(TAG, "imageHeight: " + imageHeight);
            Log.d(TAG, "imageType: " + imageType);
        }
    }

    private int calculateInSampleSize(int reqWidth, int reqHeight) {
     // Raw height and width of image
        if (imageWidth == 0 || imageHeight == 0 || imageType == null){
            return -1;
        }
        int inSampleSize = 1;

        if (imageHeight > reqHeight || imageWidth > reqWidth) {

            final int halfWidth = imageWidth / 2;
            final int halfHeight = imageHeight / 2;

         // Calculate the largest inSampleSize value that is a power of 2 and keeps both
         // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && 
                    (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        if (showLocalLogs){
            Log.d(TAG, "inSampleSize: " + inSampleSize);
        }
        return inSampleSize;
    }

    Bitmap decodeBitmapFromFile(File image, int reqWidth, int reqHeight){

        BitmapFactory.Options options = new BitmapFactory.Options();

     // First decode with inJustDecodeBounds=true to check dimensions
        options.inJustDecodeBounds = true;
        loadJustImageSizeType(image, options);
        mInSampleSize = calculateInSampleSize(reqWidth, reqHeight);
        options.inSampleSize = mInSampleSize;

     // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(image.getAbsolutePath(), options);
    }

    public int getInSampleSize(){
        return mInSampleSize;
    }

    public Bitmap getScaledBitmap(Bitmap srcImage, int orientation, int reqWidth, int reqHeight){
        Bitmap scaledImage = null;
        switch (orientation) {
        case Configuration.ORIENTATION_PORTRAIT:
            scaledImage = Bitmap.createScaledBitmap(srcImage, reqHeight, reqWidth, true);
            break;
        case Configuration.ORIENTATION_LANDSCAPE:
            scaledImage = Bitmap.createScaledBitmap(srcImage, reqWidth, reqHeight, true);
            break;
        default:
            break;
        }
        return scaledImage;
    }
}
