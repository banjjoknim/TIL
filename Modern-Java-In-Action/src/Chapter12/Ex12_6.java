package Chapter12;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

public class Ex12_6 {
    public static void main(String[] args) {
        LocalDate date1 = LocalDate.of(2017, 9, 21);
        LocalDate date2 = date1.withYear(2011);
        LocalDate date3 = date1.withDayOfMonth(25);
        LocalDate date4 = date1.with(ChronoField.MONTH_OF_YEAR, 2);
    }
}
