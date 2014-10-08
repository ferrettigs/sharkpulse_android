package edu.stanford.baseline.sharkpulse;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

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

    private static AppController sInstance;

    protected LocationManager mLocationManager;
    protected LocationListener mLocationListener;

    private Record mRecord;
    private Context mContext;

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
    void setData(String species, String email, String notes) {
    }

    public void startGPS() {
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

                if (!mLocationManager.isProviderEnabled(mLocationManager.GPS_PROVIDER)){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                Intent locationsSettings = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                mContext.startActivity(locationsSettings);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        });
                    final AlertDialog alert = builder.create();
                    alert.show();
                }
            }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) { }
        };

        // Register the listener with the Location Manager to receive location updates
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                mLocationListener);
    }

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

    }

    protected void sendBaselinePost() {

    }

    protected void sendTestsharkPost() {

    }
}
