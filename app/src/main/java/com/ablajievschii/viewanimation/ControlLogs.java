package com.ablajievschii.viewanimation;

public class ControlLogs {

    static void initLogging(){
        AnimationActivity.showLocalLogs = true; // AnimActvt
        ImageManager.showLocalLogs = false; //ImageMgr
        ImageUtils.showLocalLogs = false;
        JSONUtils.showLocalLogs = false; //JSONUtils
        SlideShowService.showLocalLogs = true; // RunSlideShow
        LocManager.showLocalLogs = false; //LocManager

    }

}
