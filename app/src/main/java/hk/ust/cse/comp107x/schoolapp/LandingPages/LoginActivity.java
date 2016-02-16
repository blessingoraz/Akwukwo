package hk.ust.cse.comp107x.schoolapp.LandingPages;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import hk.ust.cse.comp107x.schoolapp.Constants;
import hk.ust.cse.comp107x.schoolapp.R;
import hk.ust.cse.comp107x.schoolapp.Singletons.UserDetails;

public class LoginActivity extends AppCompatActivity {

    EditText mUserLoginEmail, mUserLoginPassword;
    Firebase ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acitivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUserLoginEmail = (EditText) findViewById(R.id.user_email_login);
        mUserLoginPassword = (EditText) findViewById(R.id.user_password_login);
    }

    public void login(View view){
        ref = new Firebase(Constants.FIREBASE_URL_USERS);
        String email = mUserLoginEmail.getText().toString();
        String password = mUserLoginPassword.getText().toString();

        ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Toast.makeText(LoginActivity.this, authData.getProviderData().get("name").toString(), Toast.LENGTH_SHORT).show();
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

    }
}
