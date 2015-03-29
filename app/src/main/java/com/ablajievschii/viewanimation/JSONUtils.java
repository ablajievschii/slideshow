package com.ablajievschii.viewanimation;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class JSONUtils {

    private static final String TAG = "JSONUtils";

    static boolean showLocalLogs = false;
    static Context mContext = null;

    public static String loadJsonFromAsset(Context context){
        mContext = context;
        String json = null;
        try{
           InputStream is = context.getAssets().open(Constants.JSON_FILE_NAME);
           int size = is.available();
           byte[] buffer = new byte[size];
           is.read(buffer);
           is.close();
           json = new String(buffer, "UTF-8");
        } catch (IOException e){
            if (showLocalLogs){
                Log.d(TAG, "Error: " + e.getMessage());
            }
            return null;
        }
        return json;
    }

    /**
     * 
     * @param json
     * @return hashmap object which contains mappings of image id's and image info objects
     */

    protected static HashMap<String, ImageInfo> loadImagesFromJson(String json, LocManager locManager){
        HashMap<String, ImageInfo> images = new HashMap<String, ImageInfo>();
        try {
            JSONObject jObj = new JSONObject(json);
            JSONArray jArr = jObj.optJSONArray(Constants.KEY_ARRAY_NAME);
            if (jArr == null) {
                if (showLocalLogs){
                    Log.d(TAG, "retunr null, json file is empty");
                }
                return null;
            }
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject jObjImj = jArr.optJSONObject(i);
                if (jObjImj != null){
                    boolean inRightArea = true;
                    ImageInfo iInfo = new ImageInfo();
                    iInfo.setImageID(jObjImj.optString(Constants.KEY_IMAGE_ID));
                    iInfo.setFilePath(jObjImj.optString(Constants.KEY_FILE_PATH));
                    iInfo.setNextImageID(jObjImj.optString(Constants.KEY_NEXT_IMAGE_ID));
                    iInfo.setDuration(jObjImj.optDouble((Constants.KEY_DURATION)));
                    JSONArray jCoord = jObjImj.optJSONArray(Constants.KEY_COORDINATES);
//                    Log.d(TAG, "jCoord: " + jCoord);
                    if (jCoord != null){
                        iInfo.setCoordinates(jCoord.optJSONObject(0).optDouble(Constants.KEY_LAT), jCoord.optJSONObject(1).optDouble(Constants.KEY_LONG));
                        iInfo.setRadius(jObjImj.optDouble(Constants.KEY_RADIUS)); // set the radius only if we have coordinates
                        inRightArea = locManager.islocationRight(iInfo.getCoordinates()[0], iInfo.getCoordinates()[1], iInfo.getRadius());
                    }
                    if (inRightArea){
                        if (showLocalLogs){
                            Log.d(TAG, "in right area: " + inRightArea + "; adding image into map: " + iInfo);
                        }
                        images.put(iInfo.getImageID(), iInfo);
                    }
                }
            }
        } catch (JSONException e) {
            if (showLocalLogs){
                Log.d(TAG, "Could not load images info from JSON: \n" + e.getMessage());
            }
        }
        mContext = null;
        return images;
    }
}
