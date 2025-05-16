package tj.stib;

import tj.stib.enums.RouteType;
import tj.stib.records.Route;
import tj.stib.records.Stop;
import tj.stib.records.StopTime;

import java.util.*;
import java.util.stream.IntStream;


public class AlogrithmAStar {

    private final Map<String, List<StopTime>> stopsMapByTripId;
    private final Map<String, String> tripsMap;
    private final Map<String, Route> routesMap;
    private final Map<String, Stop> stopsMap;
    private Map<String, List<Edge>> graph = new HashMap<>();

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





    public List<Node> shortestPathAStar(String departureStopID, String destinationStopID, int departureTime){
        PriorityQueue<QueueElement> openSetQueue = new PriorityQueue<>(Comparator.comparingInt(qe -> qe.fScore));
        Map<Node, Node> cameFrom = new HashMap<>();
        Map<String, Integer> gScore = new HashMap<>();

        Stop startStop = stopsMap.get(departureStopID);
        Stop destStop = stopsMap.get(destinationStopID);

        Node startNode = new Node(departureStopID, departureTime);
        gScore.put(departureStopID, 0);
        openSetQueue.add(new QueueElement(startNode, Walker.heuristic(startStop,destStop)));

        while (!openSetQueue.isEmpty()) {
            QueueElement current = openSetQueue.poll();
            Node currentNode = current.node;
            String currentStopId = current.node.stop_id;

            if (Objects.equals(currentStopId, destinationStopID)) {
                // todo Reconstruct path
                List<Node> path = new ArrayList<>();
                while (cameFrom.containsKey(currentNode)) {
                    path.add(currentNode);
                    currentNode = cameFrom.get(currentNode);
                }
                Collections.reverse(path);
                return path;
            }

            List<Edge> edges = graph.get(currentStopId);

            for (Edge edge : edges) {
                if (edge.departureTime < currentNode.time) {
                    continue;
                }
                int tentativeGScore = gScore.get(currentStopId) + (edge.arrivalTime - edge.departureTime);
                if (!gScore.containsKey(edge.endStopId) || tentativeGScore < gScore.get(edge.endStopId)) {
                    gScore.put(edge.endStopId, tentativeGScore);

                    int fScore = tentativeGScore + Walker.heuristic(stopsMap.get(edge.endStopId), destStop);
                    openSetQueue.add(new QueueElement(new Node(edge.endStopId, edge.arrivalTime), fScore));

                    cameFrom.put(new Node(edge.endStopId, edge.arrivalTime), currentNode);
                }
            }
        }
        return null; // No path found
    }


    public void printPath(List<Node> path) {
        for (Node node : path) {
            Stop stop = stopsMap.get(node.stop_id);

            System.out.println("Stop: " + stop.stop_name + ", Departure Time: " + Time.secondsToString(node.time));
        }
    }



}




