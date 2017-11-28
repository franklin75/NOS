package ceg4110.nos_android_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class PendingMenuFolder extends AppCompatActivity {

    String mCurrentPhotoPath;
    ImageView photo;
    Bitmap bitMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_menu_folder);

        if (getIntent().hasExtra("photoPath")) {
            mCurrentPhotoPath = getIntent().getStringExtra("photoPath");
            bitMap = BitmapFactory.decodeFile(getIntent().getStringExtra("photoPath"));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitMap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
            photo = findViewById(R.id.temp);
            photo.setImageBitmap(bitMap);
        }
        else
            Toast.makeText(getApplicationContext(), "Nothing here", Toast.LENGTH_LONG).show();

    }

    public void buttonUploadMenu(View view){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}
