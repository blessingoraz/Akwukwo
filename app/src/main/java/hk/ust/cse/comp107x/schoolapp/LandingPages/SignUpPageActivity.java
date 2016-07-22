package hk.ust.cse.comp107x.schoolapp.LandingPages;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

import hk.ust.cse.comp107x.schoolapp.Constants;
import hk.ust.cse.comp107x.schoolapp.R;
import hk.ust.cse.comp107x.schoolapp.RegistrationActivity;
import hk.ust.cse.comp107x.schoolapp.Singletons.UserDetails;
import hk.ust.cse.comp107x.schoolapp.Singletons.Utils;
import hk.ust.cse.comp107x.schoolapp.ViewPageActivity;

public class SignUpPageActivity extends AppCompatActivity {

    private TextView mUserName;
    private TextView mUserPassword;
    private TextView mUserEmail;
    Firebase ref = new Firebase(Constants.FIREBASE_URL);
    final Firebase userRef = ref.child("users");

    private ProgressDialog mProgress;

    @Override
    public void onBackPressed() {

        finish();
        Intent intent = new Intent(SignUpPageActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        Firebase.setAndroidContext(this);

        mUserEmail = (TextView) findViewById(R.id.user_email);
        mUserName = (TextView) findViewById(R.id.user_name);
        mUserPassword = (TextView) findViewById(R.id.user_password);

        mUserPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode){
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            Utils.hideSoftKeyboard(SignUpPageActivity.this);
                            return true;
                        default:
                            break;
                    }

                }
                return false;
            }
        });
    }

    public void signUp(View view) {

        if(Utils.isOnLine(SignUpPageActivity.this)) {

            SharedPreferences pref = getSharedPreferences("UserDetails", MODE_PRIVATE);

            final SharedPreferences.Editor editor = pref.edit();

            final String email = mUserEmail.getText().toString().trim();
            final String name = mUserName.getText().toString().trim();
            final String password = mUserPassword.getText().toString().trim();

            if(Utils.isEmpty(email)) {
                mUserEmail.setError("This field is required");
            }

            else if(Utils.isEmpty(name)) {
                mUserName.setError("This field is required");
            }
            else if(Utils.isEmpty(password)) {
                mUserPassword.setError("This field is required");
            }

            else if(!isPasswordValid(password)) {
                mUserPassword.setError( "Password is too short" );
                mUserPassword.setHint("input password");
            }

            // Remove this
            
            else if(Utils.isNotEmpty(email) && Utils.isNotEmpty(name) && Utils.isNotEmpty(password)) {

                mProgress = ProgressDialog.show(
                        SignUpPageActivity.this, "", getString(R.string.loading), true, false);
                ref.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {

                        if (mProgress != null)
                            mProgress.dismiss();

                        UserDetails userDetails = new UserDetails();
                        userDetails.setName(name);
                        userDetails.setEmail(email);
                        userDetails.setPassword(password);
                        userDetails.setAccessToken((String) result.get("uid"));

                        String uid = (String) result.get("uid");

                        editor.putString(Constants.USER_TOKEN, uid);
                        editor.putString(Constants.USER_NAME, name);
                        editor.putString(Constants.USER_EMAIL, email);

                        editor.apply();

                        compareEmail(userDetails, userDetails.email, uid);

                        alertDialog();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {

                        if (mProgress != null)
                            mProgress.dismiss();

                        Toast.makeText(SignUpPageActivity.this, "Email already exists or incorrect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Utils.showLongMessage(Constants.CHECK_CONNECTION, SignUpPageActivity.this);
        }
    }

    public void compareEmail(final UserDetails userDetails, final String emailFromUserDetail, final String userdetailUid) {

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot users : dataSnapshot.getChildren()) {

                    String email = (String) users.child("users").child("email").getValue();

                    if (emailFromUserDetail.trim().equalsIgnoreCase(email)) {

                        Utils.showShortToast("Email already exist!", SignUpPageActivity.this);
                    } else {
                        userRef.child(userdetailUid).setValue(userDetails);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private boolean isPasswordValid(String password) {
        return password.length() > 3;
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

    public void loginPage(View view) {
        startActivity(new Intent(SignUpPageActivity.this, LoginActivity.class));
    }

}
