package com.example.notepad_project;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class EditNotes extends AppCompatActivity {


    private EditText title,des;
    private ImageView tick,editImageNote,editVoiceIcon;

    private FirebaseAuth editAuth;
    private DatabaseReference editRef,favRef;

    private String key,nTitle,nDes,nDate;

    private ProgressDialog dialog;

    private SimpleDateFormat simpleDateFormat;
    private Date date;

    private RelativeLayout editRelative,rootLayout;

    private TextView topTextEdit;

    private int code=123,speech=111;

    private static int state = -1 ;

    private ArrayList<Images_Model> arrayList;

    private Edit_Images_Adapter adapter;

    private static ArrayList<Uri> uriArrayList,retrievedURIArrayList;

    private Uri multipleURI;

    private static Uri myURI[];

    private RecyclerView imagesRecycler;

    private StorageReference storageReference;

    private int noOfImages,count;

    private String SHARED_PREFS="SHARED_PREFS";

    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notes);

        Log.d("State", String.valueOf(new NotesActivity().check));

        key=getIntent().getStringExtra("key");

        title=findViewById(R.id.editNotesTitle);
        des=findViewById(R.id.editNotesDes);
        topTextEdit=findViewById(R.id.editNotesTopText);

        tick=findViewById(R.id.editNotesTickButton);
        editImageNote=findViewById(R.id.editImage);
        editVoiceIcon=findViewById(R.id.editVoice);

        dialog=new ProgressDialog(this);

        editRelative=findViewById(R.id.editNotesRelative1);
        rootLayout=findViewById(R.id.editNotesRootRelative);

        arrayList=new ArrayList<>();
        uriArrayList=new ArrayList<>();
        retrievedURIArrayList=new ArrayList<>();
        imagesRecycler=findViewById(R.id.editImageRecycler);

        adapter=new Edit_Images_Adapter(arrayList,EditNotes.this,uriArrayList,retrievedURIArrayList,key);
        imagesRecycler.setAdapter(adapter);

        storageReference= FirebaseStorage.getInstance().getReference("Images");

        editAuth=FirebaseAuth.getInstance();
        editRef= FirebaseDatabase.getInstance().getReference("Notes");
        favRef= FirebaseDatabase.getInstance().getReference("Favorites");

        sp=getApplicationContext().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        dialog.show();
        dialog.setContentView(R.layout.loading_bg);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        editRef.child(editAuth.getCurrentUser().getUid())
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dialog.dismiss();

                nTitle=snapshot.child("Title").getValue().toString();
                title.setText(nTitle);

                nDes=snapshot.child("Description").getValue().toString();
                des.setText(nDes);


                if(editRef.child(editAuth.getCurrentUser().getUid()).child(key).child("Images")!=null)
                {

                    editRef.child(editAuth.getCurrentUser().getUid())
                            .child(key)
                            .child("Images")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    arrayList.clear();

                                    noOfImages= (int) snapshot.getChildrenCount();

                                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                    {
                                        String imageKEY=dataSnapshot.getKey();
                                        Images_Model upload=snapshot.child(imageKEY).getValue(Images_Model.class);
                                        upload.setKey(imageKEY);
                                        arrayList.add(upload);

                                    }

                                    adapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                    Log.e("Error",error.getMessage());
                                }
                            });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                dialog.dismiss();
                DynamicToast.makeError(EditNotes.this,error.getMessage(),2000).show();

            }
        });


        editVoiceIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(state==-1)
                {
                    DynamicToast.makeWarning(EditNotes.this,"Sorry!",2000).show();
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

        editImageNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGallery();
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

                myURI=new Uri[uriArrayList.size()];

                Log.d("myURI SIZE", String.valueOf(myURI.length));

                for(int i=0;i<uriArrayList.size();i++)
                {
                    myURI[i]=uriArrayList.get(i);
                }

                Log.d("Total Images Count", String.valueOf(arrayList.size()+uriArrayList.size()));

                simpleDateFormat=new SimpleDateFormat("dd MMM yyyy hh:mm a");
                date=new Date();

                String textTitle = title.getText().toString();
                String textDes = des.getText().toString();
                String strTime = simpleDateFormat.format(date);

                dialog.show();
                dialog.setContentView(R.layout.loading_bg);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootLayout.getWindowToken(), 0);

//                myURI = new Uri[uriArrayList.size()];
//
//                Log.d("myURI SIZE", String.valueOf(myURI.length));
//
//                for (int i = 0; i < uriArrayList.size(); i++) {
//                    myURI[i] = uriArrayList.get(i);
//                }

                HashMap map = new HashMap();
                map.put("Title", textTitle);
                map.put("Description", textDes);
                map.put("Time", strTime);


                editRef.child(editAuth.getCurrentUser().getUid())
                        .child(key)
                        .updateChildren(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Log.d("Task", task.toString());

                                if(task.isSuccessful()) {

                                    favRef.child(editAuth.getCurrentUser().getUid()).child("FavList")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    if (snapshot.hasChild(key)) {
                                                        favRef.child(editAuth.getCurrentUser().getUid())
                                                                .child("FavList")
                                                                .child(key)
                                                                .updateChildren(map);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });


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

                                            storageReference.child(editAuth.getCurrentUser().getUid())
                                                    .child(key)
                                                    .child(imageKEY)
                                                    .putFile(myURI[i]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                    storageReference.child(editAuth.getCurrentUser().getUid())
                                                            .child(key)
                                                            .child(imageKEY)
                                                            .getDownloadUrl()
                                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {

                                                                    pushRef.child(editAuth.getCurrentUser().getUid())
                                                                            .child(key)
                                                                            .child("Images")
                                                                            .child(imageKEY)
                                                                            .child("url")
                                                                            .setValue(uri.toString());

                                                                }
                                                            });


                                                    if(myURI.length==1)
                                                    {
                                                        dialog.dismiss();
                                                        //Snackbar.make(rootLayout,"1 item is uploaded!", Snackbar.LENGTH_SHORT).show();
                                                        DynamicToast.make(EditNotes.this, "Note successfully saved!!", getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                                                getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();

                                                        startActivity(new Intent(EditNotes.this,NotesActivity.class));
                                                        //finishAffinity();
                                                    }
                                                    else
                                                    {
                                                        dialog.dismiss();
                                                        if((temp+1)==myURI.length)
                                                        {
                                                            //Snackbar.make(rootLayout,(temp+1)+" items are uploaded!", Snackbar.LENGTH_SHORT).show();
                                                            DynamicToast.make(EditNotes.this, "Note successfully saved!!", getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                                                    getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();

                                                            startActivity(new Intent(EditNotes.this,NotesActivity.class));
                                                            //finishAffinity();
                                                        }
                                                    }

                                                    //DynamicToast.make(AddNotesActivity.this,"Upload Successful!",2000).show();



                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    dialog.dismiss();
                                                    DynamicToast.makeError(EditNotes.this,e.getMessage(),2000).show();
                                                }
                                            });

                                        }

                                    }
                                    else
                                    {
                                        dialog.dismiss();
                                        DynamicToast.make(EditNotes.this, "Note successfully saved!!", getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                                getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();

                                        startActivity(new Intent(EditNotes.this,NotesActivity.class));
                                        //finishAffinity();
                                    }


                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog.dismiss();
                        DynamicToast.makeError(EditNotes.this, e.getMessage(), 2000).show();
                    }
                });

            }
        });


        boolean switchState=sp.getBoolean("state",false);
        if(switchState)
        {
            nightModeEdit();
        }
        else
        {
            dayModeEdit();
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

    @Override
    public void onBackPressed() {

        startActivity(new Intent(EditNotes.this,NotesActivity.class));
        finishAffinity();

    }

    private void nightModeEdit()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        editRelative.setBackgroundResource(R.drawable.black_bg_corner_radius);

        topTextEdit.setTextColor(Color.WHITE);

        editImageNote.setImageTintList(ColorStateList.valueOf(Color.WHITE));

        editVoiceIcon.setImageTintList(ColorStateList.valueOf(Color.WHITE));


    }

    private void dayModeEdit()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.notes_blue));

        editRelative.setBackgroundResource(R.drawable.blue_bg_corner_radius);

        topTextEdit.setTextColor(Color.BLACK);

        editImageNote.setImageTintList(ColorStateList.valueOf(Color.BLACK));

        editVoiceIcon.setImageTintList(ColorStateList.valueOf(Color.BLACK));

    }


}