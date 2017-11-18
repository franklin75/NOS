package ceg4110.nos_android_app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class PhotoFromGallery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        findFile();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_from_gallery);
    }

    private static final int readReqCode = 42;
    String TAG = "anotherTag";

    public void findFile() {
       // Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); //lets you select multiple photos
        startActivityForResult(intent, readReqCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        Uri uri;
        if (requestCode == readReqCode && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
            }
        }

    }
}
