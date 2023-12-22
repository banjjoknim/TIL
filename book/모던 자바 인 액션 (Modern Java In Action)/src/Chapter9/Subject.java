package Chapter9;

interface Subject {
    void registerObserver(Observer o);
    void notifyObservers(String tweet);
}
