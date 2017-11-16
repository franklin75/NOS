package ceg4110.nos_android_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Demo2 extends AppCompatActivity {

    TextView mTextView;
    Context mContext;
    ImageView iView;
    Bitmap bitMap;
    Uploader upL;

    private String TAG = "TheTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2);
        mTextView = findViewById(R.id.textView);
        bitMap = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/food_no.jpg");
        iView = findViewById(R.id.imageView);
        iView.setImageBitmap(bitMap);
        mContext = getApplicationContext();
        Log.i(TAG, "started");
        mTextView.setText("Waiting...");
        upL = new Uploader();
    }

    public void handleInput(View view) {
        final int clicked = view.getId();
        Log.i(TAG, "clicked: " + clicked);
        if (((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null) {
            new AsyncTask<Void, Integer, Boolean>() {

                ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(Demo2.this);
                    progressDialog.setMessage("Assessing food content...");
                    progressDialog.show();
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    String result;
                    Log.i(TAG, "in background");
                    mTextView.setText("Uploading...");
                    upL.upload(clicked);
                    result = upL.getResults();
                    Log.i(TAG, "result: " + result);
                    mTextView.setText(result);

                    if (result.equals(""))
                        return false;
                    else
                        return true;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    if (progressDialog != null)
                        progressDialog.dismiss();

                    if (aBoolean)
                        Log.i(TAG, "Upload succeeded");
                    else {
                        Log.i(TAG, "Upload failed");
                        mTextView.setText("");
                        Toast.makeText(getApplicationContext(), "Upload error!", Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

    }

    public void nextPic(View view) {
        Intent intent = new Intent(this, Demo1.class);
        Log.i(TAG, "new intent");
        startActivity(intent);

    }

    public void toTutorial(View view) {
        Intent intent = new Intent(this, Tutorial.class);
        Log.i(TAG, "new intent");
        startActivity(intent);
    }
}
