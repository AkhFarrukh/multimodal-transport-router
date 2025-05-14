package tj.stib.records;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;

import tj.stib.enums.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class Stop {
    public final String stop_id;
    public final int stop_name_idx; //index of record in csv file
    public final double stop_lat;
    public final double stop_lon;
    public final Agency agency;

    public Stop(String stopId, int stopName, double stopLat, double stopLon, Agency agency) {
        stop_id = stopId;
        stop_name_idx = stopName;
        stop_lat = stopLat;
        stop_lon = stopLon;
        this.agency = agency;
    }

    public String stop_name() {
        Path path = Path.of("GTFS", Agency.toString(agency), "stops.csv");
        int currentIndex = 0;
        try (CsvReader<NamedCsvRecord> csv = CsvReader.builder().ofNamedCsvRecord(path)) {
            for (NamedCsvRecord rec : csv) {
                if (currentIndex == stop_name_idx) {
                    return rec.getField("stop_name");
                }
                currentIndex++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("Stop name not found");
    }


}