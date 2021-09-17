package com.example.notepad_project;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout layout1,layout2,layout3;

    private EditText name,email,pass;

    private RelativeLayout relative;

    private TextView subText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(Color.WHITE);

        layout1=findViewById(R.id.editTextLayout1);
        layout2=findViewById(R.id.editTextLayout2);
        layout3=findViewById(R.id.editTextLayout3);

        name=findViewById(R.id.editText1);
        email=findViewById(R.id.editText2);
        pass=findViewById(R.id.editText3);

        subText=findViewById(R.id.registerSubText);

        relative=findViewById(R.id.registerRelative);


    }
}