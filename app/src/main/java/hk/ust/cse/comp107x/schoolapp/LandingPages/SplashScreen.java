package hk.ust.cse.comp107x.schoolapp.LandingPages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Handler;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import hk.ust.cse.comp107x.schoolapp.Constants;
import hk.ust.cse.comp107x.schoolapp.NoInternetActivity;
import hk.ust.cse.comp107x.schoolapp.R;
import hk.ust.cse.comp107x.schoolapp.Singletons.Utils;
import hk.ust.cse.comp107x.schoolapp.ViewPageActivity;

public class SplashScreen extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 6000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(!Utils.isOnLine(SplashScreen.this)) {
                    SplashScreen.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SplashScreen.this, NoInternetActivity.class));
                            SplashScreen.this.finish();
                        }
                    });

                } else {
                    checkToken();
                    SplashScreen.this.finish();

                }
                /* Create an Intent that will start the Menu-Activity. */
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void checkToken() {
        SharedPreferences prefs = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        final String token = prefs.getString(Constants.USER_TOKEN, "");

        if (!token.isEmpty()) {

            startActivity(new Intent(SplashScreen.this, ViewPageActivity.class));

        } else {
            Intent mainIntent = new Intent(SplashScreen.this, LandingPageActivity.class);
            SplashScreen.this.startActivity(mainIntent);
        }

    }

}
