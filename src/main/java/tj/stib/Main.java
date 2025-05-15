package tj.stib;

import tj.stib.records.*;


import java.util.*;
import java.util.concurrent.*;
import java.nio.file.Path;




public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        long startTime = System.currentTimeMillis();
        System.out.println("Working Directory: " + System.getProperty("user.dir"));


        if (args.length < 5) {
            System.out.println("Usage: java -jar <jarfile> ( <departure_stop> <destination_stop> <departure_time> )");
            return;
        }
        if (!args[0].equals("(") || !args[4].equals(")")) {
            System.out.println("Invalid departure stop format. Expected format: ( stop_A stop_B xx:xx:xx )");
            return;
        }

        String departureStop = args[1];
        String destinationStop = args[2];
        String departureTime = args[3];

        System.out.println("Departure stop: " + departureStop);
        System.out.println("Destination stop: " + destinationStop);
        System.out.println("Departure time: " + departureTime);




        Map<String, List<StopTime>> stopsMapByStopId = new ConcurrentHashMap<>();
        Map<String, List<StopTime>> stopsMapByTripId = new ConcurrentHashMap<>();
        Map<String, String>         tripsMap =         new ConcurrentHashMap<>();
        Map<String, Route>          routesMap =        new ConcurrentHashMap<>();
        Map<String, Stop>           stopsMap =         new ConcurrentHashMap<>();


        Parser.parseAndPopulateData(
                stopsMapByStopId,
                stopsMapByTripId,
                tripsMap,
                routesMap,
                stopsMap
        );

        System.out.println("Grouped by stop_id: " + stopsMapByStopId.size());
        System.out.println("Grouped by trip_id: " + stopsMapByTripId.size());
        System.out.println("Trips: " + tripsMap.size());
        System.out.println("Routes: " + routesMap.size());
        System.out.println("Stops: " + stopsMap.size());

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