package br.com.bruno.fraudDetector;

import br.com.bruno.email.Email;
import br.com.bruno.kafka.KafkaDispather;
import br.com.bruno.kafka.KafkaService;
import br.com.bruno.order.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class FraudDetectionService {

    public static void main(String[] args) {
        var fraudService = new FraudDetectionService();
        try (var service = new KafkaService<>(FraudDetectionService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudService::parse,
                Order.class,
                Map.of())) {
            service.run();
        }
    }

    private final KafkaDispather<Order> orderKafkaDispather = new KafkaDispather<>();
    private final KafkaDispather<Email> emailDispatcher = new KafkaDispather<>();

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException {
        System.out.println("_____________________________________________");
        System.out.println("Processing new order, checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        var order = record.value();

        if (isFraud(order)) {
            System.out.println("Order is a fraud!!!");
            orderKafkaDispather.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(), order);

            //send e-mail
            var subject = "Subject " + UUID.randomUUID().toString();
            var body = "Order rejected, this was a fraud!";
            var email = new Email(subject, body);
            emailDispatcher.send("ECOMMERCE_SEND_EMAIL", order.getEmail(), email);
        } else {
            System.out.println("Approved!");
            orderKafkaDispather.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(), order);

            //send e-mail
            var subject = "Subject " + UUID.randomUUID().toString();
            var body = "Order approved, Thanks!";
            var email = new Email(subject, body);
            emailDispatcher.send("ECOMMERCE_SEND_EMAIL", order.getEmail(), email);
        }

        System.out.println("Order processed!");

    }

    private static boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }

}
