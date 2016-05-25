package hk.ust.cse.comp107x.schoolapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.client.Firebase;

import hk.ust.cse.comp107x.schoolapp.Singletons.ColorCodes;
import hk.ust.cse.comp107x.schoolapp.Singletons.Utils;
import hk.ust.cse.comp107x.schoolapp.tool.ImageFilter;

public class UsersAccountActivity extends AppCompatActivity {

    public EditText mFirstName, mLastName, mEmail;
    public Button mEdit;
    Firebase ref;
    Firebase refEmail;
    ImageView mEditEmailIcon;
    ImageView mEditNameIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);


        final SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        sharedPreferences.getString("UserDetails", "");

        mFirstName = (EditText) findViewById(R.id.first_name);

        mEmail = (EditText) findViewById(R.id.email_edit);
        mEdit = (Button) findViewById(R.id.edit_user_details);
        mEditEmailIcon = (ImageView) findViewById(R.id.email_icon);
        mEditNameIcon = (ImageView) findViewById(R.id.name_icon);

        ImageFilter.filterImage(mEditEmailIcon, ColorCodes.GRAY);
        ImageFilter.filterImage(mEditNameIcon, ColorCodes.GRAY);

        mFirstName.setText(sharedPreferences.getString(Constants.USER_NAME, ""));
        mEmail.setText(sharedPreferences.getString(Constants.USER_EMAIL, ""));


        final String id = sharedPreferences.getString(Constants.USER_TOKEN, "");
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ref = new Firebase(Constants.FIREBASE_URL+"/users/"+id);
                ref.child("name").setValue(mFirstName.getText().toString().trim());
                ref.child("email").setValue(mEmail.getText().toString().trim());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.USER_NAME, mFirstName.getText().toString().trim());
                editor.putString(Constants.USER_EMAIL,mEmail.getText().toString().trim());

                editor.commit();

                Utils.showLongMessage("Updated", UsersAccountActivity.this);

                startActivity(new Intent(UsersAccountActivity.this, ViewPageActivity.class));
            }
        });
    }

}
