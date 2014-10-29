package edu.stanford.baseline.sharkpulse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


import java.io.File;
import android.widget.Toast;


public class FormActivity extends Activity {

    private ImageView mImageView;
    protected Context mContext;
    private String mImagePath;
    private String mEmail;
    private String mNotes;
    private String mGuessSpecies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        mContext = getApplicationContext();
        mImageView = (ImageView) findViewById(R.id.imageView);

        File imgFile = new File(getIntent().getExtras().getString(StartActivity.KEY_IMAGE_PATH));

        if(imgFile.exists()){
            Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(imgBitmap,(int)(imgBitmap.getWidth()*0.8), (int)(imgBitmap.getHeight()*0.8), true);
            mImageView.setImageBitmap(resizedBitmap);
        }

        //Store image path
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


            AppController controller = AppController.getInstance(mContext);
            if(!controller.alertDialog)
                showAlertDialog("GPS is not enabled. Do you want to go to settings menu?", "Settings","Cancel");
            else {
                controller.setData(mGuessSpecies, mEmail, mNotes, mImagePath);
                controller.startGPS();

                Toast.makeText(mContext, "Getting GPS coordinates...", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
