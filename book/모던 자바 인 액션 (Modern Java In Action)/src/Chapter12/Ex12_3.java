package Chapter12;

import java.time.LocalDate;
import java.time.LocalTime;

public class Ex12_3 {
    public static void main(String[] args) {
        LocalTime time = LocalTime.of(13, 45, 20);
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();

        LocalDate date = LocalDate.parse("2017-09-21");
        LocalTime time1 = LocalTime.parse("13:45:20");
    }
}
