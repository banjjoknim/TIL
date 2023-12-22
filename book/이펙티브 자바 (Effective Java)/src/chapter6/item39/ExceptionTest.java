package chapter6.item39;

import java.lang.annotation.*;

/**
 * 명시한 예외를 던저야만 성공하는 테스트 메서드 애너테이션
 */
//@Retention(RetentionPolicy.RUNTIME)
//@Target(ElementType.METHOD)
//public @interface ExceptionTest {
//        Class<? extends Throwable> value();
//    Class<? extends Throwable>[] value();
//}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionTestContainer.class)
@interface ExceptionTest {
    Class<? extends Throwable> value();
}
