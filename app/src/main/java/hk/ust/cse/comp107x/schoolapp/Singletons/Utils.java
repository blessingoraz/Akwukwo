package hk.ust.cse.comp107x.schoolapp.Singletons;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Created by blessingorazulume on 2/17/16.
 */
public class Utils {

    public static boolean isOnLine(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info != null && info.isConnectedOrConnecting()) {
            return true;
        }
        return  false;
    }

    public static void showShortToast (String Message, Context context) {
        Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongMessage (String Message, Context context) {
        Toast.makeText(context, Message, Toast.LENGTH_LONG).show();
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);

        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

    }

    public static boolean isEmpty(String userInput) {
        return userInput == null || userInput.equals("");
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
