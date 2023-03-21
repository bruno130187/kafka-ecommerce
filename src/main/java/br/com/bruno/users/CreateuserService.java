package br.com.bruno.users;

import br.com.bruno.kafka.KafkaDispather;
import br.com.bruno.kafka.KafkaService;
import br.com.bruno.order.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CreateuserService {

    public CreateuserService() throws SQLException, ClassNotFoundException {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:src/bd/users_database.sqlite";
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS users ( " +
                    "uuid TEXT primary key, " +
                    "email TEXT )";

            statement.executeUpdate(sql);
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("Table created successfully");

    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        var createUserService = new CreateuserService();
        try (var service = new KafkaService<>(CreateuserService.class.getSimpleName(),
                "ECOMMERCE_NEW_USER",
                createUserService::parse,
                User.class,
                Map.of())) {
            service.run();
        }
    }

    private final KafkaDispather<User> userKafkaDispather = new KafkaDispather<>();

    private void parse(ConsumerRecord<String, User> record) throws SQLException, ClassNotFoundException, ExecutionException, InterruptedException {
        System.out.println("_____________________________________________");
        System.out.println("Processing, checking for new user.");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());

        var user = record.value();

        if (isNewUser(user.getEmail())) {
            insertNewUser(user.getUuid(), user.getEmail());
            userKafkaDispather.send("ECOMMERCE_USER_CREATED", user.getUuid(), user);
            System.out.println("User created!");
        } else {
            userKafkaDispather.send("ECOMMERCE_USER_ALREADY_CREATED", user.getUuid(), user);
            System.out.println("User was already created!");
        }

    }

    public void insertNewUser(String uuid, String email) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:src/bd/users_database.sqlite";
            connection = DriverManager.getConnection(url);

            var statementInsert = connection.prepareStatement("INSERT INTO users (uuid, email) VALUES (?, ?)");

            statementInsert.setString(1, uuid);
            statementInsert.setString(2, email);
            statementInsert.execute();

            statementInsert.close();
            connection.close();
            System.out.println("User uuid " + uuid + " e email " + email + " inserted!");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

    }

    public boolean isNewUser(String email) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        boolean retorno = true;
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:src/bd/users_database.sqlite";
            connection = DriverManager.getConnection(url);

            var statementSelect = connection.prepareStatement("SELECT email FROM users WHERE email = ?");

            statementSelect.setString(1, email);
            var results = statementSelect.executeQuery();

            if (results.next()) {
                retorno = false;
            } else {
                retorno = true;
            }

            statementSelect.close();
            connection.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return retorno;

    }

}
