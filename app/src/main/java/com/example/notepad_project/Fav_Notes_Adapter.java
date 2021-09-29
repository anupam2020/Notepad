package com.example.notepad_project;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;
import java.util.HashMap;

public class Fav_Notes_Adapter extends RecyclerView.Adapter<Fav_Notes_Adapter.NotesViewHolder> {

    ArrayList<Fav_Notes_Model> arrayList;
    Context context;
    String uid="";

    String favdes="";

    boolean isFav;

    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notes");

    DatabaseReference favorites= FirebaseDatabase.getInstance().getReference("Favorites");

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
                            holder.favstar.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
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
//                            else
//                            {
//                                HashMap map=new HashMap();
//                                map.put("Fav",isFav);
//
//                                favorites.child(firebaseAuth.getCurrentUser().getUid()).child("FavList")
//                                        .child(key).setValue(map);
//                                isFav=false;
//
//                                holder.favstar.setImageResource(R.drawable.ic_baseline_bookmark_yellow_24);
//
//                                DynamicToast.make(context, "Added to Favorites!", context.getResources().getDrawable(R.drawable.ic_baseline_bookmark_added_24),
//                                        context.getResources().getColor(R.color.yellow), context.getResources().getColor(R.color.black), 2000).show();
//                            }
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


                            case R.id.shareNote:


                                favorites.child(firebaseAuth.getCurrentUser().getUid()).child("FavList").child(key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                        {

                                            Log.d("dataSnapshot",dataSnapshot.getKey());
                                            if(dataSnapshot.getKey().equals("Description"))
                                            {
                                                favdes=dataSnapshot.getValue().toString();
                                                Log.d("title",favdes);
                                            }
                                            break;

                                        }

                                        Intent shareIntent = new Intent(Intent.ACTION_SEND);

                                        String shareBody = favdes;

                                        shareIntent.setType("text/plain");

                                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

                                        context.startActivity(Intent.createChooser(shareIntent, "Share via"));

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                break;

                            case R.id.deleteNote:

                                favorites.child(firebaseAuth.getCurrentUser().getUid()).child("FavList").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful())
                                        {

                                            reference.child(firebaseAuth.getCurrentUser().getUid()).child(key)
                                                    .removeValue();


                                            DynamicToast.make(context, "Note successfully deleted!", context.getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
                                                    context.getResources().getColor(R.color.white), context.getResources().getColor(R.color.black), 2000).show();

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        DynamicToast.makeError(context,e.getMessage(),2000).show();
                                    }
                                });

                                notifyDataSetChanged();

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

        TextView favtitle;
        ImageView favmore;
        ImageView favstar;
        TextView favtime;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);

            favtitle=itemView.findViewById(R.id.favItemTitle);
            favmore=itemView.findViewById(R.id.favItemMore);
            favstar=itemView.findViewById(R.id.favItemStar);
            favtime=itemView.findViewById(R.id.favTextDateTime);
        }
    }
}
