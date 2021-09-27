package com.example.notepad_project;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FavNotesFragment extends Fragment {

    private RecyclerView recyclerView;
    private Fav_Notes_Adapter adapter;
    private ArrayList<Fav_Notes_Model> arrayList;

    private DatabaseReference favNotesRef,dRef;
    private FirebaseAuth favNotesAuth;

    private ProgressDialog dialog;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView=view.findViewById(R.id.favRecycler);

        dialog=new ProgressDialog(getActivity());

        arrayList=new ArrayList<>();
        adapter=new Fav_Notes_Adapter(arrayList,getActivity());
        recyclerView.setAdapter(adapter);

        favNotesAuth=FirebaseAuth.getInstance();
        favNotesRef= FirebaseDatabase.getInstance().getReference("Favorites");

        dRef= FirebaseDatabase.getInstance().getReference("Notes");

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
                DynamicToast.makeError(getActivity(),error.getMessage(),2000).show();

            }
        });


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fav_notes, container, false);
    }
}