package edu.stanford.baseline.sharkpulse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class StartActivity extends Activity {

    public static final int ACTION_GALLERY_SELECTED = 0;
    public static final int ACTION_CAMERA_SELECTED = 1;

    public static final String KEY_IMAGE_PATH = "KEY_IMAGE_PATH";

    //moved to FormActivity//
    //private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //moved to FormActivity//
        //mImageView = (ImageView) findViewById(R.id.imageView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
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

    public void onOpenGallery() {
        // start activity for results

        // launch the intent
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, ACTION_GALLERY_SELECTED);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String picturePath = null;
        if (requestCode == ACTION_GALLERY_SELECTED) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    picturePath = getSelectedImageFromGallery(data, this);
                    break;
                case Activity.RESULT_CANCELED:
                    // re-launching the activity!?
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
                    // re-launching the activity!?
                    break;
                default:
                    break;
            }
        }
        if (picturePath != null) {
            Intent intent = new Intent(this, FormActivity.class);
            intent.putExtra(KEY_IMAGE_PATH, picturePath);
            startActivity(intent);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonGallery:
                onOpenGallery();
                break;
            case R.id.buttonTakePicture:
                onOpenCamera();
                break;
            default: break;
        }
    }

    public void onOpenCamera() {
        // check the GPS

        // launch the intent
        final Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, ACTION_CAMERA_SELECTED);
        }

    }

    public String getSelectedImageFromGallery(Intent data, Context context) {
        final Uri selectedImage = data.getData();
        final String[] filePathColumn = { MediaStore.Images.Media.DATA };
        final Cursor cursor = context.getContentResolver()
                .query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        final int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        final String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    private String getImageFromCamera() {
        final String[] projetion = new String[]{MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.ImageColumns.ORIENTATION};
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projetion, MediaStore.Images.Media.DATE_ADDED, null, "date_added ASC");
        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        }
        return null;
    }
}
