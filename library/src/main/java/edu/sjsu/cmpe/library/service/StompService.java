package edu.sjsu.cmpe.library.service;

import edu.sjsu.cmpe.library.LibraryService;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsQueue;
import org.fusesource.stomp.jms.StompJmsTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * User: maksim
 * Date: 3/31/14 - 8:57 PM
 */
public class StompService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    String stompHost;
    String stompPort;
    String stompAdmin;
    String stompPassw;
    String stompQueue;
    String stompTopicName;
    String libraryName;


    public StompService() {

        this.stompHost = LibraryService.configuration.getApolloHost();
        this.stompPort = LibraryService.configuration.getApolloPort();
        this.stompAdmin = LibraryService.configuration.getApolloUser();
        this.stompPassw = LibraryService.configuration.getApolloPassword();
        this.stompQueue = LibraryService.configuration.getStompQueueName();
        this.stompTopicName = LibraryService.configuration.getStompTopicName();
        this.libraryName = LibraryService.configuration.getLibraryName();
    }

    public boolean sendMessageToQueue(String textMessageBody) throws JMSException {
        StompJmsConnectionFactory stompJmsConnectionFactory = new StompJmsConnectionFactory();
        stompJmsConnectionFactory.setBrokerURI("tcp://" + stompHost + ":" + stompPort);
        Connection connection = stompJmsConnectionFactory.createConnection(stompAdmin, stompPassw);
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        log.info(String.format("Sending message: [%s] to [%s:%s@%s:%s%s]", textMessageBody, stompAdmin, stompPassw, stompHost, stompPort, stompQueue));

        MessageProducer producer = session.createProducer(new StompJmsQueue("/queue/", stompQueue));
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        TextMessage textMessage = session.createTextMessage(textMessageBody);
        producer.send(textMessage);
        log.info("Finish sending message: [" + textMessageBody + "]");

        producer.close();
        session.close();
        connection.stop();
        connection.close();
        return true;
    }


    public boolean sendMessageToTopic(String textMessageBody) throws JMSException {
        StompJmsConnectionFactory stompJmsConnectionFactory = new StompJmsConnectionFactory();
        stompJmsConnectionFactory.setBrokerURI("tcp://" + stompHost + ":" + stompPort);
        Connection connection = stompJmsConnectionFactory.createConnection(stompAdmin, stompPassw);
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        log.info(String.format("Sending message: [%s] to [%s:%s@%s:%s/topic/%s]", textMessageBody, stompAdmin, stompPassw, stompHost, stompPort, stompQueue));



        MessageProducer producer = session.createProducer(new StompJmsTopic("/topic/", "53730.book.computer"));
        TextMessage textMessage = session.createTextMessage(textMessageBody);
        producer.send(textMessage, DeliveryMode.NON_PERSISTENT, 1, 300000);

        log.info("Finish sending message: [" + textMessageBody + "]");

        producer.close();
        session.close();
        connection.stop();
        connection.close();
        return true;
    }

    public String getMessageFromTopic() throws JMSException {
        StompJmsTopic dest = new StompJmsTopic("/topic/", stompTopicName);

        StompJmsConnectionFactory stompJmsConnectionFactory = new StompJmsConnectionFactory();
        stompJmsConnectionFactory.setBrokerURI("tcp://" + stompHost + ":" + stompPort);
        Connection connection = stompJmsConnectionFactory.createConnection(stompAdmin, stompPassw);
        connection.start();


        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        TopicSubscriber subscriber = session.createDurableSubscriber(dest, libraryName);

        session.createTopic(stompTopicName);
        Message msg = subscriber.receive(1000);

        String body = null;

        if (msg != null) {

            if (msg instanceof TextMessage) {
                body = ((TextMessage) msg).getText();
                log.info("Got message" + body);
            } else {
                throw new JMSException("This message is not a TextMessage");
            }

        } else {
            System.out.println("NOTHING IN THE TOPIC");
        }

        subscriber.close();
        session.close();
        connection.stop();
        connection.close();

        return body;

    }

}
