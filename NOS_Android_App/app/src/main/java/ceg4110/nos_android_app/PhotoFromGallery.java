package ceg4110.nos_android_app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class PhotoFromGallery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        findFile();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_from_gallery);
    }

    private static final int readReqCode = 42;
    String TAG = "anotherTag";
    ImageView image;

    public void findFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);       //only show files that can be opened
        intent.setType("image/*");                          //we want images, so set for only that type
       // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); //lets you select multiple photos
        startActivityForResult(intent, readReqCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        Uri uri;
        if (requestCode == readReqCode && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());   //displays URI for testing purposes
                displayPhoto(uri);
            }
        }
    }

    public void displayPhoto(Uri uri) {
        Log.i(TAG, "Path:" + uri.getPath());
        Log.i(TAG, "filename: " + uri.getPath().substring(uri.getPath().lastIndexOf('/')));
        image = findViewById(R.id.imageView2);
        image.setImageURI(uri);

    }

    public void onClickAssess(View view) {
        //do the stuff to assess the image for food
    }
    }
