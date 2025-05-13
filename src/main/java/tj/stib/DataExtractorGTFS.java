package tj.stib;


import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;
import tj.stib.records.Stop;
import tj.stib.records.StopTime;


import java.io.IOException;
import java.nio.file.Path;

import java.util.*;


public class DataExtractorGTFS {


    public static void readData(Path filePath) {
        try (CsvReader<NamedCsvRecord> csv = CsvReader.builder().ofNamedCsvRecord(filePath)) {
            csv.forEach(rec ->
                    System.out.println(rec.getField("trip_id"))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static Map<String, Stop> extractStops(Path filePath) {

        Map<String, Stop> stopsMap = new HashMap<>();

        try (CsvReader<NamedCsvRecord> csv = CsvReader.builder().ofNamedCsvRecord(filePath)) {

            csv.forEach(rec ->
                    System.out.println(rec.getField("trip_id"))

            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stopsMap;
    }


    /**
     * Extracts stop times from a CSV file and groups them by a specified key column.
     *
     * @param filePath   The path to the CSV file.
     * @param keyColumn  The column name to group the stop times by (stop_id or trip_id).
     * @return A map where the key is the value of the specified key column and the value is a list of StopTime objects.
     */
    public static Map<String, List<StopTime>> extractStopTimes(Path filePath, String keyColumn) {
        Map<String, List<StopTime>> stopsMap = new HashMap<>();

        try (CsvReader<NamedCsvRecord> csv = CsvReader.builder().ofNamedCsvRecord(filePath)) {
            csv.forEach(rec -> {
                String key = rec.getField(keyColumn);
                StopTime stopTime = new StopTime(
                        rec.getField("trip_id"),
                        (new Time(rec.getField("departure_time"))).totalSeconds(),
                        rec.getField("stop_id"),
                        Integer.parseInt(rec.getField("stop_sequence"))
                );

                List<StopTime> list = stopsMap.computeIfAbsent(key, k -> new ArrayList<>());
                list.add(stopTime);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stopsMap;
    }




}