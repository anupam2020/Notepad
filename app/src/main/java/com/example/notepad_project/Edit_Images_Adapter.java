package com.example.notepad_project;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Edit_Images_Adapter extends RecyclerView.Adapter<Edit_Images_Adapter.ImageViewHolder> {

    ArrayList<Images_Model> arrayList;
    ArrayList<Uri> uriArrayList,retrievedURIArrayList;
    Context context;
    String key;

    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Notes");
    StorageReference reference=FirebaseStorage.getInstance().getReference("Images");

    private FirebaseAuth fAuth=FirebaseAuth.getInstance();

    //private ProgressDialog dialog;

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

        //dialog=new ProgressDialog(context);

        holder.img.setImageURI(Uri.parse(arrayList.get(holder.getAdapterPosition()).getUrl()));

        Picasso.get()
                .load(arrayList.get(holder.getAdapterPosition()).getUrl())
                .placeholder(R.drawable.loading_green)
                .into(holder.img);

        holder.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog dialog = new ProgressDialog(context);

                dialog.show();
                dialog.setContentView(R.layout.loading_bg);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                Log.d("ArrayList size Before", String.valueOf(arrayList.size()));

                Images_Model model=arrayList.get(holder.getAdapterPosition());
                String selectedKEY=model.getKey();

                Log.d("Selected Key",selectedKEY);

                databaseReference.child(fAuth.getCurrentUser().getUid())
                        .child(key)
                        .child("Images")
                        .child(selectedKEY)
                        .removeValue();

                reference.child(fAuth.getCurrentUser().getUid())
                         .child(key)
                         .child(selectedKEY)
                         .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        dialog.dismiss();

                        DynamicToast.make(context, "Image successfully deleted!!", context.getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                context.getResources().getColor(R.color.white), context.getResources().getColor(R.color.black), 2000).show();

                    }
                });

                arrayList.remove(holder.getAdapterPosition());

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
