package tj.stib;

import tj.stib.records.*;


import java.util.*;
import java.util.concurrent.*;
import java.nio.file.Path;




public class Main {

    public static void printDoneWith(String company, String csvType){
        System.out.println("Done with " + company + "/" + csvType);
    }


    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();
        System.out.println("Working Directory: " + System.getProperty("user.dir"));

        List<String> companies = List.of("DELIJN", "STIB", "SNCB", "TEC");
        List<String> csvTypes = List.of("stop_times.csv", "routes.csv", "stops.csv", "trips.csv");

        Map<String, List<StopTime>> stopsMapByStopId = new ConcurrentHashMap<>();
        Map<String, List<StopTime>> stopsMapByTripId = new ConcurrentHashMap<>();
        Map<String, String> tripsMap = new ConcurrentHashMap<>();
        Map<String, Route> routesMap = new ConcurrentHashMap<>();
        Map<String, Stop> stopsMap = new ConcurrentHashMap<>();

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<?>> futures = new ArrayList<>();

        for (String csvType : csvTypes) {
            for (String company : companies) {
                Path path = Path.of("GTFS", company, csvType);
                    switch (csvType) {
                        case "stop_times.csv" -> {
                            futures.add(executor.submit(() -> {
                            DataExtractorGTFS.extractStopTimes(path, "stop_id", stopsMapByStopId);
                            System.out.println("Done with " + company + "/" + csvType + "by stop_id");
                            }));
                            futures.add(executor.submit(() -> {
                            DataExtractorGTFS.extractStopTimes(path, "trip_id", stopsMapByTripId);
                            System.out.println("Done with " + company + "/" + csvType + "by trip_id");
                            }));
                        }
                        case "trips.csv" ->{
                            futures.add(executor.submit(() -> {
                            DataExtractorGTFS.extractTrips(path, tripsMap);
                            printDoneWith(company, csvType);
                            }));
                        }
                        case "routes.csv" ->{
                            futures.add(executor.submit(() -> {
                            DataExtractorGTFS.extractRoutes(path, routesMap);
                            printDoneWith(company, csvType);
                            }));
                        }
                        case "stops.csv" -> {
                            futures.add(executor.submit(() -> {
                            DataExtractorGTFS.extractStops(path, stopsMap);
                            printDoneWith(company, csvType);
                            }));
                        }
                    }
            }
            System.out.println("Submitted all for " + csvType);
        }

        for (Future<?> future : futures) {
            future.get();
        }


        // Parallel sorting by departure time using ExecutorService
        List<Future<?>> sortFutures = new ArrayList<>();
        for (List<StopTime> stopTimes : stopsMapByStopId.values()) {
            sortFutures.add(executor.submit(() ->
                    stopTimes.sort(Comparator.comparingInt(st -> st.departure_time))
            ));
        }
        for (Future<?> f : sortFutures) f.get();
        System.out.println("Sorted by departure time");

        // Parallel sorting by stop sequence using ExecutorService
        sortFutures.clear();
        for (List<StopTime> stopTimes : stopsMapByTripId.values()) {
            sortFutures.add(executor.submit(() ->
                    stopTimes.sort(Comparator.comparingInt(st -> st.stop_sequence))
            ));
        }
        for (Future<?> f : sortFutures) f.get();
        System.out.println("Sorted by stop sequence");


        executor.shutdown();


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