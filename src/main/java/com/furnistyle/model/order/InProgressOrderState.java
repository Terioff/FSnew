package com.furnistyle.model.order;

public class InProgressOrderState implements OrderState {
    @Override
    public String getName() {
        return "В работе";
    }

    @Override
    public void next(Order order) {
        order.setState(new CompletedOrderState());
    }

    @Override
    public void cancel(Order order) {
        order.setState(new CanceledOrderState());
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
