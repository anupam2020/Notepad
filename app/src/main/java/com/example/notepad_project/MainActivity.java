package com.example.notepad_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    Button signIn,signUp;

    TextView mainText,mainSubText;

    Animation topTextAnim,subTextAnim,lottieAnim,button1Anim,button2Anim;

    LottieAnimationView lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signIn=findViewById(R.id.mainSignInButton);
        signUp=findViewById(R.id.mainSignUpButton);

        lottie=findViewById(R.id.mainLottie);

        mainText=findViewById(R.id.mainText);
        mainSubText=findViewById(R.id.mainSubText);

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

                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finishAffinity();
            }
        });




    }
}