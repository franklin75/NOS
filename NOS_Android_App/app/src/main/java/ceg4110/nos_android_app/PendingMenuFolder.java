package ceg4110.nos_android_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PendingMenuFolder extends AppCompatActivity {

    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_menu_folder);
        if (getIntent().hasExtra("photoPath"))
            mCurrentPhotoPath = getIntent().getStringExtra("photoPath");
    }
}
