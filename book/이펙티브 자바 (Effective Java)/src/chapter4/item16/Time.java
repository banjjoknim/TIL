package chapter4.item16;

final class Time {
    private static final int HOURS_PER_DAY = 24;
    private static final int MINUTES_PER_HOUR = 60;

    public final int hour;
    public final int minute;

    public Time(int hour, int minute) {
        validateTime(hour, minute);
        this.hour = hour;
        this.minute = minute;
    }

    private void validateTime(int hour, int minute) {
        // .. 유효성 검증 로직, 불변식 보장
    }

    // ...
}
