package edu.stanford.baseline.sharkpulse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class FormActivity extends Activity {

    private ImageView mImageView;
    private EditText mEditLongitude;
    private EditText mEditLatitude;
    protected Context mContext;
    private String mImagePath;
    private double mLatitude;
    private double mLongitude;
    private String mEmail;
    private String mNotes;
    private String mGuessSpecies;
    private AppController mController;
    protected Record mRecord;
    private static final int ACTION_MAP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        mContext = FormActivity.this;
        mImageView = (ImageView) findViewById(R.id.imageView);
        mEditLatitude = (EditText) findViewById(R.id.latitude_field);
        mEditLongitude = (EditText) findViewById(R.id.longitude_field);
        mController = AppController.getInstance(mContext);
        mRecord = mController.getRecord();
        mLatitude = mRecord.mLatitude;
        mLongitude = mRecord.mLongitude;

        if (!getIntent().getExtras().getBoolean(StartActivity.KEY_IS_GALLERY)) {

            if (!mController.alertDialog) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                Utility.showAlertDialog("GPS is not enabled. Do you want to go to settings menu?", "Settings", "Cancel", mContext, intent);

                if (mController.alertDialog) {
                    mController.startGPS();
                    mEditLatitude.setClickable(false);
                    mEditLatitude.setFocusable(false);
                    mEditLongitude.setClickable(false);
                    mEditLongitude.setFocusable(false);
                    mEditLatitude.setText(Double.toString(mRecord.mLatitude));
                    mEditLongitude.setText(Double.toString(mRecord.mLongitude));
                }
            }
            else{
                mController.startGPS();
                mEditLatitude.setClickable(false);
                mEditLatitude.setFocusable(false);
                mEditLongitude.setClickable(false);
                mEditLongitude.setFocusable(false);
                mEditLatitude.setText(Double.toString(mRecord.mLatitude));
                mEditLongitude.setText(Double.toString(mRecord.mLongitude));
            }
        }

        File imgFile = new File(getIntent().getExtras().getString(StartActivity.KEY_IMAGE_PATH));

        if(imgFile.exists()){
            Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(imgBitmap,(int)(imgBitmap.getWidth()*0.8), (int)(imgBitmap.getHeight()*0.8), true);
            mImageView.setImageBitmap(resizedBitmap);
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
            // pack all the info
            if (!mController.alertDialog) {


                /////////////////////////////
                //////launch google maps/////
                //Toast.makeText(getApplicationContext(), "this should pop up the map", Toast.LENGTH_SHORT).show();

                Intent mapIntent = new Intent(this, MapActivity.class);
                startActivityForResult(mapIntent, ACTION_MAP);
                /////////////////////////////

                mEditLatitude.setClickable(false);
                mEditLatitude.setFocusable(false);
                mEditLongitude.setClickable(false);
                mEditLongitude.setFocusable(false);
                ///mEditLatitude.setText(///latitude from google maps///);
                ///mEditLongitude.setText(///longitude from google maps///);


            }
            mGuessSpecies = ((EditText) findViewById(R.id.species_field))
                    .getText().toString();

            mEmail = ((EditText) findViewById(R.id.email_field)).getText().toString();
            mNotes = ((EditText) findViewById(R.id.notes_field)).getText().toString();

            if (!getIntent().getExtras().getBoolean(StartActivity.KEY_IS_GALLERY)) {
                mController.setData(mGuessSpecies, mEmail, mNotes, mImagePath, mRecord.mLongitude, mRecord.mLatitude);
            } else
                mController.setData(mGuessSpecies, mEmail, mNotes, mImagePath);

            AppController controller = AppController.getInstance(mContext);
            if(!controller.alertDialog) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                Utility.showAlertDialog("GPS is not enabled. Do you want to go to settings menu", "Settings", "Cancel", mContext, intent);
                //showAlertDialog("GPS is not enabled. Do you want to go to settings menu?", "Settings","Cancel");
            }
            else {
                controller.setData(mGuessSpecies, mEmail, mNotes, mImagePath);
                controller.startGPS();

                Toast.makeText(mContext, "Getting GPS coordinates...", Toast.LENGTH_SHORT).show();
            }
            mController.sendData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTION_MAP){
            if(resultCode == RESULT_OK){
                String coordinates = data.getStringExtra("Coordinates");
                Toast.makeText(getApplicationContext(), "Coordinates: " + coordinates, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
