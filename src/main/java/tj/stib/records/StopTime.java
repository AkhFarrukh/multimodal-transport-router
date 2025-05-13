package tj.stib.records;

public class StopTime {
    public final String trip_id;
    public final int departure_time;
    public final String stop_id;
    public final int stop_sequence;

    public StopTime(String tripId, int departureTime, String stopId, int stopSequence) {
        trip_id = tripId;
        departure_time = departureTime;
        stop_id = stopId;
        stop_sequence = stopSequence;
    }
}
