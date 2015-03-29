package com.ablajievschii.viewanimation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.viewanimation.R;

public class StartActivity extends Activity {

    private static final String TAG = "ViewAnm_StartActvt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);
        // for testing, remove later
        //JSONUtils.loadImagesFromJson(JSONUtils.loadJsonFromAsset(this));
        // end
    }

    public void onStartPressed(View view){
        Intent startAnimationIntent = new Intent(this, AnimationActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("navtype", mNavType);
//        startAnimationIntent.putExtras(bundle);
        ControlLogs.initLogging();
        startActivity(startAnimationIntent);
    }

    public void onStopPressed(View view){
    }
}
