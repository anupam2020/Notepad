package com.example.notepad_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        getWindow().setStatusBarColor(getResources().getColor(R.color.notes_blue));

        //dayMode();

        topText=findViewById(R.id.notesTopText);

        nav=findViewById(R.id.navView);
        menu=findViewById(R.id.notesMenu);

        topRelative=findViewById(R.id.notesRelative2);

        frameLayout=findViewById(R.id.frameLayout);

        view=nav.getHeaderView(0);

        nav.setNavigationItemSelectedListener(NotesActivity.this);

        drawer=findViewById(R.id.drawer_layout);

        hName=view.findViewById(R.id.headerNameText);
        hEmail=view.findViewById(R.id.headerEmailText);

        logOut=findViewById(R.id.notesLogout);
        addNotes=findViewById(R.id.addNotesIcon);

        firebaseAuth=FirebaseAuth.getInstance();

        nameRef= FirebaseDatabase.getInstance().getReference("Users");

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new NotesFragment()).commit();
        nav.getMenu().getItem(1).setChecked(true);
        topText.setText("Notes");



        Menu menu1 = nav.getMenu();
        MenuItem menuItem = menu1.findItem(R.id.nav_switch);
        View actionView = MenuItemCompat.getActionView(menuItem);

        switcher = (SwitchCompat) actionView.findViewById(R.id.switcher);
        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                check=switcher.isChecked();
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


        nameRef.child(firebaseAuth.getCurrentUser().getUid()).child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
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


    }


    @Override
    public void onBackPressed() {

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
                finish();
            }
        });

        builder.show();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {

            case R.id.profile:
                //getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Profile()).commit();
                topText.setText("Profile");
                break;

            case R.id.notes:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new NotesFragment()).commit();
                topText.setText("Notes");
                break;

            case R.id.emailVerification:
                //getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Verification()).commit();
                topText.setText("Verification");
                break;

            case R.id.logout:
                firebaseAuth.signOut();
                startActivity(new Intent(NotesActivity.this,MainActivity.class));
                finish();
                break;


            case R.id.feedback:
                //getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Feedback()).commit();
                topText.setText("Feedback");
                break;

            case R.id.shareApp:
                shareApp();
                break;

            case R.id.about:
                //getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new AboutUs()).commit();
                topText.setText("About Us");
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareApp()
    {
        try {
            final String appPackageName = NotesActivity.this.getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch(Exception e) {
            DynamicToast.makeError(NotesActivity.this,e.getMessage(),2000).show();
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

        frameLayout.setBackgroundColor(Color.WHITE);


    }

    private void dayMode()
    {

        getWindow().setStatusBarColor(getResources().getColor(R.color.notes_blue));

        topRelative.setBackgroundResource(R.drawable.blue_bg_corner_radius);

        menu.setImageTintList(ColorStateList.valueOf(Color.BLACK));

        logOut.setImageTintList(ColorStateList.valueOf(Color.BLACK));

        addNotes.setImageTintList(ColorStateList.valueOf(Color.BLACK));

        topText.setTextColor(Color.BLACK);

        frameLayout.setBackgroundColor(getResources().getColor(R.color.very_light_blue));

    }

}