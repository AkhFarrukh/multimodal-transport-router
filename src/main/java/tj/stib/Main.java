package tj.stib;

import tj.stib.enums.RouteType;
import tj.stib.records.*;


import java.util.*;
import java.util.concurrent.*;
import java.nio.file.Path;


// java -Xmx8g -jar target/PROJET-Algo2-1.0-SNAPSHOT.jar [ BOILEAU ARSENAL 10:00:00 ]

//[ Alveringem Nieuwe Herberg - Aubange - 10:30:00 ]
// [ BOILEAU - JANSON - 07:20:00 ]
// [ BOILEAU - DELTA - 09:30:00 ]

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        long startTime = System.currentTimeMillis();
        //System.out.println("Working Directory: " + System.getProperty("user.dir"));

        // Join all args to handle spaces
        String joinedArgs = String.join(" ", args);
        int leftBracket = joinedArgs.indexOf('[');
        int rightBracket = joinedArgs.indexOf(']');

        if (leftBracket == -1 || rightBracket == -1 || rightBracket < leftBracket) {
            System.out.println("Invalid input. Expected format: [ departure stop - destination stop - departure time ]");
            return;
        }

        String insideBrackets = joinedArgs.substring(leftBracket + 1, rightBracket).trim();
        String[] tokens = insideBrackets.split("\\s*-\\s*");
        if (tokens.length != 3) {
            System.out.println("Invalid input. Expected format: [ departure stop - destination stop - departure time ]");
            return;
        }

        String departureStop = tokens[0].trim();
        String destinationStop = tokens[1].trim();
        String departureTimeString = tokens[2].trim();
        int departureTime = Time.stringToSeconds(departureTimeString);

        // Parse extra options after ']'
        String extraArgs = joinedArgs.substring(rightBracket + 1).trim();

        List<String> options = new ArrayList<>();
        List<RouteType> blackListedRouteTypes = new ArrayList<>();
        boolean changesPenalty = false;

        if (!extraArgs.isEmpty()) {
            options = Arrays.asList(extraArgs.split("\\s+"));
            // Process the options
            for (String option : options) {

                RouteType routeType = RouteType.fromString(option);
                if (routeType != null) {
                    blackListedRouteTypes.add(routeType);
                }else if (option.equals("CHANGES")) {
                      changesPenalty = true;
                }
            }
        }


        //System.out.println("Departure stop: " + departureStop);
        //System.out.println("Destination stop: " + destinationStop);
        //System.out.println("Departure time: " + Time.secondsToString(departureTime));
        System.out.println("Options: " + options);





        Map<String, List<StopTime>> stopsMapByTripId = new ConcurrentHashMap<>();
        Map<String, String>         tripsMap =         new ConcurrentHashMap<>();
        Map<String, Route>          routesMap =        new ConcurrentHashMap<>();
        Map<String, Stop>           stopsMap =         new ConcurrentHashMap<>();


        Parser.parseAndPopulateData(
                stopsMapByTripId,
                tripsMap,
                routesMap,
                stopsMap
        );

        String departureStopId = FromMapExtractors.getStopIdByName(stopsMap, departureStop);
        String destinationStopId = FromMapExtractors.getStopIdByName(stopsMap, destinationStop);

        //System.out.println("Departure stop id: " + departureStopId);
        //System.out.println("Destination stop id: " + destinationStopId);

        AlogrithmAStar algorithmAStar = new AlogrithmAStar(
                stopsMapByTripId,
                tripsMap,
                routesMap,
                stopsMap,
                blackListedRouteTypes,
                changesPenalty
        );

        System.out.println("Building graph...");
        algorithmAStar.buildGraph();
        System.out.println("Finding shortest path...");


        List<Edge> path = algorithmAStar.shortestPathAStar(
                departureStopId,
                destinationStopId,
                departureTime
        );

        algorithmAStar.printPath(path);





        //System.out.println("Grouped by trip_id: " + stopsMapByTripId.size());
        //System.out.println("Trips: " + tripsMap.size());
        //System.out.println("Routes: " + routesMap.size());
        //System.out.println("Stops: " + stopsMap.size());

        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        System.out.println("Used memory: " + usedMemory + " MB");
        System.out.println("Total memory: " + totalMemory + " MB");

        long endTime = System.currentTimeMillis();
        double durationSeconds = (endTime - startTime) / 1000.0;
        System.out.println("Execution time: " + durationSeconds + " s");
    }
}