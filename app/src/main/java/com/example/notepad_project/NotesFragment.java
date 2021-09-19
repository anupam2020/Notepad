package com.example.notepad_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;

public class NotesFragment extends Fragment {

    RecyclerView recyclerView;
    public static Notes_Adapter adapter;
    ArrayList<Notes_Model> arrayList;

    FirebaseAuth showNotesAuth;
    DatabaseReference showNotesRef;

    ProgressDialog dialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView=view.findViewById(R.id.addNotesRecyclerView);

        arrayList=new ArrayList<>();
        adapter=new Notes_Adapter(arrayList,getActivity());
        recyclerView.setAdapter(adapter);

        dialog=new ProgressDialog(getActivity());

        dialog.show();
        dialog.setContentView(R.layout.loading_bg);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        showNotesAuth=FirebaseAuth.getInstance();
        showNotesRef= FirebaseDatabase.getInstance().getReference("Notes");


        showNotesRef.child(showNotesAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                arrayList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String myKey=dataSnapshot.getKey();

                    arrayList.add(new Notes_Model(myKey,String.valueOf(dataSnapshot.child("Title").getValue()),"",String.valueOf(dataSnapshot.child("Time").getValue())));

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
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }
}