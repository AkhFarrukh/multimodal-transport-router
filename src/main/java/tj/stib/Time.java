package tj.stib;

public class Time {


    public static int totalSeconds(int hours, int minutes, int seconds) {
        return hours * 3600 + minutes * 60 + seconds;
    }

    public static int stringToSeconds(String timeString) {
        String[] parts = timeString.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("time not in xx:xx:xx format");
        }
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        return totalSeconds(hours, minutes, seconds);
    }

    public static int hours(int totalSeconds) {
        return totalSeconds / 3600;
    }

    public static int minutes(int totalSeconds) {
        return (totalSeconds % 3600) / 60;
    }

    public static int seconds(int totalSeconds) {
        return totalSeconds % 60;
    }


    public static String secondsToString(int totalSeconds) {
        return String.format("%02d:%02d:%02d", hours(totalSeconds), minutes(totalSeconds), seconds(totalSeconds));
    }
}