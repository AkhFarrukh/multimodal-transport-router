package tj.stib;

import java.util.List;

import tj.stib.records.*;


import java.util.*;
import java.util.concurrent.*;
import java.nio.file.Path;


public class Parser {

    public static void printDoneWith(String company, String csvType){
        System.out.println("Done with " + company + "/" + csvType);
    }

    public static void parseAndPopulateData(
            Map<String, List<StopTime>> stopsMapByTripId,
            Map<String, String> tripsMap,
            Map<String, Route> routesMap,
            Map<String, Stop> stopsMap) throws InterruptedException, ExecutionException  {

        System.out.println("Parsing and extracting data...");

        List<String> companies = List.of("DELIJN", "STIB", "SNCB", "TEC");
        List<String> csvTypes = List.of("stop_times.csv", "routes.csv", "stops.csv", "trips.csv");

        //get all cpus cores
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        //all future tasks are extractors that return void
        List<Future<?>> futures = new ArrayList<>();

        for (String csvType : csvTypes) {
            for (String company : companies) {
                Path path = Path.of("GTFS", company, csvType);
                switch (csvType) {
                    case "stop_times.csv" -> {
                        futures.add(executor.submit(() -> {
                            DataExtractorGTFS.extractStopTimes(path, "trip_id", stopsMapByTripId);
                            //System.out.println("Done with " + company + "/" + csvType + " by trip_id");
                        }));
                    }
                    case "trips.csv" -> {
                        futures.add(executor.submit(() -> {
                            DataExtractorGTFS.extractTrips(path, tripsMap);
                            //printDoneWith(company, csvType);
                        }));
                    }
                    case "routes.csv" -> {
                        futures.add(executor.submit(() -> {
                            DataExtractorGTFS.extractRoutes(path, routesMap);
                            //printDoneWith(company, csvType);
                        }));
                    }
                    case "stops.csv" -> {
                        futures.add(executor.submit(() -> {
                            DataExtractorGTFS.extractStops(path, stopsMap);
                            //printDoneWith(company, csvType);
                        }));
                    }
                }
            }
            //System.out.println("Submitted all for " + csvType);
        }
        for (Future<?> future : futures) {
            future.get();
        }

        //sort lists per key
        List<Future<?>> sortFutures = new ArrayList<>();

        sortFutures.clear();
        for (List<StopTime> stopTimes : stopsMapByTripId.values()) {
            sortFutures.add(executor.submit(() ->
                    stopTimes.sort(Comparator.comparingInt(st -> st.stop_sequence))
            ));
        }
        for (Future<?> f : sortFutures) f.get();
        //System.out.println("All data extracted and sorted");
        executor.shutdown();
    }
}