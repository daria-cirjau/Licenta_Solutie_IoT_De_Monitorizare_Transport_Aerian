package com.licenta.monitorizareavioane.liveflights;

import com.google.android.gms.maps.model.LatLng;
import com.licenta.monitorizareavioane.flightinfo.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


public class FlightsRepository {
    private Flight[] flightsArray = new Flight[1];
    private JSONObject flightDetails;
    private JSONObject trackedFlight;
    private List<String> defaultAirports = Arrays.asList("LROP", "KJFK", "EGLL", "LEMD", "WSSS", "RJTT", "OMDB", "EHAM", "ZBAA", "CYYZ",
            "LFPG", "VIDP", "VHHH", "SBGR", "LSZH", "MMMX", "VTBS", "KDFW", "EDDF", "RKPC", "OTHH", "CYVR", "LSGG", "OMDB", "KLAX", "LEMD", "FACT", "VTBS", "UUEE");
    private Map<String, Flight> smallFlightsMap = new HashMap<>();

    public FlightsRepository() {

    }

    private Float parseOrRet(String s) {
        if (s != null && s != "null") return Float.parseFloat(s);
        else return null;
    }

    private Integer parseOrRetInt(String s) {
        if (s != null && s != "null") return Integer.parseInt(s);
        else return null;
    }

    public void getFlights() {

        int position = 0;
        Call<ResponseBody> callAll = RetrofitClientOpensky.getInstance().getOpenskyApi().getAllFlights();
        try {
            Response<ResponseBody> responseAll = callAll.execute();
            ResponseBody flightList = responseAll.body();
            if (flightList != null) {
                String flightListString = flightList.string();
                JSONObject jsonObject = new JSONObject(flightListString);
                JSONArray statesArray = jsonObject.getJSONArray("states");
                flightsArray = new Flight[statesArray.length() + 1];
                for (int i = 0; i < statesArray.length(); i++) {
                    //TODO builder
                    flightsArray[i] = new Flight(
                            statesArray.getJSONArray(i).getString(0),
                            statesArray.getJSONArray(i).getString(1),
                            statesArray.getJSONArray(i).getString(2),
                            parseOrRetInt(statesArray.getJSONArray(i).getString(3)),
                            parseOrRetInt(statesArray.getJSONArray(i).getString(4)),
                            parseOrRet(statesArray.getJSONArray(i).getString(5)),
                            parseOrRet(statesArray.getJSONArray(i).getString(6)),
                            parseOrRet(statesArray.getJSONArray(i).getString(7)),
                            statesArray.getJSONArray(i).getBoolean(8),
                            parseOrRet(statesArray.getJSONArray(i).getString(9)),
                            parseOrRet(statesArray.getJSONArray(i).getString(10)),
                            parseOrRet(statesArray.getJSONArray(i).getString(11)),
                            null,
                            parseOrRet(statesArray.getJSONArray(i).getString(13)),
                            statesArray.getJSONArray(i).getString(14),
                            statesArray.getJSONArray(i).getBoolean(15),
                            parseOrRetInt(statesArray.getJSONArray(i).getString(16))
                    );

                    flightsArray[i].setPosition(position++);
                    smallFlightsMap.put(flightsArray[i].getIcao24(), flightsArray[i]);
                }
                flightsArray[statesArray.length()] = getMyCustomFlight();
            }
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    private Flight getMyCustomFlight() {
        Flight flight = new Flight("licenta", "ROT1907", "Romania", (int) System.currentTimeMillis() / 1000, (int) System.currentTimeMillis() / 1000, 26.10002076459427f, 44.44866523974713f, 3337.56f, false, 207.34f, 121.45f, -9.1f, null, 3535.68f, null, false, 0);
        flight.setCategory(Category.LARGE);
        flight.setEstDepartureAirport("LROP");
        flight.setEstArrivalAirport("GCXO");
        flight.setWaypoints(getCustomWaypoints());
        return flight;
    }

    public static ArrayList<LatLng> getCustomWaypoints() {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(44.5803, 26.1321));
        latLngs.add(new LatLng(44.5819, 26.1552));
        latLngs.add(new LatLng(44.5833, 26.1741));
        latLngs.add(new LatLng(44.5848, 26.1952));
        latLngs.add(new LatLng(44.5841, 26.2065));
        latLngs.add(new LatLng(44.5799, 26.2174));

        return latLngs;
    }

    public Flight[] getFlightsArray() {
        return flightsArray;
    }

    public void setFlightsArray(Flight[] flightsArray) {
        this.flightsArray = flightsArray;
    }

    public synchronized String mergeMaps(String icao24) {
        if (smallFlightsMap.get(icao24) == null) {
            return "N/A";
        }
        CountDownLatch latch = new CountDownLatch(2);
        Thread t1 = new Thread(() -> {
            if (icao24 != null && icao24 != "licenta") {
                getMoreInfoFlight(icao24);
            }
            latch.countDown();
            System.out.println("gata api 1");

        });

        Thread t2 = new Thread(() -> {
            if (icao24 != null && icao24 != "licenta") {
                trackFlight(icao24);
            }
            latch.countDown();
            System.out.println("gata api 2");

        });

        t1.start();
        t2.start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Flight flight = smallFlightsMap.get(icao24);
        if (flight != null) {
            int pos = flight.getPosition();
            try {
                Random rand = new Random();
                String randomAirport;
                if (flightDetails != null) {
                    if (flightDetails.get("estDepartureAirport").toString() != "null") {
                        flightsArray[pos].setEstDepartureAirport(flightDetails.get("estDepartureAirport").toString());
                    } else {
                        randomAirport = defaultAirports.get(rand.nextInt(defaultAirports.size()));
                        flightsArray[pos].setEstDepartureAirport(randomAirport);
                    }
                    if (flightDetails.get("estArrivalAirport").toString() != "null") {
                        flightsArray[pos].setEstArrivalAirport(flightDetails.get("estArrivalAirport").toString());
                    } else {
                        int index = rand.nextInt(defaultAirports.size());
                        randomAirport = defaultAirports.get(index);
                        while (randomAirport.equals(flightsArray[pos].getEstDepartureAirport())) {
                            index = rand.nextInt(defaultAirports.size());
                            randomAirport = defaultAirports.get(index);
                        }
                        flightsArray[pos].setEstArrivalAirport(randomAirport);
                    }
                    if (flightDetails.get("estDepartureAirportHorizDistance").toString() != "null" && flightDetails.get("estDepartureAirportHorizDistance").toString() != "" && flightDetails.get("estDepartureAirportHorizDistance").toString() != " ") {
                        flightsArray[pos].setEstDepartureAirportHorizDistance(parseOrRetInt(flightDetails.get("estDepartureAirportHorizDistance").toString()));
                    } else {
                        flightsArray[pos].setEstDepartureAirportHorizDistance(0);
                    }
                    if (flightDetails.get("estArrivalAirportHorizDistance").toString() != "null" && flightDetails.get("estArrivalAirportHorizDistance").toString() != "" && flightDetails.get("estArrivalAirportHorizDistance").toString() != " ") {
                        flightsArray[pos].setEstArrivalAirportHorizDistance(parseOrRetInt(flightDetails.get("estArrivalAirportHorizDistance").toString()));
                    } else {
                        flightsArray[pos].setEstArrivalAirportHorizDistance(0);
                    }
                    if (trackedFlight != null) {
                        if (trackedFlight.get("path") != null) {
                            JSONArray waypoints = trackedFlight.getJSONArray("path");
                            ArrayList<LatLng> pathLatLng = new ArrayList<>();
                            for (int i = 0; i < waypoints.length(); i++) {
                                pathLatLng.add(new com.google.android.gms.maps.model.LatLng(waypoints.getJSONArray(i).getDouble(1), waypoints.getJSONArray(i).getDouble(2)));
                            }
                            flightsArray[pos].setWaypoints(pathLatLng);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return "found";
    }

    public void trackFlight(String icao24) {
        try {
            Call<ResponseBody> callFull = RetrofitClientOpensky.getInstance().getOpenskyApi().trackFlight(icao24, 0);
            Response<ResponseBody> responseFull = callFull.execute();
            ResponseBody flight = responseFull.body();
            if (flight != null) {
                trackedFlight = new JSONObject(flight.string());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getMoreInfoFlight(String icao24) {
        long currentUnixTime = System.currentTimeMillis() / 1000L;
        long oneHrInSeconds = 24 * 60 * 60;
        long beginUnixTime = currentUnixTime - oneHrInSeconds;
        long endUnixTime = currentUnixTime + oneHrInSeconds;
        try {
            Call<ResponseBody> callFull = RetrofitClientOpensky.getInstance().getOpenskyApi().getFlightByDateAndIcao(icao24, beginUnixTime, endUnixTime);
            Response<ResponseBody> responseFull = callFull.execute();
            ResponseBody departureArrivalData = responseFull.body();
            if (departureArrivalData != null) {
                String string = departureArrivalData.string();
                JSONArray jsonArray = new JSONArray(string);
                flightDetails = (JSONObject) jsonArray.get(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
