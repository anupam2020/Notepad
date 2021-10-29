package com.sbdev.notepad_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.HashMap;

public class TwitterActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);

        getWindow().setStatusBarColor(getResources().getColor(R.color.notes_blue));


        firebaseAuth=FirebaseAuth.getInstance();

        reference= FirebaseDatabase.getInstance().getReference("Users");


        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");

        // Target specific email with login hint.
        provider.addCustomParameter("lang", "en");


        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    HashMap map=new HashMap();
                                    map.put("Name",firebaseAuth.getCurrentUser().getDisplayName());
                                    map.put("Username",authResult.getAdditionalUserInfo().getUsername());
                                    map.put("Email",authResult.getUser().getEmail());


                                    reference.child(firebaseAuth.getCurrentUser().getUid()).child("Profile")
                                            .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                                DynamicToast.make(TwitterActivity.this, "Success!", getResources().getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                                        getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();
                                                startActivity(new Intent(TwitterActivity.this,NotesActivity.class));
                                                finish();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            DynamicToast.makeError(TwitterActivity.this,e.getMessage(),2000).show();
                                            finish();
                                        }
                                    });

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    DynamicToast.makeError(TwitterActivity.this,e.getMessage(),2000).show();
                                    finish();
                                }
                            });
        } else {
            firebaseAuth
                    .startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    HashMap map=new HashMap();
                                    map.put("Name",firebaseAuth.getCurrentUser().getDisplayName());
                                    map.put("Username",authResult.getAdditionalUserInfo().getUsername());
                                    map.put("Email",authResult.getUser().getEmail());


                                    reference.child(firebaseAuth.getCurrentUser().getUid()).child("Profile")
                                            .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                                DynamicToast.make(TwitterActivity.this, "Success!", getResources().getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                                        getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();
                                                startActivity(new Intent(TwitterActivity.this,NotesActivity.class));
                                                finish();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            DynamicToast.makeError(TwitterActivity.this,e.getMessage(),2000).show();
                                            finish();
                                        }
                                    });


                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    DynamicToast.makeError(TwitterActivity.this,e.getMessage(),2000).show();
                                    finish();
                                }
                            });
        }


    }
}