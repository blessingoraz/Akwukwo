package hk.ust.cse.comp107x.schoolapp.LandingPages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;

import hk.ust.cse.comp107x.schoolapp.Constants;
import hk.ust.cse.comp107x.schoolapp.R;
import hk.ust.cse.comp107x.schoolapp.Singletons.UserDetails;

public class SignUpPageActivity extends AppCompatActivity {

    private TextView mUserName;
    private TextView mUserPassword;
    private TextView mUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);

        mUserEmail = (TextView) findViewById(R.id.user_email);
        mUserName = (TextView) findViewById(R.id.user_name);
        mUserPassword = (TextView) findViewById(R.id.user_password);

    }

    public void signUp(View view) {

        SharedPreferences pref = getSharedPreferences("UserDetails", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        Firebase ref = new Firebase(Constants.FIREBASE_URL);
        final Firebase userRef = ref.child("users");

        String email = mUserEmail.getText().toString();
        String name = mUserName.getText().toString();
        String password = mUserPassword.getText().toString();

        editor.putString("email", email);
        editor.putString("password", password);

        UserDetails details = new UserDetails();
        details.name = name;
        details.email = email;
        details.password = password;

        userRef.push().setValue(details);

//        userRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
//            @Override
//            public void onSuccess(Map<String, Object> result) {
//                System.out.println("Successfully created user account with uid: " + result.get("uid"));
//            }
//
//            @Override
//            public void onError(FirebaseError firebaseError) {
//                // there was an error
//            }
//        });

        startActivity(new Intent(SignUpPageActivity.this, LandingPageActivity.class));
    }
}
