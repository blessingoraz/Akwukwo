package hk.ust.cse.comp107x.schoolapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hk.ust.cse.comp107x.schoolapp.Singletons.UserDetails;
import hk.ust.cse.comp107x.schoolapp.Singletons.Utils;

public class RegistrationCompleteActivity extends AppCompatActivity {

    private Spinner mFeesRange;
    private List<String> mChooseFees;
    private ArrayAdapter<String> mAdapterForFees;
    private ImageView mSchoolImage;
    private EditText mSchoolVision;
    private RadioGroup mSchoolLevel;
    private RadioButton mSelectedLevel;
    private EditText mPhoneNumber;
    private EditText mEmail;
    private EditText mDetailedAddress;
    String fileName;

    Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_complete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);
        ref = new Firebase(Constants.FIREBASE_URL);

        mSchoolVision = (EditText) findViewById(R.id.school_vision);
        mSchoolLevel = (RadioGroup) findViewById(R.id.school_level);
        mPhoneNumber = (EditText) findViewById(R.id.school_phone_number);
        mEmail = (EditText) findViewById(R.id.school_email);
        mDetailedAddress = (EditText) findViewById(R.id.school_detailed_address);

        mSchoolImage = (ImageView) findViewById(R.id.school_image);

        mFeesRange = (Spinner) findViewById(R.id.spinner_for_fees);

        mChooseFees = new ArrayList<String>();

        mChooseFees.add("-- select fees range --");
        mChooseFees.add("N5000 - N10000");
        mChooseFees.add("N5000 - N10000");
        mChooseFees.add("N5000 - N10000");
        mChooseFees.add("N5000 - N10000");
        mChooseFees.add("N5000 - N10000");

        mAdapterForFees = new ArrayAdapter<String>(RegistrationCompleteActivity.this,
                android.R.layout.simple_spinner_item, mChooseFees);

        mAdapterForFees.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mFeesRange.setAdapter(mAdapterForFees);
    }

    public void registerSchool(View view) {

        String vision = mSchoolVision.getText().toString();

        int selectedLevel = mSchoolLevel.getCheckedRadioButtonId();
        mSelectedLevel = (RadioButton) findViewById(selectedLevel);
        String level = mSelectedLevel.getText().toString();

        String fees = mFeesRange.getSelectedItem().toString();
        String phonenumber = mPhoneNumber.getText().toString();
        String email = mEmail.getText().toString();
        String detailedAddress = mDetailedAddress.getText().toString();

        SharedPreferences preferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences preferences1 = getSharedPreferences("Locations", Context.MODE_PRIVATE);

        String name = preferences.getString(Constants.SCHOOL_NAME, "");
        String address = preferences.getString(Constants.SCHOOL_ADDRESS, "");
        String motto = preferences.getString(Constants.SCHOOL_MOTTO, "");
        String id = preferences.getString(Constants.USER_TOKEN, "");
        String image = preferences.getString(Constants.SCHOOL_IMAGE, "");

        UserDetails details = new UserDetails();

        details.schoolName = name;
        details.address = address;
        details.motto = motto;
        details.vision = vision;
        details.fees = fees;
        details.level = level;
        details.phone = phonenumber;
        details.schoolEmail = email;
        details.schoolImage = image;
        details.detailedAddress = detailedAddress;
        details.latitude = preferences1.getString("latitude", "");
        details.longitude = preferences1.getString("longitude", "");


        ref.child("users").child(id).child("schools").push().setValue(details);
        ref.child("schools").push().setValue(details);

        startActivity(new Intent(RegistrationCompleteActivity.this, ListOfMySchhols.class));


    }

    Bitmap getImageFromFile(String fileName) {

        File file = new File(fileName);
        FileInputStream inputStream = null;
        Bitmap bitmap = null;


        try {

            inputStream = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(inputStream);

            return bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return null;
    }
    public void selectImage(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0)
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();

                // TODO Do something with the select image URI
                  fileName = compressImage(selectedImage);

                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                    Bitmap bitmap1 = getImageFromFile(fileName);
                    ImageCoverter coverter = new ImageCoverter(RegistrationCompleteActivity.this);
                    coverter.execute(bitmap1);
                    mSchoolImage.setImageBitmap(bitmap1);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    public String compressImage(Uri imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {

            if (imgRatio < maxRatio) {

                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;

            } else if (imgRatio > maxRatio) {

                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;

            } else {

                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(Uri uri) {

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }


    public String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString (byteArray, Base64.DEFAULT);
    }

    private class ImageCoverter extends AsyncTask<Bitmap, Void, String> {
        Context context;
      //  ProgressDialog dialog;

        public ImageCoverter(Context context) {

            this.context = context;
          //  dialog = new ProgressDialog(context);
        }


        @Override
        protected void onPreExecute() {
           // dialog.setMessage("Processing Image...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            return bitmapToBase64(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            SharedPreferences pref = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(Constants.SCHOOL_IMAGE, s);

            editor.commit();

        }
    }
}
