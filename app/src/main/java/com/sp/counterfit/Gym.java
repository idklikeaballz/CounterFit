package com.sp.counterfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class Gym extends AppCompatActivity implements OnMapReadyCallback, GymAdapter.OnGymItemClickListener {
    // Rest of your class...
    private BottomNavigationView bottomNavigationView;
    private List<GymItem> gymItems;
    private GoogleMap mMap;
    @Override
    public void onGymItemClick(GymItem gymItem) {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to view the location on Google Maps?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Uri gmmIntentUri = Uri.parse("geo:" + gymItem.getLatitude() + "," + gymItem.getLongitude() + "?q=" + gymItem.getLatitude() + "," + gymItem.getLongitude() + "(" + gymItem.getName() + ")");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GymActivity", "onCreate");

        setContentView(R.layout.gym);
        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        gymItems = new ArrayList<>();
        gymItems.add(new GymItem("Jurong East ActiveSG", "21 Jurong East Street 31, Singapore 609517", R.drawable.gym1, 1.346786, 103.729259));
        gymItems.add(new GymItem("ActiveSG Hockey Village", "288 Boon Lay Pl, Singapore 649883", R.drawable.gym2, 1.347498, 103.711198));
        gymItems.add(new GymItem("Toa Payoh West Community Centre", "200 Lor 2 Toa Payoh, Singapore 319642", R.drawable.gym3, 1.335221, 103.845106));
        gymItems.add(new GymItem(" Serangoon Central", "264 Serangoon Central, Singapore 550264", R.drawable.gym4, 1.352467, 103.872103));
        gymItems.add(new GymItem("Fernvale Square", "51A Sengkang W Ave, Singapore 797384", R.drawable.gym5, 1.391422, 103.873116));
        gymItems.add(new GymItem("Enabling Village", "100 Redhill Cl, Singapore 158901", R.drawable.gym6, 1.286959, 103.814601));
        gymItems.add(new GymItem("Bishan ActiveSG Gym", "5 Bishan St 14, Singapore 579783", R.drawable.gym7, 1.355283, 103.850922));
        gymItems.add(new GymItem("Bukit Canberra ActiveSG Gym", "Canberra Rd", R.drawable.gym8, 1.447016, 103.822852));
        gymItems.add(new GymItem("Bukit Batok ActiveSG Gym", "2 Bukit Batok Street 22, Singapore 659581", R.drawable.gym9, 1.344296, 103.748153));
        gymItems.add(new GymItem("Bukit Gombak ActiveSG Gym", "810 Bukit Batok West Ave. 5, Singapore 659088", R.drawable.gym10, 1.359812, 103.752259));
        gymItems.add(new GymItem("Choa Chu Kang ActiveSG Gym", "1 Choa Chu Kang Street 53, Singapore 689236", R.drawable.gym11, 1.390778, 103.748046));
        gymItems.add(new GymItem("Clementi ActiveSG Gym", "518 Clementi Ave 3, Clementi Sports Hall, Sports Complex, Singapore 129907", R.drawable.gym12, 1.311015, 103.764861));
        gymItems.add(new GymItem("Delta ActiveSG Gym", "Bukit Merah", R.drawable.gym13, 1.290525, 103.820588));
        gymItems.add(new GymItem("Heartbeat", "11 Bedok North Street 1, #04-02, Singapore 469662", R.drawable.gym14, 1.326985, 103.931621));
        gymItems.add(new GymItem("Hougang ActiveSG Gym", "Hougang Ave 4", R.drawable.gym15, 1.370449, 103.888075));
        gymItems.add(new GymItem("Jalan Besar ActiveSG Gym", "115 Tyrwhitt Rd, Singapore 207545", R.drawable.gym16, 1.310956, 103.859974));
        gymItems.add(new GymItem("Kallang Basin ActiveSG Gym", "23 Geylang Bahru Ln, Singapore 339628", R.drawable.gym17, 1.322628, 103.872501));
        gymItems.add(new GymItem("Jurong West ActiveSG Gym", "20 Jurong West Street 93, Singapore 648965", R.drawable.gym18, 1.338418, 103.693769));
        gymItems.add(new GymItem("Jurong Lake Gardens ActiveSG Gym", "30 Yuan Ching Rd, Singapore 618664", R.drawable.gym19, 1.330505, 103.725644));
        gymItems.add(new GymItem("Katong ActiveSG Gym", "111 Wilkinson Rd, Katong Swim Complex, Singapore 436752", R.drawable.gym20, 1.302045, 103.886179));
        gymItems.add(new GymItem("Pasir Ris ActiveSG Gym", "120 Pasir Ris Central, Singapore 519640", R.drawable.gym21, 1.374059, 103.951850));
        gymItems.add(new GymItem("Sengkang ActiveSG Gym", "Sengkang", R.drawable.gym22, 1.395466, 103.885777));
        gymItems.add(new GymItem("Senja-Cashew ActiveSG Gym", "101 Bukit Panjang Rd, Singapore 670534", R.drawable.gym23, 1.381638, 103.764676));
        gymItems.add(new GymItem("Tampines ActiveSG Gym", "495 Tampines Ave 5, Singapore 529649", R.drawable.gym24, 1.353547, 103.940381));
        gymItems.add(new GymItem("Woodlands ActiveSG Gym", "3 Woodlands Street 13, Singapore 738600", R.drawable.gym25, 1.434497, 103.779364));
        gymItems.add(new GymItem("Yio Chu Kang ActiveSG Gym", "200 Ang Mo Kio Ave 9, Na, Singapore 569770", R.drawable.gym26, 1.382272, 103.845768));






        bottomNavigationView = findViewById(R.id.bottom_navigation_gym);
        setupBottomNavigationView();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_gym);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));
        }

        GymAdapter adapter = new GymAdapter(gymItems, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_gym);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("GymActivity", "Map is ready");

        mMap = googleMap;


        for (GymItem item : gymItems) {
            // Log or place breakpoint here to check values
            Log.d("Gym", "Adding marker for: " + item.getName());
            LatLng gymLocation = new LatLng(item.getLatitude(), item.getLongitude());
            mMap.addMarker(new MarkerOptions().position(gymLocation).title(item.getName()));
        }

        if (!gymItems.isEmpty()) {
            LatLng firstGymLocation = new LatLng(gymItems.get(0).getLatitude(), gymItems.get(0).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstGymLocation, 10));
        }
    }


    private void setupBottomNavigationView() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.bot_home) {
                        Intent intent = new Intent(Gym.this, Main.class);
                        item.setCheckable(true);
                        startActivity(intent);
                        return true;
                    } else if (id==R.id.bot_gym) {
                        item.setCheckable(true);
                    }else if (id == R.id.bot_food) {
                        Intent intent = new Intent(Gym.this, Food.class);
                        item.setCheckable(true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });
            unselectBottomNavigationViewItems();


        } else {
            Log.e("AboutActivity", "BottomNavigationView not found in the layout.");
        }
    }
    private void unselectBottomNavigationViewItems() {
        // We can loop through all menu items and uncheck them
        int size = bottomNavigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            bottomNavigationView.getMenu().getItem(i).setCheckable(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GymActivity", "onResume");

        unselectBottomNavigationViewItems();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("GymActivity", "onDestroy");

        if (mMap != null) {
            mMap.clear();

            mMap = null;
        }
    }
}
