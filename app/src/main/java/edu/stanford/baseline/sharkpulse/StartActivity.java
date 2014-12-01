package edu.stanford.baseline.sharkpulse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

// Ask Brayanne for S5` `q  dxax

public class StartActivity extends Activity {

    public static final int ACTION_GALLERY_SELECTED = 0;
    public static final int ACTION_CAMERA_SELECTED = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final String KEY_IMAGE_PATH = "KEY_IMAGE_PATH";
    public static final String KEY_IS_GALLERY = "KEY_IS_GALLERY";
    public static final String LOG_TAG = StartActivity.class.getSimpleName();
    protected Context mContext;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onOpenCamera();
        setContentView(R.layout.activity_start);
        mContext = getApplicationContext();

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
        boolean isGallery = false;
        if (requestCode == ACTION_GALLERY_SELECTED) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    picturePath = getSelectedImageFromGallery(data, this);
                    isGallery = true;
                    break;
                case Activity.RESULT_CANCELED:

                    break;
                default:
                    break;
            }
        } else if (requestCode == 100) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    //Toast.makeText(this, "Image saved to:\n" +
                       //     data.getData(), Toast.LENGTH_LONG).show();
                    try {
                        picturePath = getRealPathFromURI(mContext, fileUri);

                    }catch (NullPointerException e){
                        if(fileUri == null){
                            Log.v(LOG_TAG, " file Uri is null");
                        }
                        Log.v(LOG_TAG, "caught null");
                        Log.v(LOG_TAG, "picture path: " + picturePath);
                        picturePath = getImageFromCamera();
                        Log.v(LOG_TAG, fileUri.toString());
                        Log.v(LOG_TAG, picturePath);

                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;

                default:
                    break;
            }
        }
        if (picturePath != null) {
            Log.v(LOG_TAG, "Final path: " + picturePath);
            Intent intent = new Intent(this, FormActivity.class);
            intent.putExtra(KEY_IMAGE_PATH, picturePath);
            intent.putExtra(KEY_IS_GALLERY, isGallery);
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
            default:
                break;
        }
    }

    public void onOpenCamera() {
        // launch the intent
        final Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getPackageManager()) != null) {
            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(i, 100);
        }
    }

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

    private Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SharkPulse");

        //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));


        if(!mediaStorageDir.exists()){
            if(! mediaStorageDir.mkdirs()){
                Log.d(LOG_TAG, "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if(type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_" + timeStamp + ".jpg");
        }
        else{
            return null;
        }

        Log.v(LOG_TAG, "Successfully created file!");
        return mediaFile;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        String result;

        cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        Log.v(LOG_TAG, "Result: " + result);
        return result;

    }
}