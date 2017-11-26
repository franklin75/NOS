package ceg4110.nos_android_app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
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

    private String TAG = "TheTag", res = "", serverURL = "http://52.204.111.28";
    private String[] res2;
    private File file;
    Bitmap bitMap;

    public String uploadFile(String name, String photoPath) {
        file = new File(photoPath);
        Log.i(TAG, "File...::::" + file + " : " + file.exists());
        bitMap = BitmapFactory.decodeFile(photoPath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
        Log.i(TAG, "File...::::" + file + " : " + file.exists());

        final MediaType TYPE = MediaType.parse("image/*");

        try {

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("pic", name, RequestBody.create(TYPE, bos.toByteArray()))
                    .build();

            Request request = new Request.Builder()
                    .url(serverURL)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();

            Response response = client.newCall(request).execute();

            res = response.body().string();
            res2 = res.split("\n");
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
        return res;
    }

    public String[] getAllResults() { return res2; }
}

