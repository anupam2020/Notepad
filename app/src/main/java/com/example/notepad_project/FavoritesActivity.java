package com.example.notepad_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Fav_Notes_Adapter adapter;
    private ArrayList<Fav_Notes_Model> arrayList;

    private DatabaseReference favNotesRef;
    private FirebaseAuth favNotesAuth;

    private ProgressDialog dialog;

    //private ImageView singOut;

    private RelativeLayout favRelative;

    private TextView topTextFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView=findViewById(R.id.favRecycler);

        //singOut=findViewById(R.id.favNotesLogout);

        dialog=new ProgressDialog(this);

        topTextFav=findViewById(R.id.favNotesTopText);
        favRelative=findViewById(R.id.favNotesRelative1);

        arrayList=new ArrayList<>();
        adapter=new Fav_Notes_Adapter(arrayList,this);
        recyclerView.setAdapter(adapter);

        favNotesAuth=FirebaseAuth.getInstance();
        favNotesRef= FirebaseDatabase.getInstance().getReference("Favorites");

        dialog.show();
        dialog.setContentView(R.layout.loading_bg);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        favNotesRef.child(favNotesAuth.getCurrentUser().getUid()).child("FavList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                arrayList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String myKey=dataSnapshot.getKey();

                    arrayList.add(new Fav_Notes_Model(myKey,String.valueOf(dataSnapshot.child("Title").getValue()),"",String.valueOf(dataSnapshot.child("Time").getValue())));

                }

                adapter.notifyDataSetChanged();

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                dialog.dismiss();
                DynamicToast.makeError(FavoritesActivity.this,error.getMessage(),2000).show();

            }
        });

//        singOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                favNotesAuth.signOut();
//                startActivity(new Intent(FavoritesActivity.this,MainActivity.class));
//                finishAffinity();
//            }
//        });

        if(NotesActivity.check)
        {
            nightModeEdit();
        }
        else
        {
            dayModeEdit();
        }

    }

    private void nightModeEdit()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        favRelative.setBackgroundResource(R.drawable.black_bg_corner_radius);

        topTextFav.setTextColor(Color.WHITE);

        //singOut.setImageTintList(ColorStateList.valueOf(Color.WHITE));


    }

    private void dayModeEdit()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.notes_blue));

        favRelative.setBackgroundResource(R.drawable.blue_bg_corner_radius);

        topTextFav.setTextColor(Color.BLACK);

        //singOut.setImageTintList(ColorStateList.valueOf(Color.BLACK));

    }

}