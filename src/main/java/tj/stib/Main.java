package tj.stib;

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
        DataExtractorGTFS.readData(csvPath);

        long endTime = System.currentTimeMillis();
        double durationSeconds = (endTime - startTime) / 1000.0;
        System.out.println("Execution time: " + durationSeconds + " s");
    }
}