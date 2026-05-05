package com.furnistyle.model.order;

public class NewOrderState implements OrderState {
    @Override
    public String getName() {
        return "Оформлен";
    }

    @Override
    public void next(Order order) {
        order.setState(new ConfirmedOrderState());
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
