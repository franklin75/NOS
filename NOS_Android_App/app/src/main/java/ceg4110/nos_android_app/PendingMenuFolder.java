package ceg4110.nos_android_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PendingMenuFolder extends AppCompatActivity {

    private String result1, mCurrentPhotoPath, mCurrentPhotoPath1 = "/storage/emulated/0/Android/data/ceg4110.nos_android_app/files/Pending";
    private static final int readReqCode = 42;
    String TAG = "TheTag";
    ImageView image;
    private String[] result;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_menu_folder);

        if (getIntent().hasExtra("photoPath")) {
            mCurrentPhotoPath = getIntent().getStringExtra("photoPath");
        }

        pendingFile();

    }


    public void onClickDelete(View view) {
        File toDelete = new File(mCurrentPhotoPath);
        toDelete.delete();
        Intent intent = new Intent(this, PendingMenuFolder.class);
        startActivity(intent);
    }

    public void onClickMainMenu(View view) {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }



        public void pendingFile() {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);       //only show files that can be opened
            intent.setType("image/*");                          //we want images, so set for only that type
            // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); //lets you select multiple photos
            startActivityForResult(intent, readReqCode);
        }

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

        public void displayPhoto(Uri uri) {
            mCurrentPhotoPath = mCurrentPhotoPath1 + uri.getPath().substring(uri.getPath().lastIndexOf('/'));
            Log.i(TAG, "filename: " + mCurrentPhotoPath);
            File temp2 = new File(mCurrentPhotoPath);
            Log.i(TAG, "temp2 exists: " + temp2.exists());
            image = findViewById(R.id.imageView4);
            image.setImageURI(uri);
            Log.i(TAG, "Photo displayed");

        }

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
                        progressDialog = new ProgressDialog(PendingMenuFolder.this);
                        progressDialog.setMessage("Assessing food content...");
                        progressDialog.show();
                    }

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        Log.i(TAG, "Entering Uploader");
                        Log.i(TAG, "path: " + mCurrentPhotoPath);
                        File temp = new File(mCurrentPhotoPath);
                        Log.i(TAG, "Does it exist? " + temp.exists());
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

                        if (aBoolean) {
                            Log.i(TAG, "Upload succeeded");
                            Log.i(TAG, "Entering Results Screen");
                            goToResults(movePhoto());
                        }
                        else {
                            Log.i(TAG, "Upload failed");
                            Toast.makeText(getApplicationContext(), "Upload error! Leaving photo in Pending folder", Toast.LENGTH_LONG).show();

                        }
                    }
                }.execute();
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }

        public void goToResults(String path){
            if (!path.equals("")) {
                Intent intent = new Intent(this, ResultScreen.class);
                intent.putExtra("photoPath", path);
                intent.putExtra("resultNums", result[1]);
                intent.putExtra("resultAns", result[2]);
                startActivity(intent);
            } else {
                Log.e(TAG, "results path problem, line 135");
            }
        }

        private String movePhoto() {
            File photoFile = new File(mCurrentPhotoPath);
            String path = "";
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
                        path = dest.getAbsolutePath();
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

            /*
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
            */
            photoFile.delete();
            return path;
        }


    }
