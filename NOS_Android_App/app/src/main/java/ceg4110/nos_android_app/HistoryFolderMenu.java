package ceg4110.nos_android_app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class HistoryFolderMenu extends AppCompatActivity {

    private String result1, mCurrentPhotoPath, TAG = "TheTag";
    private String[] result;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_folder_menu);
        mContext = getApplicationContext();
        if (getIntent().hasExtra("photoPath"))
            mCurrentPhotoPath = getIntent().getStringExtra("photoPath");
    }

    //save photo to history folder shit goes here

    //display shit goes here

    //upload shit
    @SuppressLint("StaticFieldLeak")
    public void onClickAssess(View view) {
        final Uploader uploader = new Uploader();
        //do the stuff to assess the image for food
        if (((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null) {
            new AsyncTask<Void, Integer, Boolean>() {

                ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(HistoryFolderMenu.this);
                    progressDialog.setMessage("Assessing food content...");
                    progressDialog.show();
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    Log.i(TAG, "Entering Uploader");
                    result1 = uploader.uploadFile("picture", mCurrentPhotoPath);
                    result = uploader.getAllResults();

                    if (result1.equals(""))
                        return false;
                    else
                        return true;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    if (progressDialog != null)
                        progressDialog.dismiss();

                    Log.i(TAG, "Entering Results Screen");
                    goToResults();

                    if (aBoolean) {
                        Log.i(TAG, "Upload succeeded");
                    }
                    else {
                        Log.i(TAG, "Upload failed");
                        Toast.makeText(getApplicationContext(), "Upload error! Moving photo to Pending folder", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(mContext, PendingMenuFolder.class);
                        intent.putExtra("photoPath", mCurrentPhotoPath);
                        startActivity(intent);

                    }
                }
            }.execute();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void goToResults(){
        Intent intent = new Intent(this, ResultScreen.class);
        intent.putExtra("photoPath", mCurrentPhotoPath);
        intent.putExtra("resultNums", result[1]);
        intent.putExtra("resultAns", result[2]);
        startActivity(intent);
    }
}
