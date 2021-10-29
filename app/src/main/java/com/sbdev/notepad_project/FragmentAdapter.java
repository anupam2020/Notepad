package com.sbdev.notepad_project;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(FragmentManager fragmentManager,Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position)
        {
            case 0:
                return new NotesFragment();
            case 1:
                return new FavNotesFragment();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
