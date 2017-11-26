package ceg4110.nos_android_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;


public class MainMenu extends AppCompatActivity {

    String TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        File test = new File("/data/data/ceg4110.nos_android_app/files/");
        Log.i(TAG, "test " + test.isDirectory());
    }

    public void buttonToUploadPhoto(View view){
        Intent intent = new Intent(this, UploadPhotoMenu.class);
        startActivity(intent);
    }

    public void buttonToHistoryFolder(View view){

        Intent intent = new Intent(this, HistoryFolderMenu.class);
        startActivity(intent);
    }

    public void buttonToPendingFolder(View view){
        Intent intent = new Intent(this, PendingMenuFolder.class);
        startActivity(intent);
    }

    public void buttonToAbout(View view){
        Intent intent = new Intent(this, AboutMenu.class);
        startActivity(intent);
    }
}
