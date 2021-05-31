package Chapter16.code;

import static Chapter16.code.Shop.delay;
import static Chapter16.code.Shop.randomDelay;

public class Discount {
    public enum Code {
        NONE(0),
        SILVER(5),
        GOLD(10),
        PLATINUM(15),
        DIAMOND(20);

        private final int percentage;

        Code(int percentage) {
            this.percentage = percentage;
        }
    }

    // 생략된 Discount 클래스 구현은 [예제 16-14] 참조
    public static String applyDiscount(Quote quote) {
        return quote.getShopName() + " price is " +
                Discount.apply(quote.getPrice(), // 기존 가격에 할인 코드를 적용한다.
                        quote.getDiscountCode());
    }

    private static double apply(double price, Code code) {
//        delay(); // Discount 서비스의 응답 지연을 흉내 낸다.
        randomDelay();
        return price * (100 - code.percentage) / 100;
    }
}
