package edu.stanford.baseline.sharkpulse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Algernon on 12/5/14.
 */
public class ReceiptActivity extends FragmentActivity {
    private GoogleMap mMap = null;
    private Button mButton = null;
    private Marker position = null;
    private static Context mContext = null;
    public static final String KEY_LATITUDE = "KEY_LATITUDE";
    public static final String KEY_LONGITUDE = "KEY_LONGITUDE";
    private static final String LOG_TAG = ReceiptActivity.class.getSimpleName();
    private ArrayList<String> stringInfo;
    private TextView email;
    private TextView species;
    private TextView latitude;
    private TextView longitude;
    private TextView notes;
    private Intent intent;
    private double latitudeDouble;
    private double longitudeDouble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        intent = getIntent();
        mButton = (Button) findViewById(R.id.coordinateButton);
        mContext = ReceiptActivity.this;
        Log.v(LOG_TAG, "On Receipt Activity");
        stringInfo = new ArrayList<String>(5);
        stringInfo = intent.getStringArrayListExtra("ArrayRecords");
        email = (TextView)findViewById(R.id.email_textview);
        species = (TextView)findViewById(R.id.species_textview);
        latitude = (TextView)findViewById(R.id.latitude_textview);
        longitude = (TextView)findViewById(R.id.longitude_textview);
        notes = (TextView)findViewById(R.id.notes_textview);
        email.setText(stringInfo.get(0));
        species.setText(stringInfo.get(1));
        latitude.setText(stringInfo.get(2));
        longitude.setText(stringInfo.get(3));
        notes.setText(stringInfo.get(4));
        latitudeDouble = Double.parseDouble(stringInfo.get(2));
        longitudeDouble = Double.parseDouble(stringInfo.get(3));
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.receipt_map))
                    .getMap();

            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        // add pin to map
        mMap.getUiSettings().setScrollGesturesEnabled(false);

        position = mMap.addMarker(new MarkerOptions().position(new LatLng(latitudeDouble, longitudeDouble)).title("Hold and Drag to Move Me!"));
        position.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position.getPosition(), 7));
    }
}
