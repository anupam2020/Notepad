package com.sbdev.notepad_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private TextView topText;

    private NavigationView nav;

    private View view;

    private TextView hName,hEmail;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference nameRef;

    private ImageView logOut,menu,addNotes;

    private DrawerLayout drawer;

    private String userName,userEmail;

    private SwitchCompat switcher;

    private RelativeLayout topRelative;

    private FrameLayout frameLayout;

    public static boolean check=false;

    private TextView notesCount;

    private ImageView notesCircle;

    private DatabaseReference notesRef;

    private SharedPreferences sp;

    private String SHARED_PREFS="SHARED_PREFS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);


        //getWindow().setStatusBarColor(getResources().getColor(R.color.notes_blue));

        topText=findViewById(R.id.notesTopText);

        nav=findViewById(R.id.navView);

        topRelative=findViewById(R.id.notesRelative2);

        frameLayout=findViewById(R.id.frameLayout);

        view=nav.getHeaderView(0);

        nav.getMenu().getItem(1).setActionView(R.layout.notes_count);

        View v=nav.getMenu().getItem(1).getActionView();
        notesCount=v.findViewById(R.id.notesCountText);
        notesCircle=v.findViewById(R.id.notesCountCircle);

        nav.setNavigationItemSelectedListener(NotesActivity.this);

        drawer=findViewById(R.id.drawer_layout);

        hName=view.findViewById(R.id.headerNameText);
        hEmail=view.findViewById(R.id.headerEmailText);

        menu=findViewById(R.id.notesMenu);
        logOut=findViewById(R.id.notesLogout);
        addNotes=findViewById(R.id.addNotesIcon);

        firebaseAuth=FirebaseAuth.getInstance();

        nameRef= FirebaseDatabase.getInstance().getReference("Users");
        notesRef= FirebaseDatabase.getInstance().getReference("Notes");

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new NotesFavNotesFragment()).commit();
        nav.getMenu().getItem(1).setChecked(true);
        topText.setText("Notes");

        sp=getApplicationContext().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        Menu menu1 = nav.getMenu();
        MenuItem menuItem = menu1.findItem(R.id.nav_switch);
        View actionView = MenuItemCompat.getActionView(menuItem);

        switcher = (SwitchCompat) actionView.findViewById(R.id.switcher);
        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                check=switcher.isChecked();

                SharedPreferences.Editor editor=sp.edit();
                editor.putBoolean("state", (check));
                editor.apply();

                if(check)
                {
                    nightMode();
                }
                else
                {
                    dayMode();
                }


            }
        });

        boolean switchState=sp.getBoolean("state",false);
        if(switchState)
        {
            nightMode();
            switcher.toggle();
        }
        else
        {
            dayMode();
        }


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(NotesActivity.this);
                builder.setTitle("Logout");
                builder.setMessage("Do you really want to logout?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        firebaseAuth.signOut();
                        startActivity(new Intent(NotesActivity.this,MainActivity.class));
                        finishAffinity();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                builder.show();

            }
        });


        nameRef.child(firebaseAuth.getCurrentUser().getUid()).child("Profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    if(dataSnapshot.getKey().equals("Name"))
                    {
                        userName=dataSnapshot.getValue().toString();
                        hName.setText(userName);

                    }
                    if(dataSnapshot.getKey().equals("Email"))
                    {
                        userEmail=dataSnapshot.getValue().toString();
                        hEmail.setText(userEmail);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawer.openDrawer(GravityCompat.START);
            }
        });

        addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(NotesActivity.this,AddNotesActivity.class));
            }
        });


        notesRef.child(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Log.d("Children Count", String.valueOf(snapshot.getChildrenCount()));

                        notesCount.setText(String.valueOf(snapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }


    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(NotesActivity.this);
            builder.setTitle("Warning");
            builder.setMessage("Please select any one!");

            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finishAffinity();
                }
            });

            builder.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    firebaseAuth.signOut();
                    startActivity(new Intent(NotesActivity.this,MainActivity.class));
                    finishAffinity();
                }
            });

            builder.show();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {

            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Profile()).commit();
                topText.setText("Profile");
                break;

            case R.id.notes:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new NotesFavNotesFragment()).commit();
                topText.setText("Notes");
                break;

            case R.id.updateApp:
                openAppInGooglePlay();
                break;

            case R.id.logout:
                firebaseAuth.signOut();
                startActivity(new Intent(NotesActivity.this,MainActivity.class));
                finish();
                break;

            case R.id.feedback:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Feedback()).commit();
                topText.setText("Feedback");
                break;

            case R.id.verification:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Verification()).commit();
                topText.setText("Verification");
                break;

            case R.id.more:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new More()).commit();
                topText.setText("More");
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openAppInGooglePlay() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) { // if there is no Google Play on device
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    private void nightMode()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        topRelative.setBackgroundResource(R.drawable.black_bg_corner_radius);

        menu.setImageTintList(ColorStateList.valueOf(Color.WHITE));

        logOut.setImageTintList(ColorStateList.valueOf(Color.WHITE));

        addNotes.setImageTintList(ColorStateList.valueOf(Color.WHITE));

        topText.setTextColor(Color.WHITE);

        notesCircle.setImageResource(R.drawable.ic_baseline_circle_24_black);

        //frameLayout.setBackgroundColor(Color.WHITE);


    }

    private void dayMode()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.notes_blue));

        topRelative.setBackgroundResource(R.drawable.blue_bg_corner_radius);

        menu.setImageTintList(ColorStateList.valueOf(Color.BLACK));

        logOut.setImageTintList(ColorStateList.valueOf(Color.BLACK));

        addNotes.setImageTintList(ColorStateList.valueOf(Color.BLACK));

        topText.setTextColor(Color.BLACK);

        notesCircle.setImageResource(R.drawable.ic_baseline_circle_24_blue);

        //frameLayout.setBackgroundColor(R.color.very_light_blue);

    }

}