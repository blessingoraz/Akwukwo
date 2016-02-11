package hk.ust.cse.comp107x.schoolapp.LandingPages;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

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


        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot userSnapShot: snapshot.getChildren()) {
                    System.out.println("++++++++++++++++++"+userSnapShot.getKey());

                    UserDetails details = userSnapShot.getValue(UserDetails.class);
//
                    System.out.println("==================" + details.name);
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }
}
