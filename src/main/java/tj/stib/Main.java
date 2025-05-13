package tj.stib;

import tj.stib.records.StopTime;

import java.util.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Main {
    public static void main(String[] args) {
        // Print the current working directory
        long startTime = System.currentTimeMillis();
        System.out.println("Working Directory: " + System.getProperty("user.dir"));

        Path csvPath = Path.of("GTFS/STIB/stop_times.csv");

        // Call the readData method with the file path
        // Extract stop times grouped by stop_id
        Map<String, List<StopTime>> byStopId = DataExtractorGTFS.extractStopTimes(csvPath, "stop_id");

        // Extract stop times grouped by trip_id
        Map<String, List<StopTime>> byTripId = DataExtractorGTFS.extractStopTimes(csvPath, "trip_id");

        // Example: print sizes
        System.out.println("Grouped by stop_id: " + byStopId.size());
        System.out.println("Grouped by trip_id: " + byTripId.size());

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