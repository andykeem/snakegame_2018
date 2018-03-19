package com.example.foo.snakegame.helper;

import android.util.Log;

/**
 * Created by foo on 3/18/18.
 */

public class L<T> {

    protected static final String TAG = L.class.getSimpleName();

    public static <T> void d(T... args) {
        String s = "";
        for (T e : args) {
            s += String.valueOf(e);
        }
        Log.d(TAG, s);
    }
}
