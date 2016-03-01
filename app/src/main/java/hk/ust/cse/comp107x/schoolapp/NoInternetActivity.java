package hk.ust.cse.comp107x.schoolapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import hk.ust.cse.comp107x.schoolapp.LandingPages.LandingPageActivity;
import hk.ust.cse.comp107x.schoolapp.LandingPages.SplashScreen;
import hk.ust.cse.comp107x.schoolapp.Singletons.Utils;

public class NoInternetActivity extends AppCompatActivity {

    Button mTryAgain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    public void tryAgain(View view) {

        if(!Utils.isOnLine(NoInternetActivity.this)) {
            return;
        } else {

            CircularProgressBar circularProgressBar = (CircularProgressBar)findViewById(R.id.progress_bar);
            circularProgressBar.setVisibility(View.VISIBLE);
            int animationDuration = 2000; // 2500ms = 2,5s
            circularProgressBar.setProgressWithAnimation(100, animationDuration);

            checkToken();
        }
    }

    private void checkToken() {
        SharedPreferences prefs = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        final String token = prefs.getString(Constants.USER_TOKEN, "");

        if (!token.isEmpty()) {

            startActivity(new Intent(NoInternetActivity.this, ViewPageActivity.class));

        } else {
            Intent mainIntent = new Intent(NoInternetActivity.this, LandingPageActivity.class);
            NoInternetActivity.this.startActivity(mainIntent);
        }

    }

}
