package com.example.movieapp;

import android.util.Log;

public class ApiKey {
    private static final String TAG = "ApiKey";
    private static final String DEFAULT_KEY = "402e749589b4d9ab00ceb07c897c068f";
    private static String cachedKey = null;

    static {
        try {
            System.loadLibrary("api_key");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load native library", e);
        }
    }

    public String getApiKey() {
        if (cachedKey != null) {
            return cachedKey;
        }
        
        try {
            cachedKey = getNativeApiKey();
            return cachedKey;
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native method not found, using default key", e);
            return DEFAULT_KEY;
        }
    }

    private native String getNativeApiKey();
} 