package com.sbdev.notepad_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;
import java.util.Locale;

public class Title_Description extends AppCompatActivity {


    private TextView title,des;

    private FirebaseAuth tdAuth;
    private DatabaseReference tdRef,favRef,imageRef;

    private String key;

    private ImageView edit,delete,back,speak,share;

    private ProgressDialog progressDialog;

    private RelativeLayout relativeLayout,rootRelative;

    private TextToSpeech textToSpeech;

    private ArrayList<Images_Model> arrayList;

    private Retrieved_Images_Adapter adapter;

    private RecyclerView imagesRecycler;

    private StorageReference storageReference;

    private int noOfImages=0;

    private String SHARED_PREFS="SHARED_PREFS";

    private SharedPreferences sp;

    private int count=0,imagesCount;

    private ArrayList<String> imagesList;

    private String nTitle,nDes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_description);


        title=findViewById(R.id.notesTitleTD);
        des=findViewById(R.id.notesDescriptionTD);

        edit=findViewById(R.id.editNoteTD);
        delete=findViewById(R.id.deleteNoteTD);
        back=findViewById(R.id.backTD);
        speak=findViewById(R.id.speakNoteTD);
        share=findViewById(R.id.shareNoteTD);

        relativeLayout=findViewById(R.id.relativeTD);
        rootRelative=findViewById(R.id.titleDesMainRelative);

        tdAuth=FirebaseAuth.getInstance();
        tdRef= FirebaseDatabase.getInstance().getReference("Notes");
        favRef= FirebaseDatabase.getInstance().getReference("Favorites");
        imageRef= FirebaseDatabase.getInstance().getReference();

        storageReference=FirebaseStorage.getInstance().getReference("Images");

        arrayList=new ArrayList<>();
        imagesRecycler=findViewById(R.id.recyclerViewTD);

        imagesList=new ArrayList<>();

        adapter=new Retrieved_Images_Adapter(arrayList,Title_Description.this);
        imagesRecycler.setAdapter(adapter);

        key=getIntent().getStringExtra("key");

        sp=getApplicationContext().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        progressDialog=new ProgressDialog(this);


        progressDialog.show();
        progressDialog.setContentView(R.layout.loading_bg);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        tdRef.child(tdAuth.getCurrentUser().getUid()).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                nTitle=snapshot.child("Title").getValue().toString();
                Log.d("Title",nTitle);
                title.setText(nTitle);

                nDes=snapshot.child("Description").getValue().toString();
                Log.d("Title",nDes);
                des.setText(nDes);

//                String nCount=snapshot.child("Count").getValue().toString();
//                noOfImages=Integer.parseInt(nCount);
//                Log.d("No of Images", String.valueOf(noOfImages));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                progressDialog.dismiss();
                DynamicToast.makeError(Title_Description.this,error.getMessage(),2000).show();
            }
        });

        if(tdRef.child(tdAuth.getCurrentUser().getUid())
                .child(key)
                .child("Images")!=null)
        {

            tdRef.child(tdAuth.getCurrentUser().getUid())
                .child(key)
                .child("Images")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        noOfImages= (int) snapshot.getChildrenCount();
                        Log.d("Images Count", String.valueOf(noOfImages));

                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            String imageKEY=dataSnapshot.getKey();
                            Images_Model upload=snapshot.child(imageKEY).getValue(Images_Model.class);
                            Log.d("GET URL",upload.getUrl());
                            arrayList.add(upload);
                        }

                        adapter.notifyDataSetChanged();

                        progressDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        progressDialog.dismiss();

                        Log.e("Error",error.getMessage());
                    }
                });

        }


        imagesCount=0;

        tdRef.child(tdAuth.getCurrentUser().getUid())
                .child(key)
                .child("Images")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Log.d("Snapshot Exist", String.valueOf(snapshot.exists()));

                        if(snapshot.exists())
                        {
                            int childCount= (int) snapshot.getChildrenCount();

                            imagesList.clear();

                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                tdRef.child(tdAuth.getCurrentUser().getUid())
                                        .child(key)
                                        .child("Images")
                                        .child(dataSnapshot.getKey())
                                        .child("url")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                imagesCount++;

                                                String url=String.valueOf(snapshot.getValue());
                                                if(imagesCount==childCount)
                                                {
                                                    imagesList.add(url);
                                                }
                                                else
                                                {
                                                    imagesList.add(url+"\n\n");
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Title_Description.this,EditNotes.class);
                intent.putExtra("key",key);
                startActivity(intent);
                //finish();
            }
        });


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        // Adding OnClickListener
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!title.isFocused() && !des.isFocused())
                {
                    DynamicToast.make(Title_Description.this, "Please select a Text!", getResources().getDrawable(R.drawable.ic_outline_info_24),
                            getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();
                }

                if(title.isFocused())
                {
                    int startSelection=title.getSelectionStart();
                    int endSelection=title.getSelectionEnd();

                    String strTitle=title.getText().toString();

                    String selectedText = strTitle.substring(startSelection, endSelection);

                    textToSpeech.speak(selectedText,TextToSpeech.QUEUE_FLUSH,null);
                }

                if(des.isFocused())
                {
                    int startSelection=des.getSelectionStart();
                    int endSelection=des.getSelectionEnd();

                    String strDes=des.getText().toString();

                    String selectedText = strDes.substring(startSelection, endSelection);

                    textToSpeech.speak(selectedText,TextToSpeech.QUEUE_FLUSH,null);
                }

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

                        tdRef.child(tdAuth.getCurrentUser().getUid())
                            .child(key)
                            .child("Images")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    noOfImages= (int) snapshot.getChildrenCount();
                                    Log.d("Images Count", String.valueOf(noOfImages));

                                    if(noOfImages==0)
                                    {

                                        tdRef.child(tdAuth.getCurrentUser().getUid()).child(key)
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful())
                                                {
                                                    progressDialog.dismiss();
                                                    DynamicToast.make(Title_Description.this, "Note successfully deleted!", getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                                            getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();


                                                    favRef.child(tdAuth.getCurrentUser().getUid()).child("FavList").child(key).removeValue();

                                                    startActivity(new Intent(Title_Description.this,NotesActivity.class));
                                                }
                                            }
                                        });

                                    }
                                    else
                                    {

                                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                        {
                                            String imageKEY=dataSnapshot.getKey();
                                            Images_Model upload=snapshot.child(imageKEY).getValue(Images_Model.class);

                                            StorageReference ref=FirebaseStorage.getInstance()
                                                    .getReferenceFromUrl(upload.getUrl());

                                            ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {


                                                    tdRef.child(tdAuth.getCurrentUser().getUid()).child(key)
                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if(task.isSuccessful())
                                                            {
                                                                progressDialog.dismiss();

                                                                count++;

                                                                if(snapshot.getChildrenCount()==0)
                                                                {
                                                                    DynamicToast.make(Title_Description.this, "Note successfully deleted!", getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                                                            getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();
                                                                }


                                                                favRef.child(tdAuth.getCurrentUser().getUid()).child("FavList").child(key).removeValue();

                                                                startActivity(new Intent(Title_Description.this,NotesActivity.class));
                                                            }
                                                        }
                                                    });

                                                }
                                            });

                                        }

                                    }



                                    adapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                    Log.e("Error",error.getMessage());
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


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tdRef.child(tdAuth.getCurrentUser().getUid()).child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        tdRef.child(tdAuth.getCurrentUser().getUid())
                                .child(key)
                                .child("Images")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if(snapshot.exists())
                                        {

                                            Log.d("Images List",imagesList.toString());

                                            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);

                                            String shareBody = "Title: "+nTitle
                                                    +"\n"+"Description: "+nDes
                                                    +"\n\n"+"Images Link: "+imagesList.toString();

                                            shareIntent.setType("text/plain");

                                            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                                            startActivity(Intent.createChooser(shareIntent, "Share via"));

                                        }
                                        else
                                        {

                                            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);

                                            String shareBody = "Title: "+nTitle
                                                    +"\n"+"Description: "+nDes;

                                            shareIntent.setType("text/plain");

                                            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                                            startActivity(Intent.createChooser(shareIntent, "Share via"));

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Title_Description.this,NotesActivity.class));
                finishAffinity();

            }
        });


        //adapter.notifyDataSetChanged();


        boolean switchState=sp.getBoolean("state",false);
        if(switchState)
        {
            nightModeTD();
        }
        else
        {
            dayModeTD();
        }


        rootRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title.clearFocus();
                des.clearFocus();
            }
        });


    }


    private void dayModeTD()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.notes_blue));

        relativeLayout.setBackgroundResource(R.drawable.blue_bg_corner_radius);

        back.setImageTintList(ColorStateList.valueOf(Color.BLACK));

        edit.setImageTintList(ColorStateList.valueOf(Color.BLACK));

        delete.setImageTintList(ColorStateList.valueOf(Color.BLACK));

        speak.setImageTintList(ColorStateList.valueOf(Color.BLACK));

        share.setImageTintList(ColorStateList.valueOf(Color.BLACK));

    }

    private void nightModeTD()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        relativeLayout.setBackgroundResource(R.drawable.black_bg_corner_radius);

        back.setImageTintList(ColorStateList.valueOf(Color.WHITE));

        edit.setImageTintList(ColorStateList.valueOf(Color.WHITE));

        delete.setImageTintList(ColorStateList.valueOf(Color.WHITE));

        speak.setImageTintList(ColorStateList.valueOf(Color.WHITE));

        share.setImageTintList(ColorStateList.valueOf(Color.WHITE));

    }
}