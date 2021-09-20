package com.example.notepad_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class Title_Description extends AppCompatActivity {


    private TextView title,des;

    private FirebaseAuth tdAuth;
    private DatabaseReference tdRef;

    private String key;

    private ImageView edit,delete,back;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_description);


        if(new NotesActivity().check)
        {
            nightModeEdit();
        }
        else
        {
            dayModeEdit();
        }


        title=findViewById(R.id.notesTitleTD);
        des=findViewById(R.id.notesDescriptionTD);

        edit=findViewById(R.id.editNoteTD);
        delete=findViewById(R.id.deleteNoteTD);
        back=findViewById(R.id.backTD);

        tdAuth=FirebaseAuth.getInstance();
        tdRef= FirebaseDatabase.getInstance().getReference("Notes");

        key=getIntent().getStringExtra("key");

        progressDialog=new ProgressDialog(this);

        progressDialog.show();
        progressDialog.setContentView(R.layout.loading_bg);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //Log.d("KEY",key);

        //title.setPaintFlags(title.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        tdRef.child(tdAuth.getCurrentUser().getUid()).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                progressDialog.dismiss();

                String nTitle=snapshot.child("Title").getValue().toString();
                Log.d("Title",nTitle);
                title.setText(nTitle);

                String nDes=snapshot.child("Description").getValue().toString();
                Log.d("Title",nDes);
                des.setText(nDes);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                progressDialog.dismiss();
                DynamicToast.makeError(Title_Description.this,error.getMessage(),2000).show();
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Title_Description.this,EditNotes.class);
                intent.putExtra("key",key);
                startActivity(intent);
                finish();
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(Title_Description.this);

                builder.setTitle("Delete");
                builder.setMessage("Do you really want to delete this note?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        progressDialog.show();
                        progressDialog.setContentView(R.layout.loading_bg);
                        progressDialog.setCancelable(false);
                        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        tdRef.child(tdAuth.getCurrentUser().getUid()).child(key)
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                    DynamicToast.make(Title_Description.this, "Note successfully deleted!", getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                            getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();
                                    startActivity(new Intent(Title_Description.this,NotesActivity.class));
                                    finishAffinity();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                progressDialog.dismiss();
                                DynamicToast.makeError(Title_Description.this,e.getMessage(),2000).show();
                            }
                        });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });


    }

    private void dayModeEdit()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.notes_blue));

    }

    private void nightModeEdit()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

    }
}