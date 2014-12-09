package edu.stanford.baseline.sharkpulse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import java.io.File;
import java.io.IOException;

public class FormActivity extends Activity {

    private ImageView mImageView;
    private EditText mEmailEditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        mContext = FormActivity.this;
        mImageView = (ImageView) findViewById(R.id.imageView);
        mEmailEditText = (EditText)findViewById(R.id.email_field);
        mController = AppController.getInstance(mContext);
        mRecord = mController.getRecord();
        mLatitude = mRecord.mLatitude;
        mLongitude = mRecord.mLongitude;
        mEmailEditText.setText(Utility.getPlayStoreEmail(mContext));
        mEmailEditText.setKeyListener(null);

        File imgFile = new File(getIntent().getExtras().getString(StartActivity.KEY_IMAGE_PATH));

        if(imgFile.exists()){
            Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(imgBitmap, (int) (imgBitmap.getWidth() * 0.8), (int) (imgBitmap.getHeight() * 0.8), true);
            mImageView.setImageBitmap(resizedBitmap);
        }

        //create new record and set image path
        mImagePath = getIntent().getExtras().getString(StartActivity.KEY_IMAGE_PATH);

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
    }

    public void onClick(View view) {

        if (view.getId() == R.id.button_send) {
            // pack all the info
            mGuessSpecies = ((EditText) findViewById(R.id.species_field))
                    .getText().toString();
            mEmail = ((EditText) findViewById(R.id.email_field)).getText().toString();
            mNotes = ((EditText) findViewById(R.id.notes_field)).getText().toString();

            if (!mController.is_GPS_on && !getIntent().getExtras().getBoolean(StartActivity.KEY_IS_GALLERY)) {
                Intent mapIntent = new Intent(this, MapActivity.class);
                startActivityForResult(mapIntent, ACTION_MAP);
            }
            else if(ExifDataNotFound){
                Intent mapIntent = new Intent(this, MapActivity.class);
                startActivityForResult(mapIntent, ACTION_MAP);
            }
            else{
                mController.setData(mGuessSpecies, mEmail, mNotes, mImagePath, mRecord.mLongitude, mRecord.mLatitude);
                mController.sendData();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTION_MAP){
            if(resultCode == RESULT_OK){
                mLatitude = Double.parseDouble(data.getExtras().getString(KEY_LATITUDE));
                mLongitude = Double.parseDouble(data.getExtras().getString(KEY_LONGITUDE));
                mController.setData(mGuessSpecies, mEmail, mNotes, mImagePath, mLongitude, mLatitude);
               mController.sendData();
            }
        }
    }
}