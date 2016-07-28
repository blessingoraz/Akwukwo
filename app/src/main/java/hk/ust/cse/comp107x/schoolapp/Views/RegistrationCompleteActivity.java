package hk.ust.cse.comp107x.schoolapp.Views;

import android.app.Activity;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hk.ust.cse.comp107x.schoolapp.tool.Constants;
import hk.ust.cse.comp107x.schoolapp.ListOfMySchhols;
import hk.ust.cse.comp107x.schoolapp.R;
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
    SharedPreferences pref;

    Firebase ref;

    public static final String VALID_PHONE_NUMBER_REGEX = "\\(\\d{3}\\)-\\d{3}-\\d{4}";

    public static boolean validatePhoneNumber(String phoneString) {
        boolean isValid = phoneString.matches(VALID_PHONE_NUMBER_REGEX);
        return isValid;
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_complete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(
                        RegistrationCompleteActivity.this, RegistrationActivity.class));
            }
        });

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
        mChooseFees.add("N10000 - N20000");
        mChooseFees.add("N20000 - N30000");
        mChooseFees.add("N30000 - N40000");
        mChooseFees.add("N40000 - N50000");
        mChooseFees.add("N50000 - N60000");

        mAdapterForFees = new ArrayAdapter<String>(RegistrationCompleteActivity.this,
                android.R.layout.simple_spinner_item, mChooseFees);

        mAdapterForFees.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFeesRange.setAdapter(mAdapterForFees);

        mDetailedAddress.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode){
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            Utils.hideSoftKeyboard(RegistrationCompleteActivity.this);
                            return true;
                        default:
                            break;
                    }

                }
                return false;
            }
        });


        pref = getSharedPreferences("EachSchool", Context.MODE_PRIVATE);

        mSchoolImage.setImageBitmap(Utils.decodeBase64(pref.getString(Constants.SCHOOL_IMAGE, "")));
        mSchoolVision.setText(pref.getString(Constants.SCHOOL_VISION, ""));
        mEmail.setText(pref.getString(Constants.SCHOOL_EMAIL, ""));
        mPhoneNumber.setText(pref.getString(Constants.SCHOOL_PHONE, ""));
        mDetailedAddress.setText(pref.getString(Constants.SCHOOL_ADDRESS, ""));

    }

    public void registerSchoolBtn(View view) {

        if(Utils.isOnLine(RegistrationCompleteActivity.this)) {

            if(Utils.isEmpty(mSchoolVision.getText().toString().trim())) {

                mSchoolVision.setError("This field is required");
                return;
            }

//            else if(mPhoneNumber.getText().toString().trim().equals("") || validatePhoneNumber(mPhoneNumber.getText().toString().trim()) == false) {
//
//                mPhoneNumber.setError("Phone number is either invalid or not provided");
//                return;
//            }

            else if(Utils.isEmpty(mEmail.getText().toString().trim()) || validate(mEmail.getText().toString().trim()) == false) {

                mEmail.setError("Email is either invalid or not provided");

            }

            else if(Utils.isEmpty(mDetailedAddress.getText().toString().trim())) {

                mDetailedAddress.setError("This field is required");
                return;
            }

            else if(mSchoolImage.getDrawable() == null) {

                Utils.showShortToast(
                        "select an image of the school", RegistrationCompleteActivity.this);
            }

            else if(mFeesRange.getSelectedItem().toString().trim().equals(
                    "-- select fees range --")) {
                Utils.showShortToast("select fees range", RegistrationCompleteActivity.this);
                return;
            }

            else if (Utils.isNotEmpty(mSchoolVision.getText().toString().trim()) && Utils.isNotEmpty(mPhoneNumber.getText().toString().trim()) && Utils.isNotEmpty(mDetailedAddress.getText().toString().trim()) && Utils.isNotEmpty(mEmail.getText().toString().trim())) {

                SharedPreferences preferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
                SharedPreferences preferences1 = getSharedPreferences("Location", Context.MODE_PRIVATE);

                int selectedLevel = mSchoolLevel.getCheckedRadioButtonId();
                mSelectedLevel = (RadioButton) findViewById(selectedLevel);

                //get date.now
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();


                UserDetails details = new UserDetails();
                details.setSchoolName(preferences.getString(Constants.SCHOOL_NAME, ""));
                details.setAddress(preferences.getString(Constants.SCHOOL_ADDRESS, ""));
                details.setMotto(preferences.getString(Constants.SCHOOL_MOTTO, ""));
                details.setVision(mSchoolVision.getText().toString().trim());
                details.setFees(mFeesRange.getSelectedItem().toString());
                details.setLevel(mSelectedLevel.getText().toString().trim());
                details.setPhone(mPhoneNumber.getText().toString().trim());
                details.setEmail(mEmail.getText().toString().trim());
                details.setDetailedAddress(mDetailedAddress.getText().toString().trim());
                details.setSchoolImage(preferences.getString(Constants.SCHOOL_IMAGE, "")); //TODO Check this out ASAP
                details.setLatitude(preferences1.getString(Constants.SCHOOL_LATITUDE, ""));
                details.setLongitude(preferences1.getString(Constants.SCHOOL_LONGITUDE, ""));
                details.setCurrentDate(dateFormat.format(cal.getTime()));

                String id = preferences.getString(Constants.USER_TOKEN, "");

                ref.child("users").child(id).child("schools").push().setValue(details);
                ref.child("schools").push().setValue(details);


                if(pref.getString(Constants.SCHOOL_ID, "").equals("")) {

                    ref.child("users").child(id).child("schools").push().setValue(details);
                    ref.child("schools").push().setValue(details);

                } else {
                    String schoolId = pref.getString(Constants.SCHOOL_ID, "");
                    ref.child("users").child(id).child("schools").child(schoolId).setValue(details);
                    ref.child("schools").child(schoolId).setValue(details);
                }

                startActivity(new Intent(RegistrationCompleteActivity.this, ListOfMySchhols.class));
            }

        } else {

            Utils.showLongMessage(Constants.CHECK_CONNECTION, RegistrationCompleteActivity.this);
        }
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
    //Compressing image
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

        float maxHeight = 300.0f;
        float maxWidth = 150.0f;
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
