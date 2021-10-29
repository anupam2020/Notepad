package com.sbdev.notepad_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.Arrays;
import java.util.HashMap;

public class FacebookActivity extends AppCompatActivity {

    private CallbackManager callbackManager;

    private FirebaseAuth mAuth;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);


        mAuth=FirebaseAuth.getInstance();

        reference= FirebaseDatabase.getInstance().getReference("Users");

        callbackManager = CallbackManager.Factory.create();
        //loginButton.setReadPermissions("email", "public_profile");

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        finish();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        DynamicToast.makeError(FacebookActivity.this,exception.getMessage(),2000).show();
                        finish();
                    }
                });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();


                            HashMap map=new HashMap();
                            map.put("Name",mAuth.getCurrentUser().getDisplayName());
                            map.put("Email",mAuth.getCurrentUser().getEmail());


                            reference.child(mAuth.getCurrentUser().getUid()).child("Profile")
                                    .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {
                                        DynamicToast.make(FacebookActivity.this, "Success!", getResources().getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                                getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();
                                        startActivity(new Intent(FacebookActivity.this,NotesActivity.class));
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    DynamicToast.makeError(FacebookActivity.this,e.getMessage(),2000).show();
                                    finish();
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            DynamicToast.makeError(FacebookActivity.this,task.getException().toString(),2000).show();
                            finish();
                        }
                    }
                });
    }


}