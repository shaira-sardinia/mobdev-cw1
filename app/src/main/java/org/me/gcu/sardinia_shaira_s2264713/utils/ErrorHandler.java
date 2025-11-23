package org.me.gcu.sardinia_shaira_s2264713.utils;

import android.content.Context;
import android.widget.Toast;

public class ErrorHandler {

    private static final int TOAST_DURATION = Toast.LENGTH_SHORT;

    /* Show error toast */
    public static void showError(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context, message, TOAST_DURATION).show();
        }
    }

    /* Show generic error */
    public static void showGenericError(Context context) {
        showError(context, "Sorry, an error occurred! Try again?");
    }

    /* Show network error */
    public static void showNetworkError(Context context) {
        showError(context, "No internet connection. Please check and try again.");
    }

    /* Show data error */
    public static void showDataError(Context context) {
        showError(context, "Failed to load data. Try again?");
    }

    /* Show conversion error */
    public static void showConversionError(Context context) {
        showError(context, "Something went wrong.");
    }

    /* Show save success */
    public static void showSaveSuccess(Context context, String currencyCode) {
        showError(context, currencyCode + " saved!");
    }

    /* Show remove success */
    public static void showRemoveSuccess(Context context, String currencyCode) {
        showError(context, currencyCode + " removed!");
    }
}