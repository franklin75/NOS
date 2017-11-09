package ceg4110.nos_android_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AboutMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_menu);
    }

    public void buttonToTutorial(View view){
        Intent intent = new Intent(this, Tutorial.class);
        startActivity(intent);
    }

    public void buttonToImageClassification(View view){
        Intent intent = new Intent(this, ImageClassification.class);
        startActivity(intent);
    }
}
