package ceg4110.nos_android_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class PhotoFromGallery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        findFile();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_from_gallery);
    }

    private static final int readReqCode = 42;
    String TAG = "anotherTag";
    ImageView image;

    public void findFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);       //
        intent.setType("image/*");
       // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); //lets you select multiple photos
        startActivityForResult(intent, readReqCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        Uri uri;
        if (requestCode == readReqCode && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                displayPhoto(uri);
            }
        }
    }

    public void displayPhoto(Uri uri) { 
        String path = uri.getPath();
        Log.i(TAG, "Path: " + path);
        image = findViewById(R.id.imageView2);
        image.setImageURI(uri);

    }

    public void onClickAssess(View view) {
        final String result1;
        final String[] result;
        //do the stuff to assess the image for food
        if (((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null) {
            new AsyncTask<Void, Integer, Boolean>() {

                ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(UploadPhotoMenu.this);
                    progressDialog.setMessage("Assessing food content...");
                    progressDialog.show();
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    Log.i(TAG, "Entering Uploader");
                    result1 = uploader.uploadFile(name, mCurrentPhotoPath);
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

                    Log.i(TAG, "Entering Results Screen...");
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
