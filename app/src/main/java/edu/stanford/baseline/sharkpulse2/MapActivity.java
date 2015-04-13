package edu.stanford.baseline.sharkpulse2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Brian De Anda and Daniel Diaz on 10/25/14.
 */
public class MapActivity extends FragmentActivity implements GoogleMap.OnMarkerDragListener {
    private GoogleMap mMap = null;
    private Button mButton = null;
    private Marker position = null;
    private static Context mContext = null;
    public static final String KEY_LATITUDE = "KEY_LATITUDE";
    public static final String KEY_LONGITUDE = "KEY_LONGITUDE";

    private final String LOG_TAG = MapActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
        mButton = (Button) findViewById(R.id.coordinateButton);
        mContext = MapActivity.this;
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setOnMarkerDragListener(this);
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        // add pin to map
        position = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Hold and Drag to Move Me!").draggable(true));
        position.showInfoWindow();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.coordinateButton:

                int length = getPosition().toString().length();

                String coordinates = getPosition().toString().substring(10, length - 1);
                String[] latitude_longitude = coordinates.split(",");

               showMapAlertDialog("Are you sure these are the coordinates?", "Confirm", "Cancel", latitude_longitude);

            break;
        }
    }

    private void updatePosition(Marker marker){
        position = marker; // our pin now points to new position
    }

    private LatLng getPosition(){
        return position.getPosition();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(getPosition()), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        updatePosition(marker);
    }

    /*
        show confirmation dialog to confirm coordinates of sighting
     */
    public void showMapAlertDialog(String message, String positiveButton, String negativeButton, final String[] coordinates)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent intent = new Intent();
                        intent.putExtra(KEY_LATITUDE, coordinates[0]);
                        intent.putExtra(KEY_LONGITUDE, coordinates[1]);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
