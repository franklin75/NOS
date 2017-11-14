package ceg4110.nos_android_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class UploadPhotoMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo_menu);
    }

    public void buttonToTakePhoto(View view) {
        Intent intent = new Intent(this, TakePhoto.class);
        startActivity(intent);
    }

    public void buttonToPhotoFromGallery(View view){
        Intent intent = new Intent(this, PhotoFromGallery.class);
        startActivity(intent);
    }

}
