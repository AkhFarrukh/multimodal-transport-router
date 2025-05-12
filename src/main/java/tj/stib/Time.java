package tj.stib;

public class Time {
    private final int hours;
    private final int minutes;
    private final int seconds;

    public Time(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public Time(int totalSeconds) {
        this.hours = totalSeconds / 3600;
        this.minutes = (totalSeconds % 3600) / 60;
        this.seconds = totalSeconds % 60;
    }

    public int hours() { return hours; }
    public int minutes() { return minutes; }
    public int seconds() { return seconds; }

    public int totalSeconds() {
        return hours * 3600 + minutes * 60 + seconds;
    }

    public Time add(Time other) {
        int s = this.seconds + other.seconds;
        int m = this.minutes + other.minutes + s / 60;
        int h = this.hours + other.hours + m / 60;
        return new Time(h, m % 60, s % 60);
    }

    public Time subtract(Time other) {
        int diff = this.totalSeconds() - other.totalSeconds();
        if (diff < 0){
            throw new IllegalArgumentException("negative time");
        }
        return new Time(diff);
    }

    public boolean isGreaterThan(Time other) {
        return this.hours > other.hours ||
               (this.hours == other.hours && this.minutes > other.minutes) ||
               (this.hours == other.hours && this.minutes == other.minutes && this.seconds > other.seconds);
    }

    public boolean isLessThan(Time other) {
        return this.hours < other.hours ||
               (this.hours == other.hours && this.minutes < other.minutes) ||
               (this.hours == other.hours && this.minutes == other.minutes && this.seconds < other.seconds);
    }

    public boolean isEqualTo(Time other) {
        return this.hours == other.hours && this.minutes == other.minutes && this.seconds == other.seconds;
    }

    public boolean isNotEqualTo(Time other) {
        return !this.isEqualTo(other);
    }

    public boolean isGreaterThanOrEqualTo(Time other) {
        return this.isGreaterThan(other) || this.isEqualTo(other);
    }

    public boolean isLessThanOrEqualTo(Time other) {
        return this.isLessThan(other) || this.isEqualTo(other);
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}