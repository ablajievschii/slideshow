package com.ablajievschii.viewanimation;

import android.os.Environment;

public class Constants {

    static final String PATH_TO_IMAGE_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/"; //tahoe_view_orig.jpg";

    // JSON constants, must be synchronized with json file
    static final String JSON_FILE_NAME = "pictures.json";
    static final String KEY_ARRAY_NAME = "images";
    static final String KEY_IMAGE_ID = "image_id";
    static final String KEY_FILE_PATH = "file_path";
    static final String KEY_NEXT_IMAGE_ID = "next_image_id";
    static final String KEY_DURATION = "duration";
    static final String KEY_COORDINATES = "coordinates";
    static final String KEY_LAT = "lat";
    static final String KEY_LONG = "long";
    static final String KEY_RADIUS = "radius";

    static final String KEY_IMG = "img";
    static final int KEY_EXIT = -1;
    static final int KEY_IMG_LOADED = 1;
    static final int KEY_UPDATE_IMAGE_INFO = 2;

    // for TimeTask
    static final int DEFAULT_DELAY = 3000;
    static final int PERIOD = 3000;

    protected static final int SHOW_NEXT_IMAGE = 100;

    static final String KEY_SERVICE_MSG = "srvc_msg";

    static final String ACTION_UPDATE_IMAGES_INFO = "action_update_images_info";

    public enum  NavTypes{
        FORWARD,
        RANDOM
    }
}

/*
"_COMMENTS":"if some value is not specified then use null",
 "images" : [
   {
     "image_id":100,
     "file_path":"/sdcard/Pictures/flight_bayview.jpg",
     "next_image_id":"200",
     "duration":4,
     "coordinates":
       {
         "lat":null,
         "long":null
       },
     "radius":null
   }
*/