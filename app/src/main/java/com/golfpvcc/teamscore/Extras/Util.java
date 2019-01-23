package com.golfpvcc.teamscore.Extras;

import android.view.View;

import java.util.List;

/**
 * Created by vinnie on 9/2/2016.
 */
public class Util {
    public static void showViews(List<View> views) {
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hideViews(List<View> views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }

}

