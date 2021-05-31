package Chapter16.code;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static java.util.stream.Collectors.toList;

public class Shops {
    private List<Shop> shops = Arrays.asList(
            new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("BuyItAll"),
            new Shop("BuyItAll"),
            new Shop("BuyItAll"),
            new Shop("BuyItAll"),
            new Shop("BuyItAll"),
            new Shop("easyShop")
    );

    private final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), // 상점 수만큼의 스레드를 갖는 풀을 생성한다(스레드 수의 범위는 0과 100 사이).
            new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread();
            t.setDaemon(true); // 프로그램 종료를 방해하지 않는 데몬 스레드를 사용한다.
            return t;
        }
    });

//    public List<String> findPrices(String product) {
//        return shops.stream()
//                .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
//                .collect(toList());
//    }

//    public List<String> findPrices(String product) {
//        return shops.parallelStream()
//                .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
//                .collect(toList());
//    }

//    public List<String> findPrices(String product) {
//        List<CompletableFuture<String>> priceFutures = shops.stream()
//                .map(shop -> CompletableFuture.supplyAsync( // CompletableFuture로 각각의 가격을 비동기적으로 계산한다.
//                        () -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product))))
//                .collect(toList());
//        return priceFutures.stream()
//                .map(CompletableFuture::join) // 모든 비동기 동작이 끝나길 기다린다.
//                .collect(toList());
//    }

    public List<String> findPrices(String product) {
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getName() + " price is " + shop.getPrice(product), executor))
                .collect(toList());
        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(toList());
    }

    public static void main(String[] args) {
        Shops shops = new Shops();
        long start = System.nanoTime();
        System.out.println(shops.findPrices("myPhone27S"));
        long duration = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Done in " + duration + " msecs");
    }
}
