package tj.stib;

import tj.stib.enums.RouteType;

public class Edge {
    public final String endStopId;
    public final int departureTime;
    public final int arrivalTime;
    public final RouteType routeType;
    public final String tripId;


    public Edge(String endStopId, int departureTime, int arrivalTime, RouteType routeType, String tripId) {
        this.endStopId = endStopId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.routeType = routeType;
        this.tripId = tripId;
    }
}


