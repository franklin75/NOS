package ceg4110.nos_android_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class ResultScreen extends AppCompatActivity {

    TextView mTextView, mTextView2;
    Context mContext;
    ImageView iView;
    Bitmap bitMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);
        mContext = getApplicationContext();
        mTextView = findViewById(R.id.text);
        mTextView2 = findViewById(R.id.text2);
        mTextView.setText(getIntent().getStringExtra("resultAns"));
        mTextView2.setText(getIntent().getStringExtra("resultNums"));
        bitMap = BitmapFactory.decodeFile(getIntent().getStringExtra("photoPath"));
        iView = findViewById(R.id.imageView);
        iView.setImageBitmap(bitMap);
    }

    public void buttonUploadMenu(View view){
        Intent intent = new Intent(this, UploadPhotoMenu.class);
        startActivity(intent);
    }
}
