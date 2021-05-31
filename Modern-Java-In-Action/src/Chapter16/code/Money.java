package Chapter16.code;

public enum Money {
    EUR(1),
    USD(1);

    private final double rate;

    Money(double rate) {
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }
}
