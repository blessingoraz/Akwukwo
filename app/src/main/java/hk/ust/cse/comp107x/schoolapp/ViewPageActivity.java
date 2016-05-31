package hk.ust.cse.comp107x.schoolapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

import hk.ust.cse.comp107x.schoolapp.LandingPages.BaseActivity;
import hk.ust.cse.comp107x.schoolapp.LandingPages.LandingPageActivity;
import hk.ust.cse.comp107x.schoolapp.LandingPages.MainActivity;
import hk.ust.cse.comp107x.schoolapp.LandingPages.SignUpPageActivity;
import hk.ust.cse.comp107x.schoolapp.Singletons.SchoolDetails;
import hk.ust.cse.comp107x.schoolapp.Singletons.UserDetails;
import hk.ust.cse.comp107x.schoolapp.Singletons.Utils;
import hk.ust.cse.comp107x.schoolapp.tool.UserDetailsManager;

public class ViewPageActivity extends BaseActivity {

    Firebase ref;
    private AuthData mAuthData;
    private GoogleApiClient mGoogleApiClient;
    ListView listView;
    ArrayList<UserDetails> mListOfSchools = new ArrayList<>();

    private ProgressDialog mProgress;
    ArrayList<UserDetails> modifiedContent;

    public Button mGetMoreSchools;

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.all_schools:
                return true;

            case R.id.my_schools:
                startActivity(new Intent(ViewPageActivity.this, ListOfMySchhols.class));
                return true;

            case R.id.register_school:
                startActivity(new Intent(ViewPageActivity.this, RegistrationActivity.class));
                return true;

            case R.id.my_account:
                startActivity(new Intent(ViewPageActivity.this, UsersAccountActivity.class));
                return true;

            case R.id.logout:

                SharedPreferences prefs = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
                prefs.edit().remove(Constants.USER_TOKEN).commit();

               logout(null);
                startActivity(new Intent(ViewPageActivity.this, LandingPageActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void logout(AuthData authData) {

        Log.i("Checking authDat==>" + authData, "");
        this.mAuthData = authData;

        if (this.mAuthData != null) {
            /* logout of Firebase */
            ref.unauth();
            /* Logout of any of the Frameworks. This step is optional, but ensures the user is not logged into
             * Facebook/Google+ after logging out of Firebase. */
            if (this.mAuthData.getProvider().equals("facebook")) {
                /* Logout from Facebook */
                LoginManager.getInstance().logOut();
            } else if (this.mAuthData.getProvider().equals("google")) {
                /* Logout from Google+ */
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                }
            }
            /* Update authenticated user and show login buttons */
//            setAuthenticatedUser(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        mGoogleApiClient = ((MainActivity)getApplicationContext()).buildApiClient();

        listView = (ListView) findViewById(R.id.get_all_registered_schools);

        mGetMoreSchools = (Button) findViewById(R.id.more);
        mGetMoreSchools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupListview();
            }
        });

        SharedPreferences preferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        Firebase.setAndroidContext(this);

        ref = new Firebase(Constants.FIREBASE_URL);


        mProgress = ProgressDialog.show(ViewPageActivity.this, "", getString(R.string.loading), true, false);

        if(Utils.isOnLine(ViewPageActivity.this)) {


            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (mProgress != null)
                        mProgress.dismiss();

                    for(DataSnapshot schools: dataSnapshot.child("schools").getChildren()) {

                        UserDetails details = schools.getValue(UserDetails.class);

                        mListOfSchools.add(details);

                    }

                    for (UserDetails content : mListOfSchools ) {

                        if (mProgress != null)
                            mProgress.dismiss();

                        //add a progress dialog here
                        setupListview();

                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                    if (mProgress != null)
                        mProgress.dismiss();

                }
            });

        } else {

            Utils.showLongMessage(Constants.CHECK_CONNECTION, ViewPageActivity.this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void setupListview() {

        Collections.reverse(mListOfSchools);

//        int size = 0;
//
//        UserDetailsManager manager = new UserDetailsManager(size+=3);
//
//        for (int i = 0; i < size; i++) {
//
//            if( i < mListOfSchools.size())
//                manager.addDetails(mListOfSchools.get(i));
//        }
//
//
//        modifiedContent = new ArrayList<>();
//
//        if (modifiedContent != null) {
//
//            for (int i = 0; i < manager.schools.length; i++) {
//                if (!manager.schools[i].address.isEmpty())
//                    modifiedContent.add(manager.schools[i]);
//            }
//
//        } else {
//
//            Utils.showLongMessage("No schools registered yet", ViewPageActivity.this);
//        }

        // we had modifiedContent in place of mListOfSchools

        final MySchoolAdapter mySchoolAdapter = new MySchoolAdapter(getApplicationContext(), mListOfSchools);

        listView.setAdapter(mySchoolAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails userDetails = modifiedContent.get(position);

                SharedPreferences pref = getSharedPreferences("SchoolDetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.putString(Constants.SCHOOL_NAME, userDetails.getSchoolName());
                editor.putString(Constants.SCHOOL_ADDRESS, userDetails.getAddress());
                editor.putString(Constants.SCHOOL_MOTTO, userDetails.getMotto());
                editor.putString(Constants.SCHOOL_IMAGE, userDetails.getSchoolImage());
                editor.putString(Constants.SCHOOL_VISION, userDetails.getVision());
                editor.putString(Constants.SCHOOL_FFES, userDetails.getFees());
                editor.putString(Constants.SCHOOL_LEVEL, userDetails.getLevel());
                editor.putString(Constants.SCHOOL_PHONE, userDetails.getPhone());
                editor.putString(Constants.SCHOOL_EMAIL, userDetails.getEmail());
                editor.putString(Constants.SCHOOL_DETAILED_ADDRESS, userDetails.getDetailedAddress());
                editor.putString(Constants.SCHOOL_LATITUDE, userDetails.getLatitude());
                editor.putString(Constants.SCHOOL_LONGITUDE, userDetails.getLongitude());
//                editor.putString(Constants.SCHOOL_ALL_ID, userDetails.schoolId);

                editor.commit();

                startActivity(new Intent(ViewPageActivity.this, DetailOfEachSchool.class));

            }
        });
    }


    public class MySchoolAdapter extends BaseAdapter {

        Context context;
        ArrayList<UserDetails> schools;

        public MySchoolAdapter(Context context, ArrayList<UserDetails> schools){
            this.context = context;
            this.schools = schools;
        }

        @Override
        public int getCount() {
            return schools.size();
        }

        @Override
        public Object getItem(int position) {
            return schools.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserDetails userDetails = schools.get(position);

            View row;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.school_adapter, parent, false);
            } else {
                row = convertView;
            }
            try {

                TextView schoolName = (TextView)row.findViewById(R.id.name_from_firebase);
                TextView schoolVision = (TextView)row.findViewById(R.id.vision);
                ImageView schoolImage = (ImageView)row.findViewById(R.id.image_from_firebase);
                TextView edit = (TextView)row.findViewById(R.id.edit);
                TextView delete = (TextView)row.findViewById(R.id.delete);

                schoolName.setText(userDetails.getSchoolName());
                schoolVision.setText(userDetails.getVision());
                schoolImage.setImageBitmap(decodeBase64(userDetails.getSchoolImage()));
                delete.setText("");
                edit.setText("More details");

            }catch (Exception e) {e.printStackTrace();}


            return row;
        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}
