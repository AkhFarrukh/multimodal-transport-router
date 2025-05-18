package tj.stib;

import tj.stib.records.Stop;

public class Walker {
    public static final double oneDegToKM = 111.3; //at equator
    public static final int maxWalkingTime = Time.stringToSeconds("00:15:00");
    public static final int maxWalkigSpeed = 5; // km/h
    public static final int maxTransportSpeed = 80; // km/h

    private static final double earthRadius = 6365.3; // earth radius at lattitude 50.85

    
    public static double getDistanceHaversine(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)) +
                (Math.sin(dLon / 2) * Math.sin(dLon / 2)) *
                Math.cos(lat1Rad) * Math.cos(lat2Rad);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c; // in km
    }
    
    public static double getDistanceFlatNaive(double lat1, double lon1, double lat2, double lon2) {
        double dx = (lon2 - lon1) * oneDegToKM;
        double dy = (lat2 - lat1) * oneDegToKM;
        return Math.sqrt(dx * dx + dy * dy); // in km
    }

    public static int walkingTimeSeconds(double lat1, double lon1, double lat2, double lon2){
        double distance = getDistanceHaversine(lat1, lon1, lat2, lon2);
        return (int) Math.round((distance / maxWalkigSpeed) * 3600); // time in seconds at walking speed
    }

    public static int walkingTimeSeconds(Stop stop1, Stop stop2){
        return walkingTimeSeconds(stop1.stop_lat, stop1.stop_lon, stop2.stop_lat, stop2.stop_lon);
    }


    //riding time in seconds
    public static int heuristic(double lat1, double lon1, double lat2, double lon2){
        double distance = getDistanceHaversine(lat1, lon1, lat2, lon2);
        return (int) Math.round((distance / maxTransportSpeed) * 3600); // time in seconds at walking speed
    }

    public static int heuristic(Stop stop1, Stop stop2){
        return heuristic(stop1.stop_lat, stop1.stop_lon, stop2.stop_lat, stop2.stop_lon);
    }
}
