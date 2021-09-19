package com.example.notepad_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout layout1,layout2,layout3;

    private EditText name,email,pass;

    private RelativeLayout relative;

    private TextView subText,signUp;

    private Button login;

    private ProgressDialog dialog;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(Color.WHITE);

        layout1=findViewById(R.id.editTextLayout1);
        layout2=findViewById(R.id.editTextLayout2);

        email=findViewById(R.id.editText1);
        pass=findViewById(R.id.editText2);

        login=findViewById(R.id.loginButton);

        subText=findViewById(R.id.loginSubText);
        signUp=findViewById(R.id.loginSignUp);

        relative=findViewById(R.id.loginRelative);

        dialog=new ProgressDialog(LoginActivity.this);

        firebaseAuth=FirebaseAuth.getInstance();

        reference= FirebaseDatabase.getInstance().getReference("Users");


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finishAffinity();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
                dialog.setContentView(R.layout.loading_bg);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                String sEmail=email.getText().toString();
                String sPass=pass.getText().toString();

                if(sEmail.isEmpty() || sPass.isEmpty())
                {
                    dialog.dismiss();
                    DynamicToast.makeWarning(LoginActivity.this,"Fields cannot be empty!",2000).show();
                }
                else
                {
                    loginUser(sEmail,sPass);
                }

            }
        });


    }


    private void loginUser(String sEmail, String sPass)
    {
        firebaseAuth.signInWithEmailAndPassword(sEmail, sPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    if(firebaseAuth.getCurrentUser()!=null)
                    {
                        dialog.dismiss();
                        DynamicToast.make(LoginActivity.this, "Login Successful!", getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();
                        startActivity(new Intent(LoginActivity.this,NotesActivity.class));
                        finishAffinity();
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog.dismiss();
                DynamicToast.makeError(LoginActivity.this,e.getMessage(),2000).show();
            }
        });


    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
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