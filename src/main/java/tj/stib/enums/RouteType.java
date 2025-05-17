package tj.stib.enums;

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
            case "WALK" -> RouteType.WALK;
            default -> null;
        };
    }
}

