package com.example.notepad_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class EditNotes extends AppCompatActivity {


    private EditText title,des;
    private ImageView tick,addImgIcon,editImageNote,editVoiceIcon;

    private FirebaseAuth editAuth;
    private DatabaseReference editRef,favRef;

    private String key,nTitle,nDes,nDate;

    private ProgressDialog dialog;

    private SimpleDateFormat simpleDateFormat;
    private Date date;

    private RelativeLayout editRelative;

    private TextView topTextEdit;

    private int code=123,speech=111;

    private static int state = -1 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notes);

        Log.d("State", String.valueOf(new NotesActivity().check));


        title=findViewById(R.id.editNotesTitle);
        des=findViewById(R.id.editNotesDes);
        topTextEdit=findViewById(R.id.editNotesTopText);

        tick=findViewById(R.id.editNotesTickButton);
        editImageNote=findViewById(R.id.editImage);
        editVoiceIcon=findViewById(R.id.editVoice);

        dialog=new ProgressDialog(this);

        editRelative=findViewById(R.id.editNotesRelative1);

        simpleDateFormat=new SimpleDateFormat("dd MMM yyyy hh:mm a");
        date=new Date();

        editAuth=FirebaseAuth.getInstance();
        editRef= FirebaseDatabase.getInstance().getReference("Notes");
        favRef= FirebaseDatabase.getInstance().getReference("Favorites");

        key=getIntent().getStringExtra("key");

        dialog.show();
        dialog.setContentView(R.layout.loading_bg);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        editRef.child(editAuth.getCurrentUser().getUid()).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dialog.dismiss();

                nTitle=snapshot.child("Title").getValue().toString();
                title.setText(nTitle);

                nDes=snapshot.child("Description").getValue().toString();
                des.setText(nDes);

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

                String textTitle=title.getText().toString();
                String textDes=des.getText().toString();
                String strTime=simpleDateFormat.format(date);

                dialog.show();
                dialog.setContentView(R.layout.loading_bg);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                HashMap map=new HashMap();
                map.put("Title",textTitle);
                map.put("Description",textDes);
                map.put("Time",strTime);


                editRef.child(editAuth.getCurrentUser().getUid()).child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Log.d("Task",task.toString());

                        if(task.isSuccessful())
                        {

                            favRef.child(editAuth.getCurrentUser().getUid()).child("FavList")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            if(snapshot.hasChild(key))
                                            {
                                                favRef.child(editAuth.getCurrentUser().getUid()).child("FavList").child(key)
                                                        .updateChildren(map);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                            dialog.dismiss();
                            DynamicToast.make(EditNotes.this, "Note successfully saved!!", getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                    getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog.dismiss();
                        DynamicToast.makeError(EditNotes.this,e.getMessage(),2000).show();
                    }
                });

            }
        });

        if(NotesActivity.check)
        {
            nightModeEdit();
        }
        else
        {
            dayModeEdit();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==speech && data!=null)
        {

            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(state==1)
            {
                String strTitle=title.getText().toString();
                title.setText(strTitle+" "+result.get(0));
            }
            else
            {
                String strDes=des.getText().toString();
                des.setText(strDes+" "+result.get(0));
            }

        }
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