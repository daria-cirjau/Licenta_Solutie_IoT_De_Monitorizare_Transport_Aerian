package com.licenta.monitorizareavioane.mainpage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.room.Room;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.licenta.monitorizareavioane.R;
import com.licenta.monitorizareavioane.aircraftdb.AircraftDatabase;
import com.licenta.monitorizareavioane.aircraftdb.AircraftInfo;
import com.licenta.monitorizareavioane.liveflights.Flight;
import com.licenta.monitorizareavioane.liveflights.FlightsRepository;
import com.licenta.monitorizareavioane.mainpage.AR.ARActivity;
import com.licenta.monitorizareavioane.mainpage.map.MapsFragment;
import com.licenta.monitorizareavioane.mainpage.predict.DelayPredictorFragment;
import com.licenta.monitorizareavioane.mainpage.userprofile.ProfileTabbedFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FlightsRepository flightsRepository;
    private Flight[] flightsArray;
    private Flight[] nearbyFlights;
    private final int MAX_DISTANCE = 30;
    private LatLng current;
    MapsFragment mapsFragment = new MapsFragment(); //gmaps
    private boolean isFiltered = false;
    private String searchedPlane = null;
    private DrawerLayout drawer;
    private AircraftDatabase aircraftDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        flightsRepository = new FlightsRepository();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerMainPage, mapsFragment)
                .addToBackStack("MapsFragment")
                .commit();
        mapsFragment.setFirstFragmentLoad(true);

        drawer = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        writeCacheAirports();
        initDatabases();

//
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    refreshUI();
//                }
//            }, 0, 5000);

//        if (mapsFragment.isAdded()) {
        refreshUI();
    }

    private void initDatabases() {
        aircraftDb = Room.databaseBuilder(getApplicationContext(), AircraftDatabase.class, "aircrafts.db").build();
        initAircraftDb();
    }


    @SuppressLint("StaticFieldLeak")
    private void initAircraftDb() {
        new AsyncTask<Void, Void, Void>() {
            @SuppressLint({"RestrictedApi", "StaticFieldLeak"})
            @Override
            protected Void doInBackground(Void... voids) {
                int lastProcessedEntry = aircraftDb.getAircraftDao().getCount();

                if (lastProcessedEntry >= 470060) {
                    return null;
                }

                InputStream inputStream = null;
                try {
                    inputStream = getResources().getAssets().open("aircraftDb.csv");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    reader.skip(1);
                    int currentEntry = 0;
                    while ((line = reader.readLine()) != null) {
                        if (currentEntry < lastProcessedEntry) {
                            currentEntry++;
                            continue;
                        }

                        String[] columns = line.split(",");

                        if (columns[0] != "" && columns.length >= 16) {
                            AircraftInfo aircraft = new AircraftInfo(columns[0], columns[3], columns[4], columns[15]);
                            aircraftDb.getAircraftDao().insert(aircraft);
                        }
                        System.out.println("Aircraft db init " + columns[0]);

                        currentEntry++;
                    }

                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                aircraftDb.close();
                return null;
            }
        }.execute();
    }


    @SuppressLint("StaticFieldLeak")
    public void refreshUI() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                flightsRepository.getFlights();
                if (isFiltered) {
                    if (!searchedPlane.equals("licenta")) {
                        if (flightsRepository.mergeMaps(searchedPlane).equals("N/A")) {
                            searchedPlane = "N/A";
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Aircraft not found", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (flightsRepository.getFlightsArray() != null) {
                    flightsArray = flightsRepository.getFlightsArray();
                    mapsFragment.setFlights(flightsArray);
                }
                if (isFiltered) {
                    if (!searchedPlane.equals("N/A")) {
                        mapsFragment.setSearchedIcao24(searchedPlane);
                    }
                } else mapsFragment.setSearchedIcao24(null);
                mapsFragment.refreshUI();
            }
        }.execute();
    }

    private void writeCacheAirports() {
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("airports.csv");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            inputStream.close();

            File cacheFile = new File(getCacheDir(), "airports");
            FileWriter fileWriter = new FileWriter(cacheFile);
            fileWriter.write(stringBuilder.toString());
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onSearchClick(View view) {
        EditText editText = findViewById(R.id.search_bar);
        searchedPlane = editText.getText().toString();
        if (searchedPlane != null)
            isFiltered = true;
        refreshUI();

    }


    public FlightsRepository getFlightsRepository() {
        return flightsRepository;
    }

    public void setSearchedPlane(String searchedPlane) {
        this.searchedPlane = searchedPlane;
    }

    public void setFiltered(boolean filtered) {
        isFiltered = filtered;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new ProfileTabbedFragment())
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.nav_map:
                getSupportFragmentManager().popBackStack("MapsFragment", 0);
                if (!mapsFragment.isFirstFragmentLoad()) {
                    refreshUI();
                }
                break;

            case R.id.nav_favorites:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new FavoritesFragment())
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.nav_predict:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new DelayPredictorFragment())
                        .addToBackStack(null)
                        .commit();
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void addToFavoritesOnClick(View view) {
        mapsFragment.getMoreInfoFragment().addToFavoritesOnClick();
    }

    public void openAR(View view) {
        getNearbyFlights();
    }

    // formula Haversine
    public static double getDistance(LatLng latLng1, LatLng latLng2) {
        double lat1 = latLng1.latitude;
        double lon1 = latLng1.longitude;
        double lat2 = latLng2.latitude;
        double lon2 = latLng2.longitude;

        double earthRadius = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = earthRadius * c;

        return distance;
    }

    private void filterFlights() {
        nearbyFlights = Arrays.stream(flightsArray)
                .filter(flight -> flight.getLatitude() != null && flight.getLongitude() != null)
                .filter(flight -> getDistance(new LatLng(flight.getLatitude(), flight.getLongitude()), current) <= MAX_DISTANCE)
                .toArray(Flight[]::new);
        Intent intent = new Intent(MainActivity.this, ARActivity.class);
        intent.putExtra("flights", nearbyFlights); // Pass the array of flights
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void getNearbyFlights() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (checkAndRequestPermissions()) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        current = new LatLng(location.getLatitude(), location.getLongitude());
                        filterFlights();
                    }
                }
            });
            mFusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Could not get location", Toast.LENGTH_LONG);
                }
            });
        }
    }


    public boolean checkAndRequestPermissions() {
        int internet = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        int loc = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int camera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;
    }

}