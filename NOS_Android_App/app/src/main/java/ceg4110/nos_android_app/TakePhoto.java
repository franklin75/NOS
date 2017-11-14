package ceg4110.nos_android_app;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TakePhoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        takePhoto();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            ;
        }
    }
}
