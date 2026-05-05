package com.furnistyle.model.order;

public class CompletedOrderState implements OrderState {
    @Override
    public String getName() {
        return "Выполнен";
    }

    @Override
    public void next(Order order) {
        throw new IllegalStateException("Выполненный заказ уже нельзя перевести дальше.");
    }

    @Override
    public void cancel(Order order) {
        throw new IllegalStateException("Выполненный заказ нельзя отменить.");
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
