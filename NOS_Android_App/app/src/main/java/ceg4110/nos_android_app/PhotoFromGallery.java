package ceg4110.nos_android_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoFromGallery extends AppCompatActivity {

    private static final int readReqCode = 42;
    String TAG = "anotherTag";
    ImageView image;
    String mCurrentPhotoPath, name, result1;
    String[] result;
    Uploader uploader;
    Context mContext;
    String pendingPath;
    File photoFile = null;
    Uri uri;
    File path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        findFile();
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_photo_from_gallery);
        uploader = new Uploader();
    }



    public void findFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);       //
        intent.setType("image/*");
       // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); //lets you select multiple photos
        startActivityForResult(intent, readReqCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

       // Uri uri;
        if (requestCode == readReqCode && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                displayPhoto();

              name =  uri.getPath().substring(uri.getPath().lastIndexOf('/'));
              mCurrentPhotoPath = "/storage/emulated/0/Android/data/ceg4110.nos_android_app/files/Pictures" + name;

                Log.i(TAG, "name: " + name.toString());
                Log.i(TAG, "mCurrentPhotoPath: " + mCurrentPhotoPath.toString());
            }
        }
    }

    public void displayPhoto() {

        image = findViewById(R.id.imageView2);
        image.setImageURI(uri);


    }


    public void goToResults(){
        Intent intent = new Intent(this, ResultScreen.class);
        intent.putExtra("photoPath", mCurrentPhotoPath);
        intent.putExtra("resultNums", result[1]);
        intent.putExtra("resultAns", result[2]);
        startActivity(intent);
    }

    public void onClickAssess(View view) {
        //do the stuff to assess the image for food
        handleInput();
       // goToResults();
    }





    @SuppressLint("StaticFieldLeak")
    public void handleInput() {
        if (((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null) {
            new AsyncTask<Void, Integer, Boolean>() {

                ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(PhotoFromGallery.this);
                    progressDialog.setMessage("Assessing food content...");
                    progressDialog.show();
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    Log.i(TAG, "Entering Uploader");
                    Log.i(TAG, "mCurrentPhotoPath again: " + mCurrentPhotoPath.toString());
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

                        Intent intent = new Intent (mContext, HistoryFolderMenu.class);

                        intent.putExtra("photoPath", mCurrentPhotoPath);
                        intent.putExtra("resultNums", result[1]);
                        intent.putExtra("resultAns", result[2]);

                        startActivity(intent);



                    }
                    else {
                        Log.i(TAG, "Upload failed");
                        Toast.makeText(getApplicationContext(), "Upload error! Moving photo to Pending folder", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(mContext, PendingMenuFolder.class);
                        intent.putExtra("photoPath", mCurrentPhotoPath);
                        startActivityForResult(intent, 3);


                    }

                }
            }.execute();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }



    }
}