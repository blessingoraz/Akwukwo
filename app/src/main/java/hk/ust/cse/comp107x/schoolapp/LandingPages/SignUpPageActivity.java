package hk.ust.cse.comp107x.schoolapp.LandingPages;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

import hk.ust.cse.comp107x.schoolapp.Constants;
import hk.ust.cse.comp107x.schoolapp.R;
import hk.ust.cse.comp107x.schoolapp.RegistrationActivity;
import hk.ust.cse.comp107x.schoolapp.Singletons.UserDetails;
import hk.ust.cse.comp107x.schoolapp.ViewPageActivity;

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

        final String email = mUserEmail.getText().toString();
        final String name = mUserName.getText().toString();
        final String password = mUserPassword.getText().toString();

        ref.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {

                UserDetails userDetails = new UserDetails();

                userDetails.name = name;
                userDetails.email = email;
                userDetails.password = password;

                String uid = (String) result.get("uid");

                userRef.child(uid).setValue(userDetails);
                Log.i("USer details", result.toString());
                alertDialog();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(SignUpPageActivity.this, "Username or password already exists", Toast.LENGTH_SHORT).show();
            }
        });


//        editor.putString("email", email);
//        editor.putString("password", password);
//
//        UserDetails details = new UserDetails();
//        details.name = name;
//        details.email = email;
//        details.password = password;
//
//        userRef.push().setValue(details);

    }

//    public
    public void alertDialog() {
        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SignUpPageActivity.this);
        builder.setTitle("Do you want to register a school?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(SignUpPageActivity.this, RegistrationActivity.class));

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(SignUpPageActivity.this, ViewPageActivity.class));

            }
        });


        builder.create();
        builder.show();
    }
}
