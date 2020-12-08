package Chapter10;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private String customer;
    private List<Trade> trades = new ArrayList<>();

    public void addTrade(Trade trade) {
        trades.add(trade);
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public double getValue() {
        return trades.stream()
                .mapToDouble(Trade::getValue)
                .sum();
    }

    public static void main(String[] args) {
        Order order = new Order();
        order.setCustomer("BigBank");

        Trade trade1 = new Trade();
        trade1.setType(Trade.Type.BUY);

        Stock stock1 = new Stock();
        stock1.setSymbol("IBM");
        stock1.setMarket("NYSE");

        trade1.setStock(stock1);
        trade1.setPrice(125.00);
        trade1.setQuantity(80);
        order.addTrade(trade1);

        Trade trade2 = new Trade();
        trade1.setType(Trade.Type.BUY);

        Stock stock2 = new Stock();
        stock1.setSymbol("GOOGLE");
        stock1.setMarket("NASDAQ");

        trade1.setStock(stock2);
        trade1.setPrice(375.00);
        trade1.setQuantity(50);
        order.addTrade(trade2);
    }
}
