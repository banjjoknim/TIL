package chapter2.item1;

public class Client {
    public static void main(String[] args) {
        Person person = new Person("Hello", 24);
        Person.fromNameAndAge("Hello", 24);

        Person colt = Person.getColt();

        Colt person1 = Person.createColtWithNameAndAge("Colt", 28);

//        Colt person2 = new Person("Colt", 28);
    }
}
