package com.example.onlineshop;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;

public class ShopApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MediaManager.init(this, new HashMap<String, Object>() {{
            put("cloud_name", "dkll2ypza");
        }});
    }
}