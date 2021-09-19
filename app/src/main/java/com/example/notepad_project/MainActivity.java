package com.example.notepad_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button signIn,signUp;

    private TextView mainText,mainSubText;

    private Animation topTextAnim,subTextAnim,lottieAnim,button1Anim,button2Anim;

    private LottieAnimationView lottie;

    private RelativeLayout relative;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(Color.WHITE);

        signIn=findViewById(R.id.mainSignInButton);
        signUp=findViewById(R.id.mainSignUpButton);

        lottie=findViewById(R.id.mainLottie);

        mainText=findViewById(R.id.mainText);
        mainSubText=findViewById(R.id.mainSubText);

        relative=findViewById(R.id.mainRelative);

        topTextAnim= AnimationUtils.loadAnimation(this,R.anim.main_window_top);
        subTextAnim= AnimationUtils.loadAnimation(this,R.anim.main_window_top_sub);
        lottieAnim= AnimationUtils.loadAnimation(this,R.anim.main_window_image);
        button1Anim= AnimationUtils.loadAnimation(this,R.anim.main_window_button1);
        button2Anim= AnimationUtils.loadAnimation(this,R.anim.main_window_button2);

        mainText.startAnimation(topTextAnim);
        mainSubText.startAnimation(subTextAnim);
        lottie.startAnimation(lottieAnim);
        signIn.startAnimation(button1Anim);
        signUp.startAnimation(button2Anim);


        int nightModeFlags = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags)
        {
            case Configuration.UI_MODE_NIGHT_YES:
                relative.setBackgroundColor(Color.WHITE);
                break;
        }


        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(MainActivity.this,NotesActivity.class));
            finishAffinity();
        }



        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finishAffinity();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                finishAffinity();
            }
        });


    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit");
        builder.setMessage("Do you really want to exit?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finishAffinity();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        builder.show();
    }

}