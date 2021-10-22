package com.example.notepad_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private WebView webView;

    private RelativeLayout relativeLayout;

    private ImageView back;

    private TextView topText;

    private String SHARED_PREFS="SHARED_PREFS";

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        relativeLayout=findViewById(R.id.privacySubRelative);

        back=findViewById(R.id.privacyBack);

        topText=findViewById(R.id.privacyTopText);

        sp=getApplicationContext().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        webView =findViewById(R.id.privacyWebView);
        //webView.setWebViewClient(client);
        webView.loadUrl("file:///android_asset/privacy_policy.html");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        boolean switchState=sp.getBoolean("state",false);
        if(switchState)
        {
            nightModeEdit();
        }
        else
        {
            dayModePolicy();
        }

    }

    private void dayModePolicy()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.notes_blue));

        relativeLayout.setBackgroundResource(R.drawable.blue_bg_corner_radius);

        back.setImageTintList(ColorStateList.valueOf(Color.BLACK));

        topText.setTextColor(Color.BLACK);

    }

    private void nightModeEdit()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        relativeLayout.setBackgroundResource(R.drawable.black_bg_corner_radius);

        back.setImageTintList(ColorStateList.valueOf(Color.WHITE));

        topText.setTextColor(Color.WHITE);

    }

}