package edu.stanford.baseline.sharkpulse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import java.io.File;

public class FormActivity extends Activity {

    private ImageView mImageView;
    private EditText mEditLongitude;
    private EditText mEditLatitude;
    protected Context mContext;
    private String mImagePath;
    private String mEmail;
    private String mNotes;
    private String mGuessSpecies;
    private double mLongitude;
    private double mLatitude;
    private AppController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        mContext = getApplicationContext();
        mImageView = (ImageView) findViewById(R.id.imageView);
        mEditLatitude = (EditText) findViewById(R.id.latitude_field);
        mEditLongitude = (EditText) findViewById(R.id.longitude_field);
        mController = AppController.getInstance(mContext);

        if (!getIntent().getExtras().getBoolean(StartActivity.KEY_IS_GALLERY)) {
            mEditLatitude.setText(Double.toString(getIntent().getExtras().getDouble(StartActivity.KEY_LATITUDE)));
            mEditLongitude.setText(Double.toString(getIntent().getExtras().getDouble(StartActivity.KEY_LONGITUDE)));
            mEditLatitude.setClickable(false);
            mEditLatitude.setFocusable(false);
            mEditLongitude.setClickable(false);
            mEditLongitude.setFocusable(false);
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

    public void showAlertDialog(String message, String positiveButton, String negativeButton)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
            mGuessSpecies = ((EditText) findViewById(R.id.species_field))
                    .getText().toString();

            mEmail = ((EditText) findViewById(R.id.email_field)).getText().toString();
            mNotes = ((EditText) findViewById(R.id.notes_field)).getText().toString();
            mLongitude = getIntent().getExtras().getDouble(StartActivity.KEY_LONGITUDE);
            mLatitude = getIntent().getExtras().getDouble(StartActivity.KEY_LATITUDE);

            if (!getIntent().getExtras().getBoolean(StartActivity.KEY_IS_GALLERY)) {
                mController.setData(mGuessSpecies, mEmail, mNotes, mImagePath, mLongitude, mLatitude);
            } else
                mController.setData(mGuessSpecies, mEmail, mNotes, mImagePath);
        }
    }
}
