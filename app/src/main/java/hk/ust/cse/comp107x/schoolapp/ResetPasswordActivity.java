package hk.ust.cse.comp107x.schoolapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import hk.ust.cse.comp107x.schoolapp.LandingPages.LoginActivity;
import hk.ust.cse.comp107x.schoolapp.LandingPages.MainActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText mEmailToReset;
    Firebase ref = new Firebase(Constants.FIREBASE_URL);
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);

        mEmailToReset = (EditText)findViewById(R.id.email_to_reset_password);

    }

    public void resetPassword(View view) {

        email = mEmailToReset.getText().toString().trim();

        ref.resetPassword(email, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {

                AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ResetPasswordActivity.this);
                builder.setTitle("A reset password email has been sent to your mail");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences pref = getSharedPreferences("Email", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();

                        Toast.makeText(ResetPasswordActivity.this, "Email for reset " + email, Toast.LENGTH_LONG).show();
                        editor.putString("email", email);

                        editor.apply();

                        startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));

                    }
                });

                builder.create();
                builder.show();
            }

            @Override
            public void onError(FirebaseError firebaseError) {

                Toast.makeText(ResetPasswordActivity.this, "Error =====> " + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
