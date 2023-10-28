package com.licenta.monitorizareavioane.mainpage.map;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.licenta.monitorizareavioane.R;
import com.licenta.monitorizareavioane.liveflights.Flight;
import com.licenta.monitorizareavioane.login.LogInActivity;
import com.licenta.monitorizareavioane.mainpage.CustomWindowAdapter;
import com.licenta.monitorizareavioane.mainpage.MainActivity;
import com.licenta.monitorizareavioane.mainpage.userprofile.Points;
import com.licenta.monitorizareavioane.planepics.PlanespotterRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;

public class MapsFragment extends Fragment {

    private Flight[] flights = new Flight[1];
    private Bitmap smallMarker;
    private Bitmap selectedMarker;
    private CameraPosition mCameraPosition;
    boolean mIsMoving = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private Map<Marker, Integer> markerMap = new HashMap();
    private String searchedIcao24;
    private PlanespotterRepository planespotterRepository;
    private MoreInfoFragment moreInfoFragment;
    private HashMap<String, LatLng> airports = new HashMap<>();

    private HashMap<String, String> regions = new HashMap<String, String>();
    private MainActivity mainActivity;
    private boolean firstFragmentLoad;
    private int randomIndex;
    private int selectedColor;
    private BitmapDrawable genericBitmapDrawable;
    private Paint paint = new Paint();

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Calendar calendar = Calendar.getInstance();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            MapStyleOptions style;
            if (currentHour >= 18 || currentHour < 6) {
                style = MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.nightmode_json);
                googleMap.setMapStyle(style);
            }

            googleMap.setMinZoomPreference(5);

            if (firstFragmentLoad) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.setMyLocationEnabled(true);
                                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(current)
                                        .zoom(8.0f)
                                        .build();
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }
                        }
                    });
                    mFusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Could not get location", Toast.LENGTH_LONG);
                        }
                    });
                } else {
                    LatLng current = new LatLng(44.44824923710023, 26.097792769313934);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(current)
                            .zoom(8.0f)
                            .build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                setListeners(googleMap);

                firstFragmentLoad = false;
            }

            if (flights != null) {
                if (flights.length > 1) {
                    addMarker(googleMap);
                }
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        initAirportInfo();
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        this.selectedColor = getSelectedColor();
        // resize plane icon
        if (selectedColor != 0) {
            genericBitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.whiteplane);
        } else {
            genericBitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.plane3);
        }
        smallMarker = Bitmap.createScaledBitmap(genericBitmapDrawable.getBitmap(), 70, 70, false);
    }

    private void setListeners(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setInfoWindowAdapter(new CustomWindowAdapter(getContext()));
        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                mIsMoving = true;
                if (!firstFragmentLoad) {
                    mCameraPosition = googleMap.getCameraPosition();
                }
            }
        });


        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (!mIsMoving && !firstFragmentLoad && mCameraPosition != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
                }
                mIsMoving = false;
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                mainActivity.setFiltered(true);
                mainActivity.setSearchedPlane(flights[markerMap.get(marker)].getIcao24());
                mainActivity.refreshUI();

                return false;
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                planespotterRepository = new PlanespotterRepository();
                Flight flight = flights[markerMap.get(marker)];
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        if (!flight.getIcao24().equals("licenta")) {
                            planespotterRepository.getURL(flight.getIcao24());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if (!flight.getIcao24().equals("licenta")) {
                            if (planespotterRepository.getPictureURL() != null) {
                                flight.setPictureURL(planespotterRepository.getPictureURL());
                            }
                        }
                        moreInfoFragment = new MoreInfoFragment();
                        moreInfoFragment.setFlight(flight);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.containerMainPage, moreInfoFragment)
                                .addToBackStack("map")
                                .commit();


                    }
                }.execute();
            }
        });
    }

    private void addMarker(GoogleMap googleMap) {
        googleMap.clear();
        markerMap.clear();
        markerMap = new HashMap<>();
        selectedColor = getSelectedColor();
        if (selectedColor != 0) {
            applySelectedColor();
        }
        for (int i = 0; i < flights.length; i++) {
            if (flights[i].getLongitude() != null && flights[i].getLatitude() != null) {
                Bitmap rotatedMarker;
                rotatedMarker = setMarkerColor(i);
                Marker currentMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(flights[i].getLatitude(), flights[i].getLongitude()))
                        .title(flights[i].getIcao24() + "/" + flights[i].getCallsign())
                        .icon(BitmapDescriptorFactory.fromBitmap(rotatedMarker))
                        .snippet(flights[i].getEstDepartureAirport() + "," + flights[i].getEstArrivalAirport()));
                markerMap.put(currentMarker, i);
            }
        }
        if (!checkIfUserClickedToday()) {
            addRandomMarker();
        }
        if (searchedIcao24 != null) {
            clickEffect(googleMap);
        }

    }

    private Bitmap setMarkerColor(int i) {
        Bitmap rotatedMarker = createRotatedMarker(i);
        Canvas canvas = new Canvas(rotatedMarker);
        canvas.drawBitmap(rotatedMarker, 0, 0, paint);
        return rotatedMarker;
    }

    private void applySelectedColor() {
        ColorFilter colorFilter = new LightingColorFilter(selectedColor, Color.BLACK);
        paint.setColorFilter(colorFilter);
    }

    private void addRandomMarker() {
        randomIndex = (int) (Math.random() * markerMap.size());
        Marker randomMarker = markerMap.keySet().toArray(new Marker[markerMap.size()])[randomIndex];
        System.out.println("Plane position is " + randomMarker.getPosition());
        Bitmap rotatedMarker = createSpecialMarker(randomIndex, R.drawable.plane_20p);
        randomMarker.setIcon(BitmapDescriptorFactory.fromBitmap(rotatedMarker));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bitmap rotatedMarker = createSpecialMarker(randomIndex, R.drawable.plane3);
                randomMarker.setIcon(BitmapDescriptorFactory.fromBitmap(rotatedMarker));
            }
        }, 300000);
    }

    private boolean checkIfUserClickedToday() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        long lastClickTimeMilis = sharedPreferences.getLong("lastClickTime", 0);
        boolean clickedOnce = true;
        if (!DateUtils.isToday(lastClickTimeMilis)) {
            clickedOnce = false;
        }
        setLastClickedToday(sharedPreferences);

        return clickedOnce;
    }

    private void setLastClickedToday(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.apply();
    }


    public void setFlights(Flight[] flights) {
        this.flights = flights;
    }


    public void refreshUI() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void clickEffect(GoogleMap googleMap) {
        if (searchedIcao24 != null) {
            for (Map.Entry<Marker, Integer> marker : markerMap.entrySet()) {
                String[] title = marker.getKey().getTitle().split("/");
                if (title[0].equals(searchedIcao24)) {
                    Integer position = markerMap.get(marker.getKey());
                    if (position != null) {
                        if (randomIndex == position) {
                            updatePoints(20);
                        } else {
                            updatePoints(5);
                        }
                        setSelectedMarkerColor(marker, position);
                        setFlightRegion(position);
                        if (flights[position].getWaypoints() != null) {
                            drawTracks(position, googleMap);
                        }
                        marker.getKey().showInfoWindow();
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(marker.getKey().getPosition())
                                .zoom(8.0f)
                                .build();

                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        break;
                    }
                }
            }
        }

    }

    private Points clickReward(Realm realm, User user, int noPoints) {
        Points userPoints = null;
        userPoints = realm.where(Points.class)
                .equalTo("_id", user.getId())
                .findFirst();

        if (userPoints != null) {
            int currentPoints = userPoints.getPoints() + noPoints;
            userPoints = new Points(user.getId(), currentPoints);
        } else {
            userPoints = new Points(user.getId(), noPoints);
        }
        return userPoints;
    }

    private void updatePoints(int noPoints) {
        App app = new App(new AppConfiguration.Builder("applicenta-rkblt")
                .build());
        User user = app.currentUser();
        SyncConfiguration config = LogInActivity.getConfig();
        Realm realm = Realm.getInstance(config);
        Points points = clickReward(realm, user, noPoints);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(points);
            }
        });
        showClickReward(noPoints);
    }

    private void showClickReward(int noPoints) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);
        TextView tvNoPoints = layout.findViewById(R.id.customToastTvNoPoints);
        tvNoPoints.setText("+" + noPoints);
        Toast toast = new Toast(getActivity());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void setFlightRegion(Integer position) {
        String estDepartureAirport = flights[position].getEstDepartureAirport();
        if (regions.containsKey(estDepartureAirport)) {
            String region = regions.get(estDepartureAirport);
            if (region != null) {
                flights[position].setEstDepatureRegion(region);
            }
        }
        String estArrivalAirport = flights[position].getEstArrivalAirport();
        if (regions.containsKey(estArrivalAirport)) {
            String region = regions.get(estArrivalAirport);
            if (region != null) {
                flights[position].setEstArrivalRegion(region);
            }
        }
    }

    private void setSelectedMarkerColor(Map.Entry<Marker, Integer> marker, Integer position) {
        Bitmap rotatedMarker = createSpecialMarker(position, R.drawable.selectedplane2);
        marker.getKey().setIcon(BitmapDescriptorFactory.fromBitmap(rotatedMarker));
    }

    private Bitmap createSpecialMarker(Integer position, int resource) {
        Bitmap rotatedMarker;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(resource);
        Bitmap b = bitmapdraw.getBitmap();
        selectedMarker = Bitmap.createScaledBitmap(b, 70, 70, false);
        Matrix matrix = new Matrix();
        if (flights[position].getTrueTrack() != null) {
            matrix.postRotate(flights[position].getTrueTrack());
            rotatedMarker = Bitmap.createBitmap(selectedMarker, 0, 0, selectedMarker.getWidth(), selectedMarker.getHeight(), matrix, true);
        } else {
            rotatedMarker = Bitmap.createBitmap(selectedMarker, 0, 0, selectedMarker.getWidth(), selectedMarker.getHeight());
        }
        return rotatedMarker;
    }

    private Bitmap createRotatedMarker(Integer position) {
        Bitmap rotatedMarker;
        Matrix matrix = new Matrix();
        if (flights[position].getTrueTrack() != null) {
            matrix.postRotate(flights[position].getTrueTrack());
            rotatedMarker = Bitmap.createBitmap(smallMarker, 0, 0, smallMarker.getWidth(), smallMarker.getHeight(), matrix, true);
        } else {
            rotatedMarker = Bitmap.createBitmap(smallMarker, 0, 0, smallMarker.getWidth(), smallMarker.getHeight());
        }
        return rotatedMarker;
    }

    private void drawTracks(int position, GoogleMap googleMap) {
        ArrayList<LatLng> waypoints = flights[position].getWaypoints();
        waypoints.add(new LatLng(flights[position].getLatitude(), flights[position].getLongitude()));
        if (waypoints != null) {
            LatLng[] array = new LatLng[waypoints.size()];
            waypoints.toArray(array);
            googleMap.addPolyline(new PolylineOptions()
                    .add(array)
                    .color(R.color.end_color));
            if (airports.containsKey(flights[position].getEstArrivalAirport())) {
                googleMap.addPolyline(new PolylineOptions().add((LatLng) waypoints
                                .get((waypoints.size() - 1)), airports
                                .get(flights[position].getEstArrivalAirport()))
                        .color(R.color.title_pink)
                        .pattern(Arrays.asList(new Dash(20), new Gap(10))));
            }
        }
    }

    public void setSearchedIcao24(String searchedIcao24) {
        this.searchedIcao24 = searchedIcao24;
    }


    private void initAirportInfo() {
        File cacheFile = new File(getContext().getCacheDir(), "airports");
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(cacheFile));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();

            String csvContents = stringBuilder.toString();
            String[] lines = csvContents.split("\n");
            for (String row : lines) {
                if (row.startsWith("country_code")) {
                    continue;
                }
                String[] parts = row.split(",");
                LatLng latLng = null;
                if (!parts[5].equals("latitude") && !parts[6].equals("longitude")) {
                    latLng = new LatLng(Double.parseDouble(parts[5]),
                            Double.parseDouble(parts[6]));
                    airports.put(parts[3], latLng);
                }

                if (!parts[1].equals("region_name")) {
                    if (parts[3] != "") {
                        String regionCountry = parts[1] + ", " + parts[0];
                        regions.put(parts[3], regionCountry);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isFirstFragmentLoad() {
        return firstFragmentLoad;
    }

    public void setFirstFragmentLoad(boolean firstFragmentLoad) {
        this.firstFragmentLoad = firstFragmentLoad;
    }

    public MoreInfoFragment getMoreInfoFragment() {
        return moreInfoFragment;
    }

    private int getSelectedColor() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int selectedColor = sharedPreferences.getInt("selectedPlaneColor", 0);
        return selectedColor;
    }
}