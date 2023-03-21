package br.com.bruno.users;

import br.com.bruno.kafka.KafkaDispather;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewUser {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var userDispatcher = new KafkaDispather<User>()) {

            //userId and email random to create a new user
//            var userId = UUID.randomUUID().toString();
//            var email = Math.random() + "@mail.com";

            //userId and email fixed to force a user already created
            var userId = "0befb31f-8ee4-4412-9c34-e375fe866509";
            var email = "0.2794497671522876@mail.com";

            var user = new User(userId, email);
            userDispatcher.send("ECOMMERCE_NEW_USER", userId, user);

        }
    }

}
