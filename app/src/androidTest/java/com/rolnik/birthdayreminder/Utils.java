package com.rolnik.birthdayreminder;

import android.content.Context;

public class Utils {
    private Utils() {

    }

    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }
}
