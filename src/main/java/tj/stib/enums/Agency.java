package tj.stib.enums;

public enum Agency {
    DELIJN,
    SNCB,
    STIB,
    TEC,
    ;


    public static Agency fromString(String type) {
        return switch (type) {
            case "DELIJN" -> Agency.DELIJN;
            case "SNCB" -> Agency.SNCB;
            case "STIB" -> Agency.STIB;
            case "TEC" -> Agency.TEC;
            default -> throw new IllegalArgumentException("Unknown agency: " + type);
        };
    }

    public static String toString(Agency agency) {
        return switch (agency) {
            case DELIJN -> "DELIJN";
            case SNCB -> "SNCB";
            case STIB -> "STIB";
            case TEC -> "TEC";
            default -> throw new IllegalArgumentException("Unknown agency: " + agency);
        };
    }
}
