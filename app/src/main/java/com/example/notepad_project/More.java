package com.example.notepad_project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class More extends Fragment {

    RelativeLayout layout1,layout2,layout3,layout4,layout5;

    TextView about,terms,policy,rate,share;

    CircleImageView imgAbout,imgTerms,imgPolicy,imgRate,imgShare;

    ImageView arrow1,arrow2,arrow3,arrow4,arrow5;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layout1=view.findViewById(R.id.moreRelative1);
        layout2=view.findViewById(R.id.moreRelative2);
        layout3=view.findViewById(R.id.moreRelative3);
        layout4=view.findViewById(R.id.moreRelative4);
        layout5=view.findViewById(R.id.moreRelative5);

        about=view.findViewById(R.id.moreAboutUs);
        terms=view.findViewById(R.id.moreTermsAndCond);
        policy=view.findViewById(R.id.morePrivacyPolicy);
        rate=view.findViewById(R.id.moreRateUs);
        share=view.findViewById(R.id.moreShareApp);

        imgAbout=view.findViewById(R.id.moreUser);
        imgTerms=view.findViewById(R.id.moreTerms);
        imgPolicy=view.findViewById(R.id.morePrivacy);
        imgRate=view.findViewById(R.id.moreRate);
        imgShare=view.findViewById(R.id.moreShare);

        arrow1=view.findViewById(R.id.arrowAbout);
        arrow2=view.findViewById(R.id.arrowTerms);
        arrow3=view.findViewById(R.id.arrowPrivacy);
        arrow4=view.findViewById(R.id.arrowRate);
        arrow5=view.findViewById(R.id.arrowShare);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false);
    }
}