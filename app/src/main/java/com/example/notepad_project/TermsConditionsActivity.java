package com.example.notepad_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TermsConditionsActivity extends AppCompatActivity {

    private WebView webView;

    private RelativeLayout relativeLayout;

    private ImageView back;

    private TextView topText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);

        relativeLayout=findViewById(R.id.termsSubRelative);

        back=findViewById(R.id.termsBack);

        topText=findViewById(R.id.termsTopText);

        webView=findViewById(R.id.termsWebView);
        //webView.setWebViewClient(client);
        webView.loadUrl("file:///android_asset/terms_conditions.html");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        if(new NotesActivity().check)
        {
            nightModeEdit();
        }
        else
        {
            dayModeEdit();
        }

    }

    private void dayModeEdit()
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