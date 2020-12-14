package Chapter12;

import java.time.*;

public class Ex12_4 {
    public static void main(String[] args) {
        LocalDate date = LocalDate.of(2017, 9, 21);
        LocalTime time = LocalTime.of(13, 45, 20);

        // 2017-09-21T13:45:20
        LocalDateTime dt1 = LocalDateTime.of(2017, Month.SEPTEMBER, 21, 13, 45, 20);
        LocalDateTime dt2 = LocalDateTime.of(date, time);
        LocalDateTime dt3 = date.atTime(13, 45, 20);
        LocalDateTime dt4 = date.atTime(time);
        LocalDateTime dt5 = time.atDate(date);

        LocalDate date1 = dt1.toLocalDate(); // 2017-09-21
        LocalTime time1 = dt1.toLocalTime(); // 13:45:30

        Instant.ofEpochSecond(3);
        Instant.ofEpochSecond(3, 0);
        Instant.ofEpochSecond(2, 1_000_000_000);
        Instant.ofEpochSecond(4, -1_000_000_000);

    }
}
