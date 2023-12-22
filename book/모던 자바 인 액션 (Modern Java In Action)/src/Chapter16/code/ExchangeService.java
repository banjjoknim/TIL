package Chapter16.code;

public class ExchangeService {

    public double getRate(Money firstMoney, Money secondMoney) {
        return firstMoney.getRate() + secondMoney.getRate();
    }
}
