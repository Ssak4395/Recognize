package com.example.recognize.utils;


import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Util class contain reusable helper functions
 */
public class Utils {

    public static float pitch = 1f;

    public static void changePitch(){
        if(pitch==.5f){
            pitch = 1f;
        }else{
            pitch =.5f;
        }
    }

    /**
     * Helper function to hide the virtual onscreen keyboard.
     *
     * @param activity current activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }


    /**
     * Helper function that recursively initialises all the views so that when clicking outside
     * an edit text div, the keyboard shuts.
     *
     * @param view     parent view containing children
     * @param activity current activity
     */
    public static void setUpCloseKeyboardOnOutsideClick(View view, Activity activity) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                Utils.hideSoftKeyboard(activity);
                return false;
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setUpCloseKeyboardOnOutsideClick(innerView, activity);
            }
        }
    }


}
