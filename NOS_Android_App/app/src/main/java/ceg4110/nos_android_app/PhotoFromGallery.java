package ceg4110.nos_android_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
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

import static android.provider.DocumentsContract.EXTRA_INITIAL_URI;

public class PhotoFromGallery extends AppCompatActivity {
    private Context mContext;
    private String result1, mCurrentPhotoPath, mCurrentPhotoPath1 = "/storage/emulated/0/Android/data/ceg4110.nos_android_app/files/Pictures";
    String pPhotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    private String[] result;

    private final String PHOTO_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();

    private static final int readReqCode = 41;
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
    boolean wrong = false;

    private Hashtable<String, String> results;
    File dict;
    FileOutputStream outputStream;

    File photoFile = null;


    /*
    The onCreate method creates a dictionary, hash table to store the results from previous uploads, and an Uploader object.
     */

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


    /*
     * The findFile method gets access to the Storage Access Framework only files
     * that can be opened are accessible. The intent is started and automatically
     * initiates onActivityResult.
     */

    public void findFile() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);       //only show files that can be opened
        intent.setType("image/*");                          //we want images, so set for only that type
        startActivityForResult(intent, readReqCode);

    }

    /*
     * This method gets the URI of the photo selected.
     * This method also gets the name and path of the image. Finally,
     * this method sends the photo to the history folder if it has been uploaded successfully.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

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

    /*
     * This method takes the URI set in onActivityResult and uses it to set the selected
     * for viewing in image view.
     */

    public void displayPhoto() {

        image = findViewById(R.id.imageView2);
        image.setImageURI(uri);


    }
    /*
     * This method is called when the Assess button is pressed. This method creates a new Uploader object that
     * sends the photo to the AI for assessment.
     */

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

                /*
                Sends photo to the AI by image path. Gets back a string array from the uploader, holding the results.
                 */
                @Override
                protected Boolean doInBackground(Void... params) {
                    Log.i(TAG, "Entering Uploader");
                    File temp = new File(mCurrentPhotoPath);
                    if (mCurrentPhotoPath == null)
                        return false;
                    if (!temp.exists()) {
                        wrong = true;
                        return false;
                    }
                    result1 = uploader.uploadFile("picture", mCurrentPhotoPath);
                    result = uploader.getAllResults();

                    if (result1.equals(""))
                        return false;
                    else
                        return true;
                }

                /*
                Moves photo to appropriate folder, depending on the success of the upload.
                Upon a successful assessment, this method moves the photo to the History folder.
                If not, it moves it to the Pending folder if there was an issue with the upload or assessment.
                It also shows the results of the upload (if successful) by redirecting the user to the
                results screen.
                 */

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
                        if (mCurrentPhotoPath == null)
                            Toast.makeText(getApplicationContext(), "No photo selected!", Toast.LENGTH_LONG).show();
                        else if(wrong)
                            Toast.makeText(getApplicationContext(), "Photo chosen from wrong directory!", Toast.LENGTH_LONG).show();
                        else {
                            Toast.makeText(getApplicationContext(), "Upload error! Moving photo to Pending folder", Toast.LENGTH_LONG).show();
                            movePhoto("Pending");
                        }
                    }
                }
            }.execute();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            movePhoto("Pending");
        }
    }

    /*
    This method moves a photo to its appropriate folder, depending on success or failure of the upload to the AI.
    It takes in a string value of the folder the photo should be moved to.
     */

    protected void movePhoto(String folder) {
        // if going to pending folder
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

            Intent intent = new Intent(mContext, PendingMenuFolder.class);
            intent.putExtra("photoPath", mCurrentPhotoPath);
            startActivityForResult(intent, 3);

        }
        // if going to history folder
        else if (folder.equals("History")) {
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

            // updates the dictionary
            results.put(mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/')), result[1] + " " + result[2]);
            try {
                outputStream.write((mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/')) + "\n").getBytes());
                Log.i(TAG, "past path, writing results");
                Log.i(TAG, "results: " + result[1] + " " + result[2]);
                outputStream.write((result[1] + " " + result[2] + "\n").getBytes());
            } catch (IOException ex) {
                Log.e(TAG, "Cannot write to dictionary file");
            } catch (Exception e) {
                Log.e(TAG, "Error with dict: " + e.getLocalizedMessage());
            }
            goToResults();
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
