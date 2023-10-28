package com.licenta.monitorizareavioane.mainpage.predict;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.gson.Gson;
import com.licenta.monitorizareavioane.R;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class DelayPredictorFragment extends Fragment {
    private EditText etDeparture;
    private EditText etArrival;
    private CalendarView cvFlightDate;
    private Button btnFindFLights;
    private ListView lvFlights;
    private Map<String, String> airlines = new HashMap<>();
    private float selectedDay;
    private int selectedMonth;
    private int selectedYear;
    private int selectedDayOfWeek;
    private BaseAdapter adapter;
    private List<FlightPrediction> flightPredictions = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delay_predictor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        readAirlines();
        init();
        setCvListener();
        setLvAdapter();
    }

    private void setCvListener() {
        cvFlightDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                selectedDay = dayOfMonth;
                selectedMonth = month;
                selectedYear = year;
                selectedDayOfWeek = dayOfWeek;
            }
        });
    }

    private void setLvAdapter() {
        adapter =
                new FlightListAdapter(
                        flightPredictions,
                        getActivity());

        lvFlights.setAdapter(adapter);
    }

    private void init() {
        etDeparture = getActivity().findViewById(R.id.predictorFragmentTvDeparture);
        etArrival = getActivity().findViewById(R.id.predictorFragmentTvArrival);
        cvFlightDate = getActivity().findViewById(R.id.predictorFragmentCalendarView);
        btnFindFLights = getActivity().findViewById(R.id.predictorFragmentBtnFind);
        lvFlights = getActivity().findViewById(R.id.predictorFragmentListViewFlights);
        setListener();
    }

    private void setListener() {
        btnFindFLights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flightPredictions.size() > 0) {
                    flightPredictions.clear();
                }
                Random random = new Random();
                int noFlights = random.nextInt(3) + 1;
                for (int i = 0; i < noFlights; i++) {
                    String iata = generateRandomIata(random);
                    String depTime = generateDepartureTime();
                    String arrTime = generateArrivalTime(depTime);
                    FlightPrediction flightPrediction = new FlightPrediction(etDeparture.getText().toString(), etArrival.getText().toString(),
                            iata, Float.parseFloat(depTime), Float.parseFloat(arrTime));
                    setSeletctedDate(flightPrediction);
                    float delay = predictDelay(flightPrediction);
                    flightPrediction.setPredictedDelay(delay);
                    flightPrediction.setAirline(airlines.get(flightPrediction.getIATA_Code_Operating_Airline()));
                    flightPredictions.add(flightPrediction);
                }
                System.out.println(flightPredictions);
                adapter.notifyDataSetChanged();
                setLvHeight();
            }
        });
    }

    private void setLvHeight() {
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, lvFlights);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = lvFlights.getLayoutParams();
        params.height = totalHeight + (lvFlights.getDividerHeight() * (adapter.getCount() - 1)); // Add padding between items
        lvFlights.setLayoutParams(params);
        lvFlights.requestLayout();
    }

    private void setSeletctedDate(FlightPrediction flightPrediction) {
        flightPrediction.setDayOfWeek(selectedDayOfWeek);
        flightPrediction.setDayofMonth(selectedDay);
        flightPrediction.setYear(selectedYear);
        flightPrediction.setMonth(selectedMonth);
    }

    private String generateRandomIata(Random random) {
        int iataIdx = random.nextInt(airlines.size());
        List<String> airlineHelper = new ArrayList<>(airlines.keySet());
        String iata = airlineHelper.get(iataIdx);
        return iata;
    }

    private String generateDepartureTime() {
        Random random = new Random();
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        return hour + "" + minute;
    }

    private String generateArrivalTime(String depTime) {
        Random random = new Random();
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        String arrTimeStr = hour + "" + minute;
        while (Double.parseDouble(arrTimeStr) <= Double.parseDouble(depTime) + 30) {
            hour = random.nextInt(24);
            minute = random.nextInt(60);
            arrTimeStr = hour + "" + minute;
        }
        return arrTimeStr;
    }

    private void readAirlines() {
        try {
            InputStream inputStream = getActivity().getAssets().open("airlines.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    airlines.put(parts[1], parts[0]);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private float predictDelay(FlightPrediction flightPrediction) {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(getContext()));
        }
        Python py = Python.getInstance();
        Gson gson = new Gson();
        String json = gson.toJson(flightPrediction);

        PyObject module = py.getModule("predict");
        PyObject result = module.callAttr("predict_output", json);

        return Float.parseFloat(result.toString());
    }

}