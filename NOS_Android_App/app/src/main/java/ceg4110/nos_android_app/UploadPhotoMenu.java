package ceg4110.nos_android_app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;


public class UploadPhotoMenu extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath, name, result1;
    String[] result;
    String TAG = "TheTag";
    Uploader uploader;
    Context mContext;
    private Hashtable<String, String> results;
    File dict;
    FileOutputStream outputStream, os;

    File photoFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo_menu);

        try {
            outputStream = openFileOutput("/data/ceg4110.nos_android_app/files/History/dict", Context.MODE_PRIVATE);
        } catch (IOException e){
            Log.e(TAG, "Cannot open dictionary file");
        }
        mContext = getApplicationContext();
        dict = new File("/data/ceg4110.nos_android_app/files/History/dict");
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
        else
            results = new Hashtable<>();

    }

    public void buttonToTakePhoto(View view) {
        uploader = new Uploader();
        takePhoto();
    }

    public void goToResults(){
        Intent intent = new Intent(this, ResultScreen.class);
        intent.putExtra("photoPath", mCurrentPhotoPath);
        intent.putExtra("resultNums", result[1]);
        intent.putExtra("resultAns", result[2]);
        startActivity(intent);
    }

    public void buttonToPhotoFromGallery(View view){
        Intent intent = new Intent(this, PhotoFromGallery.class);
        startActivity(intent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        name = imageFileName + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                //error
            }
            if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "ceg4110.nos_android_app.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.i(TAG, "Starting Async thread...");
            handleInput();
        }
        else if(requestCode == 2) { // if going to history

        }

        else if(requestCode == 3) { // if going to pending

        }
    }


    @SuppressLint("StaticFieldLeak")
    public void handleInput() {
        if (((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null) {
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
                        try {
                            byte[] buffer = new byte[2048];
                            FileInputStream input;
                            FileOutputStream output;
                            int BUFFER_SIZE = 2048;

                            input = new FileInputStream(photoFile);
                            output = new FileOutputStream("/data/ceg4110.nos_android_app/files/History/" + mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/')));

                            BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
                            BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
                            int count = 0, n = 0;
                            try {
                                while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                                    out.write(buffer, 0, n);
                                    count += n;
                                }
                                out.flush();
                            } catch (FileNotFoundException exx) {
                                Log.e(TAG, "can't find file(s)");
                            } finally {
                                try {
                                    out.close();
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

                        }

                        Intent intent = new Intent(mContext, HistoryFolderMenu.class);
                        results.put(mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/')), result[1] + " " + result[2]);
                        try {
                            outputStream.write((mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/')) + "\n").getBytes());
                            outputStream.write((result[1] + " " + result[2] + "\n").getBytes());
                        } catch (IOException ex) {
                            Log.e(TAG, "Cannot write to dictionary file");
                        } catch (Exception e) {
                            Log.e(TAG, "Error: " + e.getLocalizedMessage());
                        }
                        intent.putExtra("photoPath", mCurrentPhotoPath);
                        intent.putExtra("resultNums", result[1]);
                        intent.putExtra("resultAns", result[2]);
                        startActivityForResult(intent, 2);

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
