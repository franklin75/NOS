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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class PhotoFromGallery extends AppCompatActivity {
    private Context mContext;
    private String result1, mCurrentPhotoPath, mCurrentPhotoPath1 = "/storage/emulated/0/Android/data/ceg4110.nos_android_app/files/Pictures";
    String pPhotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    private String[] result;

    private final String PHOTO_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();

    private static final int readReqCode = 42;
    String TAG = "anotherTag";
    ImageView image;
    //String mCurrentPhotoPath, name, result1;
    //String[] result;
    String name;
    Uploader uploader;
    //Context mContext;
    String pendingPath;
    //File photoFile = null;
    Uri uri;
    File path;

    private Hashtable<String, String> results;
    File dict;
    FileOutputStream outputStream;

    File photoFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = getApplicationContext();
        Log.i(TAG, "Path: " + Environment.getExternalStorageDirectory());
        findFile();
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_photo_from_gallery);
        uploader = new Uploader();

        results = new Hashtable<>();
        mContext = getApplicationContext();
        dict = new File("/storage/emulated/0/Android/data/ceg4110.nos_android_app/files/History/dict");
        if (dict.exists()) {
            StringBuilder in = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(dict));
                String line;
                outputStream = new FileOutputStream(dict, true);

                while ((line = br.readLine()) != null) {
                    results.put(line, br.readLine());
                }
                br.close();
            } catch (IOException e) {
                Log.e(TAG, "Error: " + e.getLocalizedMessage());
            }

        }
        else {
            try {
                dict.createNewFile();
                outputStream = new FileOutputStream(dict, true);
            } catch (Exception e) {
                Log.e(TAG, "dict problem: " + e.getLocalizedMessage());
            }
        }
    }




    public void findFile() {
       Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
       intent.addCategory(Intent.CATEGORY_OPENABLE);       //only show files that can be opened
        intent.setType("image/*");                          //we want images, so set for only that type
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

                name = uri.getPath().substring(uri.getPath().lastIndexOf('/'));
                mCurrentPhotoPath = "/storage/emulated/0/Android/data/ceg4110.nos_android_app/files/Pictures" + name;
                photoFile = new File(mCurrentPhotoPath);

                Log.i(TAG, "name: " + name.toString());
                Log.i(TAG, "mCurrentPhotoPath: " + mCurrentPhotoPath.toString());
            }
        }
            else if(requestCode == 2) { // if coming from history

            }

            else if(requestCode == 3) { // if coming from pending

            }

            else if(requestCode == 4) { // if coming from goToResults(), then go to history
                Intent intent = new Intent(mContext, HistoryFolderMenu.class);
                intent.putExtra("photoPath", getIntent().getStringExtra("photoPath"));
                intent.putExtra("resultNums", getIntent().getStringExtra("resultnNums"));
                intent.putExtra("resultAns", getIntent().getStringExtra("resultAns"));
                startActivityForResult(intent, 2);
            }

    }

    public void displayPhoto() {

        image = findViewById(R.id.imageView2);
        image.setImageURI(uri);


    }

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
                    progressDialog = new ProgressDialog(PhotoFromGallery.this);
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
                        //Log.i(TAG, "Upload succeeded");
                        movePhoto("History");
                        Log.i(TAG, "Entering Results Screen...");
                        goToResults();
                    }
                    else {
                        Log.i(TAG, "Upload failed");
                        Toast.makeText(getApplicationContext(), "Upload error! Moving photo to Pending folder", Toast.LENGTH_LONG).show();
                        //Intent intent = new Intent(mContext, PendingMenuFolder.class);
                       // intent.putExtra("photoPath", mCurrentPhotoPath);
                        //startActivity(intent);
                        movePhoto("Pending");
                    }
                }
            }.execute();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            movePhoto("Pending");
        }
    }

    protected void movePhoto(String folder) {
        if (folder.equals("Pending")) {

            Log.i(TAG, "Upload failed");
            try {
                byte[] buffer = new byte[2048];
                FileInputStream input;
                FileOutputStream output;
                int BUFFER_SIZE = 2048;
                File dest = null;
                boolean created = false;

                try {
                    dest = new File("/storage/emulated/0/Android/data/ceg4110.nos_android_app/files/Pending/" + mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/')));
                    if(!dest.exists()) {
                        Log.i(TAG, "dest didn't exist, creating...");
                        created = dest.createNewFile();
                    } else
                        Log.i(TAG, "oops, line 220");

                } catch (Exception ex) {
                    Log.e(TAG, "Hate 4 " + ex.getLocalizedMessage());
                }

                input = new FileInputStream(photoFile);
                output = new FileOutputStream(dest);
                Log.i(TAG, "created? " + created);

                BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
                BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
                int count = 0, n = 0;
                try {
                    while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        out.write(buffer, 0, n);
                    }
                    Log.i(TAG, "file copied over...?");
                    out.flush();
                    Log.i(TAG, "size of dest: " + dest.length());
                    Log.i(TAG, "location of dest: " + dest.getAbsolutePath());
                } catch (FileNotFoundException exx) {
                    Log.e(TAG, "can't find file(s)");
                } finally {
                    try {
                        out.close();
                        Log.i(TAG, "out closed");
                    } catch (IOException e) {
                        Log.e(TAG, "hate 1");
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.e(TAG, "hate 2");
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "hate 3, " + e.getLocalizedMessage());
            }

            Toast.makeText(getApplicationContext(), "Upload error! Moved photo to Pending folder", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(mContext, PendingMenuFolder.class);
            intent.putExtra("photoPath", mCurrentPhotoPath);
            startActivityForResult(intent, 3);

        } else if (folder.equals("History")) {
            Log.i(TAG, "Upload succeeded");
            try {
                byte[] buffer = new byte[2048];
                FileInputStream input;
                FileOutputStream output;
                int BUFFER_SIZE = 2048;
                File dest = null;
                boolean created = false;

                try {
                    dest = new File("/storage/emulated/0/Android/data/ceg4110.nos_android_app/files/History/" + mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/')));
                    if(!dest.exists()) {
                        Log.i(TAG, "dest didn't exist, creating...");
                        created = dest.createNewFile();
                    } else
                        Log.i(TAG, "dest exists already");

                } catch (Exception ex) {
                    Log.e(TAG, "Hate 4 " + ex.getLocalizedMessage());
                }

                input = new FileInputStream(photoFile);
                output = new FileOutputStream(dest);
                Log.i(TAG, "created? " + created);

                BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
                BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
                int count = 0, n = 0;
                try {
                    while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        out.write(buffer, 0, n);
                    }
                    Log.i(TAG, "file copied over...?");
                    out.flush();
                    Log.i(TAG, "size of dest: " + dest.length());
                    Log.i(TAG, "location of dest: " + dest.getAbsolutePath());
                } catch (FileNotFoundException exx) {
                    Log.e(TAG, "can't find file(s)");
                } finally {
                    try {
                        out.close();
                        Log.i(TAG, "out closed");
                    } catch (IOException e) {
                        Log.e(TAG, "hate 1");
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.e(TAG, "hate 2");
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "hate 3, " + e.getLocalizedMessage());
            }

            results.put(mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/')), result[1] + " " + result[2]);
            try {
                outputStream.write((mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/') + 1) + "\n").getBytes());
                Log.i(TAG, "past path, writing results");
                outputStream.write((result[1] + " " + result[2] + "\n").getBytes());
            } catch (IOException ex) {
                Log.e(TAG, "Cannot write to dictionary file");
            } catch (Exception e) {
                Log.e(TAG, "Error with dict: " + e.getLocalizedMessage());
            }
            goToResults();
        }
    }


    public void goToResults(){
        Intent intent = new Intent(this, ResultScreen.class);
        intent.putExtra("photoPath", mCurrentPhotoPath);
        intent.putExtra("resultNums", result[1]);
        intent.putExtra("resultAns", result[2]);
        startActivityForResult(intent, 4);
    }

    @Override
    protected void onDestroy() {

        try {
            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close dictionary file");
        }

        super.onDestroy();
    }
}
