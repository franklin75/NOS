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
    String TAG = "TheTag", photoPath = "", name = "";
    boolean set1 = false, set2 = false;

    /*
     * This method gets the image path and results (from AI upload),
     * creates a dictionary of each images path and results
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);
        mContext = getApplicationContext();
        mTextView = findViewById(R.id.text);
        mTextView2 = findViewById(R.id.text2);
        //verifies result present, if so displays in text view
        if(getIntent().hasExtra("resultAns")) {
            mTextView.setText(getIntent().getStringExtra("resultAns"));
            set1 = true;
        }
        //verifies result present, if so displays in text view
        if (getIntent().hasExtra("resultNums")) {
            String resultNums = getIntent().getStringExtra("resultNums");
            mTextView2.setText(resultNums.substring(3, resultNums.lastIndexOf(']') - 1));
            set2 = true;
        }
        //image set and displayed
        photoPath = getIntent().getStringExtra("photoPath");
        name = photoPath.substring(photoPath.lastIndexOf('/') + 1);
        bitMap = BitmapFactory.decodeFile(getIntent().getStringExtra("photoPath"));
        iView = findViewById(R.id.imageView);
        iView.setImageBitmap(bitMap);
        results = new Hashtable<>();
        //reads results dictionary from file into hash table
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
        //retrieve key and value from hash table, sets them to text view if not already present
        if(results.containsKey(name) && !set1) {
            mTextView2.setText(results.get(name).substring(3, results.get(name).lastIndexOf(']') - 1));
            if (results.containsKey((name)) && !set2)
                mTextView.setText(results.get(name).substring(results.get(name).lastIndexOf(']') + 1).trim());
        } else if (!set1 && !set2) {
            mTextView.setText("No Results");
            mTextView2.setText("No Results");
        }
    }
    /*
     * This method initiates the main menu class
     */
    public void buttonMainMenu(View view){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}
