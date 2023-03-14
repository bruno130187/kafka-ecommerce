package br.com.bruno;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        try (var orderDispatcher = new KafkaDispather<Order>()) {
            try (var emailDispatcher = new KafkaDispather<Email>()) {

                for (var i = 0; i < 10; i++) {

                    var userId = UUID.randomUUID().toString();
                    var orderId = UUID.randomUUID().toString();
                    var amount = Math.random() * 5000 + 1;

                    var order = new Order(userId, orderId, new BigDecimal(amount));
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", userId, order);

                    var subject = "Subject " + UUID.randomUUID().toString();
                    var body = "Thank you for your order! Its in processing";
                    var email = new Email(subject, body);
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userId, email);

                }

            }
        }
    }


}
