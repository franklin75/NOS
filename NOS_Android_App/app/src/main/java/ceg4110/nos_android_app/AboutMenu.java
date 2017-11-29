package ceg4110.nos_android_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AboutMenu extends AppCompatActivity {

    /*
   References About activity xml page. All content is on the design and text page.
    */

    @Override 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_menu);
    }

    /*
     * This method initiates the tutorial class
     */
    public void buttonToTutorial(View view){
        Intent intent = new Intent(this, Tutorial.class);
        startActivity(intent);
    }

    /*
     * This method initiates the image classification class
     */
    public void buttonToImageClassification(View view){
        Intent intent = new Intent(this, ImageClassification.class);
        startActivity(intent);
    }

    /*
     * This method initiates the main menu class
     */
    public void buttonMainMenu(View view){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}
