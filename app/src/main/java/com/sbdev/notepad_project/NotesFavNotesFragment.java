package com.sbdev.notepad_project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class NotesFavNotesFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private FragmentAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout=view.findViewById(R.id.tabLayout);
        viewPager2=view.findViewById(R.id.viewPager2);

        FragmentManager fm=getChildFragmentManager();
        adapter=new FragmentAdapter(fm,getLifecycle());
        viewPager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Notes"));
        tabLayout.addTab(tabLayout.newTab().setText("Favorites"));

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_outline_sticky_note_2_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_bookmark_border_24);



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes_fav_notes, container, false);
    }
}