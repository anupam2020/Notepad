package com.sbdev.notepad_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
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

public class Fav_Notes_Adapter extends RecyclerView.Adapter<Fav_Notes_Adapter.NotesViewHolder> {

    ArrayList<Fav_Notes_Model> arrayList;
    Context context;
    String uid="";

    String favdes="",favtitle="";

    boolean isFav;

    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notes");

    DatabaseReference favorites= FirebaseDatabase.getInstance().getReference("Favorites");

    private int count=0,noOfImages=0;

    private ArrayList<String> imagesList=new ArrayList<>();

    public Fav_Notes_Adapter(ArrayList<Fav_Notes_Model> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fav_notes,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {

        //Log.d("Size", String.valueOf(arrayList.size()));

        String key=arrayList.get(position).getMyKey();

        favorites.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.child(firebaseAuth.getCurrentUser().getUid()).child("FavList").hasChild(key))
                        {
                            holder.favstar.setImageResource(R.drawable.ic_baseline_bookmark_yellow_24);
                        }
                        else
                        {
                            holder.favstar.setImageResource(R.drawable.ic_baseline_bookmark_border_24_black);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        holder.favtitle.setText(arrayList.get(position).title);
        holder.favtime.setText(arrayList.get(position).time);


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


        holder.favstar.setOnClickListener(new View.OnClickListener() {
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

                                holder.favstar.setImageResource(R.drawable.ic_baseline_bookmark_border_24);

                                DynamicToast.make(context, "Removed from Favorites!", context.getResources().getDrawable(R.drawable.ic_baseline_bookmark_remove_24),
                                        context.getResources().getColor(R.color.red), context.getResources().getColor(R.color.black), 2000).show();
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });



        holder.favmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count=0;

                reference.child(firebaseAuth.getCurrentUser().getUid())
                        .child(key)
                        .child("Images")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

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

                        switch (item.getItemId())
                        {

                            case R.id.editNote:

                                Intent intent=new Intent(context,EditNotes.class);
                                intent.putExtra("key",key);
                                context.startActivity(intent);
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

        TextView favtitle,favtime;
        ImageView favmore,favstar;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);

            favtitle=itemView.findViewById(R.id.favItemTitle);
            favmore=itemView.findViewById(R.id.favItemMore);
            favstar=itemView.findViewById(R.id.favItemStar);
            favtime=itemView.findViewById(R.id.favTextDateTime);
        }
    }
}
