package com.example.notepad_project;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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

import com.pranavpandey.android.dynamic.toasts.DynamicToast;

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


        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(),AboutUsActivity.class));
            }
        });


        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(),TermsConditionsActivity.class));
            }
        });


        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(),PrivacyPolicyActivity.class));
            }
        });


        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchMarket();
            }
        });


        layout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    final String appPackageName = getActivity().getPackageName();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                } catch(Exception e) {
                    DynamicToast.makeError(getActivity(),e.getMessage(),2000).show();
                }

            }
        });

    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            DynamicToast.makeError(getActivity(),e.getMessage(),2000).show();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false);
    }
}