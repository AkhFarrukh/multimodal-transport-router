package tj.stib.records;

public class Stop {
    public final String stop_id;
    public final String stop_name;
    public final double stop_lat;
    public final double stop_lon;

    public Stop(String stopId, String stopName, double stopLat, double stopLon) {
        stop_id = stopId;
        stop_name = stopName;
        stop_lat = stopLat;
        stop_lon = stopLon;
    }
}