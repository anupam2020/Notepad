package com.example.notepad_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Notes_Adapter extends RecyclerView.Adapter<Notes_Adapter.NotesViewHolder>{

    private ArrayList<Notes_Model> arrayList;
    private Context context;

    private String title="",des="";

    private boolean isFav;

    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notes");

    private DatabaseReference favorites= FirebaseDatabase.getInstance().getReference("Favorites");

    private StorageReference storageReference= FirebaseStorage.getInstance().getReference("Images");

    private int noOfImages=0;

    private int count=0;

    private ArrayList<String> imagesList=new ArrayList<>();

    public Notes_Adapter(ArrayList<Notes_Model> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notes,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {

        //Log.d("Size", String.valueOf(arrayList.size()));

        String key=arrayList.get(holder.getAdapterPosition()).getMyKey();


        favorites.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.child(firebaseAuth.getCurrentUser().getUid()).child("FavList").hasChild(key))
                        {
                            holder.star.setImageResource(R.drawable.ic_baseline_bookmark_yellow_24);
                        }
                        else
                        {
                            holder.star.setImageResource(R.drawable.ic_baseline_bookmark_border_24_black);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.title.setText(arrayList.get(holder.getAdapterPosition()).title);
        holder.time.setText(arrayList.get(holder.getAdapterPosition()).time);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //DatabaseReference db=reference.child(firebaseAuth.getCurrentUser().getUid()).child(arrayList.get(position).getMyKey());

                //Log.d("Intent Key",arrayList.get(position).getMyKey());

                Intent intent=new Intent(context,Title_Description.class);
                intent.putExtra("key",key);
                context.startActivity(intent);

            }
        });


        holder.star.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                isFav=true;

                favorites.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(isFav)
                        {
                            if(snapshot.child(firebaseAuth.getCurrentUser().getUid()).child("FavList").hasChild(key))
                            {
                                favorites.child(firebaseAuth.getCurrentUser().getUid()).child("FavList")
                                        .child(key).removeValue();
                                isFav=false;

                                holder.star.setImageResource(R.drawable.ic_baseline_bookmark_border_24);

                                DynamicToast.make(context, "Removed from Favorites!", context.getResources().getDrawable(R.drawable.ic_baseline_bookmark_remove_24),
                                        context.getResources().getColor(R.color.red), context.getResources().getColor(R.color.black), 2000).show();
                            }
                            else
                            {
                                String title=arrayList.get(holder.getAdapterPosition()).title;
                                String des=arrayList.get(holder.getAdapterPosition()).description;
                                String time=arrayList.get(holder.getAdapterPosition()).time;

                                HashMap map=new HashMap();
                                map.put("Title",title);
                                map.put("Description",des);
                                map.put("Time",time);

                                favorites.child(firebaseAuth.getCurrentUser().getUid()).child("FavList")
                                        .child(key).setValue(map);
                                isFav=false;

                                holder.star.setImageResource(R.drawable.ic_baseline_bookmark_yellow_24);

                                DynamicToast.make(context, "Added to Favorites!", context.getResources().getDrawable(R.drawable.ic_baseline_bookmark_added_24),
                                        context.getResources().getColor(R.color.yellow), context.getResources().getColor(R.color.black), 2000).show();
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });



        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count=0;

                reference.child(firebaseAuth.getCurrentUser().getUid())
                        .child(key)
                        .child("Images")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Log.d("Snapshot Exist", String.valueOf(snapshot.exists()));

                                if(snapshot.exists())
                                {
                                    int childCount= (int) snapshot.getChildrenCount();

                                    imagesList.clear();

                                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                    {
                                        reference.child(firebaseAuth.getCurrentUser().getUid())
                                                .child(key)
                                                .child("Images")
                                                .child(dataSnapshot.getKey())
                                                .child("url")
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        count++;

                                                        String url=String.valueOf(snapshot.getValue());
                                                        if(count==childCount)
                                                        {
                                                            imagesList.add(url);
                                                        }
                                                        else
                                                        {
                                                            imagesList.add(url+"\n\n");
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                    }

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                PopupMenu popupMenu=new PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.editNote:

                                Intent intent = new Intent(context, EditNotes.class);
                                intent.putExtra("key", key);
                                context.startActivity(intent);
                                break;


                            case R.id.shareNote:


                                reference.child(firebaseAuth.getCurrentUser().getUid()).child(key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                            Log.d("dataSnapshot", dataSnapshot.getKey());
                                            if (dataSnapshot.getKey().equals("Title"))
                                            {
                                                title = dataSnapshot.getValue().toString();
                                                Log.d("title", title);
                                            }
                                            if (dataSnapshot.getKey().equals("Description"))
                                            {
                                                des = dataSnapshot.getValue().toString();
                                                Log.d("title", des);
                                            }

                                        }

                                        Log.d("Images List",imagesList.toString());

                                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);

                                        String shareBody = "Title: "+title
                                                +"\n"+"Description: "+des
                                                +"\n\n"+"Images Link: "+imagesList.toString();

                                        shareIntent.setType("text/plain");

                                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                                        context.startActivity(Intent.createChooser(shareIntent, "Share via"));

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                break;

                            case R.id.deleteNote:

                                ProgressDialog dialog = new ProgressDialog(context);

                                dialog.show();
                                dialog.setContentView(R.layout.loading_bg);
                                dialog.setCancelable(false);
                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                                reference.child(firebaseAuth.getCurrentUser().getUid())
                                         .child(key)
                                         .child("Images")
                                         .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                noOfImages = (int) snapshot.getChildrenCount();
                                                Log.d("Images Count", String.valueOf(noOfImages));

                                                if(noOfImages==0)
                                                {

                                                    reference.child(firebaseAuth.getCurrentUser().getUid()).child(key)
                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {
                                                                DynamicToast.make(context, "Note successfully deleted!", context.getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                                                        context.getResources().getColor(R.color.white), context.getResources().getColor(R.color.black), 2000).show();


                                                                favorites.child(firebaseAuth.getCurrentUser().getUid()).child("FavList").child(key).removeValue();

                                                                dialog.dismiss();
                                                            }
                                                        }
                                                    });

                                                }
                                                else
                                                {

                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        String imageKEY = dataSnapshot.getKey();
                                                        Images_Model upload = snapshot.child(imageKEY).getValue(Images_Model.class);

                                                        StorageReference ref = FirebaseStorage.getInstance()
                                                                .getReferenceFromUrl(upload.getUrl());

                                                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {


                                                                reference.child(firebaseAuth.getCurrentUser().getUid()).child(key)
                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful())
                                                                        {

                                                                            if(snapshot.getChildrenCount()==0)
                                                                            {
                                                                                DynamicToast.make(context, "Note successfully deleted!", context.getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                                                                        context.getResources().getColor(R.color.white), context.getResources().getColor(R.color.black), 2000).show();
                                                                            }

                                                                            favorites.child(firebaseAuth.getCurrentUser().getUid()).child("FavList").child(key).removeValue();

                                                                            dialog.dismiss();
                                                                        }
                                                                    }
                                                                });

                                                            }
                                                        });

                                                    }

                                                }

                                                notifyDataSetChanged();

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                                Log.e("Error", error.getMessage());
                                            }
                                        });

                                break;
                        }

                        notifyDataSetChanged();

                        return true;
                    }
                });

                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView more;
        ImageView star;
        TextView time;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.itemTitle);
            more=itemView.findViewById(R.id.itemMore);
            star=itemView.findViewById(R.id.itemStar);
            time=itemView.findViewById(R.id.textDateTime);
        }
    }
}
