package com.sbdev.notepad_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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

public class AddNotesActivity extends AppCompatActivity {

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

    private RelativeLayout addRelative,rootLayout;

    private TextView topTextAdd;

    private Uri multipleURI;

    private StorageReference storageReference;

    private ArrayList<Images_Model> arrayList;

    private Images_Adapter adapter;

    private static ArrayList<Uri> uriArrayList;

    private static Uri myURI[];

    private String SHARED_PREFS="SHARED_PREFS";

    private SharedPreferences sp;

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
        rootLayout=findViewById(R.id.addNotesRootLayout);

        tick=findViewById(R.id.addNotesTickButton);
        addImgIcon=findViewById(R.id.addImage);
        addVoiceIcon=findViewById(R.id.addVoice);



        dialog=new ProgressDialog(AddNotesActivity.this);

        arrayList=new ArrayList<>();
        uriArrayList=new ArrayList<>();

        adapter=new Images_Adapter(arrayList,AddNotesActivity.this,uriArrayList);
        recyclerView.setAdapter(adapter);

        notesAuth=FirebaseAuth.getInstance();
        notesDatabase= FirebaseDatabase.getInstance().getReference("Notes");

        storageReference=FirebaseStorage.getInstance().getReference("Images");

        sp=getApplicationContext().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);


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

            simpleDateFormat=new SimpleDateFormat("dd MMM yyyy hh:mm a");
            date=new Date();

            dialog.show();
            dialog.setContentView(R.layout.loading_bg);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            String textTitle=title.getText().toString().trim();
            String textDes=des.getText().toString().trim();
            String strTime=simpleDateFormat.format(date);

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootLayout.getWindowToken(), 0);


            myURI=new Uri[uriArrayList.size()];

            Log.d("myURI SIZE", String.valueOf(myURI.length));

            for(int i=0;i<uriArrayList.size();i++)
            {
                myURI[i]=uriArrayList.get(i);
            }

            for(int i=0;i<arrayList.size();i++)
            {
                Log.d("myUri", String.valueOf(myURI[i]));
            }


            if(textTitle.isEmpty() || textDes.isEmpty())
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


        boolean switchState=sp.getBoolean("state",false);
        if(switchState)
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


        if(requestCode == code && resultCode == RESULT_OK && data.getData()!=null)
        {

            ClipData cd=data.getClipData();

            if(cd==null)
            {
                Uri uri=data.getData();
                uriArrayList.add(uri);
                arrayList.add(new Images_Model(uri.toString()));
            }
            else
            {

                for(int i=0;i<data.getClipData().getItemCount();i++)
                {

                    multipleURI=data.getClipData().getItemAt(i).getUri();

                    uriArrayList.add(multipleURI);

                    arrayList.add(new Images_Model(multipleURI.toString()));

                }

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

        DatabaseReference dRefPushed=notesDatabase.child(notesAuth.getCurrentUser().getUid()).push();
        String firebaseKEY=dRefPushed.getKey();

        Log.d("GET KEY",firebaseKEY);

        notesDatabase.child(notesAuth.getCurrentUser().getUid()).child(firebaseKEY).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {

                    if(myURI.length!=0)
                    {

                        if(myURI.length==1)
                        {
                            Snackbar.make(rootLayout,"1 item is uploading...", Snackbar.LENGTH_INDEFINITE).show();
                        }
                        else
                        {
                            Snackbar.make(rootLayout,myURI.length+" items are uploading...", Snackbar.LENGTH_INDEFINITE).show();
                        }

                        for(int i=0;i<myURI.length;i++)
                        {

                            DatabaseReference pushRef=FirebaseDatabase.getInstance().getReference("Notes");

                            String imageKEY=pushRef.push().getKey();

                            int temp = i;

                            storageReference.child(notesAuth.getCurrentUser().getUid())
                                    .child(firebaseKEY)
                                    .child(imageKEY)
                                    .putFile(myURI[i]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    storageReference.child(notesAuth.getCurrentUser().getUid())
                                            .child(firebaseKEY)
                                            .child(imageKEY)
                                            .getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {

                                                    pushRef.child(notesAuth.getCurrentUser().getUid())
                                                            .child(firebaseKEY)
                                                            .child("Images")
                                                            .child(imageKEY)
                                                            .child("url")
                                                            .setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {

                                                            if(myURI.length==1)
                                                            {

                                                                //Snackbar.make(rootLayout,"1 item is uploaded!", Snackbar.LENGTH_SHORT).show();
                                                                DynamicToast.make(AddNotesActivity.this, "Note successfully saved!!", getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                                                        getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();
                                                                dialog.dismiss();

                                                                startActivity(new Intent(AddNotesActivity.this,NotesActivity.class));
                                                                finishAffinity();


                                                            }
                                                            else
                                                            {
                                                                if((temp+1)==myURI.length)
                                                                {
                                                                    //Snackbar.make(rootLayout,(temp+1)+" items are uploaded!", Snackbar.LENGTH_SHORT).show();
                                                                    DynamicToast.make(AddNotesActivity.this, "Note successfully saved!!", getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                                                            getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();
                                                                    dialog.dismiss();

                                                                    startActivity(new Intent(AddNotesActivity.this,NotesActivity.class));
                                                                    finishAffinity();

                                                                }
                                                            }

                                                        }
                                                    });



                                                }
                                            });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    dialog.dismiss();
                                    DynamicToast.makeError(AddNotesActivity.this,e.getMessage(),2000).show();
                                }
                            });

                        }

                    }
                    else
                    {
                        DynamicToast.make(AddNotesActivity.this, "Note successfully saved!!", getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();
                        dialog.dismiss();

                        startActivity(new Intent(AddNotesActivity.this,NotesActivity.class));
                        finishAffinity();
                    }
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