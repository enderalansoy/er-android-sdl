package com.easyroute.Sdl;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.easyroute.R;

public class SdlActivity extends AppCompatActivity {

    public static TextView textView;
    static String text2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdl_deneme);

        textView = (TextView)findViewById(R.id.textView);


        if (AppLinkService.getProxyInstance() == null) {
            Intent startIntent = new Intent(this, AppLinkService.class);
            startService(startIntent);
        }

    }

    public static void changeTextView(String text) {
        text2 += text + "\n";
        textView.setText(text2);
    }

}
