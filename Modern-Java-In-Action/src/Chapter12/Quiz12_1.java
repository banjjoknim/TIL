package Chapter12;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

public class Quiz12_1 {
    public static void main(String[] args) {
        LocalDate date = LocalDate.of(2014, 3, 18);
        date = date.with(ChronoField.MONTH_OF_YEAR, 9);
        date = date.plusYears(2).minusDays(10);
        date.withYear(2011);
    }
}
