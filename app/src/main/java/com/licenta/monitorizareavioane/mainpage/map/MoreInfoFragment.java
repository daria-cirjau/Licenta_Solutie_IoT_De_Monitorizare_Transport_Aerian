package com.licenta.monitorizareavioane.mainpage.map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.licenta.monitorizareavioane.R;
import com.licenta.monitorizareavioane.aircraftdb.AircraftDatabase;
import com.licenta.monitorizareavioane.aircraftdb.AircraftInfo;
import com.licenta.monitorizareavioane.favoritesdb.Favorites;
import com.licenta.monitorizareavioane.flightinfo.Category;
import com.licenta.monitorizareavioane.flightinfo.PositionSource;
import com.licenta.monitorizareavioane.liveflights.Flight;
import com.licenta.monitorizareavioane.login.LogInActivity;
import com.licenta.monitorizareavioane.mainpage.flightquality.FlightQualityModel;
import com.licenta.monitorizareavioane.mainpage.flightquality.FlightQualityRepository;
import com.licenta.monitorizareavioane.mainpage.graphs.SpeedometerView;

import org.bson.types.ObjectId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;

public class MoreInfoFragment extends Fragment {

    private ImageView ivPlanePic;
    private TextView tvName;
    private TextView tvFrom;
    private TextView tvTo;
    private TextView tvFromRegion;
    private TextView tvToRegion;
    private TextView tvLat;
    private TextView tvLng;
    private TextView tvAlt;
    private TextView tvPositionSource;
    private TextView tvManufacturer;
    private TextView tvModel;
    private TextView tvCategory;
    private TextView tvRegistered;
    private Flight flight;
    private AircraftDatabase db;
    private String pictureData;
    private App app;
    private User currentUser;
    private SpeedometerView speedometerView;

    public MoreInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                fragmentManager.popBackStack("map", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentTransaction.commit();
            }
        });

        app = new App(new AppConfiguration.Builder("applicenta-rkblt")
                .build());

        currentUser = app.currentUser();

        db = Room.databaseBuilder(getActivity(), AircraftDatabase.class, "aircrafts.db").allowMainThreadQueries().build();
        initFragment();
    }

    private void initFragment() {
        initComponents();
        setLiveData();
        if (flight.getIcao24().equals("licenta")) {
            setAircraftInfo();
        }
        if (flight.getIcao24().equals("licenta")) {
            initCustomComponents();
        }
    }

    private void initCustomComponents() {
        CardView cardView = getActivity().findViewById(R.id.moreInfoFragmentCardViewCustomInfo);
        cardView.setVisibility(CardView.VISIBLE);
        TextView tvTempMean = getActivity().findViewById(R.id.moreInfoFragmentTvTempMeanValue);
        TextView tvHumMean = getActivity().findViewById(R.id.moreInfoFragmentTvHumMeanValue);
        TextView tvTempMax = getActivity().findViewById(R.id.moreInfoFragmentTvTempMaxValue);
        TextView tvTempMaxDate = getActivity().findViewById(R.id.moreInfoFragmentTvTempTimeMaxValue);
        TextView tvTempMin = getActivity().findViewById(R.id.moreInfoFragmentTvTempMinValue);
        TextView tvTempMinDate = getActivity().findViewById(R.id.moreInfoFragmentTvTempTimeMinValue);
        TextView tvHumMax = getActivity().findViewById(R.id.moreInfoFragmentTvHumMaxValue);
        TextView tvHumMaxDate = getActivity().findViewById(R.id.moreInfoFragmentTvHumTimeMaxValue);
        TextView tvHumMin = getActivity().findViewById(R.id.moreInfoFragmentTvHumMinValue);
        TextView tvHumMinDate = getActivity().findViewById(R.id.moreInfoFragmentTvHumTimeMinValue);
        TextView tvVibration = getActivity().findViewById(R.id.moreInfoFragmentTvVibrValue);
        Button btnRefresh = getActivity().findViewById(R.id.moreInfoFragmentBtnRefresh);
        btnRefresh.setVisibility(View.VISIBLE);
        FlightQualityRepository flightQualityRepository = new FlightQualityRepository();
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCustomInfoData(tvTempMean, tvHumMean, tvTempMax, tvTempMaxDate, tvTempMin, tvTempMinDate, tvHumMax, tvHumMaxDate, tvHumMin, tvHumMinDate, tvVibration, flightQualityRepository);
                    }
                });
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void setCustomInfoData(TextView tvTempMean, TextView tvHumMean, TextView tvTempMax, TextView tvTempMaxDate, TextView tvTempMin, TextView tvTempMinDate, TextView tvHumMax, TextView tvHumMaxDate, TextView tvHumMin, TextView tvHumMinDate, TextView tvVibration, FlightQualityRepository flightQualityRepository) {
        new AsyncTask<Void, Void, FlightQualityModel>() {
            @Override
            protected FlightQualityModel doInBackground(Void... voids) {
                FlightQualityModel flightQualityModel = flightQualityRepository.getFlightQualityModel(getActivity());
                return flightQualityModel;
            }

            @Override
            protected void onPostExecute(FlightQualityModel flightQualityModel) {
                if (flightQualityModel != null) {
                    if (flightQualityModel.isFinished()) {
                        tvTempMean.setText(flightQualityModel.getAvgTemp());
                        tvHumMean.setText(flightQualityModel.getAvgHum());
                        tvTempMax.setText(flightQualityModel.getMaxTemp());
                        tvTempMaxDate.setText(flightQualityModel.getMaxTempDate());
                        tvTempMin.setText(flightQualityModel.getMinTemp());
                        tvTempMinDate.setText(flightQualityModel.getMinTempDate());
                        tvHumMax.setText(flightQualityModel.getMaxHum());
                        tvHumMaxDate.setText(flightQualityModel.getMaxHumDate());
                        tvHumMin.setText(flightQualityModel.getMinHum());
                        tvHumMinDate.setText(flightQualityModel.getMinHumDate());
                        tvVibration.setText(flightQualityModel.getVibrationPercent() + "%");
                    }
                } else {
                    Toast.makeText(getContext(), "Plane is still in flight! Please refresh later, when data is loaded!", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void setAircraftInfo() {
        AircraftInfo aircraftInfo;
        if ((aircraftInfo = db.getAircraftDao().getAircraftByIcao24(flight.getIcao24())) != null) {
            if (!aircraftInfo.getManufacturer().equals("")) {
                tvManufacturer.setText(aircraftInfo.getModel());
            } else {
                tvManufacturer.setText("N/A");
            }
            if (!aircraftInfo.getModel().equals("")) {
                tvModel.setText(aircraftInfo.getManufacturer());
            } else {
                tvModel.setText("N/A");
            }
            if (!aircraftInfo.getRegistration().equals("")) {
                tvRegistered.setText(aircraftInfo.getRegistration());
            } else {
                tvRegistered.setText("N/A");
            }
        }
    }

    private void setLiveData() {
        if (flight.getPictureURL() != null) {
            if (!flight.getPictureURL().equals("")) {
                setPicture();
            }
        }

        tvName.setText(flight.getIcao24() + "/" + flight.getCallsign());
        setTvText(tvFrom, flight.getEstDepartureAirport(), "N/A");
        setTvText(tvFromRegion, flight.getEstDepatureRegion(), "N/A");
        setTvText(tvTo, flight.getEstArrivalAirport(), "N/A");
        setTvText(tvToRegion, flight.getEstArrivalRegion(), "N/A");
        setTvTextObject(tvLat, flight.getLatitude(), "N/A");
        setTvTextObject(tvLng, flight.getLongitude(), "N/A");
        setTvTextObject(tvAlt, flight.getGeoAltitude(), "N/A");

        setTvGradient();
        if (flight.getVelocity() != null) {
            speedometerView.setSpeed(flight.getVelocity() * 3.6f);
        } else {
            speedometerView.setSpeed(750f);
        }

        if (flight.getCategory() != null) {
            tvCategory.setText(flight.getCategory().name());
        } else {
            tvCategory.setText(Category.NO_INFO.name());
        }
        if (flight.getPositionSource() != null) {
            tvPositionSource.setText(flight.getPositionSource().name());
        } else {
            tvCategory.setText(PositionSource.ADS_B.name());
        }
    }

    private void setTvText(TextView textView, String text, String replacement) {
        if (text != null) {
            textView.setText(text);
        } else {
            textView.setText(replacement);
        }
    }

    private void setTvTextObject(TextView textView, Object object, String replacement) {
        if (object != null) {
            textView.setText(String.valueOf(object));
        } else {
            textView.setText(replacement);
        }
    }

    private void setTvGradient() {
        int startColor = ContextCompat.getColor(getContext(), R.color.start_color);
        int endColor = ContextCompat.getColor(getContext(), R.color.end_color);
        Shader shader = new LinearGradient(0, 0, tvTo.getPaint().getTextSize() * tvTo.getText().length(), tvTo.getPaint().getTextSize(), startColor, endColor, Shader.TileMode.CLAMP);
        tvTo.getPaint().setShader(shader);
        shader = new LinearGradient(0, 0, tvFrom.getPaint().getTextSize() * tvFrom.getText().length(), tvFrom.getPaint().getTextSize(), startColor, endColor, Shader.TileMode.CLAMP);
        tvFrom.getPaint().setShader(shader);
    }

    private void initComponents() {
        ivPlanePic = getActivity().findViewById(R.id.moreInfoFragmentIvPlane);
        tvName = getActivity().findViewById(R.id.moreInfoFragmentTvName);
        tvFrom = getActivity().findViewById(R.id.moreInfoFragmentTvFrom);
        tvTo = getActivity().findViewById(R.id.moreInfoFragmentTvTo);
        tvLat = getActivity().findViewById(R.id.moreInfoFragmentTvLatValue);
        tvLng = getActivity().findViewById(R.id.moreInfoFragmentTvLngValue);
        tvAlt = getActivity().findViewById(R.id.moreInfoFragmentTvAltValue);
        tvPositionSource = getActivity().findViewById(R.id.moreInfoFragmentTvPositionSourceValue);
        tvCategory = getActivity().findViewById(R.id.moreInfoFragmentTvCategoryValue);
        tvManufacturer = getActivity().findViewById(R.id.moreInfoFragmentTvManufacturerValue);
        tvRegistered = getActivity().findViewById(R.id.moreInfoFragmentTvRegisteredValue);
        tvModel = getActivity().findViewById(R.id.moreInfoFragmentTvModelValue);
        tvFromRegion = getActivity().findViewById(R.id.moreInfoFragmentTvFromRegion);
        tvToRegion = getActivity().findViewById(R.id.moreInfoFragmentTvToRegion);
        speedometerView = getActivity().findViewById(R.id.speedometer);
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    @SuppressLint("StaticFieldLeak")
    private void setPicture() {
        new AsyncTask<Bitmap, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Bitmap... bitmaps) {
                URL url = null;
                Bitmap image = null;
                try {
                    url = new URL(flight.getPictureURL());
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.addRequestProperty("User-Agent", "AppLicenta2");
                    if (urlConnection != null) {
                        image = BitmapFactory.decodeStream(urlConnection.getInputStream());
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return image;
            }

            @Override
            protected void onPostExecute(Bitmap image) {
                if (image != null) {
                    ivPlanePic.setImageBitmap(image);
                    pictureData = bitmapToString(image);
                }
            }
        }.execute();
    }


    public void addToFavoritesOnClick() {
        Button btnStar = getActivity().findViewById(R.id.moreInfoFragmentBtnFav);
        btnStar.setBackgroundResource(R.drawable.star_button_clicked);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        String dateString = dateFormat.format(currentDate);

        ObjectId objectId = new ObjectId();
        Favorites favorite = new Favorites(objectId.toString(), flight.getIcao24() + "/" + flight.getCallsign(), dateString, dateString,
                pictureData, flight.getEstDepartureAirport(), flight.getEstArrivalAirport(), currentUser.getId());
        insertInMongoDb(favorite);
    }

    private String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    private void insertInMongoDb(Favorites favorite) {
        SyncConfiguration config = LogInActivity.getConfig();
        Realm realm = Realm.getInstance(config);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insert(favorite);
            }
        });
    }


}