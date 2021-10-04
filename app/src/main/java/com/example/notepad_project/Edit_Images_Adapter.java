package com.example.notepad_project;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Edit_Images_Adapter extends RecyclerView.Adapter<Edit_Images_Adapter.ImageViewHolder> {

    ArrayList<Images_Model> arrayList;
    ArrayList<Uri> uriArrayList,retrievedURIArrayList;
    Context context;
    String key;

    private FirebaseAuth fAuth=FirebaseAuth.getInstance();
    private StorageReference sRef= FirebaseStorage.getInstance().getReference("Images");


    public Edit_Images_Adapter(ArrayList<Images_Model> arrayList, Context context, ArrayList<Uri> uriArrayList,ArrayList<Uri> retrievedURIArrayList,String key) {
        this.arrayList = arrayList;
        this.context = context;
        this.uriArrayList=uriArrayList;
        this.retrievedURIArrayList=retrievedURIArrayList;
        this.key=key;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_image,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        holder.img.setImageURI(Uri.parse(arrayList.get(holder.getAdapterPosition()).url));

        Picasso.get()
                .load(arrayList.get(position).url)
                .placeholder(R.drawable.loading_green)
                .into(holder.img);

        holder.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arrayList.remove(holder.getAdapterPosition());
                if(uriArrayList.size() > 0)
                {
                    uriArrayList.remove(holder.getAdapterPosition());
                }
                if(retrievedURIArrayList.size() > holder.getAdapterPosition())
                {
                    retrievedURIArrayList.remove(holder.getAdapterPosition());
                }

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
