package tj.stib.records;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;
import tj.stib.enums.Agency;
import tj.stib.enums.RouteType;

import java.io.IOException;
import java.nio.file.Path;

public class Route {
    public final Integer route_id;
    public final int route_name_idx;
    public final RouteType route_type;
    public final Agency agency;

    public Route(String routeId, int index, RouteType routeType, Agency agency) {
        route_id = routeId.hashCode();
        route_name_idx = index;
        route_type = routeType;
        this.agency = agency;
    }

    public String route_short_name() {
        Path path = Path.of("GTFS", Agency.toString(agency), "routes.csv");
        int currentIndex = 0;
        try (CsvReader<NamedCsvRecord> csv = CsvReader.builder().ofNamedCsvRecord(path)) {
            for (NamedCsvRecord rec : csv) {
                if (currentIndex == route_name_idx) {
                    return rec.getField("route_short_name");
                }
                currentIndex++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("route short name not found");
    }

    public String route_long_name() {
        Path path = Path.of("GTFS", Agency.toString(agency), "routes.csv");
        int currentIndex = 0;
        try (CsvReader<NamedCsvRecord> csv = CsvReader.builder().ofNamedCsvRecord(path)) {
            for (NamedCsvRecord rec : csv) {
                if (currentIndex == route_name_idx) {
                    return rec.getField("route_long_name");
                }
                currentIndex++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("route long name not found");
    }


    public record RouteNames(String route_short_name, String route_long_name) {}

    public RouteNames route_names() {
        Path path = Path.of("GTFS", Agency.toString(agency), "routes.csv");
        int currentIndex = 0;
        try (CsvReader<NamedCsvRecord> csv = CsvReader.builder().ofNamedCsvRecord(path)) {
            for (NamedCsvRecord rec : csv) {
                if (currentIndex == route_name_idx) {
                    String route_short_name = rec.getField("route_short_name");
                    String route_long_name = rec.getField("route_long_name");
                    return new RouteNames(route_short_name, route_long_name);
                }
                currentIndex++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("route names not found");
    }
}