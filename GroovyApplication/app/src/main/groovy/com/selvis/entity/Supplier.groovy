package com.selvis.entity

import com.selvis.entity.Debt
import com.selvis.entity.DeliveryDays

public class Supplier {

    String uid

    String name

    Boolean contract

    List<DeliveryDays> deliveryDays

    Debt debt

    @Override
    public String toString() {
        return "Supplier{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
