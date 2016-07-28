package hk.ust.cse.comp107x.schoolapp.LandingPages;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import hk.ust.cse.comp107x.schoolapp.tool.Constants;
import hk.ust.cse.comp107x.schoolapp.R;
<<<<<<< HEAD
import hk.ust.cse.comp107x.schoolapp.ResetPasswordActivity;
=======
import hk.ust.cse.comp107x.schoolapp.Views.ResetPasswordActivity;
>>>>>>> development
import hk.ust.cse.comp107x.schoolapp.Singletons.Utils;
import hk.ust.cse.comp107x.schoolapp.Views.ViewPageActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText mUserLoginEmail, mUserLoginPassword;
    private ProgressDialog mProgress;
    Firebase ref;
    SharedPreferences pref;

    private Boolean exit = false;

<<<<<<< HEAD
    public LoginActivity(EditText mUserLoginEmail, EditText mUserLoginPassword) {
        this.mUserLoginEmail = mUserLoginEmail;
        this.mUserLoginPassword = mUserLoginPassword;
    }

=======
>>>>>>> development
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acitivity);

        Firebase.setAndroidContext(this);

        mUserLoginEmail = (EditText) findViewById(R.id.user_email_login);
        mUserLoginPassword = (EditText) findViewById(R.id.user_password_login);

        mUserLoginPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            Utils.hideSoftKeyboard(LoginActivity.this);
                            return true;
                        default:
                            break;
                    }

                }
                return false;
            }
        });

        pref = getSharedPreferences("Email", Context.MODE_PRIVATE);

        String emailFromSharedPref = pref.getString("email", "");

        mUserLoginEmail.setText(emailFromSharedPref);
    }

    public void login(View view) {

        if (Utils.isOnLine(LoginActivity.this)) {

            mProgress = ProgressDialog.show(LoginActivity.this, "", getString(R.string.loading), true, false);
            ref = new Firebase(Constants.FIREBASE_URL_USERS);
            String email = mUserLoginEmail.getText().toString();
            String password = mUserLoginPassword.getText().toString();

            ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {

                    if (mProgress != null)
                        mProgress.dismiss();

                    startActivity(new Intent(LoginActivity.this, ViewPageActivity.class));
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {

                    if (mProgress != null)
                        mProgress.dismiss();

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

        finish();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void forgotPassword(View view) {

        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }
}
