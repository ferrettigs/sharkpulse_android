package edu.stanford.baseline.sharkpulse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class FormActivity extends Activity {

    private Record mRecord;
    protected Context mContext;
    private static final String LOG_TAG = FormActivity.class.getSimpleName();
    private static final String BASELINE_EMAIL_ADDRESS = "sharkbaselines@gmail.com";
    private String mImagePath;
    private String mEmail;
    private String mNotes;
    private String mGuessSpecies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        mContext = getApplicationContext();

        //create new record and set image path
        mRecord = new Record();
        mImagePath = getIntent().getExtras().getString(StartActivity.KEY_IMAGE_PATH);
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
            controller.startGPS();
            controller.setData(mGuessSpecies, mEmail, mNotes, mImagePath);
            Toast.makeText(mContext, "Getting GPS coordinates...", Toast.LENGTH_SHORT).show();
        }
    }
}
