package com.licenta.monitorizareavioane.flightinfo;

import com.licenta.monitorizareavioane.liveflights.Flight;

public enum Category {
    NO_INFO(0, "No information at all"), NO_ADS_B(1, "No ADS-B Emitter Category Information"), LIGHT(2, "Light"), SMALL(3, "Small"), LARGE(4, "Large"), HVL(5, "High Vortex Large"), HEAVY(6, "Heavy"), HIGH_PERF(7, "High Performance"), ROTORCRAFT(8, "Rotorcraft"), GLIDER(9, "Glider"), LIGHTER(10, "Lighter-than-air"), PARACHUTIST(11, "Parachutist"), ULTRALIGHT(12, "Ultralight"), RESERVED(13, "Reserved"), UNMANNED(14, "Unmanned Aerial Vehicle"), SPACE(15, "Space / Trans-atmospheric vehicle"), EMERGENCY(16, "Emergency Vehicle"), SERVICE(17, "Service Vehicle"), POINT_OBSTACLE(18, " Point Obstacle"), CLUSTER_OBSTACLE(19, "Cluster Obstacle"), LINE_OBSTACLE(20, "Line obstacle");

    private int i;
    private String value;

    Category(int i, String value) {
        this.i = i;
        this.value = value;
    }

//    public static Category returnCategory(int category) {
//        switch (category) {
//            case 1:
//                return Category.NO_ADS_B;
//            case 2:
//                return Category.LIGHT;
//            case 3:
//                return Category.SMALL;
//            case 4:
//                return Category.LARGE;
//            case 5:
//                return Category.HVL;
//            case 6:
//                return Category.HEAVY;
//            case 7:
//                return Category.HIGH_PERF;
//            case 8:
//                return Category.ROTORCRAFT;
//            case 9:
//                return Category.GLIDER;
//            case 10:
//                return Category.LIGHTER;
//            case 11:
//                return Category.PARACHUTIST;
//            case 12:
//                return Category.ULTRALIGHT;
//            case 13:
//                return Category.RESERVED;
//            case 14:
//                return Category.UNMANNED;
//            case 15:
//                return Category.SPACE;
//            case 16:
//                return Category.EMERGENCY;
//            case 17:
//                return Category.SERVICE;
//            case 18:
//                return Category.POINT_OBSTACLE;
//            case 19:
//                return Category.CLUSTER_OBSTACLE;
//            case 20:
//                return Category.LINE_OBSTACLE;
//            default:
//                return Category.NO_INFO;
//        }
//    }
}
