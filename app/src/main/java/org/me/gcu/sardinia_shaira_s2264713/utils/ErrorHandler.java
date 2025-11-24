package org.me.gcu.sardinia_shaira_s2264713.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.me.gcu.sardinia_shaira_s2264713.R;

public class ErrorHandler {

    private static final int TOAST_DURATION = Toast.LENGTH_SHORT;

    private static void showCustomToast(Context context, int imageRes) {
        if (context == null) return;

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        ImageView toastImage = layout.findViewById(R.id.toast_image);
        toastImage.setImageResource(imageRes);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM | Gravity.END, 40, 80);
        toast.setDuration(TOAST_DURATION);
        toast.setView(layout);
        toast.show();
    }

    /* Show error toast */
    public static void showError(Context context, String message) {
        showCustomToast(context, R.drawable.toast_error);
    }

    /* Show generic error */
    public static void showGenericError(Context context) {
        showCustomToast(context, R.drawable.toast_error);
    }

    /* Show network error */
    public static void showNetworkError(Context context) {
        showCustomToast(context, R.drawable.toast_no_net);
    }

    /* Show data error */
    public static void showDataError(Context context) {
        showCustomToast(context, R.drawable.toast_no_data);
    }

    /* Show conversion error */
    public static void showConversionError(Context context) {
        showCustomToast(context, R.drawable.toast_error);
    }

    /* Show save success */
    public static void showSaveSuccess(Context context, String currencyCode) {
        showCustomToast(context, R.drawable.toast_saved);
    }

    /* Show remove success */
    public static void showRemoveSuccess(Context context, String currencyCode) {
        showCustomToast(context, R.drawable.toast_removed);
    }
}