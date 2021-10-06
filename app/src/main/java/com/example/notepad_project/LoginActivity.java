package com.example.notepad_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout layout1,layout2,layout3;

    private EditText name,email,pass;

    private RelativeLayout relative;

    private TextView subText,signUp,forgot;

    private Button login;

    private ProgressDialog dialog;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference reference;

    private static final int RC_SIGN_IN = 1;

    private GoogleSignInClient mGoogleSignInClient;

    private static final String TAG="GOOGLEAUTH";

    private ImageView googleBtn,twitterBtn,facebookBtn;

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
        forgot=findViewById(R.id.loginForgotPassword);

        googleBtn=findViewById(R.id.loginGoogleButton);
        twitterBtn=findViewById(R.id.loginTwitterButton);
        facebookBtn=findViewById(R.id.loginFacebookButton);

        relative=findViewById(R.id.loginRelative);

        dialog=new ProgressDialog(LoginActivity.this);

        firebaseAuth=FirebaseAuth.getInstance();

        reference= FirebaseDatabase.getInstance().getReference("Users");


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("610572994439-maa64vnl1qf23823j386nrcd10a173io.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);


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

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();

            }
        });


        twitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(LoginActivity.this,TwitterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(LoginActivity.this,FacebookActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });



        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
                dialog.setContentView(R.layout.loading_bg);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                if(email.getText().toString().isEmpty())
                {
                    dialog.dismiss();
                    DynamicToast.makeWarning(LoginActivity.this,"Please enter your email!",2000).show();
                }
                else
                {
                    firebaseAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                dialog.dismiss();
                                DynamicToast.make(LoginActivity.this, "Password reset link was sent to your email!", getResources().getDrawable(R.drawable.ic_outline_mark_email_read_24),
                                        getResources().getColor(R.color.teal_200), getResources().getColor(R.color.black), 2000).show();
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

            }
        });


    }

    private void signIn() {

        //For fresh registers
        mGoogleSignInClient.signOut();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            dialog.show();
            dialog.setContentView(R.layout.loading_bg);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);

                dialog.dismiss();
                DynamicToast.makeError(LoginActivity.this,"Google sign in failed!",2000).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            HashMap map=new HashMap();
                            map.put("Name",firebaseAuth.getCurrentUser().getDisplayName());
                            map.put("Email",firebaseAuth.getCurrentUser().getEmail());

                            reference.child(firebaseAuth.getCurrentUser().getUid()).child("Profile")
                                    .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {
                                        dialog.dismiss();
                                        DynamicToast.make(LoginActivity.this, "Success!", getResources().getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                                getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();
                                        startActivity(new Intent(LoginActivity.this,NotesActivity.class));
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    dialog.dismiss();
                                    DynamicToast.makeError(LoginActivity.this,e.getMessage(),2000).show();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            dialog.dismiss();
                            DynamicToast.makeError(LoginActivity.this, (CharSequence) task.getException(),2000).show();
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