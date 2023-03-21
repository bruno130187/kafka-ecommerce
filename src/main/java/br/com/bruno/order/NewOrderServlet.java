package br.com.bruno.order;

import br.com.bruno.email.Email;
import br.com.bruno.kafka.KafkaDispather;
import br.com.bruno.users.CreateuserService;
import br.com.bruno.users.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispather<Order> orderDispatcher = new KafkaDispather<>();
    private final KafkaDispather<Email> emailDispatcher = new KafkaDispather<>();
    private final KafkaDispather<User> userKafkaDispather = new KafkaDispather<>();

    @Override
    public void destroy() {
        orderDispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var emailPerson = req.getParameter("email");
            var orderId = UUID.randomUUID().toString();
            var amount = req.getParameter("amount");
            var userUuid = UUID.randomUUID().toString();
            var user = new User(userUuid, emailPerson);

            //verifica se email já está cadastrado e se for novo email cadastra ele
            CreateuserService createuserService;
            try {
                createuserService = new CreateuserService();
                if (createuserService.isNewUser(emailPerson)) {
                    createuserService.insertNewUser(userUuid, emailPerson);
                    userKafkaDispather.send("ECOMMERCE_USER_CREATED", userUuid, user);
                    System.out.println("User created!");

                    //send e-mail
                    var subject = "Subject " + UUID.randomUUID().toString();
                    var body = "User created successfully!";
                    var email = new Email(subject, body);
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", emailPerson, email);
                    System.out.println("Email sent successfully!");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            var order = new Order(emailPerson, orderId, new BigDecimal(amount));
            orderDispatcher.send("ECOMMERCE_NEW_ORDER", emailPerson, order);
            System.out.println("New order sent successfully!");

            var subject = "Subject " + UUID.randomUUID().toString();
            var body = "Thank you for your order! Its in process!";
            var email = new Email(subject, body);
            emailDispatcher.send("ECOMMERCE_SEND_EMAIL", emailPerson, email);
            System.out.println("Email sent successfully!");

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("New order send successfully!");
        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }
}
