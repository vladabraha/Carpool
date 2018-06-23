package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cz.uhk.fim.brahavl1.carpoolv4.Adapter.CarManageRecyclerViewAdapter;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Car;
import cz.uhk.fim.brahavl1.carpoolv4.Model.UidEmail;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class NavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected FrameLayout frameLayout;
    private TextView textViewCurrentUser;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference myRef;
    private String userID;
    private CarManageRecyclerViewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        frameLayout = findViewById(R.id.frameLayout);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        setInformation();

    }

    private void setInformation() {

        myRef = FirebaseDatabase.getInstance().getReference("uid");

        final ArrayList<UidEmail> text = new ArrayList<>();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                text.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UidEmail uidEmail = postSnapshot.getValue(UidEmail.class);
                    text.add(uidEmail);
                }


                if (!text.isEmpty()){
                    currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    Log.d("TAG", "uzivatel je" + currentFirebaseUser.getEmail());
                    textViewCurrentUser = findViewById(R.id.textCurrentUser);
                    if (textViewCurrentUser != null){

                        if (currentFirebaseUser.getDisplayName().isEmpty()){
                            textViewCurrentUser.setText(currentFirebaseUser.getEmail());
                        }else{
                            textViewCurrentUser.setText(currentFirebaseUser.getDisplayName());
                        }
                    }



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // showText("nejde");
                // ...
            }
        };
        myRef.addValueEventListener(postListener);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage_passengers) {
            Intent intentManageProfiles = new Intent(NavigationDrawer.this, PassengerProfile.class);
            startActivity(intentManageProfiles);
        } else if (id == R.id.nav_manage_car_profiles) {
            Intent intentManageProfiles = new Intent(NavigationDrawer.this, PassengerProfile.class);
            startActivity(intentManageProfiles);
        } else if (id == R.id.nav_manage_rides) {
            Intent intent = new Intent(NavigationDrawer.this, RideOverview.class);
            startActivity(intent);
//        } else if (id == R.id.nav_manage) {

//        } else if (id == R.id.nav_share) {
//
        } else if (id == R.id.nav_logout) {
            Intent resultIntent = new Intent();
            setResult(100, resultIntent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
