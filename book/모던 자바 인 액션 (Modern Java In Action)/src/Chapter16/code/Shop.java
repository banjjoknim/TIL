package Chapter16.code;

import java.util.Random;
import java.util.concurrent.*;

public class Shop {
    private static final Random random = new Random();
    private String name;

    public Shop(String name) {
        this.name = name;
    }

    public static void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void randomDelay() {
        int delay = 500 + random.nextInt(2000);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPrice(String product) {
        double price = calculatePrice(product);
        Discount.Code code = Discount.Code.values()[random.nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", name, price, code);
    }

    private double calculatePrice(String product) {
//        delay();
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }


//    public Future<Double> getPriceAsync(String product) {
//        CompletableFuture<Double> futurePrice = new CompletableFuture<>();
//        new Thread(() -> {
//            try {
//                double price = calculatePrice(product);
//                futurePrice.complete(price); // 계산이 정상적으로 종료되면 Future에 가격 정보를 저장한채로 Future를 종료한다.
//            } catch (Exception e) {
//                futurePrice.completeExceptionally(e); // 도중에 문제가 발생하면 발생한 에러를 포함시켜 Future를 종료한다.
//            }
//        }).start();
//        return futurePrice;
//    }

    public Future<Double> getPriceAsync(String product) {
        return CompletableFuture.supplyAsync(() -> calculatePrice(product));
    }

//    private double calculatePrice(String product) {
//        delay();
//        return random.nextDouble() * product.charAt(0) + product.charAt(1);
//    }

    private static void doSomethingElse() {
    }

    public String getName() {
        return name;
    }

    public static void main(String[] args) {
        Shop shop = new Shop("bestShop");
        long start = System.nanoTime();
        Future<Double> futurePrice = shop.getPriceAsync("my favorite product"); // 상점에 제품가격 정보 요청
        long invocationTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Invocation returned after " + invocationTime + " msecs");

        // 제품의 가격을 계산하는 동안
        doSomethingElse();

        // 다른 상점 검색 등 다른 작업 수행
        try {
            double price = futurePrice.get();// 가격 정보가 있으면 Future에서 가격 정보를 읽고, 가격 정보가 없으면 가격 정보를 받을 때까지 블록한다.
            System.out.printf("Price is %.2f%n", price);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Price returned after " + retrievalTime + " msecs");
    }
}
