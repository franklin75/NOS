package ceg4110.nos_android_app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_folder_menu);

        File history = new File("/data/ceg4110.nos_android_app/files/History/");


        if (getIntent().hasExtra("photoPath"))
            mCurrentPhotoPath = getIntent().getStringExtra("photoPath");
        if(getIntent().hasExtra("resultAns"))
            answer = getIntent().getStringExtra("resultAns");
        if(getIntent().hasExtra("resultNums"))
            num = getIntent().getStringExtra("resultNums");




        if(history.exists()) {
            Toast.makeText(getApplicationContext(), "fuck yeah", Toast.LENGTH_LONG).show();


            bitMap = BitmapFactory.decodeFile("/data/ceg4110.nos_android_app/files/History/");

            photo = findViewById(R.id.temp);
            photo.setImageBitmap(bitMap);

            ans = findViewById(R.id.textView);
            number = findViewById(R.id.textView2);

            ans.setText(answer);
            number.setText(num);


        }
        else
            {
                Toast.makeText(getApplicationContext(), "Nothing in history yet!", Toast.LENGTH_LONG).show();

            }


    }
}
