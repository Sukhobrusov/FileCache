package com.selvis.entity


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
