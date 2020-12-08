package Chapter10;

import java.util.function.DoubleUnaryOperator;

public class TaxCalculator {
    public DoubleUnaryOperator taxFunction = d -> d;

    public TaxCalculator with(DoubleUnaryOperator f) {
        taxFunction = taxFunction.andThen(f);
        return this;
    }

    public double calculate(Order order) {
        return taxFunction.applyAsDouble(order.getValue());
    }

    public static void main(String[] args) {
        Order order = new Order();
        double value = new TaxCalculator()
                .with(Tax::regional)
                .with(Tax::surcharge)
                .calculate(order);
        System.out.println(value);
    }
}
