package item1;

public class Person {
    private static final Person COLT = new Person("Colt", 28);

    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static Person fromNameAndAge(String name, int age) {
        return new Person(name, age);
    }

    public static Person getColt() {
        return COLT;
    }

    public static Colt createColtWithNameAndAge(String name, int age) {
        return new Colt(name, age);
    }
}
