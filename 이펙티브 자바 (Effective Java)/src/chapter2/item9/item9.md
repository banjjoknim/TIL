# 아이템 9. try-finally보다는 try-with-resources를 사용하라

## 자바 라이브러리의 직접 닫아줘야 하는 자원

- 자바 라이브러리에는 close 메서드를 호출해 직접 닫아줘야 하는 자원이 많다.
- InputStream, OutputStream, java.sql.Connection 등이 좋은 예다.

## 전통적인 자원 회수 방식 - try-finally

- 전통적으로 자원이 제대로 닫힘을 보장하는 수단으로 try-finally가 쓰였다.
- 이는 예외가 발생하거나 메서드에서 반환되는 경우까지도 포함하여 사용되었다.

### try-finally - 더 이상 자원을 회수하는 최선의 방책이 아니다!

```java
public class Item9 {
    static String firstLineOfFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
            br.close();
        }
    }
}
```

### 자원이 둘 이상이면 try-finally 방식은 너무 지저분하다!

```java
public class Item9 {
    private static final int BUFFER_SIZE = 0; // 편의상 0으로 지정

    static void copy(String src, String dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                byte[] buf = new byte[BUFFER_SIZE];
                int n;
                while ((n = in.read(buf)) >= 0) {
                    out.write(buf, 0, n);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
```

- try-finally 문을 제대로 사용한 앞의 두 코드 예제에는 미묘한 결점이 있다.
- 예외는 try 블록과 finally 블록 모두에서 발생할 수 있는데, 예컨대 기기에 물리적인 문제가 생긴다면 firstLineOfFile 메서드 안의 readLine 메서드가 예외를 던지고, 같은 이유로
  close 메서드도 실패할 것이다.
- 이런 상황이라면 두 번째 예외가 첫 번째 예외를 완전히 집어삼켜 버린다.
- 그러면 스택 추적 내역에 첫 번째 예외에 관한 정보는 남지 않게 되어, 실제 시스템에서의 디버깅을 몹시 어렵게 한다(일반적으로 문제를 진단하려면 처음 발생한 예외를 보고 싶을 것이다).

## try-with-resources를 사용하면 문제가 모두 해결된다!

- try-with-resources 구조를 사용하려면 해당 자원이 AutoCloseable 인터페이스를 구현해야 한다.
- 닫아야 하는 자원을 뜻하는 클래스를 작성한다면 AutoCloseable을 반드시 구현하도록 하자.

### try-with-resources - 자원을 회수하는 최선책!

```java
public class Item9 {
    static String firstLineOfFile(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine();
        }
    }
}
```

### 복수의 자원을 처리하는 try-with-resources - 짧고 매혹적이다!

```java
public class Item9 {
    private static final int BUFFER_SIZE = 0; // 편의상 0으로 지정

    static void copy(String src, String dst) throws IOException {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            }
        }
    }
}
```

- readLine과 (코드에는 나타나지 않는) close 호출 양쪽에서 예외가 발생하면, close에서 발생한 예외는 숨겨지고 readLine에서 발생한 예외가 기록된다.
- 이렇게 숨겨진 예외들도 그냥 버려지지는 않고, 스택 추적 내역에 '숨겨졌다(suppressed)'는 꼬리표를 달고 출력된다.
- 자바 7에서 Throwable에 추가된 getSuppressed 메서드를 이용하면 프로그램 코드에서 가져올 수도 있다.

### try-with-resources를 catch 절과 함께 쓰는 모습

```java
public class Item9 {
    static String firstLineOfFile(String path, String defaultVal) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine();
        } catch (IOException e) {
            return defaultVal;
        }
    }
}
```

- 보통의 try-finally에서처럼 try-with-resources에서도 catch 절을 쓸 수 있다.
- catch 절 덕분에 try 문을 더 중첩하지 않고도 다수의 예외를 처리할 수 있다.

## 핵심 정리

꼭 회수해야 하는 자원을 다룰 때는 try-finally 말고, try-with-resources를 사용하자. 예외는 없다. 코드는 더 짧고 분명해지고, 만들어지는 예외 정보도 훨씬 유용하다. try-finally로
작성하면 실용적이지 못할 만큼 코드가 지저분해지는 경우라도, try-with-resources로는 정확하고 쉽게 자원을 회수할 수 있다.