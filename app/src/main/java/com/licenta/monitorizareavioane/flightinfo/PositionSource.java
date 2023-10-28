package com.licenta.monitorizareavioane.flightinfo;

public enum PositionSource {
    ADS_B(0, "ADS_B"), ASTERIX(1, "ASTERIX"), MLAT(2, "MLAT"), FLARM(3, "FLARM");

    private int i;
    private String value;

    PositionSource(int i, String value) {
    }

    public String getValue() {
        return value;
    }
}
