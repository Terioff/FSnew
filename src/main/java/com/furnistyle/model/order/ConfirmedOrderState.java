package com.furnistyle.model.order;

public class ConfirmedOrderState implements OrderState {
    @Override
    public String getName() {
        return "Подтверждён";
    }

    @Override
    public void next(Order order) {
        order.setState(new InProgressOrderState());
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
