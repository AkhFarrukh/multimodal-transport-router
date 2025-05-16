package tj.stib;


import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;
import tj.stib.enums.RouteType;
import tj.stib.records.Route;
import tj.stib.records.Stop;
import tj.stib.records.StopTime;


import java.io.IOException;
import java.nio.file.Path;

import java.util.*;

import static tj.stib.Time.*;


public class DataExtractorGTFS {

    /**
     * Extracts stop times from a CSV file and groups them by a specified key column.
     *
     * @param filePath   The path to the CSV file.
     * @param keyColumn  The column name to group the stop times by (stop_id or trip_id).
     * @return A map where the key is the value of the specified key column and the value is a list of StopTime objects.
     */
    public static void extractStopTimes(Path filePath, String keyColumn, Map<String, List<StopTime>> stopsMap) {
        try (CsvReader<NamedCsvRecord> csv = CsvReader.builder().ofNamedCsvRecord(filePath)) {
            csv.forEach(rec -> {
                String key = rec.getField(keyColumn);
                //todo check if start time from is bigger than departure time. if true, skip
                StopTime stopTime = new StopTime(
                        rec.getField("trip_id"),
                        stringToSeconds(rec.getField("departure_time")),
                        rec.getField("stop_id"),
                        Integer.parseInt(rec.getField("stop_sequence"))
                );

                List<StopTime> list = stopsMap.computeIfAbsent(key, k -> new ArrayList<>());
                list.add(stopTime);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void extractTrips(Path filePath, Map<String, String> tripsMap) {
        try (CsvReader<NamedCsvRecord> csv = CsvReader.builder().ofNamedCsvRecord(filePath)) {
            csv.forEach(rec -> {
                String tripId = rec.getField("trip_id");
                String routeId = rec.getField("route_id");
                tripsMap.put(tripId, routeId);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void extractRoutes(Path filePath, Map<String, Route> routesMap) {
        try (CsvReader<NamedCsvRecord> csv = CsvReader.builder().ofNamedCsvRecord(filePath)) {
            csv.forEach(rec -> {
                //todo make like extractStopTimes
                String routeId = rec.getField("route_id");
                String routeShortName = rec.getField("route_short_name");
                String routeLongName = rec.getField("route_long_name");
                RouteType routeType = RouteType.fromString(rec.getField("route_type"));

                Route route = new Route(routeId, routeShortName, routeLongName, routeType);
                routesMap.put(routeId, route);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void extractStops(Path filePath, Map<String, Stop> stopsMap) {
        try (CsvReader<NamedCsvRecord> csv = CsvReader.builder().ofNamedCsvRecord(filePath)) {
            csv.forEach(rec -> {
                String stopId = rec.getField("stop_id");
                String stopName = rec.getField("stop_name");
                double stopLat = Double.parseDouble(rec.getField("stop_lat"));
                double stopLon = Double.parseDouble(rec.getField("stop_lon"));

                Stop stop = new Stop(stopId, stopName, stopLat, stopLon);
                stopsMap.put(stopId, stop);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}