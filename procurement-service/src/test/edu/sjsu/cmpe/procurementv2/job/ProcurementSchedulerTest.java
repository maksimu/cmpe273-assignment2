package edu.sjsu.cmpe.procurementv2.job;

import edu.sjsu.cmpe.procurementv2.ProcurementApp;
import edu.sjsu.cmpe.procurementv2.dto.Book;
import edu.sjsu.cmpe.procurementv2.dto.ShippedBooks;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import javax.jms.*;

/**
 * User: maksim
 * Date: 3/30/14 - 8:19 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ProcurementApp.class)
public class ProcurementSchedulerTest {

    @Value("${apolloHost}")
    String apolloHost;
    @Value("${apolloPort}")
    String stompPort;
    @Value("${apolloUser}")
    String stompAdmin;
    @Value("${apolloPassword}")
    String stompPassw;
    @Value("${stompQueueName}")
    String stompQueueName;
//    private String apolloHost = "54.219.156.168";
//    private int port = 61613;
    private String destinationTopicPrefix = "/topic/53730.book.";
    private String queue = "/queue/53730.book.orders";

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ProcurementScheduler procurementScheduler;



    @Test
    public void StompTest() throws JMSException {

        StompJmsConnectionFactory stompJmsConnectionFactory = new StompJmsConnectionFactory();
        stompJmsConnectionFactory.setBrokerURI("tcp://" + apolloHost + ":" + stompPort);
        Connection connection = stompJmsConnectionFactory.createConnection("admin", "password");
        connection.start();


        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest = new StompJmsDestination(destinationTopicPrefix + "");


        MessageConsumer consumer = session.createConsumer(dest);
        long start = System.currentTimeMillis();
        long count = 1;
        System.out.println("Waiting for messages...");

        while (true) {
            Message msg = consumer.receive();
            if (msg instanceof TextMessage) {
                String body = ((TextMessage) msg).getText();
                if ("SHUTDOWN".equals(body)) {
                    long diff = System.currentTimeMillis() - start;
                    System.out.println(String.format("Received %d in %.2f seconds", count, (1.0 * diff / 1000.0)));
                    break;
                } else {
                    if (count != msg.getIntProperty("id")) {
                        System.out.println("mismatch: " + count + "!=" + msg.getIntProperty("id"));
                    }
                    count = msg.getIntProperty("id");

                    if (count == 0) {
                        start = System.currentTimeMillis();
                    }
                    if (count % 1000 == 0) {
                        System.out.println(String.format("Received %d messages.", count));
                    }
                    count++;
                }

            } else {
                System.out.println("Unexpected message type: " + msg.getClass());
            }
        }
        connection.close();
    }

    @Test
    public void testGetShippedBooks(){
        ShippedBooks shipppedBooksJsonString = restTemplate.getForObject("http://" + apolloHost + ":9000/orders/53730", ShippedBooks.class);

        for (Book book : shipppedBooksJsonString.getShipped_books()) {
            System.out.println(book.getTitle());
        }

    }

    @Test
    public void testShouldGetShippedBookThenPostToStompTopic() throws JMSException {
        procurementScheduler.newBooksFromPublisher();
        procurementScheduler.newBooksFromPublisher();
        procurementScheduler.newBooksFromPublisher();
        procurementScheduler.newBooksFromPublisher();
        procurementScheduler.newBooksFromPublisher();
        procurementScheduler.newBooksFromPublisher();
        procurementScheduler.newBooksFromPublisher();
        procurementScheduler.newBooksFromPublisher();
        procurementScheduler.newBooksFromPublisher();
        procurementScheduler.newBooksFromPublisher();
    }


}
