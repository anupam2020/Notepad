package com.example.notepad_project;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class Profile extends Fragment {


    private TextInputEditText name,email;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference reference;

    String userName,userEmail;

    private ProgressDialog dialog;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name=view.findViewById(R.id.profileEditText1);
        email=view.findViewById(R.id.profileEditText2);

        dialog=new ProgressDialog(getActivity());

        firebaseAuth=FirebaseAuth.getInstance();

        reference= FirebaseDatabase.getInstance().getReference("Users");

        dialog.show();
        dialog.setContentView(R.layout.loading_bg);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        reference.child(firebaseAuth.getCurrentUser().getUid()).child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    if(dataSnapshot.getKey().equals("Name"))
                    {
                        userName=dataSnapshot.getValue().toString();
                        name.setText(userName);

                    }
                    if(dataSnapshot.getKey().equals("Email"))
                    {
                        userEmail=dataSnapshot.getValue().toString();
                        email.setText(userEmail);
                    }
                }
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}