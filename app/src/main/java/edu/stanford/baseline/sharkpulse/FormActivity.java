package edu.stanford.baseline.sharkpulse;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
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

    private Record mRecord;
    private ImageView mImageView;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        mContext = getApplicationContext();
        mImageView = (ImageView) findViewById(R.id.imageView);
        mRecord = new Record();

        File imgFile = new File(getIntent().getExtras().getString(StartActivity.KEY_IMAGE_PATH));

        mRecord.setImage(getIntent().getExtras().getString(StartActivity.KEY_IMAGE_PATH));

        if(imgFile.exists()){
            Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(imgBitmap,(int)(imgBitmap.getWidth()*0.8), (int)(imgBitmap.getHeight()*0.8), true);
            mImageView.setImageBitmap(resizedBitmap);
        }

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
        if(view.getId() == R.id.button_send) {
            // pack all the info
            mRecord.mGuessSpecies = ((EditText) findViewById(R.id.species_field))
                    .getText().toString();
            mRecord.mEmail = ((EditText) findViewById(R.id.email_field)).getText().toString();
            mRecord.mNotes = ((EditText) findViewById(R.id.notes_field)).getText().toString();

            AppController controller = AppController.getInstance(mContext);
            controller.startGPS();
        }
    }
}
