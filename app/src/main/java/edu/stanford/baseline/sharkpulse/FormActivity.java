package edu.stanford.baseline.sharkpulse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;

public class FormActivity extends Activity {

    private ImageView mImageView;
    protected Context mContext;
    private String mImagePath;
    private Double mLatitude;
    private Double mLongitude;
    private String mEmail;
    private String mNotes;
    private String mGuessSpecies;
    private AppController mController;
    protected Record mRecord;
    private boolean ExifDataNotFound = false;
    private static final int ACTION_MAP = 0;
    public static final String KEY_LATITUDE = "KEY_LATITUDE";
    public static final String KEY_LONGITUDE = "KEY_LONGITUDE";
    private static final String LOG_TAG = FormActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        mContext = FormActivity.this;
        mImageView = (ImageView) findViewById(R.id.imageView);
        mController = AppController.getInstance(mContext);
        mRecord = mController.getRecord();
        mLatitude = mRecord.mLatitude;
        mLongitude = mRecord.mLongitude;

        if (!getIntent().getExtras().getBoolean(StartActivity.KEY_IS_GALLERY)) {

            if (!mController.is_GPS_on) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                Utility.showAlertDialog("GPS is not enabled. Do you want to go to settings menu?", "Settings", "Cancel", mContext, intent);

                ////this helps reset the value of is_GPS_on
                mController.startGPS();

                if (mController.is_GPS_on) {
                    mController.startGPS();
                }
            }
            else{
                mController.startGPS();
            }
        }
        else
        {

            ExifInterface exif;
            try {
                exif = new ExifInterface(getIntent().getExtras().getString(StartActivity.KEY_IMAGE_PATH));
                if(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) != null && exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF) != null &&
                   exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) != null && exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF) != null) {
                    mLatitude = Utility.convertToDegree(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE), exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));
                    mLongitude = Utility.convertToDegree(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE), exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));
                }
                else{
                    ExifDataNotFound = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File imgFile = new File(getIntent().getExtras().getString(StartActivity.KEY_IMAGE_PATH));

        if(imgFile.exists()){

                Log.v(LOG_TAG, "imgFiles exists");
                Log.v(LOG_TAG, "Path: " + imgFile.getAbsolutePath());
                Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(imgBitmap, (int) (imgBitmap.getWidth() * 0.8), (int) (imgBitmap.getHeight() * 0.8), true);
                mImageView.setImageBitmap(resizedBitmap);


        }
        else{
            Log.v(LOG_TAG, "img File does not exist");
        }

        //create new record and set image path
        mImagePath = getIntent().getExtras().getString(StartActivity.KEY_IMAGE_PATH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {

        if (view.getId() == R.id.button_send) {
            //mController.startGPS();
            // pack all the info
            if (!mController.is_GPS_on && !getIntent().getExtras().getBoolean(StartActivity.KEY_IS_GALLERY)) {
                Intent mapIntent = new Intent(this, MapActivity.class);
                startActivityForResult(mapIntent, ACTION_MAP);
            }
            else if(ExifDataNotFound){
                Intent mapIntent = new Intent(this, MapActivity.class);
                startActivityForResult(mapIntent, ACTION_MAP);
            }
            mGuessSpecies = ((EditText) findViewById(R.id.species_field))
                    .getText().toString();

            mEmail = ((EditText) findViewById(R.id.email_field)).getText().toString();
            mNotes = ((EditText) findViewById(R.id.notes_field)).getText().toString();

            mController.setData(mGuessSpecies, mEmail, mNotes, mImagePath, mLongitude, mLatitude);
            mController.sendData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTION_MAP){
            if(resultCode == RESULT_OK){
                String latitude = data.getStringExtra(KEY_LATITUDE);
                String longitude = data.getStringExtra(KEY_LONGITUDE);
                Toast.makeText(getApplicationContext(), "Coordinates: " + latitude + " " + longitude, Toast.LENGTH_SHORT).show();
                mLongitude = Double.parseDouble(data.getExtras().getString(KEY_LONGITUDE));
                mLatitude = Double.parseDouble(data.getExtras().getString(KEY_LATITUDE));

            }
        }
    }
}
