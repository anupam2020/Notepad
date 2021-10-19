package com.example.notepad_project;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Images_Adapter extends RecyclerView.Adapter<Images_Adapter.ImageViewHolder> {

    ArrayList<Images_Model> arrayList;
    ArrayList<Uri> uriArrayList;
    Context context;

    public Images_Adapter(ArrayList<Images_Model> arrayList, Context context,ArrayList<Uri> uriArrayList) {
        this.arrayList = arrayList;
        this.context = context;
        this.uriArrayList=uriArrayList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_image,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        holder.img.setImageURI(Uri.parse(arrayList.get(holder.getAdapterPosition()).getUrl()));

        holder.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arrayList.remove(holder.getAdapterPosition());
                uriArrayList.remove(holder.getAdapterPosition());
                notifyDataSetChanged();

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder
    {

        ImageView img;
        ImageView clear;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            img=itemView.findViewById(R.id.itemImage);
            clear=itemView.findViewById(R.id.itemClear);

        }
    }
}
