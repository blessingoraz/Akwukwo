package hk.ust.cse.comp107x.schoolapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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

import hk.ust.cse.comp107x.schoolapp.Singletons.UserDetails;
import hk.ust.cse.comp107x.schoolapp.Singletons.Utils;

public class ListOfMySchhols extends AppCompatActivity {

    private ProgressDialog mProgress;
    Firebase ref;
    ListView listView;
    ArrayList<UserDetails> mListOfSchools = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_my_schhols);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);

        listView = (ListView) findViewById(R.id.my_registered_schools);

        SharedPreferences preferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);

        String id = preferences.getString(Constants.USER_TOKEN, "");

        ref = new Firebase(Constants.FIREBASE_URL_USERS+"/"+id);

        mProgress = ProgressDialog.show(ListOfMySchhols.this, "", getString(R.string.loading), true, false);

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

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                UserDetails userDetails = mListOfSchools.get(position);
//            }
//        });
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

            return row;
        }
    }

}
