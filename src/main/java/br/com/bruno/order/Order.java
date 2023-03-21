package br.com.bruno.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {

    private final String email, orderId;
    private final BigDecimal amount;

    public Order(String email, String orderId, BigDecimal amount) {
        this.email = email;
        this.orderId = orderId;
        this.amount = amount;
    }

}
