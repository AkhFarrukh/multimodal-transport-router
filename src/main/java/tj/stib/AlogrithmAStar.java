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

    public AlogrithmAStar(
            Map<String, List<StopTime>> stopsMapByTripId,
            Map<String, String> tripsMap,
            Map<String, Route> routesMap,
            Map<String, Stop> stopsMap
    ) {
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
    /*
    public List<Edge> getEdgesToNeighbors(String stop_id) {
        List<Edge> neighbors = new ArrayList<>();
        List<StopTime> stopTimes = stopsMapByStopId.get(stop_id);

        if (stopTimes == null) {
            return neighbors; // No stop times for this stop_id
        }
        for (StopTime stopTime : stopTimes) {
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

    */

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






    public List<Node> shortestPathAStar(String departureStopID, String destinationStopID, int departureTime) {
        PriorityQueue<QueueElement> openSetQueue = new PriorityQueue<>(Comparator.comparingInt(qe -> qe.fScore));
        Map<Edge, Edge> cameFrom = new HashMap<>();
        Map<String, Integer> bestArrivalTime = new HashMap<>();

        Stop startStop = stopsMap.get(departureStopID);
        Stop destStop = stopsMap.get(destinationStopID);

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
                        continue;// skip trips from past
                    }
                    newDepartureTime = edge.departureTime;
                    newArrivalTime = edge.arrivalTime;
                }

                // Create a new edge with updated times
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


                    openSetQueue.add(new QueueElement(newEdge, timeScore + hScore));
                    cameFrom.put(newEdge, currentEdge);
                }
            }
        }
        return null;
    }

    private List<Node> reconstructPath(Map<Edge, Edge> cameFrom, Edge current, Edge start) {
        List<Node> path = new ArrayList<>();
        while (current != null) {
            path.add(0, new Node(current.endStopId, current.arrivalTime));
            if (current.departureTime != current.arrivalTime) {
                path.add(0, new Node(current.endStopId, current.departureTime));
            }
            current = cameFrom.get(current);
        }
        path.add(0, new Node(start.endStopId, start.departureTime));
        return path;
    }

    public void printPath(List<Node> path) {
        for (int i = 0; i < path.size(); i++) {
            Node node = path.get(i);
            Stop stop = stopsMap.get(node.stop_id);

            if (i < path.size() - 1 && path.get(i + 1).stop_id.equals(node.stop_id)) {
                // This is a departure time
                System.out.println("Stop: " + stop.stop_name +
                        ", Wait until: " + Time.secondsToString(node.time));
            } else {
                // This is an arrival time
                System.out.println("Stop: " + stop.stop_name +
                        ", Arrive at: " + Time.secondsToString(node.time));
            }
        }
    }






}




