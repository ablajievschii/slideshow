package com.ablajievschii.viewanimation;

import java.util.Arrays;

public class ImageInfo {

    private static final String TAG = "ImageInfo";
	private String mImageID = null;
    private String mFilePath = null;
    private String mNextImageID = null;
    private long mDuration = 0;
    private double[] mCoordinates = null; // should be new long[2] = {latitude, longitude};
    private double mRadius = 0;

    public ImageInfo() {
        mCoordinates = new double[2];
    }

    public String getImageID() {
        return mImageID;
    }
    public void setImageID(String imageID) {
        this.mImageID = imageID;
    }
    public String getFilePath() {
        return mFilePath;
    }
    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }
    public String getNextImageID() {
        return mNextImageID;
    }
    public void setNextImageID(String nextImageID) {
        this.mNextImageID = nextImageID;
    }
    public long getDuration() {
        return mDuration;
    }
    public void setDuration(double duration) {
        if (duration <= 0){
            this.mDuration = Constants.DEFAULT_DELAY;
        } else {
            this.mDuration = (long)(duration * 1000);
        }
    }
    /**
     * return array of 2 values arr[0] == latitude, arr[1] == longitude
     */
    public double[] getCoordinates() {
        return mCoordinates;
    }
    public void setCoordinates(double lat, double lon) {
        mCoordinates[0] = lat;
        mCoordinates[1] = lon;
    }
    public double getRadius() {
        return mRadius;
    }
    public void setRadius(double radius) {
        this.mRadius = radius;
    }

    @Override
    public String toString() {
        return "ImageInfo [mImageID=" + mImageID + ", mFilePath=" + mFilePath
             + ", mNextImageID=" + mNextImageID + ", mDuration=" + mDuration
             + ", mCoordinates=" + Arrays.toString(mCoordinates)
             + ", mRadius=" + mRadius + "]";
    }
}
