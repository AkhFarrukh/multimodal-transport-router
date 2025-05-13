package tj.stib.records;

import tj.stib.RouteType;

public class Route {
    public final String route_id;
    public final String route_short_name;
    public final String route_long_name;
    public final RouteType route_type;

    public Route(String routeId, String routeShortName, String routeLongName, RouteType routeType) {
        route_id = routeId;
        route_short_name = routeShortName;
        route_long_name = routeLongName;
        route_type = routeType;
    }
}