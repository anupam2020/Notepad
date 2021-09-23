package com.example.notepad_project;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class Notes_Adapter extends RecyclerView.Adapter<Notes_Adapter.NotesViewHolder> {

    ArrayList<Notes_Model> arrayList;
    Context context;
    String uid="";

    String title="",checkedState="";

    boolean isFav;

    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notes");

    DatabaseReference favorites= FirebaseDatabase.getInstance().getReference("Favorites");

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

        favorites.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.child(firebaseAuth.getCurrentUser().getUid()).child("FavList").hasChild(arrayList.get(position).getMyKey()))
                        {
                            holder.star.setImageResource(R.drawable.ic_baseline_yellow_star_24);
                        }
                        else
                        {
                            holder.star.setImageResource(R.drawable.ic_baseline_star_border_24);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.title.setText(arrayList.get(position).title);
        holder.time.setText(arrayList.get(position).time);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //DatabaseReference db=reference.child(firebaseAuth.getCurrentUser().getUid()).child(arrayList.get(position).getMyKey());

                //Log.d("Intent Key",arrayList.get(position).getMyKey());

                Intent intent=new Intent(context,Title_Description.class);
                intent.putExtra("key",arrayList.get(position).getMyKey());
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
                            if(snapshot.child(firebaseAuth.getCurrentUser().getUid()).child("FavList").hasChild(arrayList.get(position).getMyKey()))
                            {
                                favorites.child(firebaseAuth.getCurrentUser().getUid()).child("FavList")
                                        .child(arrayList.get(position).getMyKey()).removeValue();
                                isFav=false;

                                holder.star.setImageResource(R.drawable.ic_baseline_star_border_24);
                            }
                            else
                            {
                                HashMap map=new HashMap();
                                map.put("Fav",isFav);

                                favorites.child(firebaseAuth.getCurrentUser().getUid()).child("FavList")
                                        .child(arrayList.get(position).getMyKey()).setValue(map);
                                isFav=false;

                                holder.star.setImageResource(R.drawable.ic_baseline_yellow_star_24);
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

                PopupMenu popupMenu=new PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId())
                        {

                            case R.id.editNote:

                                Intent intent=new Intent(context,EditNotes.class);
                                intent.putExtra("key",arrayList.get(position).getMyKey());
                                context.startActivity(intent);
                                break;


                            case R.id.shareNote:


                                reference.child(firebaseAuth.getCurrentUser().getUid()).child(arrayList.get(position)
                                        .getMyKey()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                        {

                                            Log.d("dataSnapshot",dataSnapshot.getKey());
                                            if(dataSnapshot.getKey().equals("Description"))
                                            {
                                                title=dataSnapshot.getValue().toString();
                                                Log.d("title",title);
                                            }
                                            break;

                                        }

                                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);

                                        String shareBody = title;

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

                                reference.child(firebaseAuth.getCurrentUser().getUid()).child(arrayList.get(position)
                                        .getMyKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful())
                                        {
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
