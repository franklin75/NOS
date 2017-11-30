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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.regex.Pattern;

public class HistoryFolderMenu extends AppCompatActivity {

    private String result1, mCurrentPhotoPath, mCurrentPhotoPath1 = "/storage/emulated/0/Android/data/ceg4110.nos_android_app/files/History";
    String TAG = "TheTag";
    File dict;
    private String[] result;
    Context mContext;
    private Hashtable<String, String> results;
    private static final int readReqCode = 42;
    ImageView image;
    boolean wrong = false;

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
        File temp2 = new File(mCurrentPhotoPath);
        if (mCurrentPhotoPath == null)
            Toast.makeText(getApplicationContext(), "No photo selected!", Toast.LENGTH_LONG).show();
        else if (!temp2.exists())
            Toast.makeText(getApplicationContext(), "Photo chosen from wrong directory!", Toast.LENGTH_LONG).show();
        else {
            results = new Hashtable<>();
            FileOutputStream outputStream;
            if (dict.exists()) {
                StringBuilder in = new StringBuilder();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(dict));
                    String line;

                    while ((line = br.readLine()) != null) {
                        results.put(line, br.readLine());
                    }
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error: " + e.getLocalizedMessage());
                }
            }
            Intent intent = new Intent(this, ResultScreen.class);
            String temp = results.get(mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/')));
            Log.i(TAG, "temp: " + temp);
            intent.putExtra("photoPath", mCurrentPhotoPath);
            intent.putExtra("resultAns", temp.substring(temp.lastIndexOf(']') + 1).trim());
            intent.putExtra("resultNums", temp.substring(0, temp.lastIndexOf(']')));
            startActivity(intent);
        }
    }

    /*
     * This method deletes the selected photo on selection of the delete button and
     * returns to the History folder.
     */
    public void onClickDelete(View view) {
        File temp3 = new File(mCurrentPhotoPath);
        if (mCurrentPhotoPath == null)
            Toast.makeText(getApplicationContext(), "No photo selected!", Toast.LENGTH_LONG).show();
        else if(!temp3.exists())
            Toast.makeText(getApplicationContext(), "Photo chosen from wrong directory!", Toast.LENGTH_LONG).show();
        else {
            results = new Hashtable<>();
            FileOutputStream outputStream;
            if (dict.exists()) {
                StringBuilder in = new StringBuilder();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(dict));
                    String line;

                    while ((line = br.readLine()) != null) {
                        results.put(line, br.readLine());
                    }
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error: " + e.getLocalizedMessage());
                }
            }

            results.remove(mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/')));

            try {
                String temp = results.toString().substring(1, results.toString().length() - 1);
                temp = temp.replace("=", "\n");
                temp = temp.replace(", ", "\n");
                Log.i(TAG, "temp: " + temp);
                outputStream = new FileOutputStream(dict);
                outputStream.write(temp.getBytes());
            } catch (IOException ex) {
                Log.e(TAG, "Cannot write to dictionary file");
            } catch (Exception e) {
                Log.e(TAG, "Error with dict: " + e.getLocalizedMessage());
            }

            Intent intent = new Intent(this, HistoryFolderMenu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            File toDelete = new File(mCurrentPhotoPath);
            toDelete.delete();
            finish();
            startActivity(intent);
        }
    }

    /*
     * This method is called when the assess button is pressed.
     * This method creates a new Uploader object that sends the photo to the AI for assessment
     */
    @SuppressLint("StaticFieldLeak")
    public void onClickAssess(View view) {
        final Uploader uploader = new Uploader();
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
                /*
                 * Sends photo to AI by image path, gets back a string array from the
                 * uploader holding the results
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
                 * Upon successful assessment, this method takes the user to the results window.
                 * If not it moves it to the pending folder if there was an issue with the upload or assessment
                 */
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    if (progressDialog != null)
                        progressDialog.dismiss();

                    Log.i(TAG, "Entering Results Screen");


                    if (aBoolean) {
                        Log.i(TAG, "Upload succeeded");
                        goToResults();
                    }
                    else {
                        Log.i(TAG, "Upload failed");
                        if(mCurrentPhotoPath == null)
                            Toast.makeText(getApplicationContext(), "No photo to upload!", Toast.LENGTH_LONG).show();
                        else if(wrong)
                            Toast.makeText(getApplicationContext(), "Photo chosen from wrong directory!", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(), "Upload error! Try again later.", Toast.LENGTH_LONG).show();
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

    /*
     * Initiates main menu class
     */
    public void onClickMainMenu(View view){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}
