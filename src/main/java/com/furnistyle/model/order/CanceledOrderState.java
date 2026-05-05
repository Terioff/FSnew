package com.furnistyle.model.order;

public class CanceledOrderState implements OrderState {
    @Override
    public String getName() {
        return "Отменён";
    }

    @Override
    public void next(Order order) {
        throw new IllegalStateException("Отменённый заказ уже нельзя перевести дальше.");
    }

    @Override
    public void cancel(Order order) {
        throw new IllegalStateException("Заказ уже отменён.");
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
