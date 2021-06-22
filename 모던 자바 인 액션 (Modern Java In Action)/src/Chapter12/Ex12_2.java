package Chapter12;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

public class Ex12_2 {
    public static void main(String[] args) {
        LocalDate date = LocalDate.of(2017, 9, 21);

        int year = date.get(ChronoField.YEAR);
        int month = date.get(ChronoField.MONTH_OF_YEAR);
        int day = date.get(ChronoField.DAY_OF_MONTH);
    }
}
