package tj.stib;

/**
 * Enum for Route Types.
 */
public enum RouteType {
    WALK,
    BUS,
    TRAM,
    METRO,
    TRAIN,
    ;

    public static RouteType fromString(String type) {
        return switch (type) {
            case "BUS" -> RouteType.BUS;
            case "TRAM" -> RouteType.TRAM;
            case "METRO" -> RouteType.METRO;
            case "TRAIN" -> RouteType.TRAIN;
            default -> RouteType.WALK;
        };
    }
}

