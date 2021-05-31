package Chapter16.code;

import java.util.concurrent.*;

public class Example_Future_Before_Java8 {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool(); // 스레드 풀에 태스크를 제출하려면 ExecutorService를 만들어야 한다.
        Future<Double> future = executor.submit(new Callable<Double>() { // Callable을 ExecutorService로 제출한다.
            @Override
            public Double call() throws Exception {
                return doSomeLongComputation(); // 시간이 오래 걸리는 작업은 다른 스레드에서 비동기적으로 실행한다.
            }
        });
        doSomethingElse(); // 비동기 작업을 수행하는 동안 다른 작업을 한다.
        try {
            Double result = future.get(1, TimeUnit.SECONDS); // 비동기 작업의 결과를 가져온다. 결과가 준비되어 있지 않으면 호출 스레드가 블록된다. 하지만 최대 1초까지만 기다린다.
        } catch (InterruptedException e) {
            // 계산 중 예외 발생
        } catch (ExecutionException e) {
            // 현재 스레드에서 대기 중 인터럽트 발생
        } catch (TimeoutException e) {
            // Future가 완료되기전에 타임아웃 발생
        }
    }

    private static Double doSomeLongComputation() {
        // 시간이 오래 걸리는 작업
        return null;
    }

    private static void doSomethingElse() {
        // 다른 작업 수행
    }
}
