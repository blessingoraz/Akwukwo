package hk.ust.cse.comp107x.schoolapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import hk.ust.cse.comp107x.schoolapp.Views.ViewPageActivity;
import hk.ust.cse.comp107x.schoolapp.tool.Constants;

public class DetailOfEachSchool extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageView mSchoolImage;
    private TextView mSchoolName;
    private TextView mSchoolMotto;
    private TextView mSchoolAddress;
    private TextView mSchoolTelephone;
    private TextView mSchoolFees;
    private TextView mSchoolEmail;

    SharedPreferences pref;

    @Override
    public void onBackPressed() {

        finish();
        Intent intent = new Intent(DetailOfEachSchool.this, ViewPageActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_of_each_school);

//        mSchoolImage = (ImageView) findViewById(R.id.school_image_from_firebase);
//        mSchoolName = (TextView) findViewById(R.id.school_name_from_firebase);
//        mSchoolMotto = (TextView) findViewById(R.id.school_motto_from_firebase);
//        mSchoolAddress = (TextView) findViewById(R.id.school_address_from_firebase);
//        mSchoolTelephone = (TextView) findViewById(R.id.school_phone_number_from_firebase);
//        mSchoolFees = (TextView) findViewById(R.id.school_fees_range_from_firebase);
//        mSchoolEmail = (TextView) findViewById(R.id.school_email_from_firebase);
//
//        pref = getSharedPreferences("SchoolDetails", android.content.Context.MODE_PRIVATE);
//
//        mSchoolImage.setImageBitmap(decodeBase64(pref.getString(Constants.SCHOOL_IMAGE, "")));
//        mSchoolName.setText(pref.getString(Constants.SCHOOL_NAME, ""));
//        mSchoolMotto.setText(pref.getString(Constants.SCHOOL_MOTTO, ""));
//        mSchoolAddress.setText(pref.getString(Constants.SCHOOL_DETAILED_ADDRESS, "")+ ", " + pref.getString(Constants.SCHOOL_ADDRESS, ""));
//        mSchoolTelephone.setText(pref.getString(Constants.SCHOOL_PHONE, ""));
//        mSchoolFees.setText(pref.getString(Constants.SCHOOL_FFES, ""));
//        mSchoolEmail.setText(pref.getString(Constants.SCHOOL_EMAIL, ""));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        // do share pref for long and latit

        pref = getSharedPreferences("SchoolDetails", android.content.Context.MODE_PRIVATE);

        Double latitude = Double.parseDouble(pref.getString(Constants.SCHOOL_LATITUDE, ""));
        Double longitude = Double.parseDouble(pref.getString(Constants.SCHOOL_LONGITUDE, ""));


        LatLng schoolAddress = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(schoolAddress));
        mMap.addMarker(new MarkerOptions().position(schoolAddress).title(pref.getString(Constants.SCHOOL_DETAILED_ADDRESS, "" + ","
                + pref.getString(Constants.SCHOOL_ADDRESS, ""))));
    }
}
