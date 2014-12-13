package edu.stanford.baseline.sharkpulse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
/**
 * Created by Brian De Anda and Daniel Diaz on 12/5/14.
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
    private ImageView image;
    private Intent intent;
    private Bitmap imgBitmap, resizedBitmap;
    private double latitudeDouble;
    private double longitudeDouble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        intent = getIntent();
        mButton = (Button) findViewById(R.id.coordinateButton);
        mContext = ReceiptActivity.this;
        stringInfo = new ArrayList<String>(5);
        stringInfo = intent.getStringArrayListExtra("ArrayRecords");

        latitudeDouble = Double.parseDouble(stringInfo.get(2));
        longitudeDouble = Double.parseDouble(stringInfo.get(3));

        imgBitmap = BitmapFactory.decodeFile(stringInfo.get(5));
        resizedBitmap = Bitmap.createScaledBitmap(imgBitmap, (int) (imgBitmap.getWidth() * 0.1), (int) (imgBitmap.getHeight() * 0.1), true);

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

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.windowlayout, null);

                email = (TextView) v.findViewById(R.id.email_textview);
                species = (TextView) v.findViewById(R.id.species_textview);
                latitude = (TextView) v.findViewById(R.id.latitude_textview);
                longitude = (TextView) v.findViewById(R.id.longitude_textview);
                notes = (TextView) v.findViewById(R.id.notes_textview);
                image = (ImageView) v.findViewById(R.id.imageView);

                email.setText(stringInfo.get(0));

                // edge cases of no info given on form
                if (!stringInfo.get(1).equals("")){
                    species.setText(stringInfo.get(1));
                }
                latitude.setText(stringInfo.get(2));
                longitude.setText(stringInfo.get(3));
                if (!stringInfo.get(4).equals("")) {
                    notes.setText(stringInfo.get(4));
                }
                image.setImageBitmap(resizedBitmap);

                // Returning the view containing InfoWindow contents
                return v;
            }
        });

        mMap.getUiSettings().setScrollGesturesEnabled(false);

        position = mMap.addMarker(new MarkerOptions().position(new LatLng(latitudeDouble, longitudeDouble)));

        position.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position.getPosition(), 7));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.receipt, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_home:
                Intent intent = new Intent(this, StartActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }

        return true;
    }
    @Override
    public void onBackPressed() {
    }
}
