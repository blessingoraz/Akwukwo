package hk.ust.cse.comp107x.schoolapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import hk.ust.cse.comp107x.schoolapp.Singletons.UserDetails;
import hk.ust.cse.comp107x.schoolapp.Singletons.Utils;

public class ListOfMySchools extends AppCompatActivity {

    private ProgressDialog mProgress;
    private TextView mDelete;

    String id;
    Firebase ref;
    Firebase schoolRef;

    ListView listView;
    MySchoolAdapter mySchoolAdapter;
    ArrayList<UserDetails> mListOfSchools = new ArrayList<>();

    public boolean delete (int position) {

        if(Utils.isOnLine(ListOfMySchools.this)) {
            //save the item
            UserDetails details = mListOfSchools.get(position);
            //remove the item from the arraylist
            mListOfSchools.remove(position) ;

            //delete from firebase database here .... and then
            //notify dataset changed on done, callbacck
            mySchoolAdapter.notifyDataSetChanged();
            return true;
        } else {

            Utils.showShortToast("Check your internet", ListOfMySchools.this);
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.all_schools:
                startActivity(new Intent(ListOfMySchools.this, ViewPageActivity.class));
                return true;

            case R.id.my_schools:
                return true;

            case R.id.register_school:
                startActivity(new Intent(ListOfMySchools.this, RegistrationActivity.class));
                return true;
            case R.id.my_account:
                startActivity(new Intent(ListOfMySchools.this, UsersAccountActivity.class));
                return true;

            case R.id.logout:
                Utils.showLongMessage("I am logout", ListOfMySchools.this);

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

        finish();
        Intent intent = new Intent(ListOfMySchools.this, ViewPageActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_my_schhols);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListOfMySchools.this, ViewPageActivity.class));
            }
        });

        Firebase.setAndroidContext(this);

        listView = (ListView) findViewById(R.id.my_registered_schools);

        SharedPreferences preferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);

        id = preferences.getString(Constants.USER_TOKEN, "");

        ref = new Firebase(Constants.FIREBASE_URL_USERS+"/"+id);

        mProgress = ProgressDialog.show(ListOfMySchools.this, "", getString(R.string.loading), true, false);

        if(Utils.isOnLine(ListOfMySchools.this)) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (mProgress != null)
                        mProgress.dismiss();

                    for(DataSnapshot schools: dataSnapshot.child("schools").getChildren()) {

                        Utils.showLongMessage(schools.getKey(), ListOfMySchools.this);

                         UserDetails details = schools.getValue(UserDetails.class);
                        details.schoolId = schools.getKey();

                        mListOfSchools.add(details);

                    }

                    for (UserDetails content : mListOfSchools ) {

                        if (mProgress != null)
                            mProgress.dismiss();

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

            Utils.showLongMessage(Constants.CHECK_CONNECTION, ListOfMySchools.this);
        }

    }

    private void setupListview() {

        mySchoolAdapter = new MySchoolAdapter(getApplicationContext(), mListOfSchools);
        listView.setAdapter(mySchoolAdapter);

    }

    public void getSchoolId() {

        schoolRef = new Firebase(Constants.FIREBASE_URL+"schools");
        schoolRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getKey();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final UserDetails userDetails = schools.get(position);

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

            final TextView delete = (TextView)row.findViewById(R.id.delete);
            TextView edit = (TextView)row.findViewById(R.id.edit);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // add a progress dialog
                    SharedPreferences pref = getSharedPreferences("EachSchool", Context.MODE_PRIVATE);
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
                    editor.putString(Constants.SCHOOL_ID, userDetails.schoolId);
                    editor.putString(Constants.SCHOOL_ALL_ID, userDetails.allSchoolId);

                    editor.commit();
                    startActivity(new Intent(ListOfMySchools.this, RegistrationActivity.class));
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final SharedPreferences pref = getSharedPreferences("SchoolDetails", Context.MODE_PRIVATE);

                    AlertDialog.Builder builder = new AlertDialog.Builder(ListOfMySchools.this);
                    builder.setTitle("Are you sure you want to delete this entry?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            delete(position);

                            ref = new Firebase(Constants.FIREBASE_URL_USERS+"/"+id+"/schools/"+userDetails.schoolId);
                            ref.removeValue();

                            String id = pref.getString(Constants.SCHOOL_ALL_ID, "");
                            Firebase schoolRef = new Firebase(Constants.FIREBASE_URL+"/schools");
                            schoolRef.child(id).removeValue();

                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });

                    builder.create();
                    builder.show();
                }
            });

            schoolName.setText(userDetails.schoolName);
            schoolVision.setText(userDetails.vision);
            schoolImage.setImageBitmap(Utils.decodeBase64(userDetails.schoolImage));
            return row;
        }
    }

}
