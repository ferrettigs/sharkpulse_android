package edu.stanford.baseline.sharkpulse2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StartActivity extends Activity {

    public static final int ACTION_GALLERY_SELECTED = 0;
    public static final int ACTION_CAMERA_SELECTED = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final String KEY_BITMAP = "KEY_BITMAP";
    public static final String KEY_IS_GALLERY = "KEY_IS_GALLERY";
    private static boolean openCamera = true;
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(openCamera) {
            onOpenCamera();
        }
        setContentView(R.layout.activity_start);
        mContext = getApplicationContext();
    }
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            openCamera = false;
        }
    }
    public void onOpenGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, ACTION_GALLERY_SELECTED);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean isGallery = false;
        Bitmap bitmap = null;
        if (requestCode == ACTION_GALLERY_SELECTED) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        isGallery = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        } else if (requestCode == 100) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    bitmap = data.getParcelableExtra("data");
                    break;
                case Activity.RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        }
        if (bitmap != null) {
            Intent intent = new Intent(this, FormActivity.class);
            AppController.getInstance(mContext).setBitmap(getCompressedBitmap(bitmap));
//            intent.putExtra(KEY_BITMAP, getCompressedBitmap(bitmap));
            intent.putExtra(KEY_IS_GALLERY, isGallery);
            startActivity(intent);
        }
    }

    private Bitmap getCompressedBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(stream.toByteArray()));
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
        final Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, 100);
        }
    }
}