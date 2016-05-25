package hk.ust.cse.comp107x.schoolapp.tool;

import android.graphics.Color;
import android.widget.ImageView;

/**
 * Created by blessingorazulume on 4/5/16.
 */
public class ImageFilter {
    public static void filterImage (ImageView imageView, String colorCode) {
        int color = Color.parseColor(colorCode);
        imageView.setColorFilter(color);
    }

}
