package ceg4110.nos_android_app;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;


public class MainMenu extends AppCompatActivity {

    File pendingDir = new File("/data/ceg4110.nos_android_app/files/Pending");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!(pendingDir.exists())) {
            pendingDir.mkdir();
        }

        setContentView(R.layout.activity_main_menu);
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
