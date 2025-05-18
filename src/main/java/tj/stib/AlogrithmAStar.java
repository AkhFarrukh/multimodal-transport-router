package tj.stib;

import tj.stib.enums.RouteType;
import tj.stib.records.Route;
import tj.stib.records.Stop;
import tj.stib.records.StopTime;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;


public class AlogrithmAStar {

    private final Map<String, List<StopTime>> stopsMapByTripId;
    private final Map<String, String> tripsMap;
    private final Map<String, Route> routesMap;
    private final Map<String, Stop> stopsMap;
    private Map<String, List<Edge>> graph = new ConcurrentHashMap<>();
    private final List<RouteType> routeTypeBlacklsit;
    private final boolean changesPenalty;

    public AlogrithmAStar(
            Map<String, List<StopTime>> stopsMapByTripId,
            Map<String, String> tripsMap,
            Map<String, Route> routesMap,
            Map<String, Stop> stopsMap,
            List<RouteType> routeTypeBlacklsit,
            boolean changesPenalty
    ) {
        this.stopsMapByTripId = stopsMapByTripId;
        this.tripsMap = tripsMap;
        this.routesMap = routesMap;
        this.stopsMap = stopsMap;
        this.routeTypeBlacklsit = routeTypeBlacklsit;
        this.changesPenalty = changesPenalty;
    }


    public void buildGraph() {
        buildTransportGraph();
        buildWalkingGraphParallel();

    }

    public void buildTransportGraph(){
        for (String tripID : stopsMapByTripId.keySet()) {
            List<StopTime> stopTimes = stopsMapByTripId.get(tripID);
            for (int i = 0; i < stopTimes.size() - 1; i++) {
                StopTime current = stopTimes.get(i);
                StopTime next = stopTimes.get(i + 1);

                List<Edge> edges = graph.computeIfAbsent(current.stop_id, k -> new ArrayList<>());
                edges.add(new Edge(next.stop_id, current.departure_time, next.departure_time, routesMap.get(tripsMap.get(current.trip_id)).route_type, tripID));
            }
        }
    }

    public void buildWalkingGraph() {
        for (Stop stopA : stopsMap.values()) {
            for (Stop stopB : stopsMap.values()) {
                if (!stopA.stop_id.equals(stopB.stop_id)) {
                    int weight = Walker.walkingTimeSeconds(stopA, stopB);
                    if (weight < Walker.maxWalkingTime) {
                        List<Edge> edges = graph.computeIfAbsent(stopA.stop_id, k -> new ArrayList<>());
                        edges.add(new Edge(stopB.stop_id, 0, weight, RouteType.WALK, null));
                    }
                }
            }
        }
    }

    public void buildWalkingGraphParallel() {
        List<Stop> stops = new ArrayList<>(stopsMap.values());
        int n = stops.size();
        IntStream.range(0, n).parallel().forEach(i -> {
            Stop stopA = stops.get(i);
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                Stop stopB = stops.get(j);
                int weight = Walker.walkingTimeSeconds(stopA, stopB);
                if (weight < Walker.maxWalkingTime) {
                    synchronized (graph) {
                        List<Edge> edges = graph.computeIfAbsent(stopA.stop_id, k -> new ArrayList<>());
                        edges.add(new Edge(stopB.stop_id, 0, weight, RouteType.WALK, null));
                    }
                }
            }
        });
    }







    public List<Edge> shortestPathAStar(String departureStopID, String destinationStopID, int departureTime) {
        PriorityQueue<QueueElement> openSetQueue = new PriorityQueue<>(Comparator.comparingInt(qe -> qe.fScore));
        Map<Edge, Edge> cameFrom = new HashMap<>();
        Map<String, Integer> bestArrivalTime = new HashMap<>();

        Stop startStop = stopsMap.get(departureStopID);
        Stop destStop = stopsMap.get(destinationStopID);

        //by convention the first stop is transformed to an Edge to it
        Edge startEdge = new Edge(departureStopID, departureTime, departureTime, RouteType.WALK, null);

        bestArrivalTime.put(departureStopID, departureTime);
        openSetQueue.add(new QueueElement(startEdge, Walker.heuristic(startStop, destStop)));

        while (!openSetQueue.isEmpty()) {
            QueueElement current = openSetQueue.poll();
            Edge currentEdge = current.edge;

            if (currentEdge.endStopId.equals(destinationStopID)) {
                return reconstructPath(cameFrom, currentEdge, startEdge);
            }

            List<Edge> edges = graph.get(currentEdge.endStopId);
            if (edges == null) continue;

            for (Edge edge : edges) {
                int newDepartureTime;
                int newArrivalTime;

                if (edge.routeType == RouteType.WALK) {
                    // walk at any time
                    newDepartureTime = currentEdge.arrivalTime;
                    newArrivalTime = newDepartureTime + (edge.arrivalTime - edge.departureTime);
                } else {
                    if (edge.departureTime < currentEdge.arrivalTime) {
                        continue; // skip trips from past
                    }
                    newDepartureTime = edge.departureTime;
                    newArrivalTime = edge.arrivalTime;
                }

                // since Edges are immutable, we create a new one with current times
                // needed for time accurate walking
                Edge newEdge = new Edge(
                        edge.endStopId,
                        newDepartureTime,
                        newArrivalTime,
                        edge.routeType,
                        edge.tripId
                );

                // if not visited or found a better arrival time
                if (!bestArrivalTime.containsKey(edge.endStopId) || newArrivalTime < bestArrivalTime.get(edge.endStopId)) {

                    bestArrivalTime.put(edge.endStopId, newArrivalTime);

                    int timeScore = newArrivalTime - departureTime;
                    int hScore = Walker.heuristic(stopsMap.get(edge.endStopId), destStop);
                    int fScore = timeScore + hScore;

                    if (routeTypeBlacklsit.contains(edge.routeType)) {
                        fScore *= 2; // penalize blacklisted route types
                    }

                    if (changesPenalty
                            && edge.tripId != null
                            && currentEdge.tripId != null
                            && !edge.tripId.equals(currentEdge.tripId)) {
                        fScore *= 2; // penalty for changing routes
                    }


                    openSetQueue.add(new QueueElement(newEdge, fScore));
                    cameFrom.put(newEdge, currentEdge);
                }
            }
        }
        return null;
    }

    private List<Edge> reconstructPath(Map<Edge, Edge> cameFrom, Edge current, Edge start) {
        List<Edge> path = new ArrayList<>();
        while (current != null) {
            path.addFirst(current);
            current = cameFrom.get(current);
        }
        path.addFirst(start);
        return path;
    }


    public void printPath(List<Edge> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            Edge current = path.get(i);
            Edge next = path.get(i + 1);


            Stop currentStop = stopsMap.get(current.endStopId);
            Stop nextStop = stopsMap.get(next.endStopId);

            if (next.routeType == RouteType.WALK) {
                System.out.printf("Walk from %s (%s) to %s (%s)%n",
                        currentStop.stop_name,
                        Time.secondsToString(next.departureTime),
                        nextStop.stop_name,
                        Time.secondsToString(next.arrivalTime));
            } else {
                // find the last used stop of this trip
                Edge lastEdgeOfTrip = next;
                for (int j = i + 2; j < path.size(); j++) {
                    // check after the next edge
                    if (path.get(j).tripId != null && path.get(j).tripId.equals(next.tripId)) {
                        lastEdgeOfTrip = path.get(j);
                    } else {
                        break;
                    }
                }

                Stop firstStop = stopsMap.get(current.endStopId);
                Stop finalStop = stopsMap.get(lastEdgeOfTrip.endStopId);
                Route route = routesMap.get(tripsMap.get(next.tripId));

                System.out.printf("Take %s %s %s from %s (%s) to %s (%s)%n",
                        extractAgencyName(route.route_id),
                        route.route_type,
                        route.route_short_name,
                        firstStop.stop_name,
                        Time.secondsToString(next.departureTime),
                        finalStop.stop_name,
                        Time.secondsToString(lastEdgeOfTrip.arrivalTime));

                // skip the middle stops of the same trip
                while (i < path.size() - 2 && path.get(i + 2).tripId != null && path.get(i + 2).tripId.equals(next.tripId)) {
                    i++;
                }
            }
        }
    }

    public static String extractAgencyName(String input) {
        int index = input.indexOf('-');
        if (index == -1) {
            return input; // No dash found, return the whole string
        }
        return input.substring(0, index);
    }

}




