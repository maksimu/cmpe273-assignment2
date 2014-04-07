package edu.sjsu.cmpe.procurementv2.services;

import edu.sjsu.cmpe.procurementv2.ProcurementApp;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsQueue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import javax.jms.*;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * User: maksim
 * Date: 4/1/14 - 7:15 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ProcurementApp.class)
public class StompServiceTest {

    @Autowired
    StompService stompService;

    @Autowired
    RestTemplate restTemplate;

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


    @Test
    public void testShouldSendAndReceiveStompMessage() throws Exception{

        String messageToSend = "HI MAX";

        System.out.println("sending [" + messageToSend + "]");
        stompService.sendMessage(messageToSend);

        System.out.println("getting message");
        String messageToReceive = stompService.getMessage();
        System.out.println("got [" + messageToReceive + "]");


        System.out.println("messageToSend   =[" + messageToSend + "]");
        System.out.println("messageToReceive=[" + messageToReceive + "]");

        Assert.assertEquals("NOT EQUAL", messageToReceive, messageToSend);

    }

    @Test
    public void testLoopThroughAllQueues() throws JMSException {
        StompJmsConnectionFactory stompJmsConnectionFactory = new StompJmsConnectionFactory();
        stompJmsConnectionFactory.setBrokerURI("tcp://" + apolloHost + ":" + stompPort);
        Connection connection = stompJmsConnectionFactory.createConnection(stompAdmin, stompPassw);
        connection.start();


        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        StompJmsQueue dest = new StompJmsQueue("/queue/", "53730.book.orders");
        QueueBrowser browser = session.createBrowser(dest);

        Enumeration enumeration = browser.getEnumeration();

        while (enumeration.hasMoreElements()) {
            System.out.println("Got extra message: " + ((TextMessage) enumeration.nextElement()).getText());

            MessageConsumer consumer = session.createConsumer(dest);

            Message msg = consumer.receive(1000);

            if(msg != null) {

                String body;
                if (msg instanceof TextMessage) {
                    body = ((TextMessage) msg).getText();
                    System.out.println("   ----> " + body);
                } else {
                    throw new JMSException("This message is not a TextMessage");
                }

            }

        }
        session.close();
        browser.close();
        connection.stop();
        connection.close();

    }


    @Test
    public void testLoop() throws JMSException {
        StompJmsQueue dest = new StompJmsQueue("/queue/", "53730.book.orders");

        StompJmsConnectionFactory stompJmsConnectionFactory = new StompJmsConnectionFactory();
        stompJmsConnectionFactory.setBrokerURI("tcp://" + apolloHost + ":" + stompPort);
        Connection connection = stompJmsConnectionFactory.createConnection(stompAdmin, stompPassw);
        connection.start();


        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(dest);

        Message msg = consumer.receive(1000);

        if(msg != null) {

            String body;
            if (msg instanceof TextMessage) {
                body = ((TextMessage) msg).getText();
                System.out.println("   ----> " + body);
            } else {
                throw new JMSException("This message is not a TextMessage");
            }

        }


        consumer.close();
        session.close();
        connection.stop();
        connection.close();
    }



    @Test
    public void testPostToPublisher(){

        int[] bookIsbns = {1, 2, 3, 4};

        HashMap<String, Object> bookOrder = new HashMap<String, Object>();
        bookOrder.put("id", "53730");
        bookOrder.put("order_book_isbns", bookIsbns);

        HashMap<String, String> hashMap = restTemplate.postForObject("http://" + apolloHost + ":9000/orders", bookOrder, HashMap.class);

        System.out.println(hashMap.get("msg"));
    }


    @Test
    public void testShouldGetBooksFromPublisherThenPostToStompTopic() throws JMSException {
        long isbn = 19999;
        String title = "Book about Max";
        String category = "documentary";
        String coverimage = "http://someimagewebsite.com/image.jpg";

        String topicName = "53730.book." + category; // 53730.book.computer
        // 123:"Restful Web Services":"computer":"http://goo.gl/ZGmzoJ"
        String topicMessage = String.format("%d:\"%s\":\"%s\":\"%s\"", isbn, title, category, coverimage);

        boolean isSent = stompService.sendMessageToTopic(topicName, topicMessage);
        System.out.println(isSent ? "Sent" : "Not Sent");

        isSent = stompService.sendMessageToTopic(topicName, topicMessage);
        System.out.println(isSent ? "Sent" : "Not Sent");
        isSent = stompService.sendMessageToTopic(topicName, topicMessage);
        System.out.println(isSent ? "Sent" : "Not Sent");
        isSent = stompService.sendMessageToTopic(topicName, topicMessage);
        System.out.println(isSent ? "Sent" : "Not Sent");
        isSent = stompService.sendMessageToTopic(topicName, topicMessage);
        System.out.println(isSent ? "Sent" : "Not Sent");
        isSent = stompService.sendMessageToTopic(topicName, topicMessage);
        System.out.println(isSent ? "Sent" : "Not Sent");
        isSent = stompService.sendMessageToTopic(topicName, topicMessage);
        System.out.println(isSent ? "Sent" : "Not Sent");
        isSent = stompService.sendMessageToTopic(topicName, topicMessage);
        System.out.println(isSent ? "Sent" : "Not Sent");
        isSent = stompService.sendMessageToTopic(topicName, topicMessage);
        System.out.println(isSent ? "Sent" : "Not Sent");
        isSent = stompService.sendMessageToTopic(topicName, topicMessage);
        System.out.println(isSent ? "Sent" : "Not Sent");
        isSent = stompService.sendMessageToTopic(topicName, topicMessage);
        System.out.println(isSent ? "Sent" : "Not Sent");
        isSent = stompService.sendMessageToTopic(topicName, topicMessage);
        System.out.println(isSent ? "Sent" : "Not Sent");
        isSent = stompService.sendMessageToTopic(topicName, topicMessage);
        System.out.println(isSent ? "Sent" : "Not Sent");
        isSent = stompService.sendMessageToTopic(topicName, topicMessage);
        System.out.println(isSent ? "Sent" : "Not Sent");
        isSent = stompService.sendMessageToTopic(topicName, topicMessage);
        System.out.println(isSent ? "Sent" : "Not Sent");
    }

}
