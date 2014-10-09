package edu.stanford.baseline.sharkpulse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by Emanuel Mazzilli on 9/16/14.
 */
public class AppController {

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

    private static final String LOG_TAG = AppController.class.getSimpleName();

    private static AppController sInstance;

    protected LocationManager mLocationManager;
    protected LocationListener mLocationListener;

    private Record mRecord;
    private Context mContext;
    private boolean alertDialog = true;

    private AppController(Context context) {
        mContext = context;
        mRecord = new Record();

        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public static AppController getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppController(context);
        }
        return sInstance;
    }

    // todo change ambiguous name
    void setData(String species, String email, String notes, String imagePath) {
        mRecord.mGuessSpecies = species;
        mRecord.mEmail = email;
        mRecord.mNotes = notes;
        mRecord.mImagePath = imagePath;
    }

    boolean startGPS() {


        // Define a listener that responds to location updates

        mLocationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                // set the record
                Log.v(LOG_TAG, "onLocationChanged");
                mRecord.setCoordinates(location.getLatitude(), location.getLongitude());
                sendData();
                Log.v(LOG_TAG, String.valueOf(mRecord.mLatitude));
                // unregister the listener
                stopGPS();


            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // todo check gps unavailable
                Log.v(LOG_TAG, "onStatusChanged");


            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.v(LOG_TAG, "onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                alertDialog = false;
                Log.v(LOG_TAG, "onProviderDisabled");
                Log.v(LOG_TAG, Boolean.toString(alertDialog));
            }
        };

        // Register the listener with the Location Manager to receive location updates

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,
                mLocationListener);

        return alertDialog;
    }
////////////left here///////
    public void showAlertDialog(String message, Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Dialog dialog =builder.create();
        dialog.show();
    }
//////////////////////////////////////
    protected void stopGPS() {
        mLocationManager.removeUpdates(mLocationListener);
    }


    protected void sendData() {
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

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, Double.toString(mRecord.mLatitude));
        emailIntent.putExtra(Intent.EXTRA_TEXT, Double.toString(mRecord.mLongitude));


        mContext.startActivity(emailIntent);


    }

    protected void sendBaselinePost() {
        //todo send post directly to php script (needs json encoding)
    }

    protected void sendTestsharkPost() {
        //todo send post directly to test site
    }
}