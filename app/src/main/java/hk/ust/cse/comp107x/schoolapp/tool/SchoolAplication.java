package hk.ust.cse.comp107x.schoolapp.tool;

import android.app.Application;

import com.facebook.FacebookSdk;

/**
 * Created by blessingorazulume on 4/6/16.
 */
public class SchoolAplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
    }
}
