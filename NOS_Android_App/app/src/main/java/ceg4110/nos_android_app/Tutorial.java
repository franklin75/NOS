package ceg4110.nos_android_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Tutorial extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
    }

    public void buttonToDemo(View view){
        Intent intent = new Intent(this, Demo1.class);
        startActivity(intent);
    }


}
