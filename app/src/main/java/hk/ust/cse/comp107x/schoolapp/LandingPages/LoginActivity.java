package hk.ust.cse.comp107x.schoolapp.LandingPages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import hk.ust.cse.comp107x.schoolapp.Constants;
import hk.ust.cse.comp107x.schoolapp.R;
import hk.ust.cse.comp107x.schoolapp.ResetPasswordActivity;
import hk.ust.cse.comp107x.schoolapp.Singletons.UserDetails;
import hk.ust.cse.comp107x.schoolapp.Singletons.Utils;
import hk.ust.cse.comp107x.schoolapp.ViewPageActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText mUserLoginEmail, mUserLoginPassword;
    Firebase ref;
    SharedPreferences pref;

    private Boolean exit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acitivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUserLoginEmail = (EditText) findViewById(R.id.user_email_login);
        mUserLoginPassword = (EditText) findViewById(R.id.user_password_login);

        pref = getSharedPreferences("Email", Context.MODE_PRIVATE);

        String emailFromSharedPref = pref.getString("email", "");

        mUserLoginEmail.setText(emailFromSharedPref);
    }

    public void login(View view){

        if(Utils.isOnLine(LoginActivity.this)) {

            ref = new Firebase(Constants.FIREBASE_URL_USERS);
            String email = mUserLoginEmail.getText().toString();
            String password = mUserLoginPassword.getText().toString();

            ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    Toast.makeText(LoginActivity.this, ""+authData, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, ViewPageActivity.class));
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {

                    switch (firebaseError.getCode()) {
                        case FirebaseError.USER_DOES_NOT_EXIST:
                            // handle a non existing user
                            Toast.makeText(LoginActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();

                            break;
                        case FirebaseError.INVALID_PASSWORD:
                            // handle an invalid password
                            Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            // handle other errors
                            break;
                    }
                }
            });

            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();

        } else {
            Utils.showLongMessage(Constants.CHECK_CONNECTION, LoginActivity.this);
        }

    }
    @Override
    public void onBackPressed() {
        if(exit) {
            finish();
        } else {
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
        super.onBackPressed();
    }

    public void forgotPassword(View view) {

        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }
}
