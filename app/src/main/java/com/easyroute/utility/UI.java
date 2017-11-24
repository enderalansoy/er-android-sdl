package com.easyroute.utility;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easyroute.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class UI {

    public static BitmapDescriptor getBitmapDescriptorFromResource(Context context, int iconResId, int size) {
        Bitmap iconBmp = BitmapFactory.decodeResource(context.getResources(), iconResId);
        float aspectRatio = (float) iconBmp.getWidth() / iconBmp.getHeight();
        Bitmap resizedIconBmp = Bitmap.createScaledBitmap(iconBmp, size, (int) ((float) size / aspectRatio), false);
        return BitmapDescriptorFactory.fromBitmap(resizedIconBmp);
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap createNotificationLargeIconBitmap(Context context, Bitmap source) {
        int scaleSize = (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_width);
        Bitmap resizedBitmap = null;
        int originalWidth = source.getWidth();
        int originalHeight = source.getHeight();
        int newWidth = -1;
        int newHeight = -1;
        float multFactor = -1.0F;
        if (originalHeight > originalWidth) {
            newHeight = scaleSize;
            multFactor = (float) originalWidth / (float) originalHeight;
            newWidth = (int) (newHeight * multFactor);
        } else if (originalWidth > originalHeight) {
            newWidth = scaleSize;
            multFactor = (float) originalHeight / (float) originalWidth;
            newHeight = (int) (newWidth * multFactor);
        } else if (originalHeight == originalWidth) {
            newHeight = scaleSize;
            newWidth = scaleSize;
        }
        resizedBitmap = Bitmap.createScaledBitmap(source, newWidth, newHeight, false);
        return resizedBitmap;
    }

    public static Snackbar snackbar(View view, String text, int duration) {
        return snackbar(view, text, null, null, duration);
    }

    public static Snackbar snackbar(View view, String text, String action, View.OnClickListener actionClickListener, int duration) {
        Snackbar snackbar = Snackbar.make(view, text, duration);
        if (action != null) {
            snackbar.setAction(action, actionClickListener);
        }
        View v = snackbar.getView();
        v.setBackgroundResource(R.color.primary);
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(ContextCompat.getColor(v.getContext(), android.R.color.white));
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(ContextCompat.getColor(v.getContext(), R.color.black));
        return snackbar;
    }

    public static Toast toast(Context context, int textResId, int duration) {
        Toast toast = Toast.makeText(context, textResId, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, context.getResources().getDimensionPixelSize(R.dimen.large_padding));
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.bg_toast);
        toast.show();
        return toast;
    }

    /**
     * Ekranın o anki yönünü verir.
     *
     * @param activity
     * @return
     */
    public static int getScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && height > width || (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }
        return orientation;
    }

    /**
     * Cihazın 7 inch olup olmadığı bilgisini dönderir.
     *
     * @param activity
     * @return 7 inch ise true
     */
    public static boolean isDevice7Inch(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float widthInInches = metrics.widthPixels / metrics.xdpi;
        float heightInInches = metrics.heightPixels / metrics.ydpi;
        double sizeInInches = Math.sqrt(Math.pow(widthInInches, 2) + Math.pow(heightInInches, 2));
        return sizeInInches <= 8;
    }

    /**
     * ScreenDensity değerini verir
     *
     * @param context
     * @return
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * Ekran genişliğini verir
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * Ekran yüksekliğini verir
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * Parametre ile gönderilen view'bg_circular_progress_bar alpha set'ler.
     *
     * @param view
     * @param alpha
     */
    public static void setAlpha(View view, float alpha) {
        AlphaAnimation anim = new AlphaAnimation(0, alpha);
        anim.setDuration(0);
        anim.setFillAfter(true);
        view.startAnimation(anim);
    }

    /**
     * Parametre ile gönderilen view'ın gerçek boyutunu hesaplayıp dönderir
     *
     * @param view
     * @return
     */
    public static int[] getViewSize(View view) {
        int _visibility = view.getVisibility();
        if (_visibility == View.GONE) view.setVisibility(View.VISIBLE);
        int _desiredWidth = MeasureSpec.makeMeasureSpec(view.getWidth(), MeasureSpec.UNSPECIFIED);
        int _desiredHeight = MeasureSpec.makeMeasureSpec(view.getHeight(), MeasureSpec.UNSPECIFIED);
        view.measure(_desiredWidth, _desiredHeight);
        int[] rtn = new int[]{view.getMeasuredWidth(), view.getMeasuredHeight()};
        view.setVisibility(_visibility);
        return rtn;
    }

    /**
     * Density independent pixel değerinin pixel cinsinden karşılığını dönderir
     *
     * @param dp
     * @param context
     * @return
     */
    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    /**
     * Scale independent pixel değerinin pixel cinsinden karşılığını dönderir
     *
     * @param sp
     * @param context
     * @return
     */
    public static int spToPx(int sp, Context context) {
        float density = context.getResources().getDisplayMetrics().scaledDensity;
        return Math.round((float) sp * density);
    }

    /**
     * Parametre ile gönderilen view içerisinde EditText bulur. KeyboardUtils'de kullanılır.
     *
     * @param view
     * @return
     */
    public static EditText findEditText(ViewGroup view) {
        EditText rtn = null;
        if (view instanceof ViewGroup) {
            for (int i = 0; i < view.getChildCount(); i++) {
                View child = view.getChildAt(i);
                if (child instanceof EditText) {
                    rtn = (EditText) child;
                    break;
                } else if (child instanceof ViewGroup) {
                    rtn = findEditText((ViewGroup) child);
                    if (rtn != null) break;
                }
            }
        }
        return rtn;
    }
}
