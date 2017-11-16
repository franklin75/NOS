package ceg4110.nos_android_app;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by dan on 11/09/17.
 */

public class Uploader {

    private String TAG = "TheTag", results, res = "", name, serverURL = "http://52.204.111.28";
    private String[] res2;
    private File file;
    final int B4 = R.id.button4;
    final int B5 = R.id.button2;

    public void upload(int clicked) {

        if(clicked == B4)
            name = "/food.jpeg";
        else if (clicked == B5)
            name = "/food_no.jpg";

        Log.i(TAG, "in upL background");
        results = uploadFile(name);
        Log.i(TAG, "result: " + results);
    }

    private String uploadFile(String name) {
        Log.i(TAG, "in uploadFile");
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + name);
        Log.i(TAG, "File...::::" + file + " : " + file.exists());

        final MediaType TYPE = MediaType.parse("image/*");

        try {

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("pic", name, RequestBody.create(TYPE, file))
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
            return res;


        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e(TAG, "Error: " + e.getLocalizedMessage());
            return res;
        } catch (Exception e) {
            Log.e(TAG, "Other Error: " + e.getLocalizedMessage());
            return res;
        }
    }

    public String getResults() {
        return results;
    }
}

