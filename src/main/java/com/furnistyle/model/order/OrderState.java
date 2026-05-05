package com.furnistyle.model.order;

import java.io.Serializable;

public interface OrderState extends Serializable {
    String getName();

    void next(Order order);

    void cancel(Order order);

    boolean isFinished();
}
