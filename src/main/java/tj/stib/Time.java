package tj.stib;

public class Time {
    private final int totalSeconds;

    public Time(int hours, int minutes, int seconds) {
        this.totalSeconds = hours * 3600 + minutes * 60 + seconds;
    }

    public Time(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public Time(String timeString) {
        String[] parts = timeString.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("time not in xx:xx:xx format");
        }
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        this.totalSeconds = hours * 3600 + minutes * 60 + seconds;
    }

    public int hours() {
        return totalSeconds / 3600;
    }

    public int minutes() {
        return (totalSeconds % 3600) / 60;
    }

    public int seconds() {
        return totalSeconds % 60;
    }

    public int totalSeconds() {
        return totalSeconds;
    }

    public Time add(Time other) {
        return new Time(this.totalSeconds + other.totalSeconds);
    }

    public Time subtract(Time other) {
        int diff = this.totalSeconds - other.totalSeconds;
        if (diff < 0) {
            throw new IllegalArgumentException("negative time");
        }
        return new Time(diff);
    }

    public boolean isGreaterThan(Time other) {
        return this.totalSeconds > other.totalSeconds;
    }

    public boolean isLessThan(Time other) {
        return this.totalSeconds < other.totalSeconds;
    }

    public boolean isEqualTo(Time other) {
        return this.totalSeconds == other.totalSeconds;
    }

    public boolean isNotEqualTo(Time other) {
        return this.totalSeconds != other.totalSeconds;
    }

    public boolean isGreaterThanOrEqualTo(Time other) {
        return this.totalSeconds >= other.totalSeconds;
    }

    public boolean isLessThanOrEqualTo(Time other) {
        return this.totalSeconds <= other.totalSeconds;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", hours(), minutes(), seconds());
    }
}