package ceg4110.nos_android_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultScreen extends AppCompatActivity {

    TextView mTextView;
    Context mContext;
    ImageView iView;
    Bitmap bitMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);
        mContext = getApplicationContext();
        mTextView = findViewById(R.id.text);
        mTextView.setText(getIntent().getStringExtra("result"));
        bitMap = BitmapFactory.decodeFile(getIntent().getStringExtra("photoPath"));
        iView = findViewById(R.id.imageView);
        iView.setImageBitmap(bitMap);
    }
}
