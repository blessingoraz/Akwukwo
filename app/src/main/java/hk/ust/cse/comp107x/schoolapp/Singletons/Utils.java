package hk.ust.cse.comp107x.schoolapp.Singletons;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import hk.ust.cse.comp107x.schoolapp.LandingPages.SignUpPageActivity;
import hk.ust.cse.comp107x.schoolapp.RegistrationActivity;
import hk.ust.cse.comp107x.schoolapp.ViewPageActivity;

/**
 * Created by blessingorazulume on 2/17/16.
 */
public class Utils {

//    public void alertDialog(final Context context) {
//
//        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//        builder.setTitle("Do you want to register a school?");
//
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                startActivity(new Intent(context, RegistrationActivity.class));
//
//            }
//        });
//
//        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                context.startActivity(Intent );
//
//            }
//        });
//
//
//        builder.create();
//        builder.show();
//    }

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
}
