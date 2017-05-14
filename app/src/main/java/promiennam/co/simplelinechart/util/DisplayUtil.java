package promiennam.co.simplelinechart.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Phuong on 14-May-17.
 */

public class DisplayUtil {

    /**
     * Get centerX point on screen
     * @param activity
     * @return
     */
    public float getCenterX(Activity activity) {
        if (activity != null) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);
            return size.x / 2;
        }
        return 0;
    }

    /**
     * Get centerY point on screen
     * @param activity
     * @return
     */
    public float getCenterY(Activity activity) {
        if (activity != null) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);
            return size.y / 2;
        }
        return 0;
    }

    /**
     * Get screen width in pixel
     *
     * @param activity
     * @return
     */
    public int getScreenWidth(Activity activity) {
        if (activity != null) {
            WindowManager windowManager = activity.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
        return 0;
    }

    /**
     * Get screen height in pixel
     *
     * @param activity
     * @return
     */
    public int getScreenHeight(Activity activity) {
        if (activity != null) {
            WindowManager windowManager = activity.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        }
        return 0;
    }
}
