package ceg4110.nos_android_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class HistoryFolderMenu extends AppCompatActivity {

    private String result1, mCurrentPhotoPath, mCurrentPhotoPath1 = "/storage/emulated/0/Android/data/ceg4110.nos_android_app/files/History";
    String TAG = "TheTag";
    File dict;
    private String[] result;
    Context mContext;
    private static final int readReqCode = 42;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_folder_menu);
        dict = new File("/storage/emulated/0/Android/data/ceg4110.nos_android_app/files/History/dict");
        mContext = getApplicationContext();
        if (getIntent().hasExtra("photoPath"))
            mCurrentPhotoPath = getIntent().getStringExtra("photoPath");
        historyFile();
    }

    /*
     * This method gets access to the Storage Access Framework only files
     * that can be opened are accessible. The intent is started and automatically
     * initiates onActivityResult.
     */
    public void historyFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);       //only show files that can be opened
        intent.setType("image/*");                          //we want images, so set for only that type
        startActivityForResult(intent, readReqCode);
    }

    /*
     * This method gets the URI of the photo selected.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        Uri uri;
        if (requestCode == readReqCode && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                uri = resultData.getData();
                displayPhoto(uri);
            }
        }
    }

    /*
     * This method takes the URI set in onActivityResult and uses it to set the selected
     * for viewing in image view. It also gets the path of the image.
     */
    public void displayPhoto(Uri uri) {
        mCurrentPhotoPath = mCurrentPhotoPath1 + uri.getPath().substring(uri.getPath().lastIndexOf('/'));
        Log.i(TAG, "filename: " + mCurrentPhotoPath);
        image = findViewById(R.id.imageView3);
        image.setImageURI(uri);


    }

    /*
     * This method opens the results class on selection of the View Results button
     */
    public void onClickViewResults(View view) {
        Intent intent = new Intent(this, ResultScreen.class);
        intent.putExtra("photoPath", mCurrentPhotoPath);
        startActivity(intent);
    }

    /*
     * This method deleted the selected photo on selection of the delete button and
     * returns to the History folder.
     */
    public void onClickDelete(View view) {
        File toDelete = new File(mCurrentPhotoPath);
        toDelete.delete();
        Intent intent = new Intent(this, HistoryFolderMenu.class);
        startActivity(intent);
    }

    //upload
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


                    if (aBoolean) {
                        Log.i(TAG, "Upload succeeded");
                        goToResults();
                        //update dict
                    }
                    else {
                        Log.i(TAG, "Upload failed");
                        Toast.makeText(getApplicationContext(), "Upload error! Try again later.", Toast.LENGTH_LONG).show();
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

    /*
     * This method initiates the results class and send the photo path and results to the class.
     */
    public void goToResults(){
        Intent intent = new Intent(this, ResultScreen.class);
        intent.putExtra("photoPath", mCurrentPhotoPath);
        intent.putExtra("resultNums", result[1]);
        intent.putExtra("resultAns", result[2]);
        startActivity(intent);
    }


    public void onClickMainMenu(View view){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}
