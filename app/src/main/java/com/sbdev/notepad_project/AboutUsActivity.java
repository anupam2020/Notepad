package com.sbdev.notepad_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class AboutUsActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;

    private TextView email;

    private ImageView back;

    private TextView topText;

    private String SHARED_PREFS="SHARED_PREFS";

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        relativeLayout=findViewById(R.id.aboutUsSubRelative);

        email=findViewById(R.id.aboutUsText5);

        back=findViewById(R.id.aboutUsBack);

        topText=findViewById(R.id.aboutUsTopText);

        sp=getApplicationContext().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email.clearFocus();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        boolean switchState=sp.getBoolean("state",false);
        if(switchState)
        {
            nightModeAbout();
        }
        else
        {
            dayModeAbout();
        }

    }

    private void dayModeAbout()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.notes_blue));

        relativeLayout.setBackgroundResource(R.drawable.blue_bg_corner_radius);

        back.setImageTintList(ColorStateList.valueOf(Color.BLACK));

        topText.setTextColor(Color.BLACK);

    }

    private void nightModeAbout()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        relativeLayout.setBackgroundResource(R.drawable.black_bg_corner_radius);

        back.setImageTintList(ColorStateList.valueOf(Color.WHITE));

        topText.setTextColor(Color.WHITE);

    }

}