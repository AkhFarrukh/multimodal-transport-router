package tj.stib;


import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import de.siegmar.fastcsv.reader.NamedCsvRecord;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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
}