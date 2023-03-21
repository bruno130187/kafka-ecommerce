package br.com.bruno.http;

import br.com.bruno.order.NewOrderServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ecommerceServiceHttp {

    public static void main(String[] args) throws Exception {

        //Link para chamar no navegador ou no postman como GET
        //http://localhost:8080/newOrderServlet?email=email@email.com&amount=4600
        var server = new Server(8080);

        var context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new NewOrderServlet()), "/newOrderServlet");

        server.setHandler(context);

        server.start();
        server.join();

    }

}
