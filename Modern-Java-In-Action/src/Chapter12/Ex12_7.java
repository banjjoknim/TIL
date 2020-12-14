package Chapter12;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Ex12_7 {
    public static void main(String[] args) {
        LocalDate date1 = LocalDate.of(2017, 9, 21); // 2017-09-21
        LocalDate date2 = date1.plusWeeks(1); // 2017-09-28
        LocalDate date3 = date2.minusYears(6); // 2011-09-28
        LocalDate date4 = date3.plus(6, ChronoUnit.MONTHS); // 2012-03-28
    }
}
