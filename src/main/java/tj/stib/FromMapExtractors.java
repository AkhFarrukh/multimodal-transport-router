package tj.stib;

import tj.stib.records.Stop;

import java.util.Map;

public class FromMapExtractors {

    public static String getStopIdByName(Map<String, Stop> stopsMap, String stopName) {
        for (Map.Entry<String, Stop> entry : stopsMap.entrySet()) {
            if (entry.getValue().stop_name.equals(stopName)) {
                return entry.getKey();
            }
        }
        return null; // or throw an exception if not found
    }


}
