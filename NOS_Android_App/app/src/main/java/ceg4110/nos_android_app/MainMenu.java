package ceg4110.nos_android_app;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;


public class MainMenu extends AppCompatActivity {

    final private String TAG = "TheTag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        File pending = new File("/storage/emulated/0/Android/data/ceg4110.nos_android_app/files/Pending");
        File history = new File("/storage/emulated/0/Android/data/ceg4110.nos_android_app/files/History");
        File dir = new File("/data/data/ceg4110.nos_android_app/files");
        File dict = new File("/data/data/ceg4110.nos_android_app/files/History/dict");
        if (!history.exists())
            history.mkdir();
        if(!pending.exists())
            pending.mkdir();

        Log.i(TAG, history.getAbsolutePath());
        Log.i(TAG,  "dict length: " + dict.length());
        Log.i(TAG, "dict exists: " + dict.exists());
        Log.i(TAG, "dir1 is dir: " + dir.isDirectory());
        Log.i(TAG, "history files: " + history.listFiles());
        Log.i(TAG, "history size: " + history.length());
        Log.i(TAG, "writable? : " + history.canWrite());
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
