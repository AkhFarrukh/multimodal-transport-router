package tj.stib;

public class Walker {
    public static final double oneDegToKM = 111.3;
    public static final int maxWalkingTime = Time.stringToSeconds("00:15:00");
    public static final int maxWalkigSpeed = 5; // km/h
    public static final int maxTransportSpeed = 80; // km/h
    //public static final int maxWalkingDistanceMeters = (maxWalkingTime / 3600) * maxWalkigSpeed * 1000; // in meters


    public static double getDistanceFlatNaive(double lat1, double lon1, double lat2, double lon2) {
        double dx = (lon2 - lon1) * oneDegToKM;
        double dy = (lat2 - lat1) * oneDegToKM;
        return Math.sqrt(dx * dx + dy * dy); // in km
    }

    public static int walkingTimeSeconds(double lat1, double lon1, double lat2, double lon2){
        double distance = getDistanceFlatNaive(lat1, lon1, lat2, lon2);
        return (int) Math.round((distance / maxWalkigSpeed) * 3600); // time in seconds at walking speed
    }


}
