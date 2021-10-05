package com.example.notepad_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Retrieved_Images_Adapter extends RecyclerView.Adapter<Retrieved_Images_Adapter.RetrievedViewHolder> {

    ArrayList<Images_Model> arrayList;
    Context context;

    public Retrieved_Images_Adapter(ArrayList<Images_Model> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RetrievedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RetrievedViewHolder(LayoutInflater.from(context).inflate(R.layout.item_image_retrieved,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RetrievedViewHolder holder, int position) {

        Picasso.get()
                .load(arrayList.get(position).url)
                .placeholder(R.drawable.loading_green)
                .fit()
                .centerCrop()
                .into(holder.image);



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class RetrievedViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public RetrievedViewHolder(@NonNull View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.itemImageRetrieved);
        }
    }

}
