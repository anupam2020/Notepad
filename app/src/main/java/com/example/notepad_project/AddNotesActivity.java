package com.example.notepad_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddNotesActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    private EditText title,des;
    private ImageView tick,addImgIcon,addVoiceIcon;

    private FirebaseAuth notesAuth;
    private DatabaseReference notesDatabase;

    private ProgressDialog dialog;

    private SimpleDateFormat simpleDateFormat;
    private Date date;

    private int code=123,speech=111;

    private static int state = -1 ;

    private RecyclerView recyclerView;

    private RelativeLayout addRelative;

    private TextView topTextAdd;

    private Uri multipleURI;

    private StorageReference storageReference;

    private ArrayList<Images_Model> arrayList;

    Images_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        Log.d("State", String.valueOf(new NotesActivity().check));


        title=findViewById(R.id.addNotesTitle);
        des=findViewById(R.id.addNotesDes);
        topTextAdd=findViewById(R.id.addTopText);

        recyclerView=findViewById(R.id.addImageRecycler);

        addRelative=findViewById(R.id.addNotesRelative1);

        tick=findViewById(R.id.addNotesTickButton);
        addImgIcon=findViewById(R.id.addImage);
        addVoiceIcon=findViewById(R.id.addVoice);

        simpleDateFormat=new SimpleDateFormat("dd MMM yyyy hh:mm a");
        date=new Date();

        dialog=new ProgressDialog(AddNotesActivity.this);

        arrayList=new ArrayList<>();

        adapter=new Images_Adapter(arrayList,AddNotesActivity.this);
        recyclerView.setAdapter(adapter);

        notesAuth=FirebaseAuth.getInstance();
        notesDatabase= FirebaseDatabase.getInstance().getReference("Notes");

        storageReference=FirebaseStorage.getInstance().getReference("Images");


        addVoiceIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(state==-1)
                {
                    DynamicToast.makeWarning(AddNotesActivity.this,"Sorry!",2000).show();
                }
                else
                {
                    Intent speechIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speech to text");
                    startActivityForResult(speechIntent,speech);
                }

            }
        });

        title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                state=1;
                return false;
            }
        });

        des.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                state=0;
                return false;
            }
        });


        tick.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            dialog.show();
            dialog.setContentView(R.layout.loading_bg);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            String textTitle=title.getText().toString();
            String textDes=des.getText().toString();
            String strTime=simpleDateFormat.format(date);

            if(textTitle.isEmpty() && textDes.isEmpty())
            {
                dialog.dismiss();
                DynamicToast.makeWarning(AddNotesActivity.this,"Fields cannot be empty!",2000).show();
            }
            else
            {
                sendDataToDatabase(textTitle,textDes,strTime);
            }
        }
    });


        addImgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGallery();
            }
        });

        if(NotesActivity.check)
        {
            nightModeAdd();
        }
        else
        {
            dayModeAdd();
        }


    }


    private void openGallery()
    {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"Please select your files"),code);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == code && resultCode == RESULT_OK && data!=null)
        {

            for(int i=0;i<data.getClipData().getItemCount();i++)
            {

                multipleURI=data.getClipData().getItemAt(i).getUri();
                arrayList.add(new Images_Model(multipleURI.toString()));

                tick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!title.getText().toString().isEmpty() || !des.getText().toString().isEmpty())
                        {

                            storageReference.child(notesAuth.getCurrentUser().getUid()).child("Image_"+System.nanoTime())
                                    .putFile(multipleURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    DynamicToast.make(AddNotesActivity.this,"Upload Successful!",2000).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    DynamicToast.makeError(AddNotesActivity.this,e.getMessage(),2000).show();
                                }
                            });

                        }

                    }
                });

            }

            adapter.notifyDataSetChanged();

        }






        if(requestCode==speech && data!=null)
        {

            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(state==1)
            {
                String strTitle=title.getText().toString();
                title.setText(strTitle+" "+result.get(0));
            }
            else if(state==0)
            {
                String strDes=des.getText().toString();
                des.setText(strDes+" "+result.get(0));
            }

        }
    }


    private void sendDataToDatabase(String textTitle, String textDes, String strTime)
    {

        HashMap map=new HashMap();
        map.put("Title",textTitle);
        map.put("Description",textDes);
        map.put("Time",strTime);

        notesDatabase.child(notesAuth.getCurrentUser().getUid()).push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    dialog.dismiss();
                    DynamicToast.make(AddNotesActivity.this, "Note successfully saved!!", getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                            getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog.dismiss();
                DynamicToast.makeError(AddNotesActivity.this,e.getMessage(),2000).show();
            }
        });

    }


    private void nightModeAdd()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        addRelative.setBackgroundResource(R.drawable.black_bg_corner_radius);

        topTextAdd.setTextColor(Color.WHITE);

        addImgIcon.setImageTintList(ColorStateList.valueOf(Color.WHITE));

        addVoiceIcon.setImageTintList(ColorStateList.valueOf(Color.WHITE));

    }

    private void dayModeAdd()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.notes_blue));

        addRelative.setBackgroundResource(R.drawable.blue_bg_corner_radius);

        topTextAdd.setTextColor(Color.BLACK);

        addImgIcon.setImageTintList(ColorStateList.valueOf(Color.BLACK));

        addVoiceIcon.setImageTintList(ColorStateList.valueOf(Color.BLACK));

    }



}