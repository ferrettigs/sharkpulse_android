package edu.stanford.baseline.sharkpulse;


import android.content.Context;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;


import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.client.methods.HttpPost;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

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

    private static final int SEND_MODE = MODE_POST_TESTSHARK;

    private static final String TESTSHARK_URL = "http://testshark.herokuapp.com/recoreds/create";
    private static final String BASELINE_URL = "http://baseline2.stanford.edu/uploadImage.php";
    private static final String BASELINE_EMAIL_ADDRESS = "sharkbaselines@gmail.com";
    private static final String PHOTOGRAPH = "PHOTOGRAPH";
    private static final String LATITUDE = "LATITUDE";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String EMAIL = "EMAIL";
    private static final String NOTES = "NOTES";
    private static final String DATE = "DATE";
    private static final String TIME = "TIME";
    private static final String SPECIES = "SPECIES";
    private static final String LOG_TAG = AppController.class.getSimpleName();

    private static AppController sInstance;



    protected LocationManager mLocationManager;
    protected LocationListener mLocationListener;

    private Record mRecord;
    private Context mContext;
    protected boolean alertDialog;

    private SimpleDateFormat localDateFormat;

    private AppController(Context context) {
        // get application context
        mContext = context;
        // create record
        mRecord = new Record();
        // format date to extract time
        localDateFormat = new SimpleDateFormat("HH:mm:ss");
        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        // check if location tracking is currently off
        alertDialog = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    public static AppController getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppController(context);
        }
        return sInstance;
    }

    // todo change ambiguous name
    void setData(String species, String email, String notes, String imagePath) {
        // Set instance variables of record
        mRecord.mGuessSpecies = species;
        mRecord.mEmail = email;
        mRecord.mNotes = notes;
        mRecord.mImagePath = "file://" + imagePath;
        mRecord.setCurrentDate();
        mRecord.mTime = localDateFormat.format(mRecord.mDate);
    }

    void startGPS() {

        // Define a listener that responds to location updates
        mLocationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                // set the record
                mRecord.setCoordinates(location.getLatitude(), location.getLongitude());
                //once we have everything for the record, send data
                sendData();
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

        // create new email intent and construct email address
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",BASELINE_EMAIL_ADDRESS, null));

        // since we're not inheriting from activity class, flag intent
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // place subject line and body with all record information
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "I saw a shark!!");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Date: " + mRecord.mDate
                                   + "\nLocation: " + mRecord.mLongitude + " , "
                                   + mRecord.mLatitude + "\nGuess Species: "
                                   + mRecord.mGuessSpecies + "\nNotes: "
                                   + mRecord.mNotes) ;

        // attach picture to email
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mRecord.mImagePath));
        // start activity from context of application
        mContext.startActivity(emailIntent);
    }

    protected void sendBaselinePost() {
        //todo send post directly to php script (needs json encoding)
        postFile(mRecord, BASELINE_URL);
    }

    protected void sendTestsharkPost() {
        //todo send post directly to test site
        Log.v(LOG_TAG, "sendTestsharkPost");
        postFile(mRecord, "http://192.168.1.82/~edsan/SharkPulse/website/baseline2/androidPost.php");

    }

    private static void postFile(final Record record, final String url){
        // create new file object from path
        Log.v(LOG_TAG, record.mImagePath);
        Log.v(LOG_TAG, url);
        final File file = new File(record.mImagePath);
        final ResponseHandler<String> handler = new BasicResponseHandler();

        // create thread to POST
        final Thread thread =  new Thread() {

            public void run(){
                // create client and connection timeout
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);

                try{
                    // generate post request object
                    HttpPost post = new HttpPost(url);
                    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                    multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                    if(file != null){
                        multipartEntityBuilder.addBinaryBody(PHOTOGRAPH, file);
                    }

                    Log.v(LOG_TAG, String.valueOf(record.mDate));
                    Log.v(LOG_TAG, record.mTime);
                    Log.v(LOG_TAG, String.valueOf(record.mLatitude));
                    Log.v(LOG_TAG, String.valueOf(record.mLongitude));
                    Log.v(LOG_TAG, record.mEmail);
                    Log.v(LOG_TAG, record.mGuessSpecies);
                    Log.v(LOG_TAG, record.mNotes);

                    // place record in json
                    multipartEntityBuilder.addTextBody(DATE, String.valueOf(record.mDate));
                    multipartEntityBuilder.addTextBody(TIME, record.mTime);
                    multipartEntityBuilder.addTextBody(LATITUDE, String.valueOf(record.mLatitude));
                    multipartEntityBuilder.addTextBody(LONGITUDE, String.valueOf(record.mLongitude));
                    multipartEntityBuilder.addTextBody(EMAIL, record.mEmail);
                    multipartEntityBuilder.addTextBody(SPECIES, record.mGuessSpecies);
                    multipartEntityBuilder.addTextBody(NOTES, record.mNotes);

                    HttpEntity entity = multipartEntityBuilder.build();
                    post.setEntity(entity);

                    HttpResponse response = client.execute(post);
                    String body  = handler.handleResponse(response);
                    Log.v(LOG_TAG, body);


                } catch (ClientProtocolException e){
                    Log.v(LOG_TAG, "Fatal protocol exception: " + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e){
                    Log.v(LOG_TAG, "Fatal transport error: " + e.getMessage());
                }
            }

        };
        thread.start();
    }
}