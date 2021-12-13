package chapter5.item33;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class Favorites {
//    public <T> void putFavorite(Class<T> type, T instance) {
//        // ...
//    }
//
//    public <T> T getInstance(Class<T> type) {
//        return null; // 편의상 null을 리턴하도록 작성했다.
//    }

    public static void main(String[] args) {
        Favorites favorites = new Favorites();

        favorites.putFavorite(String.class, "Java");
        favorites.putFavorite(Integer.class, 0xcafebabe);
        favorites.putFavorite(Class.class, Favorites.class);

        String favoriteString = favorites.getInstance(String.class);
        int favoriteInteger = favorites.getInstance(Integer.class);
        Class<?> favoriteClass = favorites.getInstance(Class.class);

        System.out.printf("%s %x %s%n", favoriteString, favoriteInteger, favoriteClass.getName());
    }

    private Map<Class<?>, Object> favorites = new HashMap<>();

//    public <T> void putFavorite(Class<T> type, T instance) {
//        favorites.put(Objects.requireNonNull(type), instance);
//    }

    public <T> T getInstance(Class<T> type) {
        return type.cast(favorites.get(type));
    }

    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), type.cast(instance));
    }
}
