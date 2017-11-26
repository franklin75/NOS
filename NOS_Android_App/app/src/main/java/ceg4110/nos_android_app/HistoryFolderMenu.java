package ceg4110.nos_android_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class HistoryFolderMenu extends AppCompatActivity {

    String mCurrentPhotoPath;
    String answer;
    String num;
    ImageView photo;
    Bitmap bitMap;
    TextView ans;
    TextView number;
    String [] r;
    String TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_folder_menu);
        r = new String [100];

        //File history = new File("/data/ceg4110.nos_android_app/files/History");


        if (getIntent().hasExtra("photoPath")) {
            mCurrentPhotoPath = getIntent().getStringExtra("photoPath");
            r[0] = mCurrentPhotoPath;
        }
        if(getIntent().hasExtra("resultAns")) {
            answer = getIntent().getStringExtra("resultAns");
            r[1] = answer;
        }
        if(getIntent().hasExtra("resultNums")) {
            num = getIntent().getStringExtra("resultNums");
            r[2] = num;
        }




        if(getIntent().hasExtra("photoPath"))
        {
            Toast.makeText(getApplicationContext(), "oh yeah", Toast.LENGTH_LONG).show();


            bitMap = BitmapFactory.decodeFile(mCurrentPhotoPath);

            photo = findViewById(R.id.temp);
            photo.setImageBitmap(bitMap);

            ans = findViewById(R.id.textView);
            number = findViewById(R.id.textView2);

            Log.i(TAG, "so far so right..");
            ans.setText(r[1]);
            number.setText(r[2]);
            Log.i(TAG, "fingers crossed...");

        }
        else
            {
                Toast.makeText(getApplicationContext(), "Nothing in history yet!", Toast.LENGTH_LONG).show();

            }


    }

    public void buttonUploadMenu(View view){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}
