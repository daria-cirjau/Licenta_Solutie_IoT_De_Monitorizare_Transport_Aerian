package com.licenta.monitorizareavioane.liveflights;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.licenta.monitorizareavioane.flightinfo.Category;
import com.licenta.monitorizareavioane.flightinfo.PositionSource;

import java.util.ArrayList;
import java.util.Arrays;

public class Flight implements Parcelable {
    private String icao24;
    private String callsign;
    private String originCountry;
    private Integer timePosition;
    private Integer lastContact;
    private Float longitude;
    private Float latitude;
    private Float baroAltitude;
    private boolean onGround;
    private Float velocity;
    private Float trueTrack;
    private Float verticalRate;
    private int[] sensors;
    private Float geoAltitude;
    private String squawk;
    private boolean spi;
    private PositionSource positionSource;
    private Category category;
    private String estDepartureAirport;
    private String estArrivalAirport;
    private Integer estDepartureAirportHorizDistance;
    private Integer estArrivalAirportHorizDistance;
    private int position;
    private ArrayList<LatLng> waypoints;
    private String pictureURL;
    private String estDepatureRegion;
    private String estArrivalRegion;

    public Flight(String icao24, String callsign, String originCountry, Integer timePosition, Integer lastContact, Float longitude,
                  Float latitude, Float baroAltitude, boolean onGround, Float velocity, Float trueTrack, Float verticalRate, int[] sensors,
                  Float geoAltitude, String squawk, boolean spi, Integer positionSource) {
        this.icao24 = icao24;
        this.callsign = callsign;
        this.originCountry = originCountry;
        this.timePosition = timePosition;
        this.lastContact = lastContact;
        this.longitude = longitude;
        this.latitude = latitude;
        this.baroAltitude = baroAltitude;
        this.onGround = onGround;
        this.velocity = velocity;
        this.trueTrack = trueTrack;
        this.verticalRate = verticalRate;
        this.sensors = sensors;
        this.geoAltitude = geoAltitude;
        this.squawk = squawk;
        this.spi = spi;
        switch (positionSource) {
            case 0:
                this.positionSource = PositionSource.ADS_B;
                break;
            case 1:
                this.positionSource = PositionSource.ASTERIX;
                break;
            case 2:
                this.positionSource = PositionSource.MLAT;
                break;
            case 3:
                this.positionSource = PositionSource.FLARM;
                break;
        }


    }


    protected Flight(Parcel in) {
        icao24 = in.readString();
        callsign = in.readString();
        originCountry = in.readString();
        if (in.readByte() == 0) {
            timePosition = null;
        } else {
            timePosition = in.readInt();
        }
        if (in.readByte() == 0) {
            lastContact = null;
        } else {
            lastContact = in.readInt();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readFloat();
        }
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readFloat();
        }
        if (in.readByte() == 0) {
            baroAltitude = null;
        } else {
            baroAltitude = in.readFloat();
        }
        onGround = in.readByte() != 0;
        if (in.readByte() == 0) {
            velocity = null;
        } else {
            velocity = in.readFloat();
        }
        if (in.readByte() == 0) {
            trueTrack = null;
        } else {
            trueTrack = in.readFloat();
        }
        if (in.readByte() == 0) {
            verticalRate = null;
        } else {
            verticalRate = in.readFloat();
        }
        sensors = in.createIntArray();
        if (in.readByte() == 0) {
            geoAltitude = null;
        } else {
            geoAltitude = in.readFloat();
        }
        squawk = in.readString();
        spi = in.readByte() != 0;
        estDepartureAirport = in.readString();
        estArrivalAirport = in.readString();
        if (in.readByte() == 0) {
            estDepartureAirportHorizDistance = null;
        } else {
            estDepartureAirportHorizDistance = in.readInt();
        }
        if (in.readByte() == 0) {
            estArrivalAirportHorizDistance = null;
        } else {
            estArrivalAirportHorizDistance = in.readInt();
        }
        position = in.readInt();
        waypoints = in.createTypedArrayList(LatLng.CREATOR);
        pictureURL = in.readString();
        estDepatureRegion = in.readString();
        estArrivalRegion = in.readString();
    }

    public static final Creator<Flight> CREATOR = new Creator<Flight>() {
        @Override
        public Flight createFromParcel(Parcel in) {
            return new Flight(in);
        }

        @Override
        public Flight[] newArray(int size) {
            return new Flight[size];
        }
    };

    public String getIcao24() {
        return icao24;
    }

    public void setIcao24(String icao24) {
        this.icao24 = icao24;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public Integer getTimePosition() {
        return timePosition;
    }

    public void setTimePosition(Integer timePosition) {
        this.timePosition = timePosition;
    }

    public Integer getLastContact() {
        return lastContact;
    }

    public void setLastContact(Integer lastContact) {
        this.lastContact = lastContact;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getBaroAltitude() {
        return baroAltitude;
    }

    public void setBaroAltitude(Float baroAltitude) {
        this.baroAltitude = baroAltitude;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public Float getVelocity() {
        return velocity;
    }

    public void setVelocity(Float velocity) {
        this.velocity = velocity;
    }

    public Float getTrueTrack() {
        return trueTrack;
    }

    public void setTrueTrack(Float trueTrack) {
        this.trueTrack = trueTrack;
    }

    public Float getVerticalRate() {
        return verticalRate;
    }

    public void setVerticalRate(Float verticalRate) {
        this.verticalRate = verticalRate;
    }

    public int[] getSensors() {
        return sensors;
    }

    public void setSensors(int[] sensors) {
        this.sensors = sensors;
    }

    public Float getGeoAltitude() {
        return geoAltitude;
    }

    public void setGeoAltitude(Float geoAltitude) {
        this.geoAltitude = geoAltitude;
    }

    public String getSquawk() {
        return squawk;
    }

    public void setSquawk(String squawk) {
        this.squawk = squawk;
    }

    public boolean isSpi() {
        return spi;
    }

    public void setSpi(boolean spi) {
        this.spi = spi;
    }

    public PositionSource getPositionSource() {
        return positionSource;
    }

    public void setPositionSource(PositionSource positionSource) {
        this.positionSource = positionSource;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getEstDepartureAirport() {
        return estDepartureAirport;
    }

    public void setEstDepartureAirport(String estDepartureAirport) {
        this.estDepartureAirport = estDepartureAirport;
    }

    public String getEstArrivalAirport() {
        return estArrivalAirport;
    }

    public void setEstArrivalAirport(String estArrivalAirport) {
        this.estArrivalAirport = estArrivalAirport;
    }

    public int getEstDepartureAirportHorizDistance() {
        return estDepartureAirportHorizDistance;
    }

    public void setEstDepartureAirportHorizDistance(int estDepartureAirportHorizDistance) {
        this.estDepartureAirportHorizDistance = estDepartureAirportHorizDistance;
    }

    public int getEstArrivalAirportHorizDistance() {
        return estArrivalAirportHorizDistance;
    }

    public void setEstArrivalAirportHorizDistance(int estArrivalAirportHorizDistance) {
        this.estArrivalAirportHorizDistance = estArrivalAirportHorizDistance;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public ArrayList<LatLng> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(ArrayList<LatLng> waypoints) {
        this.waypoints = waypoints;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getEstDepatureRegion() {
        return estDepatureRegion;
    }

    public void setEstDepatureRegion(String estDepatureRegion) {
        this.estDepatureRegion = estDepatureRegion;
    }

    public String getEstArrivalRegion() {
        return estArrivalRegion;
    }

    public void setEstArrivalRegion(String estArrivalRegion) {
        this.estArrivalRegion = estArrivalRegion;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "icao24='" + icao24 + '\'' +
                ", callsign='" + callsign + '\'' +
                ", originCountry='" + originCountry + '\'' +
                ", timePosition=" + timePosition +
                ", lastContact=" + lastContact +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", baroAltitude=" + baroAltitude +
                ", onGround=" + onGround +
                ", velocity=" + velocity +
                ", trueTrack=" + trueTrack +
                ", verticalRate=" + verticalRate +
                ", sensors=" + Arrays.toString(sensors) +
                ", geoAltitude=" + geoAltitude +
                ", squawk='" + squawk + '\'' +
                ", spi=" + spi +
                ", positionSource=" + positionSource +
                ", category=" + category +
                ", estDepartureAirport='" + estDepartureAirport + '\'' +
                ", estArrivalAirport='" + estArrivalAirport + '\'' +
                ", estDepartureAirportHorizDistance=" + estDepartureAirportHorizDistance +
                ", estArrivalAirportHorizDistance=" + estArrivalAirportHorizDistance +
                ", position=" + position +
                ", waypoints=" + waypoints +
                ", pictureURL='" + pictureURL + '\'' +
                ", estDepatureRegion='" + estDepatureRegion + '\'' +
                ", estArrivalRegion='" + estArrivalRegion + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(icao24);
        parcel.writeString(callsign);
        parcel.writeString(originCountry);
        if (timePosition == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(timePosition);
        }
        if (lastContact == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(lastContact);
        }
        if (longitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(longitude);
        }
        if (latitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(latitude);
        }
        if (baroAltitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(baroAltitude);
        }
        parcel.writeByte((byte) (onGround ? 1 : 0));
        if (velocity == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(velocity);
        }
        if (trueTrack == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(trueTrack);
        }
        if (verticalRate == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(verticalRate);
        }
        parcel.writeIntArray(sensors);
        if (geoAltitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(geoAltitude);
        }
        parcel.writeString(squawk);
        parcel.writeByte((byte) (spi ? 1 : 0));
        parcel.writeString(estDepartureAirport);
        parcel.writeString(estArrivalAirport);
        if (estDepartureAirportHorizDistance == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(estDepartureAirportHorizDistance);
        }
        if (estArrivalAirportHorizDistance == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(estArrivalAirportHorizDistance);
        }
        parcel.writeInt(position);
        parcel.writeTypedList(waypoints);
        parcel.writeString(pictureURL);
        parcel.writeString(estDepatureRegion);
        parcel.writeString(estArrivalRegion);
    }
}

