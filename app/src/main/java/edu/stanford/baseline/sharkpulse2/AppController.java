package edu.stanford.baseline.sharkpulse2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.client.methods.HttpPost;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Emanuel Mazzilli on 9/16/14.
 */
public class AppController {

    private static final int MODE_EMAIL = 0;
    private static final int MODE_POST_BASELINE = 1;
    private static final int MODE_POST_TESTSHARK = 2;

    private static final int SEND_MODE = MODE_POST_BASELINE;

    private static final String TESTSHARK_URL = "http://testshark.herokuapp.com/recoreds/create";
    private static final String TEST_BASELINE_URL = "http://baseline2.stanford.edu/testdistro/mobileUpload.php";
    private static final String BASELINE_URL = "http://baseline2.stanford.edu/mobileUpload.php";
    private static final String TEST_DEPLOYMENT = "http://54.67.32.82/EmailphpSharkPulse/mobileUpload.php";
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
    public static final String KEY_RECORD = "key_record";

    private static AppController sInstance;

    protected LocationManager mLocationManager;
    protected LocationListener mLocationListener;

    private Record mRecord;
    private Context mContext;
    protected boolean is_GPS_on;

    private SimpleDateFormat localDateFormat;

    private ArrayList<String> stringRecords;

    private AppController(Context context) {
        mContext = context;
        mRecord = new Record();
        localDateFormat = new SimpleDateFormat("HH:mm:ss");
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        // check if location tracking is currently off
        is_GPS_on = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        stringRecords = new ArrayList<String>(5);

    }

    public static AppController getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppController(context);
        }
        return sInstance;
    }

    public Record getRecord() {
        return mRecord;
    }

    void setData(String species, String email, String notes, Bitmap bitmap, Double longitude, Double latitude) {
        mRecord.mGuessSpecies = species;
        mRecord.mEmail = email;
        mRecord.mNotes = notes;
        mRecord.mBitmap = bitmap;
        mRecord.mLongitude = longitude;
        mRecord.mLatitude = latitude;
        mRecord.setCurrentDate();
        mRecord.mTime = localDateFormat.format(mRecord.mDate);
    }

    void setBitmap(Bitmap bitmap) {
        mRecord.mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mRecord.mBitmap;
    }

    void startGPS(Activity activity) {

        // Define a listener that responds to location updates
        mLocationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                mRecord.setCoordinates(location.getLatitude(), location.getLongitude());

                // unregister the listener
                stopGPS();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                is_GPS_on = true;
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                final String [] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(activity, permissions, 10);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    mLocationListener);
        }
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

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",BASELINE_EMAIL_ADDRESS, null));

        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "I saw a shark!!");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Date: " + mRecord.mDate
                                   + "\nLocation: " + mRecord.mLongitude + " , "
                                   + mRecord.mLatitude + "\nGuess Species: "
                                   + mRecord.mGuessSpecies + "\nNotes: "
                                   + mRecord.mNotes) ;

        emailIntent.putExtra(Intent.EXTRA_STREAM, mRecord.mBitmap);
        mContext.startActivity(emailIntent);
    }

    protected void sendBaselinePost() {
       new postSighting().execute(mRecord);
    }

    protected void sendTestsharkPost() {
        new postSighting().execute(mRecord);

    }

    private class postSighting extends AsyncTask<Record, Void, Void>{

        @Override
        protected Void doInBackground(Record... records) {

            int count = records.length;
            for(int i = 0; i < count; i++){
                postFile(records[i]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent(mContext.getApplicationContext(), ReceiptActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putStringArrayListExtra("ArrayRecords", stringRecords);
            mContext.startActivity(intent);
        }
    }

    private static void postFile(final Record record){
        final ResponseHandler<String> handler = new BasicResponseHandler();

        // create thread to POST
        final Thread thread =  new Thread() {

            public void run() {
                // create client and connection timeout
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);

                /**
                 * NOTE: Method to post works. Waiting for production server endpoint before publishing
                 */
                try {

                    // generate post request object
                    HttpPost post = new HttpPost(BASELINE_URL);
                    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                    multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                    Log.v(LOG_TAG, "File exists!");

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    record.mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte [] data = bos.toByteArray();
                    multipartEntityBuilder.addPart(PHOTOGRAPH, new ByteArrayBody(data,"image/jpeg", "test2.jpg"));

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
                    String body = handler.handleResponse(response);
                    Log.v(LOG_TAG, "Works!");
                    Log.v(LOG_TAG, body);


                } catch (ClientProtocolException e) {
                    Log.v(LOG_TAG, "Fatal protocol exception: " + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.v(LOG_TAG, "Fatal transport error: " + e.getMessage());
                }

            }
        };
        thread.start();
    }
}
