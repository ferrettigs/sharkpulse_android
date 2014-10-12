package edu.stanford.baseline.sharkpulse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class StartActivity extends Activity {

    public static final int ACTION_GALLERY_SELECTED = 0;
    public static final int ACTION_CAMERA_SELECTED = 1;
    public static final String KEY_IMAGE_PATH = "KEY_IMAGE_PATH";
    public static final String KEY_IS_GALLERY = "KEY_IS_GALLERY";
    public static final String KEY_LONGITUDE = "KEY_LONGITUDE";
    public static final String KEY_LATITUDE = "KEY_LATITUDE";
    protected Context mContext;
    protected AppController mController;
    protected Record mRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mContext = getApplicationContext();
        mController = AppController.getInstance(mContext);
        mRecord = mController.getRecord();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    public void showAlertDialog(String message, String positiveButton, String negativeButton) {
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

    public void onOpenGallery() {
        // start activity for results
        // launch the intent
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, ACTION_GALLERY_SELECTED);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String picturePath = null;
        boolean isGallery = false;
        if (requestCode == ACTION_GALLERY_SELECTED) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    //if gallery went ok, get path from image
                    picturePath = getSelectedImageFromGallery(data, this);
                    isGallery = true;
                    break;
                case Activity.RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        } else if (requestCode == ACTION_CAMERA_SELECTED) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    picturePath = getImageFromCamera();
                    break;
                case Activity.RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        }
        if (picturePath != null) {
            Intent intent = new Intent(this, FormActivity.class);
            intent.putExtra(KEY_LATITUDE, mRecord.mLatitude);
            intent.putExtra(KEY_LONGITUDE, mRecord.mLongitude);
            intent.putExtra(KEY_IMAGE_PATH, picturePath);
            intent.putExtra(KEY_IS_GALLERY, isGallery);
            startActivity(intent);
        }
        isGallery = false;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonGallery:
                onOpenGallery();
                break;
            case R.id.buttonTakePicture:
                onOpenCamera();
                break;
            default:
                break;
        }
    }

    public void onOpenCamera() {
        // check the GPS
        mController.startGPS();

        if (!mController.alertDialog)
            showAlertDialog("GPS is not enabled. Do you want to go to settings menu?", "Settings", "Cancel");

        if (mController.alertDialog) {
            // launch the intent
            final Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (i.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(i, ACTION_CAMERA_SELECTED);
            }
        }
    }

    //get path
    public String getSelectedImageFromGallery(Intent data, Context context) {
        final Uri selectedImage = data.getData();
        final String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION};
        final Cursor cursor = context.getContentResolver()
                .query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        final int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        final String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    //get image info
    private String getImageFromCamera() {
        final String[] projection = new String[]{MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.ImageColumns.ORIENTATION};
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, MediaStore.Images.Media.DATE_ADDED, null, "date_added ASC");
        if (cursor != null && cursor.moveToLast()) {
            return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        }
        return null;
    }
}
