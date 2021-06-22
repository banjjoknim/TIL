package Chapter10;

public class MethodChainingOrderBuilder {

    public final Order order = new Order();

    private MethodChainingOrderBuilder(String customer) {
        order.setCustomer(customer);
    }

    public static MethodChainingOrderBuilder forCustomer(String customer) {
        return new MethodChainingOrderBuilder(customer);
    }

//    public TradeBuilder buy(int quantity) {
//        return new TradeBuilder(this, Trade.Type.BUY, quantity); // 주식을 파는 TradeBuilder 만들기
//    }
//
//    public TradeBuilder sell(int quantity) {
//        return new TradeBuilder(this, Trade.Type.SELL, quantity);
//    }

    public MethodChainingOrderBuilder addTrade(Trade trade) {
        order.addTrade(trade);
        return this;
    }

    public Order end() {
        return order;
    }
}
