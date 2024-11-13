package com.mlt.driver.utills;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class FontCache {
    private static HashMap<String, Typeface> fontCache = new HashMap<>();

    public static Typeface getTypeface(String fontPath, Context context) {
        Typeface typeface = fontCache.get(fontPath);
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), fontPath);
                fontCache.put(fontPath, typeface);
            } catch (Exception e) {
                e.printStackTrace(); // Optional: Log the error
            }
        }
        return typeface;
    }
}
