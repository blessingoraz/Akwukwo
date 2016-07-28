package hk.ust.cse.comp107x.schoolapp.Views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import hk.ust.cse.comp107x.schoolapp.tool.Constants;
import hk.ust.cse.comp107x.schoolapp.ListOfMySchhols;
import hk.ust.cse.comp107x.schoolapp.R;
import hk.ust.cse.comp107x.schoolapp.Singletons.Utils;

public class RegistrationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private TextView mSchoolAddress;
    private EditText mSchoolName;
    private EditText mSchoolMotto;

    public void completeRegistration(View view) {
        if(Utils.isOnLine(RegistrationActivity.this)) {

            if(Utils.isEmpty(mSchoolName.getText().toString().trim())) {
                mSchoolName.setError("This field is required");
                return;

            }
            else if(Utils.isEmpty(mSchoolAddress.getText().toString().trim())) {
                mSchoolAddress.setError("This field is required");
                return;
            }

            else if(Utils.isEmpty(mSchoolMotto.getText().toString().trim())) {
                mSchoolMotto.setError("This field is required");
                return;
            }

            else if((Utils.isNotEmpty(mSchoolName.getText().toString().trim())) && Utils.isNotEmpty(mSchoolName.getText().toString().trim())
                    && Utils.isNotEmpty(mSchoolName.getText().toString().trim())) {

                SharedPreferences preferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString(Constants.SCHOOL_NAME, mSchoolName.getText().toString().trim());
                editor.putString(Constants.SCHOOL_MOTTO, mSchoolMotto.getText().toString().trim());
                editor.putString(Constants.SCHOOL_ADDRESS, mSchoolAddress.getText().toString().trim());

                editor.commit();

                mSchoolAddress.setText("");
                startActivity(new Intent(RegistrationActivity.this, RegistrationCompleteActivity.class));
            }

        } else {

            Utils.showLongMessage(Constants.CHECK_CONNECTION, RegistrationActivity.this);
        }

    }

    @Override
    public void onBackPressed() {

        finish();
        Intent intent = new Intent(RegistrationActivity.this, ViewPageActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.all_schools:
                return true;

            case R.id.my_schools:
                startActivity(new Intent(RegistrationActivity.this, ListOfMySchhols.class));
                return true;

            case R.id.register_school:
                return true;

            case R.id.my_account:
                startActivity(new Intent(RegistrationActivity.this, UsersAccountActivity.class));
                return true;

            case R.id.logout:
//                startActivity(new Intent(ViewPageActivity.this));
                Utils.showLongMessage("I am logout", RegistrationActivity.this);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        mSchoolAddress = (TextView) findViewById(R.id.load_map_page);

        mSchoolAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, SearchLocationActivity.class));
            }
        });

        SharedPreferences preferences = getSharedPreferences("Location", Context.MODE_PRIVATE);
        mSchoolAddress.setText(preferences.getString("Address", ""));

        mSchoolName = (EditText) findViewById(R.id.school_name);
        mSchoolMotto = (EditText) findViewById(R.id.school_motto);

        SharedPreferences pref = getSharedPreferences("EachSchool", Context.MODE_PRIVATE);
        mSchoolName.setText(pref.getString(Constants.SCHOOL_NAME, ""));
//        mSchoolAddress.setText();
        mSchoolMotto.setText(pref.getString(Constants.SCHOOL_MOTTO, ""));

        mSchoolMotto.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            Utils.hideSoftKeyboard(RegistrationActivity.this);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }


}
