package ceg4110.nos_android_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class ResultScreen extends AppCompatActivity {

    TextView mTextView, mTextView2;
    Context mContext;
    ImageView iView;
    Bitmap bitMap;
    File dict;
    FileOutputStream outputStream;
    private Hashtable<String, String> results;
    String TAG = "TheTag", photoPath = "", name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);
        mContext = getApplicationContext();
        mTextView = findViewById(R.id.text);
        mTextView2 = findViewById(R.id.text2);
        if(getIntent().hasExtra("resultAns")) {
            mTextView.setText(getIntent().getStringExtra("resultAns"));
        }
        if (getIntent().hasExtra("resultNum")) {
            mTextView2.setText(getIntent().getStringExtra("resultNums"));
        }
        photoPath = getIntent().getStringExtra("photoPath");
        name = photoPath.substring(photoPath.lastIndexOf('/') + 1);
        bitMap = BitmapFactory.decodeFile(getIntent().getStringExtra("photoPath"));
        iView = findViewById(R.id.imageView);
        iView.setImageBitmap(bitMap);
        results = new Hashtable<>();

        dict = new File("/storage/emulated/0/Android//data/ceg4110.nos_android_app/files/History/dict");
        if(dict.exists()) {
            StringBuilder in = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(dict));
                String line;
                outputStream = new FileOutputStream(dict, true);

                while((line = br.readLine()) != null) {
                    results.put(line, br.readLine());
                    Log.i(TAG, "result added");
                }
                br.close();
            } catch (IOException e) {
                Log.e(TAG, "Error: " + e.getLocalizedMessage());
            }
        }
        Log.i(TAG, "table: " + results.toString());
        Log.i(TAG, "name is: " + name);
        if(results.containsKey(name)) {
            mTextView2.setText(results.get(name).substring(3, results.get(name).lastIndexOf(']') - 1));
            mTextView.setText(results.get(name).substring(results.get(name).lastIndexOf(']') + 1).trim());
        } else {
            mTextView.setText("No Results");
            mTextView2.setText("No Results");
        }
    }

    public void buttonMainMenu(View view){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}
