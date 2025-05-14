package tj.stib;

import tj.stib.records.*;


import java.util.*;
import java.nio.file.Path;


public class Main {
    public static void main(String[] args) {
        // Print the current working directory
        long startTime = System.currentTimeMillis();
        System.out.println("Working Directory: " + System.getProperty("user.dir"));



        /*Path csvPathDELIJN = Path.of("GTFS/DELIJN/stop_times.csv");
        Path csvPathSTIB =   Path.of("GTFS/STIB/stop_times.csv");
        Path csvPathSNCB =   Path.of("GTFS/SNCB/stop_times.csv");
        Path csvPathTEC =    Path.of("GTFS/TEC/stop_times.csv");
        */

        List<String> companies = List.of("DELIJN", "STIB", "SNCB", "TEC");
        List<String> csvTypes = List.of("stop_times.csv", "routes.csv", "stops.csv", "trips.csv");



        Map<String, List<StopTime>> stopsMapByStopId = new HashMap<>();
        Map<String, List<StopTime>> stopsMapByTripId = new HashMap<>();

        Map<String, String> tripsMap = new HashMap<>();

        Map<String, Route> routesMap = new HashMap<>();
        Map<String, Stop> stopsMap = new HashMap<>();

        for (String csvType : csvTypes){
            for (String company : companies) {
                Path path = Path.of("GTFS", company, csvType);
                switch (csvType){
                    case "stop_times.csv" -> {
                        // Extract stop times grouped by stop_id
                        DataExtractorGTFS.extractStopTimes(path, "stop_id", stopsMapByStopId);

                        // Extract stop times grouped by trip_id
                        DataExtractorGTFS.extractStopTimes(path, "trip_id", stopsMapByTripId);
                    }
                    case "trips.csv" -> {
                        DataExtractorGTFS.extractTrips(path, tripsMap);
                    }
                    case "routes.csv" -> {
                        DataExtractorGTFS.extractRoutes(path, routesMap);
                    }
                    case "stops.csv" -> {
                        DataExtractorGTFS.extractStops(path, stopsMap);
                    }
                }
                System.out.println("Done with " + company + "/" + csvType);
            }
            System.out.println("Done with " + csvType);        }

        /*
        // Extract stop times grouped by stop_id
        DataExtractorGTFS.extractStopTimes(csvPathDELIJN, "stop_id", stopsMapByStopId);
        DataExtractorGTFS.extractStopTimes(csvPathSTIB, "stop_id", stopsMapByStopId);
        DataExtractorGTFS.extractStopTimes(csvPathSNCB, "stop_id", stopsMapByStopId);
        DataExtractorGTFS.extractStopTimes(csvPathTEC, "stop_id", stopsMapByStopId);

        // Extract stop times grouped by trip_id
        DataExtractorGTFS.extractStopTimes(csvPathDELIJN, "trip_id", stopsMapByTripId);
        DataExtractorGTFS.extractStopTimes(csvPathSTIB, "trip_id", stopsMapByTripId);
        DataExtractorGTFS.extractStopTimes(csvPathSNCB, "trip_id", stopsMapByTripId);
        DataExtractorGTFS.extractStopTimes(csvPathTEC, "trip_id", stopsMapByTripId);
        */


        // Example: print sizes
        System.out.println("Grouped by stop_id: " + stopsMapByStopId.size());
        System.out.println("Grouped by trip_id: " + stopsMapByTripId.size());

        System.out.println("Trips: " + tripsMap.size());
        System.out.println("Routes: " + routesMap.size());
        System.out.println("Stops: " + stopsMap.size());

        // Print memory usage
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