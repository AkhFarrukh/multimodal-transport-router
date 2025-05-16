package tj.stib;

import tj.stib.enums.RouteType;
import tj.stib.records.Route;
import tj.stib.records.Stop;
import tj.stib.records.StopTime;

import java.util.*;

public class AlogrithmAStar {

    private final Map<String, List<StopTime>> stopsMapByStopId;
    private final Map<String, List<StopTime>> stopsMapByTripId;
    private final Map<String, String> tripsMap;
    private final Map<String, Route> routesMap;
    private final Map<String, Stop> stopsMap;

    public AlogrithmAStar(
            Map<String, List<StopTime>> stopsMapByStopId,
            Map<String, List<StopTime>> stopsMapByTripId,
            Map<String, String> tripsMap,
            Map<String, Route> routesMap,
            Map<String, Stop> stopsMap
    ) {
        this.stopsMapByStopId = stopsMapByStopId;
        this.stopsMapByTripId = stopsMapByTripId;
        this.tripsMap = tripsMap;
        this.routesMap = routesMap;
        this.stopsMap = stopsMap;
    }


    /**
     * Get all outgoing Edges from a node
     * @param node
     * @return
     */
    public List<Edge> getEdgesToNeighbors(Node node) {
        List<Edge> neighbors = new ArrayList<>();

        for (StopTime stopTime : stopsMapByStopId.get(node.stop_id)) {
            //for every stop_time that has Node's stop_id
            if (stopTime.departure_time > node.time) {
                //if stop_time occurs after we are on the stop.
                StopTime nextStopTime = getNextInTrip(stopTime);

                Node nextNode = new Node(nextStopTime.stop_id, nextStopTime.departure_time);
                int weight = nextStopTime.departure_time - node.time;
                RouteType routeType = routesMap.get(tripsMap.get(stopTime.trip_id)).route_type;

                neighbors.add(new Edge(nextNode, weight, routeType));
            }
        }

        Stop currentStop = stopsMap.get(node.stop_id);
        for (Stop stop : stopsMap.values()){
            if(currentStop.stop_id != stop.stop_id){
                int weight = Walker.walkingTimeSeconds(currentStop, stop);
                if (weight < Walker.maxWalkingTime) {
                    Node nextNode = new Node(stop.stop_id, node.time + weight);
                    RouteType routeType = RouteType.WALK;
                    neighbors.add(new Edge(nextNode, weight, routeType));
                }
            }
        }

        return neighbors;
    }

    public StopTime getNextInTrip(StopTime stopTime){
        List<StopTime> stopTimes = stopsMapByTripId.get(stopTime.trip_id); //sorted by stop_sequence
        for (int i = 0; i < stopTimes.size() - 1; i++) {
            // get next stop_time in trip
            if (stopTimes.get(i).stop_sequence == stopTime.stop_sequence){
                return stopTimes.get(i + 1);
            }
        }
        return null;
    }



    public void shrotestPathAStar(){

    }

}
