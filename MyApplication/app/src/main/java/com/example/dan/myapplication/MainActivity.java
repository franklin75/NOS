package com.example.dan.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button bFourth;
    TextView mTextView;
    Context mContext;
    ImageView iView;
    Bitmap bitMap;
    Uploader upL;

    private String TAG = "TheTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bFourth = findViewById(R.id.button4);
        mTextView = findViewById(R.id.text);
        bitMap = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/food.jpeg");
        iView = findViewById(R.id.imageView2);
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
                        progressDialog = new ProgressDialog(MainActivity.this);
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

    public String uploadFile() {

        Log.i(TAG, "in upload");
        Log.i(TAG, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/food.jpeg");
        String serverURL = "http://52.204.111.28", res = "";
        String[] res2;
        mTextView.setText("Uploading " + file.getPath());
        Log.i(TAG, "File...::::" + file + " : " + file.exists());

        final MediaType TYPE = MediaType.parse("image/*");

        try {

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("pic", "food.jpg", RequestBody.create(TYPE, file))
                    .build();

            Request request = new Request.Builder()
                    .url(serverURL)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            res = response.body().string();
            Log.i(TAG, "res: " + res);
            res2 = res.split("\n");
            res = res2[2];
            Log.i(TAG, "res2: " + res);
            mTextView.setText(res);
            return res;


        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e(TAG, "Error: " + e.getLocalizedMessage());
            return res;
        } catch (Exception e) {
            Log.e(TAG, "Other Error: " + e.getLocalizedMessage());
            return res;
        }
    }

    public void nextPic(View view) {
        Intent intent = new Intent(this, Main2Activity.class);
        Log.i(TAG, "new intent");
        startActivity(intent);

    }
}



