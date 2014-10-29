package edu.stanford.baseline.sharkpulse;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import java.io.Serializable;

/**
 * Created by Emanuel Mazzilli on 9/16/14.
 */
@SuppressWarnings("serial")
public class AppController{

    // open the default email with a preconfigure email to sharkpulse
    private static final int MODE_EMAIL = 0;
    // send the json to the endpoint http://baseline2.stanford.edu/uploadImage.php
    private static final int MODE_POST_BASELINE = 1;
    // send the json to the endpoint http://testshark.herokuapp.com/recoreds/create
    private static final int MODE_POST_TESTSHARK = 2;

    private static final int SEND_MODE = MODE_EMAIL;

    private static final String TESTSHARK_URL = "http://testshark.herokuapp.com/recoreds/create";
    private static final String BASELINE_URL = "http://baseline2.stanford.edu/uploadImage.php";
    private static final String BASELINE_EMAIL_ADDRESS = "sharkbaselines@gmail.com";

    private static AppController sInstance;

    protected LocationManager mLocationManager;
    protected LocationListener mLocationListener;

    private Record mRecord;
    private Context mContext;
    protected boolean alertDialog;

    private AppController(Context context) {
        mContext = context;
        mRecord = new Record();
        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        alertDialog = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    public static AppController getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppController(context);
        }
        return sInstance;
    }

    public Record getRecord(){
        return mRecord;
    }

    void setData(String species, String email, String notes, String imagePath) {
        mRecord.mGuessSpecies = species;
        mRecord.mEmail = email;
        mRecord.mNotes = notes;
        mRecord.mImagePath = "file://" + imagePath;
        mRecord.setCurrentDate();
    }

    void setData(String species, String email, String notes, String imagePath, Double longitude, Double latitude) {
        mRecord.mGuessSpecies = species;
        mRecord.mEmail = email;
        mRecord.mNotes = notes;
        mRecord.mImagePath = "file://" + imagePath;
        mRecord.mLongitude = longitude;
        mRecord.mLatitude = latitude;
        mRecord.setCurrentDate();
    }

    void startGPS() {

        // Define a listener that responds to location updates
        mLocationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                // set the record
                mRecord.setCoordinates(location.getLatitude(), location.getLongitude());

                // unregister the listener
                stopGPS();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // todo check gps unavailable
            }

            @Override
            public void onProviderEnabled(String provider) {
                alertDialog = true;
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,
                mLocationListener);

    }

    protected void stopGPS() {
        mLocationManager.removeUpdates(mLocationListener);
    }


    protected void sendData() {
        alertDialog = true;
        switch (SEND_MODE) {
            case MODE_EMAIL: sendEmail();
                break;
            case MODE_POST_BASELINE: sendBaselinePost();
                break;
            case MODE_POST_TESTSHARK: sendTestsharkPost();
                break;
            default: break;
        }
    }

    protected void sendEmail() {
        //todo send post through email

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",BASELINE_EMAIL_ADDRESS, null));

        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "I saw a shark!!");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Date: " + mRecord.mDate
                                   + "\nLocation: " + mRecord.mLongitude + " , "
                                   + mRecord.mLatitude + "\nGuess Species: "
                                   + mRecord.mGuessSpecies + "\nNotes: "
                                   + mRecord.mNotes) ;

        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mRecord.mImagePath));
        mContext.startActivity(emailIntent);


    }

    protected void sendBaselinePost() {
        //todo send post directly to php script (needs json encoding)
    }

    protected void sendTestsharkPost() {
        //todo send post directly to test site
    }
}
