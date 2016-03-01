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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import hk.ust.cse.comp107x.schoolapp.LandingPages.SignUpPageActivity;
import hk.ust.cse.comp107x.schoolapp.Singletons.SchoolDetails;
import hk.ust.cse.comp107x.schoolapp.Singletons.UserDetails;
import hk.ust.cse.comp107x.schoolapp.Singletons.Utils;

public class ViewPageActivity extends AppCompatActivity {

    Firebase ref;
    ListView listView;
    ArrayList<UserDetails> mListOfSchools = new ArrayList<>();
    private TextView mNextPage;
    private ProgressDialog mProgress;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.all_schools:
                return true;

            case R.id.my_schools:
                startActivity(new Intent(ViewPageActivity.this, ListOfMySchhols.class));
                return true;

            case R.id.my_account:
                startActivity(new Intent(ViewPageActivity.this, UsersAccountActivity.class));
                return true;

            case R.id.logout:
//                startActivity(new Intent(ViewPageActivity.this));
                Utils.showLongMessage("I am logout", ViewPageActivity.this);

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.get_all_registered_schools);
        mNextPage = (TextView) findViewById(R.id.whats_next);

        SharedPreferences preferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        Firebase.setAndroidContext(this);

        ref = new Firebase(Constants.FIREBASE_URL);

        mProgress = ProgressDialog.show(ViewPageActivity.this, "", getString(R.string.loading), true, false);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (mProgress != null)
                    mProgress.dismiss();

                for(DataSnapshot schools: dataSnapshot.child("schools").getChildren()) {

                    UserDetails details = schools.getValue(UserDetails.class);

//                    Utils.showLongMessage("+++++> " + details.accessToken, ViewPageActivity.this);
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
    }

    private void setupListview() {

        final MySchoolAdapter mySchoolAdapter = new MySchoolAdapter(getApplicationContext(), mListOfSchools);
        listView.setAdapter(mySchoolAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails userDetails = mListOfSchools.get(position);

                SharedPreferences pref = getSharedPreferences("SchoolDetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.putString(Constants.SCHOOL_NAME, userDetails.schoolName);
                editor.putString(Constants.SCHOOL_ADDRESS, userDetails.address);
                editor.putString(Constants.SCHOOL_MOTTO, userDetails.motto);
                editor.putString(Constants.SCHOOL_IMAGE, userDetails.schoolImage);
                editor.putString(Constants.SCHOOL_VISION, userDetails.vision);
                editor.putString(Constants.SCHOOL_FFES, userDetails.fees);
                editor.putString(Constants.SCHOOL_LEVEL, userDetails.level);
                editor.putString(Constants.SCHOOL_PHONE, userDetails.phone);
                editor.putString(Constants.SCHOOL_EMAIL, userDetails.schoolEmail);
                editor.putString(Constants.SCHOOL_DETAILED_ADDRESS, userDetails.detailedAddress);
                editor.putString(Constants.SCHOOL_LATITUDE, userDetails.latitude);
                editor.putString(Constants.SCHOOL_LONGITUDE, userDetails.longitude);

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

            TextView schoolName = (TextView)row.findViewById(R.id.name_from_firebase);
            TextView schoolVision = (TextView)row.findViewById(R.id.vision);
            ImageView schoolImage = (ImageView)row.findViewById(R.id.image_from_firebase);

            schoolName.setText(userDetails.schoolName);
            schoolVision.setText(userDetails.vision);
            schoolImage.setImageBitmap(decodeBase64(userDetails.schoolImage));

            return row;
        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}
